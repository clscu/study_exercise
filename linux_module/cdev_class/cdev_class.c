#include <linux/init.h>
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/fs.h>
#include <linux/cdev.h>
#include <asm/uaccess.h>
#include <linux/device.h>

MODULE_LICENSE("GPL");
MODULE_AUTHOR("CL");
MODULE_DESCRIPTION("vir_cdev");
MODULE_VERSION("1.0.0");

#define CL_PRINT(fmt,args...) printk("CL: %s  %d  "fmt,__FILE__,__LINE__,##args)
#define BUF_LEN 128
static char device_name[]="vir_cdev";
static dev_t dev;
static struct cdev *vir_cdev;
static char cdev_buf[BUF_LEN];
static struct class *vir_cdev_class;


static ssize_t cdev_write (struct file *filp, const char __user *buf,
                            size_t count, loff_t *pos);

static int cdev_open (struct inode *inode, struct file *filp)
{
    return 0;
}

static int cdev_release (struct inode *inode, struct file *filp)
{
    return 0;
}

static ssize_t cdev_read (struct file *filp, char __user *buf,
                           size_t count, loff_t *pos)
{
	CL_PRINT("count = %d\n",count);
    int size = count < BUF_LEN ? count : BUF_LEN;
	CL_PRINT("size = %d count=%d\n",size,count);
    printk("cdev: Read  !\n");
	*pos+=size;
    if (copy_to_user(buf, cdev_buf, size))
        return -ENOMEM;
    return size;
}
static ssize_t cdev_write (struct file *filp, const char __user *buf,
                            size_t count, loff_t *pos)
{
    int size = count < BUF_LEN ? count :BUF_LEN;
    printk("cdev: Write  !\n");
    memset(cdev_buf, 0, sizeof(cdev_buf));
    if (copy_from_user(cdev_buf, buf, size))
        return -ENOMEM;
    return size;
}

static struct file_operations cdev_fops = {
        .read = cdev_read,
        .write = cdev_write,
        .open = cdev_open,
        .release = cdev_release,
};

static int __init cdev_vir_init(void)
{
	int ret=0;
	printk(KERN_ERR "cdev world!\n");
	ret = alloc_chrdev_region(&dev, 0, 2, "vir_cdev");
    if (ret)
    {
        printk("cdev: alloc_chardev_region failed!\n");
        return ret;
    }
	
    vir_cdev = cdev_alloc();
    if (vir_cdev == NULL)
    {
        printk("cdev: alloc cdev failed!\n");
        unregister_chrdev_region(dev, 2);
        return -ENOMEM;
    }	

	vir_cdev->ops = &cdev_fops;
    vir_cdev->owner = THIS_MODULE;

    ret = cdev_add(vir_cdev, dev, 1);
    if (ret)
    {
        printk("cdev: cdev_add failed!\n");
        unregister_chrdev_region(vir_cdev->dev, 2);
        cdev_del(vir_cdev);
        return ret;
    }
	CL_PRINT("new dev id is %d\n",dev);
	CL_PRINT("MAJOR= %d, MINOR = %d\n",MAJOR(dev),MINOR(dev));
	

	vir_cdev_class = class_create(THIS_MODULE,device_name);

	if(IS_ERR(vir_cdev_class)) {
        printk("Err: failed in creating class./n");
        return -1;
    }
	CL_PRINT("class_create\n");
	dev = device_create(vir_cdev_class,NULL,dev,NULL,"vir_cdev");
	CL_PRINT("device_create\n");



 return 0;
}
static void __exit cdev_vir_exit(void)
{
	device_destroy(vir_cdev_class,dev);
	CL_PRINT("device unregister\n");
    unregister_chrdev_region(dev, 2);
	CL_PRINT("unregister chrdev\n");
    cdev_del(vir_cdev);
	CL_PRINT("cdev del\n");
	class_destroy(vir_cdev_class);
	printk(KERN_EMERG "cdev exit!\n");
}

module_init(cdev_vir_init);
module_exit(cdev_vir_exit);


