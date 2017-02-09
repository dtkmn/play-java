name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava,SonarRunnerPlugin)

scalaVersion := "2.11.8"

libraryDependencies += javaJdbc
libraryDependencies += cache
libraryDependencies += javaWs
libraryDependencies += "com.couchbase.client" % "java-client" % "2.4.1"
libraryDependencies += "commons-io" % "commons-io" % "2.5"

sonarProperties ++= Map(
  "sonar.host.url" -> "http://localhost:9000",
  "sonar.jdbc.username" -> "admin",
  "sonar.jdbc.password" -> "admin"
//  "sonar.coverage.exclusions" -> "**/MobileAppController.java,**/SomeClass.java"
)

import com.typesafe.sbt.packager.MappingsHelper._
mappings in Universal ++= directory(baseDirectory.value / "public")

jacoco.settings