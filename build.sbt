scalaVersion := "3.8.4"

lazy val root = rootProject
  .settings(
    name := "ShortUrl",
    libraryDependencies ++= Seq(
      //You can add library dependencies here, for example,
      "org.scalatest" %% "scalatest" % "3.2.20" % Test,
      "org.apache.pekko" %% "pekko-actor-typed" % "1.7.0",
      "org.apache.pekko" %% "pekko-stream" % "1.7.0",
      "org.apache.pekko" %% "pekko-http" % "1.4.0",
      "org.apache.pekko" %% "pekko-http-spray-json" % "1.4.0",
      "ch.qos.logback" % "logback-classic" % "1.6.3",
      "org.apache.pekko" %% "pekko-http-testkit" % "1.4.0" % Test,
      "org.apache.pekko" %% "pekko-actor-testkit-typed" % "1.7.0" % Test
      //"org.scalameta" %% "munit" % "1.2.3" % Test
    )
  )
