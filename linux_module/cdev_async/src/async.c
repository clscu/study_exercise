
#include <sys/mman.h>
#include <sys/types.h>
#include <fcntl.h>
#include <unistd.h>
#include <signal.h>
#include <unistd.h>


#define PAGE_SIZE 4096
#define MAX_LEN 128



int fd;
void input_handler(int num)
{

	char data[MAX_LEN];
	int len;    
		 //��ȡ�����STDIN_FILENO�ϵ�����
	lseek(fd,0,SEEK_SET);
	len = read(fd, &data, MAX_LEN);
	data[len] = 0; 
	printf("input available:%s\n", data);
}





int main(int argc, char** argv) // map a normal file as shared mem:
{
	int oflags;
	char * async_file = "/dev/vir_cdev0";
	int i;
	signal(SIGIO, input_handler);    
	fd=open(async_file,O_RDWR);
	fcntl(fd, F_SETOWN, getpid());    
	oflags = fcntl(fd, F_GETFL);
  fcntl(fd, F_SETFL, oflags | FASYNC);   // ��ʱϵͳ�ͻ��Զ��������������fasync������
	while (1);


}

