package au.com.nicta.util

import org.joda.time.format.ISODateTimeFormat
import argonaut.{DecodeJson, EncodeJson}
import com.github.nscala_time.time.Imports._
import argonaut.Argonaut._

object DateTimeISO8601CodecJsons {
  lazy val FULL_ISO8601_FORMAT = ISODateTimeFormat.dateTime

  implicit def DateTimeAsISO8601EncodeJson: EncodeJson[DateTime] =
    EncodeJson(s => jString(s.toString(FULL_ISO8601_FORMAT)))

  implicit def DateTimeAsISO8601DecodeJson: DecodeJson[DateTime] =
    implicitly[DecodeJson[String]].map(FULL_ISO8601_FORMAT.parseDateTime) setName "org.joda.time.DateTime"
}