package au.com.nicta
package postmark
package receiving

class EmailSpec extends test.Spec with PostmarkArbitraries {
  "Attachment" should {
    "satisfy codec laws" ! codecprop[Attachment]
  }

  "FullEmailAddress" should {
    "satisfy codec laws" ! codecprop[FullEmailAddress]
  }

  "Email" should {
    "satisfy codec laws" ! codecprop[Email]
  }
}
