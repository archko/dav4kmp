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

public class SupportedCalendarData : Property {

    public companion object {

        public val NAME: QName = QName(XmlUtils.NS_CALDAV, "supported-calendar-data")

        public val CALENDAR_DATA_TYPE: QName = QName(XmlUtils.NS_CALDAV, "calendar-data")
        public const val CONTENT_TYPE: String = "content-type"
        public const val VERSION: String = "version"

        public val jCalType: ContentType = ContentType("application", "calendar+json")
    }

    public val types: MutableSet<ContentType> = mutableSetOf<ContentType>()

    public fun hasJCal(): Boolean = types.any { jCalType == it }

    override fun toString(): String = "[${types.joinToString(", ")}]"

    public object Factory : PropertyFactory {

        override fun getName(): QName = NAME

        override fun create(parser: XmlReader): SupportedCalendarData? {
            val supported = SupportedCalendarData()

            try {
                XmlUtils.processTag(parser, CALENDAR_DATA_TYPE) {
                    parser.getAttributeValue(null, CONTENT_TYPE)?.let { contentType ->
                        var type = contentType.run(ContentType::parse)
                        type = parser.getAttributeValue(null, SupportedAddressData.VERSION)
                            ?.let { version -> type.withParameter("version", version) } ?: type
                        supported.types += type
                    }
                }
            } catch (e: XmlException) {
                Dav4jvm.log.error("Couldn't parse <resourcetype>", e)
                return null
            }

            return supported
        }
    }
}
