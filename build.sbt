ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.2"

lazy val root = (project in file("."))
  .settings(
    name := "GH_Archive_Allegro"
  )

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "requests" % "0.7.0",
  "com.lihaoyi" %% "ujson" % "1.6.0",
  "com.github.losizm" %% "little-io" % "8.0.0",
  ("org.apache.spark" %% "spark-core" % "3.2.0" % "provided").cross(CrossVersion.for3Use2_13),
  ("org.apache.spark" %% "spark-sql" % "3.2.0" % "provided").cross(CrossVersion.for3Use2_13)
)

Compile / run := Defaults.runTask(Compile / fullClasspath, Compile / run / mainClass, Compile / run / runner).evaluated