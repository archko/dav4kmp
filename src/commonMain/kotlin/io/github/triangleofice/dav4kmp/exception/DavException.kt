/*
 *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package io.github.triangleofice.dav4kmp.exception

import io.github.triangleofice.dav4kmp.Dav4jvm
import io.github.triangleofice.dav4kmp.Error
import io.github.triangleofice.dav4kmp.XmlUtils
import io.github.triangleofice.dav4kmp.isEmpty
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentType
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.errors.IOException
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.readText
import nl.adaptivity.xmlutil.XmlException
import kotlin.math.min

/**
 * Signals that an error occurred during a WebDAV-related operation.
 *
 * This could be a logical error like when a required ETag has not been
 * received, but also an explicit HTTP error.
 */
public open class DavException internal constructor(
    message: String,
    ex: Throwable? = null,
    exceptionData: ExceptionData,
) : Exception(message, ex) {

    public data class ExceptionData(
        val request: String? = null,
        val response: String? = null,
        val requestBody: String? = null,
        val responseBody: String? = null,
        val errors: List<Error> = emptyList(),
    )

    public companion object {

        public const val MAX_EXCERPT_SIZE: Int = 10 * 1024 // don't dump more than 20 kB

        public fun isPlainText(type: ContentType): Boolean =
            type.match(ContentType.Text.Any) ||
                (type.contentType == "application" && type.contentSubtype in arrayOf("html", "xml"))

        public suspend operator fun invoke(
            message: String,
            ex: Throwable? = null,
            httpResponse: HttpResponse? = null,
        ): DavException = DavException(message, ex, createExceptionData(httpResponse))

        @OptIn(InternalAPI::class)
        internal suspend fun createExceptionData(
            httpResponse: HttpResponse? = null,
        ): ExceptionData {
            var response: String? = null
            var request: String? = null
            var requestBody: String? = null
            var responseBody: String? = null
            var errors = emptyList<Error>()

            if (httpResponse != null) {
                response = httpResponse.toString()
                Dav4jvm.log.trace("Reading request")
                try {
                    request = httpResponse.request.toString()

                    httpResponse.request.content.let { body ->
                        httpResponse.request.contentType()?.let { type ->
                            if (isPlainText(type)) {
                                requestBody = when (body) {
                                    is OutgoingContent.ByteArrayContent -> {
                                        val bytes = body.bytes()
                                        bytes.copyOfRange(0, min(MAX_EXCERPT_SIZE, bytes.size)).decodeToString()
                                    }

                                    is OutgoingContent.ReadChannelContent -> body.readFrom()
                                        .readRemaining(MAX_EXCERPT_SIZE.toLong()).readText()

                                    else -> "Unknown outgoing content ${body::class.simpleName}"
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Dav4jvm.log.warn("Couldn't read HTTP request", e)
                    requestBody = "Couldn't read HTTP request: ${e.message}"
                }
                Dav4jvm.log.trace("Reading response $response")
                try {
                    // save response body excerpt
                    val bodyChannel = httpResponse.rawContent
                    val contentType = httpResponse.contentType()
                    if (!bodyChannel.isEmpty() && contentType != null && isPlainText(contentType)) {
                        // Read a length limited version of the body
                        val read = bodyChannel.readRemaining(MAX_EXCERPT_SIZE.toLong())
                        responseBody = read.readBytes().decodeToString()
                        if (contentType.match(ContentType.Application.Xml) || contentType.match(ContentType.Text.Xml)) {
                            val xmlBody = responseBody + bodyChannel.readRemaining().readBytes().decodeToString()
                            try {
                                val parser = XmlUtils.createReader(xmlBody)
                                XmlUtils.processTag(parser, name = Error.NAME) {
                                    errors = Error.parseError(parser)
                                }
                            } catch (e: XmlException) {
                                Dav4jvm.log.warn("Couldn't parse XML response", e)
                            }
                        }
                    }
                } catch (e: IOException) {
                    Dav4jvm.log.warn("Couldn't read HTTP response", e)
                    responseBody = "Couldn't read HTTP response: ${e.message}"
                }
            } else {
                response = null
            }
            return ExceptionData(request, response, requestBody, responseBody, errors)
        }
    }

    public val request: String? = exceptionData.request
    public val response: String? = exceptionData.response

    /**
     * Body excerpt of [request] (up to [MAX_EXCERPT_SIZE] characters). Only available
     * if the HTTP request body was textual content and could be read again.
     */
    public val requestBody: String? = exceptionData.requestBody

    /**
     * Body excerpt of [response] (up to [MAX_EXCERPT_SIZE] characters). Only available
     * if the HTTP response body was textual content.
     */
    public val responseBody: String? = exceptionData.responseBody

    /**
     * Precondition/postcondition XML elements which have been found in the XML response.
     */
    public val errors: List<Error> = exceptionData.errors
}
