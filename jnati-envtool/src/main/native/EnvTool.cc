#include "net_sf_jnati_envtool_EnvTool.h"
#include <stdlib.h>
#include <string.h>

JNIEXPORT jstring JNICALL Java_net_sf_jnati_envtool_EnvTool_jniGetEnvVar
  (JNIEnv *env, jobject, jstring key) {
  	
  	const char *pKey = env->GetStringUTFChars(key, 0);
  	char *pVal;
    pVal = getenv (pKey);
    env->ReleaseStringUTFChars(key, pKey);
    
    if (pVal != NULL) {
    	return env->NewStringUTF(pVal);
    } else {
    	return NULL;
    }
  }

JNIEXPORT void JNICALL Java_net_sf_jnati_envtool_EnvTool_jniSetEnvVar
  (JNIEnv *env, jobject, jstring key, jstring value) {
  	
  	const char *pKey = env->GetStringUTFChars(key, 0);
  	const char *pVal = env->GetStringUTFChars(value, 0);
  	
  	char   *line;
    line = (char*) malloc(strlen(pKey) + strlen(pVal) + 2);
    sprintf(line, "%s=%s", pKey, pVal);
    putenv(line);
    env->ReleaseStringUTFChars(key, pKey);
    env->ReleaseStringUTFChars(value, pVal);
  	
  }
