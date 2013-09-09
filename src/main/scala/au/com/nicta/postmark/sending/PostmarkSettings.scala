package au.com.nicta.postmark
package sending

case class PostmarkSettings(apiUrl: String, apiToken: String)

object PostmarkSettings {
  def http(token: String) = PostmarkSettings("http://api.postmarkapp.com", token)
  def https(token: String) = PostmarkSettings("https://api.postmarkapp.com", token)
}