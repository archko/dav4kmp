/*
 *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package io.github.triangleofice.dav4kmp

import io.ktor.client.statement.HttpResponse

/**
 * Callback for the OPTIONS request.
 */
public fun interface CapabilitiesCallback {
    public fun onCapabilities(davCapabilities: Set<String>, response: HttpResponse)
}

/**
 * Callback for 207 Multi-Status responses.
 */
public fun interface MultiResponseCallback {
    /**
     * Called for every `<response>` element in the `<multistatus>` body. For instance,
     * in response to a `PROPFIND` request, this callback will be called once for every found
     * member resource.
     *
     * @param response   the parsed response (including URL)
     * @param relation   relation of the response to the called resource
     */
    public fun onResponse(response: Response, relation: Response.HrefRelation)
}

/**
 * Callback for HTTP responses.
 */
public fun interface ResponseCallback {
    /**
     * Called for a HTTP response. Typically this is only called for successful/redirect
     * responses because HTTP errors throw an exception before this callback is called.
     */
    public suspend fun onResponse(response: HttpResponse)
}
