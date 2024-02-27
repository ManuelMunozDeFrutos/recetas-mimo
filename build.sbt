name := """recetas-mimo"""
organization := "com.mimo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.12"

libraryDependencies += guice

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.5"

libraryDependencies += "org.slf4j" % "slf4j-jdk14" % "1.7.32" exclude("org.slf4j", "slf4j-log4j12")

enablePlugins(PlayEbean)
libraryDependencies += evolutions
libraryDependencies += jdbc

libraryDependencies += "com.h2database" % "h2" % "2.1.214"

libraryDependencies += ehcache

assemblyJarName in assembly := "recetas-mimo-fat-jar-assembly.jar"

import sbtassembly.AssemblyPlugin.autoImport._

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", x, xs @ _*) if x.toLowerCase == "services" => MergeStrategy.filterDistinctLines
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

