import sbt._
import Keys._
import Tests._

object B extends Build
{
  lazy val CustomIntegrationTest = config("it") extend(Test)
  lazy val root =
    Project("root", file("."))
      .configs( CustomIntegrationTest )
      .settings( inConfig(CustomIntegrationTest)(Defaults.testSettings) : _*)
      .settings(
      // FIX - Remove dummy test group as a fix for: https://github.com/sbt/sbt/issues/539
      testGrouping in CustomIntegrationTest <<= definedTests in CustomIntegrationTest map partitionTests("integrationTests"),
      testGrouping in Test <<= definedTests in Test map partitionTests("unitTests"),
      parallelExecution in CustomIntegrationTest := false
    )

  def partitionTests(groupName: String)(tests: Seq[TestDefinition]) = {
    Seq(
      new Group(groupName, tests, SubProcess(Seq()))
    )
  }
}