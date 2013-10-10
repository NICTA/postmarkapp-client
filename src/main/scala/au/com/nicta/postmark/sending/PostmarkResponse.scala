package au.com.nicta.postmark
package sending

/**
 * Data type wrapping different responses from postmark
 * @tparam A successful response value from postmark. For single emails, this would be a SentEmail. For batch emails,
 *           this would be a List[SentEmail]
 */
sealed trait PostmarkResponse[+A]

/**
 * Postmark correctly parsed your request and sent emails
 * @param value A SentEmail for single email requests, or a List[SentEmail] where each SentEmail represents one Email in
 *              the batch request.
 * @tparam A successful response value from postmark. For single emails, this would be a SentEmail. For batch emails,
 *           this would be a List[SentEmail]
 */
case class PostmarkSuccess[A](value: A) extends PostmarkResponse[A]

/**
 * We got a 401 Unauthorized response back from Postmark.
 */
case class PostmarkUnauthorized[A]() extends PostmarkResponse[A]

/**
 * Postmark returned an error meaning there was something wrong with your request.
 * @param error Parsed error returned from Postmark
 */
case class PostmarkFailure[A](error: PostmarkError) extends PostmarkResponse[A]

/**
 * Something unexpected came back from Postmark. Typically this means there is some server error, some JSON parsing issue,
 * or for batch requests some of the emails successfully sent but some failed to be sent
 * @param unexpectedType Type of unexpected error
 * @param code Postmark's error code
 * @param payload What we received from Postmark
 * @param message Message we got from postmark (typically empty)
 */
case class PostmarkUnexpected[A](unexpectedType: PostmarkUnexpectedType, code: Int, payload: Option[String], message: Option[String]) extends PostmarkResponse[A]

sealed trait PostmarkUnexpectedType
case object ServerError extends PostmarkUnexpectedType
case object UnexpectedResponseCode extends PostmarkUnexpectedType
case object JsonSyntaxError extends PostmarkUnexpectedType
case object JsonFormatError extends PostmarkUnexpectedType

object PostmarkResponse {
  def syntaxError(statusCode: Int, body: String, error: String) =
    PostmarkUnexpected(JsonSyntaxError, statusCode, Some(body), Some(error))

  def formatError(statusCode: Int, body: String, error: String) =
    PostmarkUnexpected(JsonFormatError, statusCode, Some(body), Some(error))
}
