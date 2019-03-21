package com.willowtreeapps.willowbrew.api.utils

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

/**
 * A logging interceptor with a max body size limit to avoid OOM when transferring large files.
 */
class SafeLoggingInterceptor(private var logger: HttpLoggingInterceptor): Interceptor by logger {

    companion object {
        private const val MAX_BODY_SIZE = 50000
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        // If not logging body, then proceed as normal.
        if (logger.level != HttpLoggingInterceptor.Level.BODY) {
            return logger.intercept(chain)
        }

        // If request size is greater than max...
        val requestSize = chain.request()?.body()?.contentLength() ?: 0
        if (requestSize > MAX_BODY_SIZE) {

            // Reset log level to headers only and make the intercept.
            logger.level = HttpLoggingInterceptor.Level.HEADERS
            val result = logger.intercept(chain)

            // Reset level and return the result.
            logger.level = HttpLoggingInterceptor.Level.BODY
            return result
        }

        // Otherwise, proceed as normal.
        return logger.intercept(chain)
    }
}
