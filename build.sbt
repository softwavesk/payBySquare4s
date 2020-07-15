import sbtassembly.AssemblyPlugin.autoImport._

ThisBuild / organization := "sk.softwave"
ThisBuild / name := "payBySquare4s"
ThisBuild / version := "1.0.1"

ThisBuild / scalaVersion := "2.13.3"

val buildScalaVersion = "2.13.3"
val scala212 = "2.12.10"

scalafmtOnCompile in ThisBuild := true

val dependencies = Seq(
  //"org.apache.commons" % "commons-compress" % "1.20",
  "org.tukaani" % "xz" % "1.8",
  "commons-codec" % "commons-codec" % "1.14",
  "com.google.zxing" % "javase" % "3.4.0",
  "org.scalatest" %% "scalatest" % "3.2.0" % "test"
)

lazy val core = Project(id="payBySquare4s", base=file("."))
  .settings(Defaults.coreDefaultSettings,
    libraryDependencies ++= dependencies,
    crossScalaVersions := Seq(buildScalaVersion, scala212)
  )

lazy val app = Project(id="app", base=file("app"))
  .settings(Defaults.coreDefaultSettings)
  .dependsOn(core)
  .aggregate(core)
