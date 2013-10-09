package au.com.nicta.postmark
package sending

case class PostmarkSettings(apiUrl: String, apiToken: String)

object PostmarkSettings {
  val TEST_TOKEN = "POSTMARK_API_TEST"
  def http(token: String) = PostmarkSettings("http://api.postmarkapp.com", token)
  def https(token: String) = PostmarkSettings("https://api.postmarkapp.com", token)
  def httpTest = http(TEST_TOKEN)
  def httpsTest = https(TEST_TOKEN)
}