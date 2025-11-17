/*
 * Copyright © Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package io.github.triangleofice.dav4kmp

import nl.adaptivity.xmlutil.QName
import nl.adaptivity.xmlutil.XmlReader

/**
 * Represents an XML precondition/postcondition error. Every error has a name, which is the XML element
 * name. Subclassed errors may have more specific information available.
 *
 * At the moment, there is no logic for subclassing errors.
 */
public class Error(
    public val name: QName,
) {

    public companion object {

        public val NAME: QName = QName(XmlUtils.NS_WEBDAV, "error")

        public fun parseError(parser: XmlReader): List<Error> {
            val names = mutableSetOf<QName>()

            XmlUtils.processTag(parser) { names += parser.name }

            return names.map { Error(it) }
        }

        // some pre-defined errors

        public val NEED_PRIVILEGES: Error = Error(QName(XmlUtils.NS_WEBDAV, "need-privileges"))
        public val VALID_SYNC_TOKEN: Error = Error(QName(XmlUtils.NS_WEBDAV, "valid-sync-token"))
    }

    override fun equals(other: Any?): Boolean =
        (other is Error) && other.name == name

    override fun hashCode(): Int = name.hashCode()
}
