package au.com.nicta
package test

import org.specs2.{mutable, ScalaCheck}, mutable._
import org.scalacheck.{Pretty, Arbitrary}
import com.github.nscala_time.time.Imports._
import scalaz._, syntax.show._

abstract class Spec
  extends Specification
  with TestProps
  with ScalaCheck
  with BasicArbitraries {

  implicit def ShowPretty[A: Show](a: A): Pretty =
    Pretty(_ => a.shows)

  implicit def DateEqual =
    Equal.equalA[DateTime]

}
