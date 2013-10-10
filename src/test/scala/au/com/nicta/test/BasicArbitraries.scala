package au.com.nicta
package test

import org.scalacheck.{Gen, Arbitrary}
import com.github.nscala_time.time.Imports._
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen._
import scalaz._, std.option._, syntax.show._, syntax.apply._, scalacheck.ScalaCheckBinding._
import au.com.nicta.postmark.common.Header

trait BasicArbitraries {

  def genLimitedList[A: Arbitrary](min: Int = 0, max: Int = 20) =
    for {
      num <- Gen.choose(min, max)
      list <- Gen.listOfN(num, arbitrary[A])
    } yield list


  implicit def DateTimeArbitrary: Arbitrary[DateTime] = Arbitrary(for {
    l <- Gen.posNum[Long]
    d = DateTime.now
  } yield d.plus(l))

  case class EmailString(value: String)
  implicit def EmailStringArbitrary: Arbitrary[EmailString] =
    Arbitrary(for {
      usernameFirstChar <- alphaChar
      username <- alphaStr
      domainFirstChar <- alphaChar
      domain <- alphaStr
    } yield EmailString(usernameFirstChar + username + "@" + domainFirstChar + domain + ".com"))

  implicit def HeaderArbitrary: Arbitrary[Header] =
    Arbitrary(for {
      name <- arbitrary[NonEmptyString]
      value <- arbitrary[NonEmptyString]
    } yield Header(name.value, value.value))

  case class NonEmptyString(value: String)
  implicit def NonEmptyStringArbitrary: Arbitrary[NonEmptyString] =
    Arbitrary(for {
      firstChar <- alphaChar
      rest <- arbitrary[String]
    } yield NonEmptyString(firstChar + rest))
}
