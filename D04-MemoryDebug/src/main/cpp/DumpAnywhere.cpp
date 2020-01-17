//
// Created by luocj on 18-12-25.
//

#ifndef LOG_TAG
#define LOG_TAG "DumpAnywhere"
#endif
#define LOG_NDEBUG 0

#include <qdMetaData.h>
#include <sys/stat.h>
#include <time.h>
#include <utils/Timers.h>

#include "DumpAnywhere.h"

namespace android {
    DumpAnywhere::DumpAnywhere() {
        ALOGI("xm-gfx: new DumpAnywhere\n");
    }

    DumpAnywhere::DumpAnywhere(const String8 name, sp <GraphicBuffer> buffer, bool dumpPngs)
            : DumpAnywhere(name, buffer, Rect::INVALID_RECT, dumpPngs) {
        ALOGI("xm-gfx: new DumpAnywhere name %s dumpPngs %d\n", name.string(), dumpPngs);
    }

    DumpAnywhere::DumpAnywhere(const String8 name, sp <GraphicBuffer> buffer, const Rect rect,
                               bool dumpPngs) {
        ALOGI("xm-gfx: new DumpAnywhere name %s dumpPngs %d\n", name.string(), dumpPngs);
        dumpLayer(name, buffer, rect, dumpPngs);
    }

    bool DumpAnywhere::isYuvBuffer(const private_handle_t *hnd) {
        return (hnd && (hnd->buffer_type == BUFFER_TYPE_VIDEO));
    }

    int DumpAnywhere::getWidth(const private_handle_t *hnd) {
        if (isYuvBuffer(hnd)) {
            MetaData_t *metadata = (MetaData_t *) hnd->base_metadata;
            if (metadata && metadata->operation & UPDATE_BUFFER_GEOMETRY) {
                return metadata->bufferDim.sliceWidth;
            }
        }
        return hnd->width;
    }

    int DumpAnywhere::getHeight(const private_handle_t *hnd) {
        if (isYuvBuffer(hnd)) {
            MetaData_t *metadata = (MetaData_t *) hnd->base_metadata;
            if (metadata && metadata->operation & UPDATE_BUFFER_GEOMETRY) {
                return metadata->bufferDim.sliceHeight;
            }
        }
        return hnd->height;
    }

    sk_sp <SkColorSpace> DumpAnywhere::dataSpaceToColorSpace(android_dataspace d) {
        switch (d) {
            case HAL_DATASPACE_V0_SRGB:
                return SkColorSpace::MakeSRGB();
            case HAL_DATASPACE_DISPLAY_P3:
                return SkColorSpace::MakeRGB(
                        SkColorSpace::kSRGB_RenderTargetGamma, SkColorSpace::kDCIP3_D65_Gamut);
            default:
                return nullptr;
        }
    }

    SkColorType DumpAnywhere::flinger2skia(PixelFormat f) {
        switch (f) {
            case PIXEL_FORMAT_RGB_565:
                return kRGB_565_SkColorType;
            default:
                return kN32_SkColorType;
        }
    }

