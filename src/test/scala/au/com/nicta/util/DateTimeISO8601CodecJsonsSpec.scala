package au.com.nicta
package util

import org.joda.time.DateTime

class DateTimeISO8601CodecJsonsSpec extends test.Spec {
  import DateTimeISO8601CodecJsons._
  "DateTimeISO8601 codec" should {
    "satisfy codec laws" ! codecprop[DateTime]
  }
}
