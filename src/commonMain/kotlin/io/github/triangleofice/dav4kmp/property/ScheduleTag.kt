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
import io.github.triangleofice.dav4kmp.QuotedStringUtils
import io.github.triangleofice.dav4kmp.XmlUtils
import io.ktor.client.statement.HttpResponse
import nl.adaptivity.xmlutil.QName
import nl.adaptivity.xmlutil.XmlReader
import kotlin.jvm.JvmField

public class ScheduleTag(
    rawScheduleTag: String?,
) : Property {

    public companion object {
        public val NAME: QName = QName(XmlUtils.NS_CALDAV, "schedule-tag")

        public fun fromResponse(response: HttpResponse): ScheduleTag? =
            response.headers["Schedule-Tag"]?.let { ScheduleTag(it) }
    }

    /* Value:  opaque-tag
       opaque-tag = quoted-string
    */
    public val scheduleTag: String? = rawScheduleTag?.let { QuotedStringUtils.decodeQuotedString(it) }

    override fun toString(): String = scheduleTag ?: "(null)"

    public object Factory : PropertyFactory {

        override fun getName(): QName = NAME

        override fun create(parser: XmlReader): ScheduleTag =
            ScheduleTag(XmlUtils.readText(parser))
    }
}
