name := "fun-with-weather"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.8"

parallelExecution in Test := false

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalanlp" %% "breeze" % "0.11.2",
  "org.scalanlp" %% "breeze-natives" % "0.11.2",
  "org.scalanlp" %% "breeze-viz" % "0.11.2",
  "org.scala-saddle" %% "saddle-core" % "1.3.+",
  "com.github.tototoshi" %% "scala-csv" % "1.1.2",
  "com.github.martincooper" %% "scala-datatable" % "0.7.0",
  "org.apache.spark" % "spark-core_2.11" % "2.0.0",
  "org.apache.spark" % "spark-mllib_2.11" % "2.0.0"
)

resolvers ++= Seq(
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)
