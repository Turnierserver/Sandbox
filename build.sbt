name := "Sandbox"
version := "1.0"
scalaVersion := "2.12.1"

libraryDependencies += "net.lingala.zip4j" % "zip4j" % "1.3.2"
libraryDependencies += "com.jsuereth" % "scala-arm_2.11" % "2.0-RC1"

autoCompilerPlugins := true
addCompilerPlugin("org.scala-lang.plugins" % "scala-continuations-plugin_2.12.0" % "1.0.3")
libraryDependencies += "org.scala-lang.plugins" %% "scala-continuations-library" % "1.0.3"
scalacOptions += "-P:continuations:enable"
