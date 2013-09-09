package au.com.nicta
package util

object EmailUtil {
  def splitEmailsByCommas(emailStr: String) = emailStr.split(",").toList.map(_.trim).filter(_.length > 0)

  def joinEmailsByCommas(emails: List[String]) = emails.mkString(",")
}
