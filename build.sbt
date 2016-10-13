name := "bayesian_online_learning_exploration_model"
version := "0.1"
scalaVersion := "2.11.8"


resolvers ++= Seq(
  "Typesafe repository snapshots"    at "http://repo.typesafe.com/typesafe/snapshots/",
  "Typesafe repository releases"     at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype repo"                    at "https://oss.sonatype.org/content/groups/scala-tools/",
  "Sonatype releases"                at "https://oss.sonatype.org/content/repositories/releases",
  "Sonatype snapshots"               at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype staging"                 at "http://oss.sonatype.org/content/repositories/staging",
  "Java.net Maven2 Repository"       at "http://download.java.net/maven/2/",
  "websudos"                         at "http://dl.bintray.com/websudos/oss-releases",
  "Typesafe Repo"                    at "http://repo.typesafe.com/typesafe/releases/",
  "Job Server Bintray"               at "https://dl.bintray.com/spark-jobserver/maven"
)


libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-math3" % "3.4",
  "com.typesafe" % "config" % "1.2.1"
)