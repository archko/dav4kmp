/*
 *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package io.github.triangleofice.dav4kmp.property

import io.github.triangleofice.dav4kmp.Dav4jvm
import io.github.triangleofice.dav4kmp.Property
import io.github.triangleofice.dav4kmp.PropertyFactory
import io.github.triangleofice.dav4kmp.XmlUtils
import io.ktor.http.ContentType
import nl.adaptivity.xmlutil.QName
import nl.adaptivity.xmlutil.XmlException
import nl.adaptivity.xmlutil.XmlReader
import kotlin.jvm.JvmField

public class SupportedAddressData : Property {

    public companion object {

        @JvmField
        public val NAME: QName = QName(XmlUtils.NS_CARDDAV, "supported-address-data")

        public val ADDRESS_DATA_TYPE: QName = QName(XmlUtils.NS_CARDDAV, "address-data-type")
        public const val CONTENT_TYPE: String = "content-type"
        public const val VERSION: String = "version"

        public val jCardContentType: ContentType = ContentType("application", "vcard+json")
    }

    public val types: MutableSet<ContentType> = mutableSetOf<ContentType>()

    public fun hasVCard4(): Boolean = types.any { ContentType.Text.VCard.withParameter("version", "4.0") == it }
    public fun hasJCard(): Boolean = types.any { jCardContentType == it }

    override fun toString(): String = "[${types.joinToString(", ")}]"

    public object Factory : PropertyFactory {

        override fun getName(): QName = NAME

        override fun create(parser: XmlReader): SupportedAddressData? {
            val supported = SupportedAddressData()

            try {
                XmlUtils.processTag(parser, ADDRESS_DATA_TYPE) {
                    parser.getAttributeValue(null, CONTENT_TYPE)?.let { contentType ->
                        var type = contentType.run(ContentType::parse)
                        type = parser.getAttributeValue(null, VERSION)
                            ?.let { version -> type.withParameter("version", version) } ?: type
                        supported.types += type
                    }
                }
            } catch (e: XmlException) {
                Dav4jvm.log.warn("Couldn't parse <resourcetype>", e)
                return null
            }

            return supported
        }
    }
}
