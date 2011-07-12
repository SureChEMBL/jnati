#include "net_sf_jnati_testharness_NativeWrapper.h"

JNIEXPORT jint JNICALL Java_net_sf_jnati_testharness_NativeWrapper_getAnswer
  (JNIEnv *env, jobject obj) {
  return 42;
}
