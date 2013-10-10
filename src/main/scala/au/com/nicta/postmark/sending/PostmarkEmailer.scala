package au.com.nicta.postmark
package sending

import dispatch._, Defaults._
import argonaut._, Argonaut._
import com.ning.http.client.Response

/**
 * Main object that has functions for actually doing sending. Typically you would create a PostmarkRequest using either
 * the email (for single email) or emails (for batch) functions, and then pass the request to request() when you're
 * ready to send it.
 */
object PostmarkEmailer {
  /**
   * Create a request for sending a single email
   * @param email The email to send
   * @return a request to send a single email
   */
  def email(email: Email): PostmarkRequest[Email] =
    PostmarkRequest("email", email)

  /**
   * Create a request for sending a batch of emails
   * @param emails The emails to send in a batch
   * @return Suitable request
   */
  def emails(emails: List[Email]): PostmarkRequest[List[Email]] =
    PostmarkRequest("email/batch", emails)

  /**
   * Fire off the request to postmark
   * @param settings The settings to use for sending request to postmark
   * @param req The request itself
   * @tparam A The request content type (Email or List[Email])
   * @tparam B The response type if you care (SentEmail for single emails, List[SentEmail] for batch)
   * @return Future of a PostmarkResponse.
   */
  def request[A: EncodeJson, B: DecodeJson](settings: PostmarkSettings)(req: PostmarkRequest[A]) = {
    val r = (url(settings.apiUrl) / req.path)
      .addHeader("Accept", "application/json")
      .addHeader("Content-Type", "application/json")
      .addHeader("X-Postmark-Server-Token", settings.apiToken)
      .POST << req.content.asJson.nospaces

    def decodeResponse[X: DecodeJson](f: X => PostmarkResponse[B])(code: Int, body: String) =
      body.decodeWith[PostmarkResponse[B], X](f,
        PostmarkResponse.syntaxError(code, body, _),
        (err, c) => PostmarkResponse.formatError(code, body, err))

    def responseHandler(response: Response): PostmarkResponse[B] =
      (response.getStatusCode, response.getResponseBody) match {
        case (code @ 200, body) => decodeResponse[B](PostmarkSuccess.apply)(code, body)
        case (401, _) => PostmarkUnauthorized()
        case (code @ 422, body) => decodeResponse[PostmarkError](PostmarkFailure.apply)(code, body)
        case (500, body) => PostmarkUnexpected(ServerError, 500, Some(body), None)
        case (code, body) => PostmarkUnexpected(UnexpectedResponseCode, code, Some(body), None)
      }
    Http(r > responseHandler _)
  }
}
