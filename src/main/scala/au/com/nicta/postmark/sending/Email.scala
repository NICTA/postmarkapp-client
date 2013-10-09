package au.com.nicta
package postmark
package sending

import argonaut._, Argonaut._
import com.github.nscala_time.time.Imports._
import scalaz._
import au.com.nicta.postmark.common.Header

/**
 * Email data type for sending emails. You can use the defaultEmail as a base to create new emails e.g.
 *   Email.defaultEmail.copy(from = "you@yourdomain.com", to = "person@example.com", subject = "This is an example")
 * @param from
 * @param to
 * @param cc
 * @param bcc
 * @param subject
 * @param tag
 * @param html
 * @param text
 * @param replyTo
 * @param headers
 * @param attachments
 */
case class Email(from: String = "", to: List[String] = Nil, cc: List[String] = Nil, bcc: List[String] = Nil,
                 subject: String = "", tag: Option[String] = None, html: Option[String] = None, text: Option[String] = None,
                 replyTo: String = "", headers: List[Header] = Nil, attachments: List[Attachment] = Nil)

object Email {
  import util.EmailUtil._

  private def applySplitEmailsByCommas(from: String, to: String, cc: String, bcc: String,
                                       subject: String, tag: Option[String], html: Option[String], text: Option[String],
                                       replyTo: String, headers: List[Header], attachments: List[Attachment]) =
    Email(from, splitEmailsByCommas(to), splitEmailsByCommas(cc), splitEmailsByCommas(bcc), subject, tag, html, text,
      replyTo, headers, attachments)

  private def unapplySplitEmailsByCommas(email: Email) =
    Some(email.from, joinEmailsByCommas(email.to), joinEmailsByCommas(email.cc), joinEmailsByCommas(email.bcc),
      email.subject, email.tag, email.html, email.text, email.replyTo, email.headers, email.attachments)

  implicit def EmailCodecJson: CodecJson[Email] =
    casecodec11(applySplitEmailsByCommas, unapplySplitEmailsByCommas)("From", "To", "Cc", "Bcc", "Subject", "Tag", "HtmlBody",
      "TextBody", "ReplyTo", "Headers", "Attachments")

  implicit def EmailEqual: Equal[Email] = Equal.equalA[Email]
}

case class Attachment(name: String, content: String, contentType: String)

object Attachment {
  implicit def AttachmentCodecJson: CodecJson[Attachment] =
    casecodec3(Attachment.apply, Attachment.unapply)("Name", "Content", "ContentType")

  implicit def AttachmentEqual: Equal[Attachment] = Equal.equalA[Attachment]
}

case class SentEmail(messageID: String, submittedAt: DateTime, to: String)


object SentEmail {
  import util.DateTimeISO8601CodecJsons._

  implicit def SentEmailCodecJson: CodecJson[SentEmail] =
    casecodec3(SentEmail.apply, SentEmail.unapply)("MessageID", "SubmittedAt", "To")

  implicit def SentEmailEqual: Equal[SentEmail] = Equal.equalA[SentEmail]
}