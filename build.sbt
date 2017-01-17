name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava,SonarRunnerPlugin)

scalaVersion := "2.11.8"

libraryDependencies += javaJdbc
libraryDependencies += cache
libraryDependencies += javaWs

sonarProperties ++= Map(
  "sonar.host.url" -> "http://localhost:9000",
  "sonar.jdbc.username" -> "admin",
  "sonar.jdbc.password" -> "admin"
//  "sonar.coverage.exclusions" -> "**/MobileAppController.java,**/SomeClass.java"
)

jacoco.settings