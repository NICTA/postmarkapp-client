package au.com.nicta
package postmark.sending

class EmailSpec extends test.Spec with PostmarkArbitraries {
  "Attachment" should {
    "satisfy codec laws" ! codecprop[Attachment]
  }

  "SentEmail" should {
    "satisfy codec laws" ! codecprop[SentEmail]
  }

  "Email" should {
    "satisfy codec laws" ! codecprop[Email]
  }
}
