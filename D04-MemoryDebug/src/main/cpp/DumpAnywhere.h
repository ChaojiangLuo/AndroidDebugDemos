//
// Created by luocj on 18-12-25.
//

#ifndef DUMP_ANYWHERE_H
#define DUMP_ANYWHERE_H

#include <gralloc_priv.h>
#include <system/graphics.h>
#include <ui/GraphicBuffer.h>
#include <ui/DisplayInfo.h>
#include <ui/PixelFormat.h>
#include <utils/Log.h>
#include <utils/String8.h>

// TODO: Fix Skia.
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wunused-parameter"

#include <SkImageEncoder.h>
#include <SkData.h>
#include <SkColorSpace.h>

#pragma GCC diagnostic pop

namespace android {
    class DumpAnywhere {
    public:
        DumpAnywhere();

        DumpAnywhere(const String8 name, sp <GraphicBuffer> buffer, bool dumpPng = true);

        DumpAnywhere(const String8 name, sp <GraphicBuffer> buffer, const Rect rect, bool dumpPngs = true);

        ~DumpAnywhere() {};

        int dumpLayer(const String8 name, sp <GraphicBuffer> buffer, const Rect rect, bool dumpPngs = true);

        sk_sp <SkColorSpace> dataSpaceToColorSpace(android_dataspace d);

        SkColorType flinger2skia(PixelFormat f);

        bool isYuvBuffer(const private_handle_t *hnd);

        int getWidth(const private_handle_t *hnd);

        int getHeight(const private_handle_t *hnd);

        int writePngFile(const char *filePath, void *base, int width, int height, int stride, int format);

        int writeRawFile(const char *filePath, void *base, int size);
    };
};

#endif
