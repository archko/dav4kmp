/*
 *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package io.github.triangleofice.dav4kmp.exception

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

/**
 * Signals that a HTTP error was sent by the server.
 */
public open class HttpException internal constructor(statusCode: HttpStatusCode, exceptionData: ExceptionData) : DavException(
    "HTTP $statusCode",
    exceptionData = exceptionData,
) {

    public companion object {
        public suspend operator fun invoke(response: HttpResponse): HttpException =
            HttpException(response.status, createExceptionData(response))
    }

    public var code: Int

    init {
        code = statusCode.value
    }
}
