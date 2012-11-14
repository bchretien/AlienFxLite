#include <libusb.h>
#include "LEDController.h"

//some defines
#define SEND_REQUEST_TYPE 0x21
#define SEND_REQUEST 0x09
#define SEND_VALUE 0x202
#define SEND_INDEX 0x00

#define READ_REQUEST_TYPE 0xa1
#define READ_REQUEST 0x01
#define READ_VALUE 0x101
#define READ_INDEX 0x0

#define ALLPOWERFULL_ALIENFX_PID 0x512
#define ALLPOWERFULL_ALIENFX_VID 0x187c

#define AREA51_ALIENFX_PID 0x511
#define AREA51_ALIENFX_VID 0x187c

#define ALLPOWERFULL_ALIENFX 1
#define AREA51_ALIENFX 2
#define NOT_FOUND 0

#define SEND_DATA_SIZE 9
#define READ_DATA_SIZE (alienFXid == ALLPOWERFULL_ALIENFX ? 8 : 9)

//the apps global variables
libusb_context* context;
libusb_device_handle* alienFx;
int alienFXid;

void detach(libusb_device_handle* device){
  int r = libusb_kernel_driver_active(device, 0);
  if (r == 1) 
    r = libusb_detach_kernel_driver(device, 0);
  
}

void attach(libusb_device_handle* device){
  libusb_attach_kernel_driver(device, 0);
}


int WriteDevice(unsigned char* pData, int pDataLength){
  return libusb_control_transfer(alienFx, SEND_REQUEST_TYPE, SEND_REQUEST, SEND_VALUE, SEND_INDEX, pData,pDataLength,0);
}


int ReadDevice(unsigned char* pData, int pDataLength){
  return libusb_control_transfer(alienFx, READ_REQUEST_TYPE, READ_REQUEST, READ_VALUE, READ_INDEX, pData,pDataLength,0);
}

int AlienfxInit(){
  libusb_init(&context);
  libusb_set_debug (context, 3);
  alienFx = libusb_open_device_with_vid_pid(context, ALLPOWERFULL_ALIENFX_VID, ALLPOWERFULL_ALIENFX_PID);
  
  //check if we found the device
  alienFXid = ALLPOWERFULL_ALIENFX;  
  if(alienFx == NULL){
    alienFx = libusb_open_device_with_vid_pid(context, AREA51_ALIENFX_VID, AREA51_ALIENFX_PID);
    alienFXid = AREA51_ALIENFX;

    if(alienFx == NULL)
      return NOT_FOUND;
  }
  
  detach(alienFx);
  int res = libusb_claim_interface(alienFx,0);
  if(res < 0)
    return NOT_FOUND;

  return alienFXid;
}

void AlienfxDeinit(){
  libusb_release_interface(alienFx, 0);
  attach(alienFx);
  
  libusb_close(alienFx);
  libusb_exit(context);
}



/*The Interface*/


/*
 * Class:     uk_co_progger_LEDController
 * Method:    initialize
 * Signature: ()Z
 */
JNIEXPORT jint JNICALL Java_uk_co_progger_alienFXLite_led_LEDController_initialize(JNIEnv* env, jclass c){
  return AlienfxInit();
}

/*
 * Class:     uk_co_progger_LEDController
 * Method:    write
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_uk_co_progger_alienFXLite_led_LEDController_write(JNIEnv* env, jclass c, jbyteArray data){
  unsigned char dataImpl[SEND_DATA_SIZE];
  jsize len = env->GetArrayLength(data);
  if(len != SEND_DATA_SIZE)
    return 0;
  
  jbyte* body = env->GetByteArrayElements(data, 0);
  for(int i=0; i < len; ++i)
    dataImpl[i] = body[i];
  
  jint result = WriteDevice(dataImpl, len);
  env->ReleaseByteArrayElements(data, body, 0);
  
  return result;
}

/*
 * Class:     uk_co_progger_LEDController
 * Method:    read
 * Signature: ([BI)I
 */
JNIEXPORT jbyteArray JNICALL Java_uk_co_progger_alienFXLite_led_LEDController_read(JNIEnv* env, jclass c){
  unsigned char dataImpl[READ_DATA_SIZE];
  jbyteArray data = env->NewByteArray(READ_DATA_SIZE);
  int len = ReadDevice(dataImpl, READ_DATA_SIZE);
  
  jbyte* body = env->GetByteArrayElements(data, 0);
  for(int i = 0; i < len; ++i)
    body[i] = dataImpl[i];
  env->ReleaseByteArrayElements(data, body, 0);
  
  return data;
}

/*
 * Class:     uk_co_progger_LEDController
 * Method:    destroy
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_uk_co_progger_alienFXLite_led_LEDController_destroy(JNIEnv *, jclass){
  AlienfxDeinit();
}
