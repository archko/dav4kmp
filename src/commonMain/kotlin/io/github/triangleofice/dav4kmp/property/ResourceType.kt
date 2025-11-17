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

public class ResourceType : Property {

    public companion object {
        public val NAME: QName = QName(XmlUtils.NS_WEBDAV, "resourcetype")

        public val COLLECTION: QName = QName(XmlUtils.NS_WEBDAV, "collection") // WebDAV
        public val PRINCIPAL: QName = QName(XmlUtils.NS_WEBDAV, "principal") // WebDAV ACL
        public val ADDRESSBOOK: QName = QName(XmlUtils.NS_CARDDAV, "addressbook") // CardDAV
        public val CALENDAR: QName = QName(XmlUtils.NS_CALDAV, "calendar") // CalDAV
        public val SUBSCRIBED: QName = QName(XmlUtils.NS_CALENDARSERVER, "subscribed")
    }

    public val types: MutableSet<QName> = mutableSetOf<QName>()

    override fun toString(): String = "[${types.joinToString(", ")}]"

    public object Factory : PropertyFactory {

        override fun getName(): QName = NAME

        override fun create(parser: XmlReader): ResourceType {
            val type = ResourceType()

            XmlUtils.processTag(parser) {
                // use static objects to allow types.contains()
                var typeName = parser.name
                when (typeName) {
                    COLLECTION -> typeName = COLLECTION
                    PRINCIPAL -> typeName = PRINCIPAL
                    ADDRESSBOOK -> typeName = ADDRESSBOOK
                    CALENDAR -> typeName = CALENDAR
                    SUBSCRIBED -> typeName = SUBSCRIBED
                }
                type.types.add(typeName)
            }

            return type
        }
    }
}
