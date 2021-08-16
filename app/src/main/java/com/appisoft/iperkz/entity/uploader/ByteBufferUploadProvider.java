package com.appisoft.iperkz.entity.uploader;

import org.chromium.net.UploadDataProvider;
import org.chromium.net.UploadDataSink;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferUploadProvider extends UploadDataProvider {
    private final ByteBuffer mUploadBuffer;

    public static UploadDataProvider create(byte[] data) {
        return create(data, 0, data.length);
    }

    public static UploadDataProvider create(byte[] data, int offset, int length) {
        return new ByteBufferUploadProvider(ByteBuffer.wrap(data, offset, length).slice());
    }

    private ByteBufferUploadProvider(ByteBuffer uploadBuffer) {
        this.mUploadBuffer = uploadBuffer;

    }

    @Override
    public long getLength() {
        return mUploadBuffer.limit();
    }

    @Override
    public void read(UploadDataSink uploadDataSink, ByteBuffer byteBuffer) {
        if (!byteBuffer.hasRemaining()) {
            throw new IllegalStateException("Cronet passed a buffer with no bytes remaining");
        }
        if (byteBuffer.remaining() >= mUploadBuffer.remaining()) {
            byteBuffer.put(mUploadBuffer);
        } else {
            int oldLimit = mUploadBuffer.limit();
            mUploadBuffer.limit(mUploadBuffer.position() + byteBuffer.remaining());
            byteBuffer.put(mUploadBuffer);
            mUploadBuffer.limit(oldLimit);
        }
        uploadDataSink.onReadSucceeded(false);
    }

    @Override
    public void rewind(UploadDataSink uploadDataSink) {
        mUploadBuffer.position(0);
        uploadDataSink.onRewindSucceeded();
    }
}


