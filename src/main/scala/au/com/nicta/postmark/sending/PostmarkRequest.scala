package au.com.nicta.postmark
package sending

case class PostmarkRequest[A](path: String, content: A)