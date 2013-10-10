postmarkapp.com HTTP API client for Scala
===

[![Build Status](https://travis-ci.org/NICTA/postmarkapp-client.png?branch=master)](https://travis-ci.org/NICTA/postmarkapp-client)

A basic postmarkapp.com HTTP API client for Scala. Uses [Argonaut](http://argonaut.io) for JSON encoding/decoding and [Dispatch](http://http://dispatch.databinder.net/) for the HTTP calls.

Sending emails
---
1. Import the library

```
libraryDependencies += "au.com.nicta" %% "postmarkapp-client" % "0.2.0"
```

2. Create your email to send and send away.

```
import au.com.nicta.postmark.sending._
import scala.concurrent._
import scala.concurrent.duration._

val email = Email(to=List("myemail@example.com"), subject="Hello", text=Some("My email body"), from="sender@example.com")

val settings = PostmarkSettings.https("your api key")   // Or use a PostmarkSettings.httpsTest to just test hitting the API without sending anything

val emailRequest = PostmarkEmailer.email(email)

val sentTheEmail = PostmarkEmailer.request(settings)(emailRequest)  // This returns a PostmarkResponse that you can parse of success/failure

Await.result(sentTheEmail, 0 nanos) // Get the response from the future
```


PostmarkEmailer has two methods (email and emails) that support sending single and batch emails. Both return a Future (courtesy of dispatch's async HTTP requests).

Just remember the following fields are required for an email:
- from - this needs to be a registered sender email with Postmark
- to - this obviously needs to be set
- either the text or html fields need to be non-empty


Receiving emails
---
A basic data type (with JSON codec mappings) for receiving emails from Postmark is in au.com.nicta.postmark.receiving package.


Testing
---
There are separate unit and integration test trees. Unit tests primarily test the codecs, while the integration tests send requests to Postmark using the test token.

To run the tests, start up SBT (e.g. using the sbt runner in the root directory of this repository), and then run:
```
test
it:test
```

There is also a demo application (demo.Sender under src/it/scala) that you can use to send emails from the command line. From SBT:
```
it:run-main demo.Sender -s -t <your token> <from email address> <to email address> <subject> <html body>
```

Credits
---
Shamelessly ported Mark Hibberd's [Haskell client](https://github.com/apiengine/postmark)
