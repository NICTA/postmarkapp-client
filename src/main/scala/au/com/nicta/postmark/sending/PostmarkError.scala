package au.com.nicta.postmark
package sending

import scalaz._
import argonaut._, Argonaut._

case class PostmarkError(errorType: PostmarkErrorType, message: String)

object PostmarkError {
  implicit def PostmarkErrorDecodeJson: DecodeJson[PostmarkError] =
    DecodeJson(c => for {
      errorType <- (c --\ "ErrorCode").as[Int].map(_ match {
        case 0 => PostmarkBadApiToken
        case 300 => PostmarkInvalidEmail
        case 400 => PostmarkSenderNotFound
        case 401 => PostmarkSenderNotConfirmed
        case 402 => PostmarkInvalidJson
        case 403 => PostmarkIncompatibleJson
        case 405 => PostmarkNotAllowed
        case 406 => PostmarkInactive
        case 407 => PostmarkBounceNotFound
        case 408 => PostmarkBounceQueryException
        case 409 => PostmarkJsonRequired
        case 410 => PostmarkTooManyMessages
        case code => PostmarkUnknownError(code)
      })
      message <- (c --\ "Message").as[String]
    } yield PostmarkError(errorType, message))
}

sealed trait PostmarkErrorType
case object PostmarkBadApiToken extends PostmarkErrorType
case object PostmarkInvalidEmail extends PostmarkErrorType
case object PostmarkSenderNotFound extends PostmarkErrorType
case object PostmarkSenderNotConfirmed extends PostmarkErrorType
case object PostmarkInvalidJson extends PostmarkErrorType
case object PostmarkIncompatibleJson extends PostmarkErrorType
case object PostmarkNotAllowed extends PostmarkErrorType
case object PostmarkInactive extends PostmarkErrorType
case object PostmarkBounceNotFound extends PostmarkErrorType
case object PostmarkBounceQueryException extends PostmarkErrorType
case object PostmarkJsonRequired extends PostmarkErrorType
case object PostmarkTooManyMessages extends PostmarkErrorType
case class PostmarkUnknownError(code: Int) extends PostmarkErrorType

object PostmarkErrorType {
  implicit def PostmarkErrorTypeShow: Show[PostmarkErrorType] = Show.shows(_ match {
    case PostmarkBadApiToken => "Your request did not submit the correct API token in the X-Postmark-Server-Token header."
    case PostmarkInvalidEmail => "Validation failed for the email request JSON data that you provided."
    case PostmarkSenderNotFound => "You are trying to send email with a From address that does not have a sender signature."
    case PostmarkSenderNotConfirmed => "You are trying to send email with a From address that does not have a corresponding confirmed sender signature."
    case PostmarkInvalidJson => "The JSON input you provided is syntactically incorrect."
    case PostmarkIncompatibleJson => "The JSON input you provided is syntactically correct, but still not the one we expect."
    case PostmarkNotAllowed => "You ran out of credits."
    case PostmarkInactive => "You tried to send to a recipient that has been marked as inactive. Inactive recipients are ones that have generated a hard bounce or a spam complaint."
    case PostmarkBounceNotFound => "You requested a bounce by ID, but we could not find an entry in our database."
    case PostmarkBounceQueryException => "You provided bad arguments as a bounces filter."
    case PostmarkJsonRequired => "Your HTTP request does not have the Accept and Content-Type headers set to application/json."
    case PostmarkTooManyMessages => "Your batched request contains more than 500 messages."
    case PostmarkUnknownError(code) => "An unexpected error code [" + code + "] was retured from postmark."
  })
}
