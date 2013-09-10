import sbt._
import Keys._
import util.Properties

object ApplicationBuild extends Build {

  val appName = "docsetter"
  val appVersion = "1.0"
  lazy val buildSettings = Seq(
    organization := "com.ryanbrainard",
    scalaVersion := "2.9.1"
  )

  val appDependencies = Seq(
    "org.rogach" %% "scallop" % "0.9.1"
  )

  val scalaqlite = RootProject(uri("git://github.com/srhea/scalaqlite.git"))

  lazy val root = Project(id = appName,
                          base = file("."),
                          settings = Project.defaultSettings ++ buildSettings
                         ).dependsOn(scalaqlite)

}
