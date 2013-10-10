package au.com.nicta
package postmark
package sending

import org.specs2.matcher.{Expectable, Matcher}

class PostmarkResponseMatcher[A](check: PostmarkResponse[A] => Boolean) extends Matcher[PostmarkResponse[A]] {
  def apply[S <: PostmarkResponse[A]](s: Expectable[S]) = {
    val message = s.value match {
      case PostmarkSuccess(v) => "Sent emails: " + v
      case PostmarkUnauthorized() => "Unauthorised API key"
      case PostmarkFailure(error) => "Failed to send emails: " + error
      case e @ PostmarkUnexpected(_, _, _, _) => "Unexpected error: " + e
    }

    result(check(s.value), message, message, s)
  }
}

trait PostmarkResponseMatchers {
  def haveBatchEmailsSentTo(emails: List[List[String]]) =
    resultIs[List[SentEmail]](_ match {
      case PostmarkSuccess(v) => {
        val sentEmails = v.map(_.to)
        val sortedExpectedEmails = emails.map(_.sorted)
        sentEmails.forall(es => sortedExpectedEmails.contains(es.sorted))
      }
      case _ => false
    })
  def haveSentTo(emails: List[String]) =
    resultIs[SentEmail](_ match {
      case PostmarkSuccess(v) => emails.diff(v.to).isEmpty && v.to.diff(emails).isEmpty
      case _ => false
    })

  def resultIs[A](f: PostmarkResponse[A] => Boolean) =
    new PostmarkResponseMatcher[A](f)
}
