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

public class NotFoundException internal constructor(statusCode: HttpStatusCode, exceptionData: ExceptionData) :
    HttpException(statusCode, exceptionData) {

    public companion object {
        public suspend operator fun invoke(httpResponse: HttpResponse): NotFoundException =
            NotFoundException(httpResponse.status, createExceptionData(httpResponse))
    }
}
