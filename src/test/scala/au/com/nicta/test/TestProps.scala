package au.com.nicta
package test

import argonaut._
import scalaz._
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._

trait TestProps {
  def codecprop[A: EncodeJson: DecodeJson : Arbitrary : Equal] =
    forAll(CodecJson.derived[A].codecLaw.encodedecode _)
}
