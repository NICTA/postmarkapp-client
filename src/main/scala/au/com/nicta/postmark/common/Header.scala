package au.com.nicta
package postmark
package common

import argonaut._, Argonaut._

case class Header(name: String, value: String)

object Header {
  implicit def HeaderCodecJson: CodecJson[Header] = casecodec2(Header.apply, Header.unapply)("Name", "Value")
}