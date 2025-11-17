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

public data class SupportedCalendarComponentSet(
    var supportsEvents: Boolean,
    var supportsTasks: Boolean,
    var supportsJournal: Boolean,
) : Property {

    public companion object {

        public val NAME: QName = QName(XmlUtils.NS_CALDAV, "supported-calendar-component-set")

        public val ALLCOMP: QName = QName(XmlUtils.NS_CALDAV, "allcomp")
        public val COMP: QName = QName(XmlUtils.NS_CALDAV, "comp")
    }

    public object Factory : PropertyFactory {

        override fun getName(): QName = NAME

        override fun create(parser: XmlReader): SupportedCalendarComponentSet {
            /* <!ELEMENT supported-calendar-component-set (comp+)>
               <!ELEMENT comp ((allprop | prop*), (allcomp | comp*))>
               <!ATTLIST comp name CDATA #REQUIRED>
            */
            val components = SupportedCalendarComponentSet(false, false, false)

            val depth = parser.depth
            var eventType = parser.eventType
            while (!(eventType == EventType.END_ELEMENT && parser.depth == depth)) {
                if (eventType == EventType.START_ELEMENT && parser.depth == depth + 1) {
                    when (parser.name) {
                        ALLCOMP -> {
                            components.supportsEvents = true
                            components.supportsTasks = true
                            components.supportsJournal = true
                        }

                        COMP ->
                            when (parser.getAttributeValue(null, "name")?.uppercase()) {
                                "VEVENT" -> components.supportsEvents = true
                                "VTODO" -> components.supportsTasks = true
                                "VJOURNAL" -> components.supportsJournal = true
                            }
                    }
                }
                eventType = parser.next()
            }

            return components
        }
    }
}
