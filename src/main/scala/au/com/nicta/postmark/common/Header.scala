package au.com.nicta
package postmark
package common

import argonaut._, Argonaut._

/**
 * Wraps custom headers in emails. When sending emails, the header name cannot be blank.
 * @param name Header name
 * @param value Header value.
 */
case class Header(name: String, value: String)

object Header {
  implicit def HeaderCodecJson: CodecJson[Header] = casecodec2(Header.apply, Header.unapply)("Name", "Value")
}