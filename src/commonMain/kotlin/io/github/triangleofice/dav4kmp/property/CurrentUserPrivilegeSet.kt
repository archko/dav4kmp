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
import nl.adaptivity.xmlutil.EventType
import nl.adaptivity.xmlutil.QName
import nl.adaptivity.xmlutil.XmlReader
import kotlin.jvm.JvmField

public data class CurrentUserPrivilegeSet(
    // not all privileges from RFC 3744 are implemented by now
    // feel free to add more if you need them for your project
    var mayRead: Boolean = false,
    var mayWriteProperties: Boolean = false,
    var mayWriteContent: Boolean = false,
    var mayBind: Boolean = false,
    var mayUnbind: Boolean = false,
) : Property {

    public companion object {

        @JvmField
        public val NAME: QName = QName(XmlUtils.NS_WEBDAV, "current-user-privilege-set")

        public val PRIVILEGE: QName = QName(XmlUtils.NS_WEBDAV, "privilege")
        public val READ: QName = QName(XmlUtils.NS_WEBDAV, "read")
        public val WRITE: QName = QName(XmlUtils.NS_WEBDAV, "write")
        public val WRITE_PROPERTIES: QName = QName(XmlUtils.NS_WEBDAV, "write-properties")
        public val WRITE_CONTENT: QName = QName(XmlUtils.NS_WEBDAV, "write-content")
        public val BIND: QName = QName(XmlUtils.NS_WEBDAV, "bind")
        public val UNBIND: QName = QName(XmlUtils.NS_WEBDAV, "unbind")
        public val ALL: QName = QName(XmlUtils.NS_WEBDAV, "all")
    }

    public object Factory : PropertyFactory {

        override fun getName(): QName = NAME

        override fun create(parser: XmlReader): CurrentUserPrivilegeSet {
            // <!ELEMENT current-user-privilege-set (privilege*)>
            // <!ELEMENT privilege ANY>
            val privs = CurrentUserPrivilegeSet()

            XmlUtils.processTag(parser, PRIVILEGE) {
                val depth = parser.depth
                var eventType = parser.eventType
                while (!(eventType == EventType.END_ELEMENT && parser.depth == depth)) {
                    if (eventType == EventType.START_ELEMENT && parser.depth == depth + 1) {
                        when (parser.name) {
                            READ ->
                                privs.mayRead = true

                            WRITE -> {
                                privs.mayBind = true
                                privs.mayUnbind = true
                                privs.mayWriteProperties = true
                                privs.mayWriteContent = true
                            }

                            WRITE_PROPERTIES ->
                                privs.mayWriteProperties = true

                            WRITE_CONTENT ->
                                privs.mayWriteContent = true

                            BIND ->
                                privs.mayBind = true

                            UNBIND ->
                                privs.mayUnbind = true

                            ALL -> {
                                privs.mayRead = true
                                privs.mayBind = true
                                privs.mayUnbind = true
                                privs.mayWriteProperties = true
                                privs.mayWriteContent = true
                            }
                        }
                    }
                    eventType = parser.next()
                }
            }

            return privs
        }
    }
}
