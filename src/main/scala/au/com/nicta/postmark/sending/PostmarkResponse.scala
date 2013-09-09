package au.com.nicta.postmark
package sending

sealed trait PostmarkResponse[+A]
case class PostmarkSuccess[A](value: A) extends PostmarkResponse[A]
case class PostmarkUnauthorized[A]() extends PostmarkResponse[A]
case class PostmarkFailure[A](error: PostmarkError) extends PostmarkResponse[A]
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
