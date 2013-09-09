package au.com.nicta
package postmark
package sending

import org.scalacheck.{Choose, Gen, Arbitrary}, Arbitrary.arbitrary, Gen._
import scalaz._, std.option._, syntax.show._, syntax.apply._, scalacheck.ScalaCheckBinding._
import org.joda.time.DateTime
import au.com.nicta.test.BasicArbitraries
import au.com.nicta.postmark.common.Header

trait PostmarkArbitraries extends BasicArbitraries {
  implicit def AttachmentArbitrary: Arbitrary[Attachment] =
    Arbitrary((arbitrary[String] |@| arbitrary[String] |@| arbitrary[String])(Attachment.apply))

  implicit def SentEmailArbitrary: Arbitrary[SentEmail] =
    Arbitrary((arbitrary[String] |@| arbitrary[DateTime] |@| arbitrary[String])(SentEmail.apply))

  implicit def EmailArbitrary: Arbitrary[Email] = Arbitrary(for {
    from <- arbitrary[EmailString]
    to <- genLimitedList[EmailString](1, 20)
    cc <- genLimitedList[EmailString](0, 20)
    bcc <- genLimitedList[EmailString](0, 20)
    subject <- arbitrary[String]
    tag <- arbitrary[Option[String]]
    html <- arbitrary[Option[String]]
    text <- arbitrary[Option[String]]
    replyTo <- arbitrary[EmailString]
    headers <- genLimitedList[Header](0, 20)
    attachments <- genLimitedList[Attachment](0, 20)
  } yield
    Email(
      from.value,
      to.map(_.value), cc.map(_.value), bcc.map(_.value),
      subject, tag, html, text, replyTo.value,
      headers,
      attachments))
}
