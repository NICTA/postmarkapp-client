package au.com.nicta.postmark
package sending

/**
 * Wraps up settings required to send things to Postmark. Use the PostmarkSettings.http/https factory functions to
 * create suitable settings.
 * @param apiUrl The Postmark API url
 * @param apiToken Your API token
 */
case class PostmarkSettings(apiUrl: String, apiToken: String)

object PostmarkSettings {
  val TEST_TOKEN = "POSTMARK_API_TEST"
  def http(token: String) = PostmarkSettings("http://api.postmarkapp.com", token)
  def https(token: String) = PostmarkSettings("https://api.postmarkapp.com", token)
  def httpTest = http(TEST_TOKEN)
  def httpsTest = https(TEST_TOKEN)
}