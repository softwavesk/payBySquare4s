import sbtassembly.AssemblyPlugin.autoImport._

ThisBuild / organization := "sk.softwave"
ThisBuild / name := "payBySquare4s"
ThisBuild / version := "1.0"

ThisBuild / scalaVersion := "2.13.2"

scalafmtOnCompile in ThisBuild := true

val dependencies = Seq(
  //"org.apache.commons" % "commons-compress" % "1.20",
  "org.tukaani" % "xz" % "1.8",
  "commons-codec" % "commons-codec" % "1.14",
  "com.google.zxing" % "javase" % "3.4.0",
  "org.scalatest" %% "scalatest" % "3.2.0" % "test"
)

lazy val core = (project in file("."))
  .settings(Defaults.coreDefaultSettings,
    libraryDependencies ++= dependencies
  )

lazy val app = Project(id="app", base=file("app"))
  .settings(Defaults.coreDefaultSettings)
  .dependsOn(core)
  .aggregate(core)
