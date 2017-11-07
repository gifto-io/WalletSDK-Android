//
// Created by Thong Nguyen on 9/13/17.
//

#include <jni.h>
#include "utils.h"


/**
 * Native JNI method
 * Generate secret key
 */
JNIEXPORT jstring JNICALL Java_io_gifto_wallet_utils_AES256Cipher_generateSecretKey(JNIEnv * env, jobject obj, jstring apiKey, jstring identity)
{
    jboolean iscopy = JNI_FALSE;
    const char* api = (*env)->GetStringUTFChars(env, apiKey, &iscopy);
    const char* iden = (*env)->GetStringUTFChars(env, identity, &iscopy);
    char* key = utils_generate_secret_key_ahihi(api, iden);
    LOGE("keyjni = %s", key);
    jstring jkey = (*env)->NewStringUTF(env, key);
    (*env)->ReleaseStringUTFChars(env, apiKey, api);
    (*env)->ReleaseStringUTFChars(env, identity, iden);
    return jkey;
}
