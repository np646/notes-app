name := "notes-app"
version := "1.0"
scalaVersion := "2.13.16"

libraryDependencies ++= Seq( "com.twitter" %% "finatra-http-server" % "24.2.0",
  "org.reactivemongo" %% "reactivemongo" % "1.0.10"
)