package au.com.nicta
package postmark
package receiving

import org.scalacheck.{Gen, Arbitrary}, Arbitrary.arbitrary, Gen._
import scalaz._, syntax.apply._, scalacheck.ScalaCheckBinding._
import org.joda.time.DateTime
import au.com.nicta.test.BasicArbitraries
import au.com.nicta.postmark.common.Header

trait PostmarkArbitraries extends BasicArbitraries {
  implicit def AttachmentArbitrary: Arbitrary[Attachment] =
    Arbitrary(for {
      name <- arbitrary[String]
      content <- arbitrary[String]
      contentType <- oneOf(Seq("application/json", "text/plain"))
      contentID <- arbitrary[String]
    } yield Attachment(name, content, contentType, content.length, contentID))

  implicit def FullEmailAddressArbitrary: Arbitrary[FullEmailAddress] =
    Arbitrary(for {
      email <- arbitrary[EmailString]
      name <- arbitrary[String]
    } yield FullEmailAddress(email.value, name))

  implicit def EmailArbitrary: Arbitrary[Email] = Arbitrary(for {
    from <- arbitrary[EmailString]
    fromFull <- arbitrary[FullEmailAddress]
    numTos <- Gen.choose(1, 20)
    to <- listOfN(numTos, arbitrary[EmailString])
    toFull <- listOfN(numTos, arbitrary[FullEmailAddress])
    numCcs <- Gen.choose(0, 20)
    cc <- listOfN(numCcs, arbitrary[EmailString])
    ccFull <- listOfN(numCcs, arbitrary[FullEmailAddress])
    replyTo <- arbitrary[EmailString]
    subject <- arbitrary[String]
    messageID <- arbitrary[String]
    date <- arbitrary[DateTime]
    mailboxHash <- arbitrary[Option[String]]
    text <- arbitrary[Option[String]]
    html <- arbitrary[Option[String]]
    tag <- arbitrary[Option[String]]
    headers <- genLimitedList[Header](0, 20)
    attachments <- genLimitedList[Attachment](0, 20)
  } yield
    Email(
      from.value, fromFull,
      to.map(_.value), toFull, cc.map(_.value), ccFull, replyTo.value,
      subject, messageID, date, mailboxHash, text, html, tag,
      headers,
      attachments))
}
