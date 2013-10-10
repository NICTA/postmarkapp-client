package au.com.nicta.postmark
package sending

/**
 * Wraps a request to Postmark. Use PostmarkEmailer.email or PostmarkEmailer.emails to create this
 * @param path The context path to use for the request.
 * @param content The request content (either an Email or List[Email])
 * @tparam A Either Email or List[Email]
 */
case class PostmarkRequest[A](path: String, content: A)