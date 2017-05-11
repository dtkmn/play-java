name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava,SonarRunnerPlugin)

scalaVersion := "2.11.8"

libraryDependencies += javaJdbc
libraryDependencies += cache
libraryDependencies += javaWs
libraryDependencies += "com.couchbase.client" % "java-client" % "2.4.1"
libraryDependencies += "commons-io" % "commons-io" % "2.5"
//libraryDependencies += "com.typesafe.akka" % "akka-testkit_2.11" % "2.3.8" % "test"
libraryDependencies ++= Seq(  "com.typesafe.akka" %% "akka-actor" % "2.4.12",
                              "com.typesafe.akka" %% "akka-testkit" % "2.4.12" % "test",
                              "org.scala-lang.modules" %% "scala-java8-compat" % "0.6.0",
                              "com.typesafe.akka" %% "akka-camel" % "2.4.11",
                              "org.apache.camel" % "camel-jetty" % "2.10.3",
                              "org.apache.camel" % "camel-quartz" % "2.10.3",
                              "org.apache.camel" % "camel-smpp" % "2.10.3",
                              "junit"             % "junit"           % "4.11"  % "test",
                              "com.novocode"      % "junit-interface" % "0.10"  % "test" )


sonarProperties ++= Map(
  "sonar.host.url" -> "http://localhost:9000",
  "sonar.jdbc.username" -> "admin",
  "sonar.jdbc.password" -> "admin",
  "sonar.jacoco.reportPath" -> "target/scala-2.11/jacoco/jacoco.exec",
  "sonar.java.binaries" -> "target/scala-2.11/classes"
//  "sonar.coverage.exclusions" -> "**/MobileAppController.java,**/SomeClass.java"
)

import com.typesafe.sbt.packager.MappingsHelper._
mappings in Universal ++= directory(baseDirectory.value / "public")

jacoco.settings