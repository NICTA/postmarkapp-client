package au.com.nicta.postmark
package sending

import dispatch._, Defaults._
import argonaut._, Argonaut._
import com.ning.http.client.Response


object PostmarkEmailer {
  def email(email: Email): PostmarkRequest[Email, SentEmail] =
    PostmarkRequest("email", email)

  def emails(emails: List[Email]): PostmarkRequest[List[Email], List[SentEmail]] =
    PostmarkRequest("email/batch", emails)

  def request[A: EncodeJson, B: DecodeJson](settings: PostmarkSettings)(req: PostmarkRequest[A, B]) = {
    val r = (url(settings.apiUrl) / req.path)
      .addHeader("Accept", "application/json")
      .addHeader("Content-Type", "application/json")
      .addHeader("X-Postmark-Server-Token", settings.apiToken)
      .POST << implicitly[EncodeJson[A]].encode(req.content).nospaces

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
