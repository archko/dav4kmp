/*
 *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package io.github.triangleofice.dav4kmp.property

import io.ktor.client.plugins.auth.AuthConfig
import io.ktor.client.plugins.auth.AuthProvider
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.auth.HttpAuthHeader

public fun AuthConfig.forDomain(pattern: String, block: AuthConfig.() -> Unit): Unit = forDomain(pattern.toRegex(), block)

public fun AuthConfig.forDomain(pattern: Regex, block: AuthConfig.() -> Unit) {
    val old = this.providers.toSet()
    block()
    val newProviders = providers - old
    providers.removeAll(newProviders)
    newProviders.forEach { providers += AuthDomainLimiter(pattern, it) }
}

public class AuthDomainLimiter(private val domain: Regex, private val downstreamProvider: AuthProvider) : AuthProvider {
    @Deprecated("Please use sendWithoutRequest function instead")
    override val sendWithoutRequest: Boolean
        get() = error("Deprecated")

    override fun sendWithoutRequest(request: HttpRequestBuilder): Boolean =
        domain.matches(request.url.buildString()) && downstreamProvider.sendWithoutRequest(request)

    override suspend fun addRequestHeaders(request: HttpRequestBuilder, authHeader: HttpAuthHeader?) {
        if (domain.matches(request.url.buildString())) downstreamProvider.addRequestHeaders(request, authHeader)
    }

    override fun isApplicable(auth: HttpAuthHeader): Boolean = downstreamProvider.isApplicable(auth)
}
