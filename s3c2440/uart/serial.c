#include "s3c24xx.h"
#include "serial.h"

#define TXD0READY   (1<<2)
#define RXD0READY   (1)

#define PCLK            50000000    //50M
#define UART_CLK        PCLK       
#define UART_BAUD_RATE  115200      
#define UART_BRD        ((UART_CLK  / (UART_BAUD_RATE * 16)) - 1)

/*
 * ��ʼ��UART0
 * 115200,8N1,������
 */
void uart0_init(void)
{
    GPHCON  |= 0xa0;    // GPH2,GPH3 as TXD0,RXD0
    GPHUP   = 0x0c;     // GPH2,GPH3

    ULCON0  = 0x03;     
    UCON0   = 0x05;     
    UFCON0  = 0x00;    
    UMCON0  = 0x00;     
    UBRDIV0 = UART_BRD;  
}

// send
void putc(unsigned char c)
{    
    while (!(UTRSTAT0 & TXD0READY));
    UTXH0 = c;
}
// recv
unsigned char getc(void)
{   
    while (!(UTRSTAT0 & RXD0READY));
    return URXH0;
}
