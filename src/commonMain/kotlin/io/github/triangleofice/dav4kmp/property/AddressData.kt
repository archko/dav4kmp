/*
 *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package io.github.triangleofice.dav4kmp.property

import io.github.triangleofice.dav4kmp.Property
import io.github.triangleofice.dav4kmp.PropertyFactory
import io.github.triangleofice.dav4kmp.XmlUtils
import nl.adaptivity.xmlutil.QName
import nl.adaptivity.xmlutil.XmlReader
import kotlin.jvm.JvmField

public data class AddressData(
    val card: String?,
) : Property {

    public companion object {
        public val NAME: QName = QName(XmlUtils.NS_CARDDAV, "address-data")

        // attributes
        public const val CONTENT_TYPE: String = "content-type"
        public const val VERSION: String = "version"
    }

    public object Factory : PropertyFactory {

        override fun getName(): QName = NAME

        override fun create(parser: XmlReader): AddressData =
            // <!ELEMENT address-data (#PCDATA)>
            AddressData(XmlUtils.readText(parser))
    }
}
