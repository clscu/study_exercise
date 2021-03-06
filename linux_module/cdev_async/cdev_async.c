#include "cdev_async.h"

MODULE_LICENSE("GPL");
MODULE_AUTHOR("CL");
MODULE_DESCRIPTION("vir_cdev");
MODULE_VERSION("1.0.0");

static struct vir_device *vir_devices;
static struct class *vir_cdev_class;
static struct kmem_cache *vir_dev_cachep;

static int cdev_fasync(int fd, struct file *filp, int mode);

static ssize_t cdev_write (struct file *filp, const char __user *buf,
		size_t count, loff_t *pos);

static int cdev_open (struct inode *inode, struct file *filp)
{
	struct vir_device *dev;
	dev = container_of(inode->i_cdev,struct vir_device,cdev);
	CL_PRINT("dev = %p\n",dev);
	filp->private_data = dev;
	return 0;
}



static int cdev_release (struct inode *inode, struct file *filp)
{
	cdev_fasync(-1, filp, 0);
	return 0;
}

static ssize_t cdev_read (struct file *filp, char __user *buf,
		size_t count, loff_t *pos)
{	
	ssize_t size;	
	if(count <=0 )
	{
		return -EINVAL;
	}	


	struct vir_device *dev = filp->private_data;
	if(down_interruptible(&dev->sem))
		return -ERESTARTSYS;
	size = dev->buf_len;
	if(*pos >= size)
	{
		up (&dev->sem);
		return 0;
	}
	size = count < size ? count :size;
	if (copy_to_user(buf, dev->buf, size))
	{
		up (&dev->sem);
		return -EFAULT;
	}

	*pos += size; 
	up (&dev->sem);
	return size;
}
static ssize_t cdev_write (struct file *filp, const char __user *buf,
		size_t count, loff_t *pos)
{	
	size_t size;
	if(count <=0)
		return -EINVAL;

	struct vir_device *dev = filp->private_data;
	if (down_interruptible(&dev->sem))
		return -ERESTARTSYS;
		
		size = dev->buf_len;
		size = count < size ? count :size;
		if (copy_from_user(dev->buf, buf, size))
		{
			up (&dev->sem);
			return -EFAULT;
		}
		*pos +=size;
		up (&dev->sem);
		if (dev->async_queue){
		kill_fasync(&dev->async_queue, SIGIO, POLL_IN);
	}
		return size;

}

static int cdev_mmap(struct file *filp,struct vm_area_struct *vma)  
{  
	 int ret;
	 unsigned char *kpuf_mmap_address;  
	 int pfn;   // page frame number  describe the physical address
	 struct vir_device *dev = filp->private_data;
	 vma->vm_flags |= VM_NONLINEAR;  // cant't swap
	 kpuf_mmap_address = (char *)(((unsigned long)(dev->buf)+PAGE_SIZE-1) & PAGE_MASK);     // get memory page address
	 pfn = virt_to_phys(kpuf_mmap_address) >> PAGE_SHIFT;

		CL_INFO("vma start %u mmap\n",vma->vm_start);
  ret=remap_pfn_range(vma,vma->vm_start,pfn,vma->vm_end-vma->vm_start,PAGE_SHARED);  
  if(ret!=0)  
   {  
   	CL_ERR("remap_pfn\n");
    return -EAGAIN;  
   }   
  return ret;  
}    

loff_t cdev_llseek(struct file *filp, loff_t off, int whence)
{
	 struct vir_device *dev = filp->private_data;
	loff_t newpos;

	switch(whence) {
	  case 0: /* SEEK_SET */
		newpos = off;
		break;

	  case 1: /* SEEK_CUR */
		newpos = filp->f_pos + off;
		break;

	  case 2: /* SEEK_END */
		newpos = dev->buf_len + off;
		break;

	  default: /* can't happen */
		return -EINVAL;
	}
	if (newpos < 0) return -EINVAL;
	filp->f_pos = newpos;
	return newpos;
}
static int cdev_fasync(int fd, struct file *filp, int mode)
{
	struct vir_device *dev = filp->private_data;
	printk("fasync\n");

	return fasync_helper(fd, filp, mode, &dev->async_queue);
}

