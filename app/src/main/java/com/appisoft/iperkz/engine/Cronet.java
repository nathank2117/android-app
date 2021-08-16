package com.appisoft.iperkz.engine;

import android.content.Context;

import org.chromium.net.CronetEngine;

public class Cronet {

    private static CronetEngine cronetEngine;

    public static synchronized CronetEngine getCronetEngine(Context context) {
        // Lazily create the Cronet engine.
        if (cronetEngine == null) {
            CronetEngine.Builder myBuilder = new CronetEngine.Builder(context);
            // Enable caching of HTTP data and
            // other information like QUIC server information, HTTP/2 protocol and QUIC protocol.
            cronetEngine = myBuilder
                    .enableHttpCache(CronetEngine.Builder.HTTP_CACHE_IN_MEMORY, 100 * 1024)
                    .enableHttp2(true)
                    .enableQuic(true)
                    .build();
        }
        return cronetEngine;
    }
}
