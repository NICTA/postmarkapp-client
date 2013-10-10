package au.com.nicta
package postmark
package sending

import argonaut._, Argonaut._
import com.github.nscala_time.time.Imports._
import scalaz._
import au.com.nicta.postmark.common.Header

/**
 * Email data type for sending emails. You can use the defaultEmail as a base to create new emails e.g.
 *   Email(from = "you@yourdomain.com", to = "person@example.com", subject = "This is an example")
 * @param from The from email address. This must match one configured within Postmark
 * @param to To email addresses. In total, you can only send to 20 emails (including CCs and BCCs)
 * @param cc  CC email addresses. In total, you can only send to 20 emails (including CCs and BCCs)
 * @param bcc BCC email addresses. In total, you can only send to 20 emails (including CCs and BCCs)
 * @param subject Email subject
 * @param tag Optional email tags
 * @param html HTML body for the email. Either this or the text field (or both) must be provided.
 * @param text Plain text body for the email. Either this or the HTML field (or both) must be provided.
 * @param replyTo Reply to email address
 * @param headers Any custom headers.
 * @param attachments Any attachments.
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

/**
 * Attachment to send
 * @param name Name of the attachment (See the Postmark API docs; the file extension is important!)
 * @param content Base64 encoded content
 * @param contentType The content type (See the Postmark API docs on supported content types)
 */
case class Attachment(name: String, content: String, contentType: String)

object Attachment {
  implicit def AttachmentCodecJson: CodecJson[Attachment] =
    casecodec3(Attachment.apply, Attachment.unapply)("Name", "Content", "ContentType")

  implicit def AttachmentEqual: Equal[Attachment] = Equal.equalA[Attachment]
}

/**
 * Response from Postmark
 * @param messageID The unique message Id for the request
 * @param submittedAt when the request was submitted
 * @param to The people who the email was sent to (only the people on the 'to' list; cc and bcc addresses are not returned)
 */
case class SentEmail(messageID: String, submittedAt: DateTime, to: List[String])

object SentEmail {
  import util.DateTimeISO8601CodecJsons._
  import util.EmailUtil._

  private def applySplitEmailsByCommas(messageID: String, submittedAt: DateTime, to: String) =
    SentEmail(messageID, submittedAt, splitEmailsByCommas(to))

  private def unapplySplitEmailsByCommas(emailResponse: SentEmail) =
    Some(emailResponse.messageID, emailResponse.submittedAt, joinEmailsByCommas(emailResponse.to))

  implicit def SentEmailCodecJson: CodecJson[SentEmail] =
    casecodec3(applySplitEmailsByCommas, unapplySplitEmailsByCommas)("MessageID", "SubmittedAt", "To")

  implicit def SentEmailEqual: Equal[SentEmail] = Equal.equalA[SentEmail]
}