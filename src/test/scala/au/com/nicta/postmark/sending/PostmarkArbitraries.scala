package au.com.nicta
package postmark
package sending

import org.scalacheck.{Choose, Gen, Arbitrary}, Arbitrary.arbitrary, Gen._
import scalaz._, std.option._, syntax.show._, syntax.apply._, scalacheck.ScalaCheckBinding._
import org.joda.time.DateTime
import au.com.nicta.test.BasicArbitraries
import au.com.nicta.postmark.common.Header
import org.apache.commons.codec.binary.Base64

trait PostmarkArbitraries extends BasicArbitraries {
  implicit def AttachmentArbitrary: Arbitrary[Attachment] =
    Arbitrary(for {
      name <- arbitrary[NonEmptyString]
      content <- arbitrary[NonEmptyString]
      contentType <- arbitrary[ContentTypeString]
    } yield Attachment(name.value + ".txt", Base64.encodeBase64String(content.value.toCharArray.map(_.toByte)), contentType.value))

  implicit def SentEmailArbitrary: Arbitrary[SentEmail] =
    Arbitrary(for {
      id <- arbitrary[String]
      submittedAt <- arbitrary[DateTime]
      to <- genLimitedList[EmailString](0, 20)
    } yield SentEmail(id, submittedAt, to.map(_.value)))

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
    backupText <- arbitrary[NonEmptyString]
  } yield {
    val (textToUse, htmlToUse) = (text, html) match {
      case (None, None) => (Some(backupText.value), Some(backupText.value))
      case (a, b) => (a, b)
    }
    Email(
      from.value,
      to.map(_.value), cc.map(_.value), bcc.map(_.value),
      subject, tag, htmlToUse, textToUse, replyTo.value,
      headers,
      attachments)
  })

  case class ContentTypeString(value: String)

  implicit def ContentTypeStringArbitrary: Arbitrary[ContentTypeString] =
    Arbitrary(Gen.oneOf(List("text/plain")) map ContentTypeString)
}
