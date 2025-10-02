name := "notes-app"
version := "1.0"
scalaVersion := "2.13.16"

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra-http-server" % "24.2.0",
  "org.reactivemongo" %% "reactivemongo" % "1.1.0-RC10", 
  "redis.clients" % "jedis" % "6.1.0"
)