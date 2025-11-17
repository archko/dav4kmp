/*
 *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package io.github.triangleofice.dav4kmp.property

import io.github.triangleofice.dav4kmp.DavResource
import io.github.triangleofice.dav4kmp.Property
import io.github.triangleofice.dav4kmp.PropertyFactory
import io.github.triangleofice.dav4kmp.XmlUtils
import nl.adaptivity.xmlutil.QName
import nl.adaptivity.xmlutil.XmlReader

/**
 * Defined in RFC 5995 3.2.1 DAV:add-member Property (Protected).
 */
public data class AddMember(
    val href: String?,
) : Property {
    public companion object {
        public val NAME: QName = QName(XmlUtils.NS_WEBDAV, "add-member")
    }

    public object Factory : PropertyFactory {
        override fun getName(): QName = NAME

        override fun create(parser: XmlReader): AddMember =
            AddMember(XmlUtils.readTextProperty(parser, DavResource.HREF))
    }
}
