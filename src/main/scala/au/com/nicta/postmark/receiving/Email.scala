package au.com.nicta
package postmark
package receiving

import argonaut._, Argonaut._
import com.github.nscala_time.time.Imports._
import scalaz.Equal
import au.com.nicta.postmark.common.Header

case class Email(from: String, fromFull: FullEmailAddress, to: List[String], toFull: List[FullEmailAddress],
                 cc: List[String], ccFull: List[FullEmailAddress], replyTo: String, subject: String, messageID: String,
                 date: DateTime, mailboxHash: Option[String], text: Option[String], html: Option[String],
                 tag: Option[String], headers: List[Header], attachments: List[Attachment])

object Email {
  import util.DateTimeISO8601CodecJsons._
  import util.EmailUtil._

  private def applySplitEmailsByCommas(from: String, fromFull: FullEmailAddress, to: String, toFull: List[FullEmailAddress],
                                       cc: String, ccFull: List[FullEmailAddress], replyTo: String, subject: String, messageID: String,
                                       date: DateTime, mailboxHash: Option[String], text: Option[String], html: Option[String],
                                       tag: Option[String], headers: List[Header], attachments: List[Attachment]) =
    Email(from, fromFull, splitEmailsByCommas(to), toFull, splitEmailsByCommas(cc), ccFull, replyTo, subject, messageID, date,
      mailboxHash, text, html, tag, headers, attachments)

  private def unapplySplitEmailsByCommas(e: Email) =
    Some(e.from, e.fromFull, joinEmailsByCommas(e.to), e.toFull, joinEmailsByCommas(e.cc), e.ccFull, e.replyTo, e.subject,
      e.messageID, e.date, e.mailboxHash, e.text, e.html, e.tag, e.headers, e.attachments)

  implicit def EmailCodecJson: CodecJson[Email] =
    casecodec16(applySplitEmailsByCommas, unapplySplitEmailsByCommas)("From", "FromFull", "To", "ToFull", "Cc", "CcFull", "ReplyTo",
      "Subject", "MessageID", "Date", "MailboxHash", "TextBody", "HtmlBody", "Tag", "Headers", "Attachments")

  implicit def EmailEqual: Equal[Email] = Equal.equalA[Email]
}

case class FullEmailAddress(email: String, name: String)

object FullEmailAddress {
  implicit def FullEmailAddressCodecJson: CodecJson[FullEmailAddress] =
    casecodec2(FullEmailAddress.apply, FullEmailAddress.unapply)("Email", "Name")

  implicit def FullEmailAddressEqual: Equal[FullEmailAddress] = Equal.equalA[FullEmailAddress]

}

case class Attachment(name: String, content: String, contentType: String, length: Long, contentID: String)

object Attachment {
  implicit def AttachmentCodecJson: CodecJson[Attachment] =
    casecodec5(Attachment.apply, Attachment.unapply)("Name", "Content", "ContentType", "ContentLength", "ContentID")

  implicit def AttachmentEqual: Equal[Attachment] = Equal.equalA[Attachment]
}