import sbtrelease.Version

val versions = new {
  val xray       = "2.2.1"
  val awsJavaSdk = "1.11.510"
}

lazy val commonSettings = Seq(
  resolvers += Resolver.sonatypeRepo("public"),
  scalaVersion := "2.12.8",
  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-Xlint",
    "-Xfatal-warnings"
  )
)

lazy val app = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "t99-analytics",
    releaseNextVersion := { ver =>
      Version(ver).map(_.bumpMinor.string).getOrElse("Error")
    },
    assemblyJarName in assembly := "t99.jar",
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-lambda-java-events"                     % "2.2.5",
      "com.amazonaws" % "aws-lambda-java-core"                       % "1.2.0",
      "com.amazonaws" % "aws-java-sdk-rekognition"                   % versions.awsJavaSdk,
      "com.lihaoyi"   %% "upickle"                                   % "0.7.1",
      "org.scalaj"    %% "scalaj-http"                               % "2.4.1",
      "com.amazonaws" % "aws-xray-recorder-sdk-core"                 % versions.xray,
      "com.amazonaws" % "aws-xray-recorder-sdk-aws-sdk"              % versions.xray,
      "com.amazonaws" % "aws-xray-recorder-sdk-aws-sdk-instrumentor" % versions.xray,
      "com.amazonaws" % "aws-lambda-java-log4j2"                     % "1.1.0",
      "org.scalatest" %% "scalatest"                                 % "3.0.6" % Test,
      "org.mockito"   % "mockito-core"                               % "2.24.5" % Test
    ),
    assemblyMergeStrategy in assembly := {
      case PathList("com", "amazonaws", "xray", "sdk.properties") => MergeStrategy.first
      case PathList(ps @ _*) if ps.last == "Log4j2Plugins.dat"    => Log4j2MergeStrategy.plugincache
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )

lazy val integrationTest = (project in file("integrationTest"))
  .dependsOn(app % "test->test;compile->compile")
  .settings(commonSettings)
