import sbt._
import Keys._
import util.Properties

object ApplicationBuild extends Build {

  val appName = "docsetter"
  val appVersion = "1.0"
  lazy val buildSettings = Seq(
    organization := "com.ryanbrainard",
    scalaVersion := "2.9.1",
    libraryDependencies ++= Seq(
      "org.rogach" %% "scallop" % "0.9.1",
      "org.xerial" % "sqlite-jdbc" % "3.7.2",
      "org.scalatest" %% "scalatest" % "2.0.M6-SNAP22" % "test"
    )
  )

  lazy val root = Project(id = appName,
                          base = file("."),
                          settings = Project.defaultSettings ++ buildSettings
                         )
}
