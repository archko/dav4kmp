/*
 *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package io.github.triangleofice.dav4kmp.property

import io.github.triangleofice.dav4kmp.XmlUtils
import nl.adaptivity.xmlutil.QName
import nl.adaptivity.xmlutil.XmlReader
import kotlin.jvm.JvmField

public class GroupMembership : HrefListProperty() {

    public companion object {
        public val NAME: QName = QName(XmlUtils.NS_WEBDAV, "group-membership")
    }

    public object Factory : HrefListProperty.Factory() {

        override fun getName(): QName = NAME

        override fun create(parser: XmlReader): HrefListProperty =
            create(parser, GroupMembership())
    }
}
