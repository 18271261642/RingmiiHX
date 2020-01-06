package com.guider.health.apilib;

import com.orhanobut.logger.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;

public class MyLogInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");


    private volatile HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;

    @Override public Response intercept(Chain chain) throws IOException {
        HttpLoggingInterceptor.Level level = this.level;

        Request request = chain.request();
        if (level == HttpLoggingInterceptor.Level.NONE) {
            return chain.proceed(request);
        }

        boolean logBody = level == HttpLoggingInterceptor.Level.BODY;
        boolean logHeaders = logBody || level == HttpLoggingInterceptor.Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        Logger.i(requestStartMessage);

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    Logger.i("Content-Type: " + requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    Logger.i("Content-Length: " + requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    Logger.i(name + ": " + headers.value(i));
                }
            }

            if (!logBody || !hasRequestBody) {
//                Logger.i("--> END " + request.method());
            } else if (bodyEncoded(request.headers())) {
                Logger.i("--> END " + request.method() + " (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                Logger.i("");
                if (isPlaintext(buffer)) {
                    Logger.i(buffer.readString(charset));
                    Logger.i("--> END " + request.method()
                            + " (" + requestBody.contentLength() + "-byte body)");
                } else {
                    Logger.i("--> END " + request.method() + " (binary "
                            + requestBody.contentLength() + "-byte body omitted)");
                }
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            Logger.i("<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        Logger.i("<-- " + response.code() + ' ' + response.message() + ' '
                + response.request().url() + " (" + tookMs + "ms" + (!logHeaders ? ", "
                + bodySize + " body" : "") + ')');

        if (logHeaders) {
            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                Logger.i(headers.name(i) + ": " + headers.value(i));
            }

            if (!logBody) {
                Logger.i("<-- END HTTP");
            } else if (bodyEncoded(response.headers())) {
                Logger.i("<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        Logger.i("");
                        Logger.i("Couldn't decode the response body; charset is likely malformed.");
                        Logger.i("<-- END HTTP");

                        return response;
                    }
                }

                if (!isPlaintext(buffer)) {
                    Logger.i("");
                    Logger.i("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                    return response;
                }

                if (contentLength != 0) {
                    Logger.i("");
                    Logger.i(buffer.clone().readString(charset));
                    StringBuilder sb = new StringBuilder();
                    sb.append("method:")
                            .append(request.method())
                            .append(";")
                            .append("url:")
                            .append(request.url())
                            .append(";");

                    if (!(!logBody || !hasRequestBody||bodyEncoded(request.headers()))){
                        Buffer buffer1 = new Buffer();
                        requestBody.writeTo(buffer1);

                        if (isPlaintext(buffer1)) {
                            sb.append("body:")
                                    .append(buffer1.readString(charset))
                                    .append(";");
                        }
                    }


                    sb.append("response:")
                            .append(buffer.clone().readString(charset))
                            .append(";");
//                    L.sendLogToServer("httpDetail",sb.toString());
                }else{
                    StringBuilder sb = new StringBuilder();
                    sb.append("method:")
                            .append(request.method())
                            .append(";")
                            .append("\n")
                            .append("url:")
                            .append(request.url())
                            .append(";")
                            .append("\n");

                    if (!(!logBody || !hasRequestBody||bodyEncoded(request.headers()))){
                        Buffer buffer1 = new Buffer();
                        requestBody.writeTo(buffer1);

                        MediaType contentType1 = requestBody.contentType();
                        if (contentType != null) {
                            charset = contentType1.charset(UTF8);
                        }
                        if (isPlaintext(buffer1)) {
                            sb.append("body:")
                                    .append(buffer.readString(charset))
                                    .append(";")
                                    .append("\n");
                        }
                    }
                    sb.append("response:")
                            .append(response.code())
                            .append(";")
                            .append("\n");
//                    L.sendLogToServer("httpDetail",sb.toString());
                }

                Logger.i("<-- END HTTP (" + buffer.size() + "-byte body)");
            }
        }

        return response;
    }



    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }
}