static struct file_operations cdev_fops = {
	.read = cdev_read,
	.write = cdev_write,
	.open = cdev_open,
	.release = cdev_release,
	.mmap = cdev_mmap,
	.llseek = cdev_llseek,
	.fasync =	cdev_fasync,     

};

static int cdev_setup(struct vir_device *vir_device,dev_t dev,char *node_name)
{
	int ret = 0;

	cdev_init(&vir_device->cdev,dev);
	vir_device->head = vir_device->tail = 0;
	vir_device->cdev.ops=&cdev_fops;
	vir_device->cdev.owner = THIS_MODULE;
	vir_device->buf_len = BUF_LEN;
	vir_device->buf = kmem_cache_zalloc(vir_dev_cachep, GFP_KERNEL);
	ret = cdev_add(&vir_device->cdev, dev, 1);
	if (ret)
	{
		CL_ERR("cdev: cdev_add failed!\n");
		cdev_del(&vir_device->cdev);      
		return ret;
	}

	sema_init(&vir_device->sem,1);	
	dev = device_create(vir_cdev_class,NULL,dev,NULL,node_name);	
	if(dev == NULL)
	{
		ret = -1;
		cdev_del(&vir_device->cdev);
	}	
	return ret;

}
static void cdev_cleanup(struct vir_device *vir_device)
{
	if(	vir_device->buf !=NULL)
		kmem_cache_free(vir_dev_cachep,vir_device->buf);
	cdev_del(&vir_device->cdev);
}

static int __init cdev_vir_init(void)
{
	int ret=0;
	dev_t dev;
	int i;
	char node_name[NAME_LEN];
	printk(KERN_ERR "cdev ringbuffer world!\n");
	ret = alloc_chrdev_region(&dev, 0, DEV_NUM, device_name);
	if (ret)
	{
		CL_ERR("cdev: alloc_chardev_region failed!\n");
		ret = -1;
		return ret;
	}

	vir_cdev_class = class_create(THIS_MODULE,device_name);
	if(IS_ERR(vir_cdev_class)) {
		CL_ERR(" failed in creating class.\n");
		ret = -1;
		goto err_class_create;
	}
	CL_PRINT("class_create\n");
	vir_dev_cachep = kmem_cache_create("vir_pdev",
			BUF_LEN, 0, 0, NULL);
	if(vir_dev_cachep == NULL)
	{
		CL_ERR("kmem create error\n");
		ret = -1;
		goto err_kmem_cache_create;
	}
	vir_devices = kzalloc(sizeof(struct vir_device)*DEV_NUM,GFP_KERNEL);	
	if(vir_devices == NULL)
	{
		CL_ERR("kmalloc vir_device \n");
		ret = -1;
		goto err_kzalloc_err;
	}
	for(i=0;i<DEV_NUM;i++)
	{
		sprintf(node_name,"%s%d",device_name,i);		
		cdev_setup(&vir_devices[i],dev+i,node_name);		
	}
	goto err_chrdev_region;


err_kzalloc_err:
	kmem_cache_destroy(vir_dev_cachep);
err_kmem_cache_create:
	class_destroy(vir_cdev_class);
err_class_create:
	unregister_chrdev_region(dev, DEV_NUM);
err_chrdev_region:	

	return ret;
}
static void __exit cdev_vir_exit(void)
{
	int i;
	for(i=0;i<DEV_NUM;i++)
	{		
		cdev_cleanup(&vir_devices[i]);
		device_destroy(vir_cdev_class,vir_devices[i].cdev.dev);
		CL_PRINT("ddd	\n");
	}

	unregister_chrdev_region(vir_devices[0].cdev.dev, DEV_NUM); 
	kmem_cache_destroy(vir_dev_cachep);	
	class_destroy(vir_cdev_class);
	kfree(vir_devices);
}

module_init(cdev_vir_init);
module_exit(cdev_vir_exit);


