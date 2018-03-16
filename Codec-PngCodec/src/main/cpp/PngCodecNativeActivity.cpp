#include <android_native_app_glue.h>

#include <malloc.h>

#include "PngTest.h"

#include "JpegTest.h"

void android_main(android_app *app) {
    ALOGE("android_main start");

    char fileName[512] = {0};
    sprintf(fileName, "/data/png/%s", "png_4_2_32bit.png");

    readPngFile(fileName);

    sprintf(fileName, "/data/png/%s", "png_4_2_32bit_alpha.png");
    buildPngFile(fileName);

    sprintf(fileName, "/data/png/%s", "jpg_4_2_32bit.jpg");
    readJpegFile(fileName);

    return;
}