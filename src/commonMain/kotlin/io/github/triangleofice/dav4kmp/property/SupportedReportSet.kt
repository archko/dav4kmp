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

public class SupportedReportSet : Property {

    public companion object {

        public val NAME: QName = QName(XmlUtils.NS_WEBDAV, "supported-report-set")

        public val SUPPORTED_REPORT: QName = QName(XmlUtils.NS_WEBDAV, "supported-report")
        public val REPORT: QName = QName(XmlUtils.NS_WEBDAV, "report")

        public const val SYNC_COLLECTION: String = "DAV:sync-collection" // collection synchronization (RFC 6578)
    }

    public val reports: MutableSet<String> = mutableSetOf<String>()

    override fun toString(): String = "[${reports.joinToString(", ")}]"

    public object Factory : PropertyFactory {

        override fun getName(): QName = NAME

        override fun create(parser: XmlReader): SupportedReportSet {
            /* <!ELEMENT supported-report-set (supported-report*)>
               <!ELEMENT supported-report report>
               <!ELEMENT report ANY>
            */

            val supported = SupportedReportSet()
            XmlUtils.processTag(parser, SUPPORTED_REPORT) {
                XmlUtils.processTag(parser, REPORT) {
                    parser.nextTag()
                    if (parser.eventType == EventType.TEXT) {
                        supported.reports += parser.text
                    } else if (parser.eventType == EventType.START_ELEMENT) {
                        supported.reports += "${parser.namespaceURI}${parser.localName}"
                    }
                }
            }
            return supported
        }
    }
}
