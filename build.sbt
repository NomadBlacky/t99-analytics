import sbt.Keys._
import sbt._
import sbtrelease.Version

name := "t99-analytics"

resolvers += Resolver.sonatypeRepo("public")
scalaVersion := "2.12.8"
releaseNextVersion := { ver =>
  Version(ver).map(_.bumpMinor.string).getOrElse("Error")
}
assemblyJarName in assembly := "t99.jar"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-events" % "2.2.5",
  "com.amazonaws" % "aws-lambda-java-core"   % "1.2.0",
  "com.lihaoyi"   %% "ujson"                 % "0.7.1",
  "org.scalatest" %% "scalatest"             % "3.0.6" % Test,
  "org.mockito"   % "mockito-core"           % "2.24.5" % Test
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings"
)