    int DumpAnywhere::dumpLayer(const String8 name, sp <GraphicBuffer> buffer, const Rect rect,
                                bool dumpPngs) {
        ALOGE("xm-gfx: DumpAnywhere dumpLayer %s \n", name.string());
        private_handle_t *hnd = (private_handle_t *) buffer->handle;

        if (hnd == nullptr) {
            ALOGE("xm-gfx: dumpLayer %s failed to get handle\n", name.string());
            return -1;
        }

        void *base = nullptr;
        if (rect.isValid()) {
            buffer->lock(GraphicBuffer::USAGE_SW_READ_OFTEN, rect, &base);
        } else {
            buffer->lock(GraphicBuffer::USAGE_SW_READ_OFTEN, &base);
        }
        if (base == nullptr) {
            ALOGE("xm-gfx: dumpLayer %s failed to lock buffer\n", name.string());
            return -1;
        }

        struct stat buf;
        std::string filePath("/data/vendor/display/frame_dump_primary/");
        if (stat(filePath.c_str(), &buf) != 0) {
            mkdir(filePath.c_str(), 0777);
        }

        std::string fileName(name.string());
        if (fileName.find("#") != std::string::npos) {
            fileName.replace(fileName.find("#"), 1, "_");
        }
        if (fileName.find("/") != std::string::npos) {
            fileName.replace(fileName.find("/"), 1, "_");
        }

        ALOGE("xm-gfx: DumpAnywhere dumpLayer %s filePath %s fileName %s\n",
              name.string(), filePath.c_str(), fileName.c_str());

        char size_str[64];
        int width = getWidth(hnd);
        int height = getHeight(hnd);
        int stride = hnd->GetStride();
        if (rect.isValid()) {
            width = rect.getWidth();
            height = rect.getHeight();
            stride = width;
        }
        snprintf(size_str, sizeof(size_str), "_%d.%dx%d", stride, width, height);
        fileName.append(size_str);

        ALOGE("xm-gfx: DumpAnywhere dumpLayer %s filePath %s fileName %s size_str %s\n",
              name.string(), filePath.c_str(), fileName.c_str(), size_str);

        char time_str[64];
        time_t timeNow;
        tm dumpTime;

        time(&timeNow);
        localtime_r(&timeNow, &dumpTime);

        snprintf(time_str, sizeof(time_str), "_%04d.%02d.%02d.%02d.%02d.%02d",
                 dumpTime.tm_year + 1900, dumpTime.tm_mon + 1, dumpTime.tm_mday, dumpTime.tm_hour,
                 dumpTime.tm_min, dumpTime.tm_sec);

        fileName.append(time_str);

        ALOGE("xm-gfx: DumpAnywhere dumpLayer %s filePath %s fileName %s time_str %s\n",
              name.string(), filePath.c_str(), fileName.c_str(), time_str);

        fileName = filePath.append(fileName);
        if (isYuvBuffer(hnd)) {
            fileName.append(".yuv");
            writeRawFile(fileName.c_str(), base, stride * height * bytesPerPixel(hnd->format));
        } else if (dumpPngs) {
            fileName.append(".png");
            writePngFile(fileName.c_str(), base, width, height, stride, hnd->format);
        } else {
            fileName.append(".raw");
            writeRawFile(fileName.c_str(), base, stride * height * bytesPerPixel(hnd->format));
        }

        ALOGE("xm-gfx: DumpAnywhere dumpLayer %s filePath %s fileName %s\n",
              name.string(), filePath.c_str(), fileName.c_str());
        return 0;
    }

    int
    DumpAnywhere::writePngFile(const char *filePath, void *base, int width, int height, int stride,
                               int format) {
        int fd = open(filePath, O_WRONLY | O_CREAT | O_TRUNC, 0664);
        if (fd < 0) {
            ALOGE("xm-gfx: DumpAnywhere failed to open file %s\n", filePath);
            return -1;
        }

        android_dataspace d = HAL_DATASPACE_UNKNOWN;
        const SkImageInfo info = SkImageInfo::Make(width, height,
                                                   flinger2skia(format),
                                                   kPremul_SkAlphaType,
                                                   dataSpaceToColorSpace(d));
        SkPixmap pixmap(info, base, stride * bytesPerPixel(format));

        struct FDWStream final : public SkWStream {
            size_t fBytesWritten = 0;
            int fFd;

            FDWStream(int f) : fFd(f) {}

            size_t bytesWritten() const override { return fBytesWritten; }

            bool write(const void *buffer, size_t size) override {
                fBytesWritten += size;
                return size == 0 || ::write(fFd, buffer, size) > 0;
            }
        } fdStream(fd);

        (void) SkEncodeImage(&fdStream, pixmap, SkEncodedImageFormat::kPNG, 100);
        close(fd);

        return 0;
    }

    int DumpAnywhere::writeRawFile(const char *filePath, void *base, int size) {
        ALOGE("xm-gfx: DumpAnywhere dumpLayertoRAW %s \n", filePath);

        int results = -1;
        int fd = open(filePath, O_WRONLY | O_CREAT | O_TRUNC, 0664);
        if (fd) {
            results = write(fd, base, size);
            close(fd);
        }
        ALOGE("DumpAnywhere dumpLayertoRAW %s : %s", filePath, results > 0 ? "Success" : "Fail");

        return results;
    }
}