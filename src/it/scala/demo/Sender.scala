package demo

import au.com.nicta.postmark.sending._
import concurrent._, duration._

object Sender {
  def main(args: Array[String]): Unit = {
    case class CmdParams(https: Boolean = false, token: String = PostmarkSettings.TEST_TOKEN, from: String = "", to: String = "",
                         subject: String = "", html: String = "")

    val parser = new scopt.OptionParser[CmdParams]("demo.Sender"){
      head("demo.Sender")
      opt[Unit]('s', "https") action { (_, c) => c.copy(https = true) } text("Use HTTPS instead of HTTP")
      opt[String]('t', "token") action { (x, c) => c.copy(token = x) } text("The Postmark API token to use")
      arg[String]("<from>") action { (x, c) => c.copy(from = x) } text("The from email address")
      arg[String]("<to>") action { (x, c) => c.copy(to = x) } text("The to email address")
      arg[String]("<subject>") action { (x, c) => c.copy(subject = x) } text("The email subject")
      arg[String]("<html body>") action { (x, c) => c.copy(html = x) } text("The email body")
    }

    parser.parse(args, CmdParams()) map { params => {
        import ExecutionContext.Implicits.global
        val email = Email(to = List(params.to), from = params.from, subject = params.subject, html = Some(params.html))
        val settings = if (params.https) PostmarkSettings.https(params.token)
                       else PostmarkSettings.http(params.token)
        val emailRequest = PostmarkEmailer.email(email)
        val sentTheEmail = PostmarkEmailer.request[Email, SentEmail](settings)(emailRequest)
        sentTheEmail onSuccess {
          case r => { println("Response from Postmark:" + r); System.exit(0); }
        }
        sentTheEmail onFailure {
          case t => { println("Error sending email:" + t.getMessage); System.exit(1); }
        }
        Await.result(sentTheEmail, 5.seconds)
      }
    } getOrElse {
      System.exit(1)
    }
  }

}
