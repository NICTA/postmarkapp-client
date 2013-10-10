package au.com.nicta
package postmark
package sending

import org.specs2.time.NoTimeConversions
import concurrent._
import duration._

class SendToPostmarkSpec extends test.Spec with PostmarkArbitraries with PostmarkResponseMatchers with NoTimeConversions {
  "PostmarkEmailer" should {
    "send and receive single email correctly" ! prop((from: EmailString, firstTo: EmailString, otherTos: List[EmailString],
                                                      ccs: List[EmailString], bccs: List[EmailString],
                                                      email: Email) => {
      val sendToEmails = firstTo.value :: otherTos.take(10).map(_.value)
      val sendCcEmails = ccs.take(4).map(_.value)
      val sendBccEmails = bccs.take(4).map(_.value)
      val emailToSend = email.copy(to = sendToEmails, from = from.value, cc = sendCcEmails, bcc = sendBccEmails)

      val settings = PostmarkSettings.httpsTest

      val emailRequest = PostmarkEmailer.email(emailToSend)

      val sentTheEmail = PostmarkEmailer.request[Email, SentEmail](settings)(emailRequest)
      sentTheEmail must haveSentTo(sendToEmails).await(timeout = 5.seconds)
    }).set(minTestsOk = 5)
    "send and receive batch emails correctly" ! prop((from: EmailString,
                                                      to1: EmailString,
                                                      otherTo1: List[EmailString],
                                                      to2: EmailString,
                                                      otherTo2: List[EmailString],
                                                      email1: Email, email2: Email) => {
      val sendToEmails1 = to1.value :: otherTo1.take(10).map(_.value)
      val sendToEmails2 = to2.value :: otherTo2.take(10).map(_.value)
      val emailToSend1 = email1.copy(to = sendToEmails1, from = from.value, cc = Nil, bcc = Nil)
      val emailToSend2 = email2.copy(to = sendToEmails2, from = from.value, cc = Nil, bcc = Nil)

      val settings = PostmarkSettings.httpsTest

      val emailRequest = PostmarkEmailer.emails(List(emailToSend1, emailToSend2))

      val sentTheEmail = PostmarkEmailer.request[List[Email], List[SentEmail]](settings)(emailRequest)
      sentTheEmail must haveBatchEmailsSentTo(List(sendToEmails1, sendToEmails2)).await(timeout = 5.seconds)
    }).set(minTestsOk = 5)
  }
}
