ifneq ($(KERNELRELEASE),)

obj-m :=netlink_broad.o

else
#KDIR:= /lib/modules/$(shell uname -r)
KDIR:=/home/cl/Project/linux/linux-3.9.6
#KDIR:= /usr/include/build
all:
	make -C $(KDIR) M=$(PWD) modules 
clean:
	rm -f *.ko *.o *.mod.o *.mod.c .symvers

endif


