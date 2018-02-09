#include <android/log.h>
#include <android_native_app_glue.h>

#include <malloc.h>

#include <string.h>

#include <png.h>

#define ALOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "PngCodec", __VA_ARGS__))
#define ALOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "PngCodec", __VA_ARGS__))

void readPngFile(char *name) {
    ALOGE("readPngFile %s\n",  name);
    // 前边几句是扯淡，初始化各种结构
    FILE *file = fopen(name, "rb");

    png_structp png_ptr = png_create_read_struct(PNG_LIBPNG_VER_STRING, 0, 0, 0);

    png_infop info_ptr = png_create_info_struct(png_ptr);

    setjmp(png_jmpbuf(png_ptr));

    // 这句很重要
    png_init_io(png_ptr, file);

    // 读文件了
    png_read_png(png_ptr, info_ptr, PNG_TRANSFORM_EXPAND, 0);

    // 得到文件的宽高色深
    int m_width = png_get_image_width(png_ptr, info_ptr);
    int m_height = png_get_image_height(png_ptr, info_ptr);

    int color_type = png_get_color_type(png_ptr, info_ptr);

    // 申请个内存玩玩，这里用的是c++语法，甭想再c上编过
    int size = m_height * m_width * 4;

    char* head = static_cast<char*>(malloc(size));

    int pos = 0;

    // row_pointers里边就是传说中的rgba数据了
    png_bytep *row_pointers = png_get_rows(png_ptr, info_ptr);

    // 拷贝！！注意，如果你读取的png没有A通道，就要3位3位的读。还有就是注意字节对其的问题，最简单的就是别用不能被4整除的宽度就行了。读过你实在想用，就要在这里加上相关的对齐处理。
    ALOGE("png file %s size(%dx%d) pixles:", name, m_width, m_height);
    for (int i = 0; i < m_height; i++) {
        for (int j = 0; j < (4 * m_width); j += 4) {
            head[pos] = row_pointers[i][j + 2]; // blue
            pos++;
            head[pos] = row_pointers[i][j + 1]; // green
            pos++;
            head[pos] = row_pointers[i][j];   // red
            pos++;
            head[pos] = row_pointers[i][j + 3]; // alpha
            pos++;
            ALOGE("%02x %02x %02x %02x", head[pos-4], head[pos-3], head[pos-2], head[pos-1]);
        }
    }

    free(head);

    // 好了，你可以用这个数据作任何的事情了。。。把它显示出来或者打印出来都行。
    png_destroy_read_struct(&png_ptr, &info_ptr, 0);

    fclose(file);

    return;
}

void android_main(android_app *app) {
    ALOGE("android_main start");

    char fileName[512] = {0};
    sprintf(fileName, "/data/png/%s", "png_4_2_32bit.png");

    readPngFile(fileName);

    return;
}