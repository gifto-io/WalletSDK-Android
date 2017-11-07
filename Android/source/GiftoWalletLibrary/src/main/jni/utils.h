//
// Created by Thong Nguyen on 9/13/17.
//



#include "android/log.h"

#ifdef _DEBUG
#define  LOG_TAG    "Ahihi"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#else
#define  LOGD(...)
#define  LOGE(...)
#endif


#ifndef TESTROSECOINSDK_UTILS_H
#define TESTROSECOINSDK_UTILS_H

/**
 * Generate secret key from apikey and identity data
 * @param apiKey
 * @param identity
 * @return secret key
 */
char* utils_generate_secret_key_ahihi(char* apiKey, char* identity);

#endif //TESTROSECOINSDK_UTILS_H