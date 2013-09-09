package au.com.nicta.postmark
package sending

import argonaut._

case class PostmarkRequest[A: EncodeJson, B: DecodeJson](path: String, content: A)