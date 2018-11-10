name := """play-osm-routing-prototype"""
organization := "bitub"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"
val circeV = "0.9.0"
val scalacheckVersion = "1.13.4"
val scalatestVersion = "3.0.1"
val betterFilesVersion = "3.2.0"

libraryDependencies ++= List(
	guice,
	ws,

	"com.github.pathikrit" %% "better-files" % betterFilesVersion,

	// JSON serialization library
	"io.circe" %% "circe-core" % circeV,
	"io.circe" %% "circe-generic" % circeV,
	"io.circe" %% "circe-parser" % circeV,

	"org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
	"org.scalatest" %% "scalatest" % scalatestVersion % Test,
	"org.scalacheck" %% "scalacheck" % scalacheckVersion % Test
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "bitub.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "bitub.binders._"

initialCommands in console := """import scala.concurrent.ExecutionContext.Implicits._
								|import scala.concurrent.{Await,Future}
								|import scala.concurrent.duration._
								|import io.circe.syntax._
								|import better.files._
								|import better.files.Dsl.SymbolicOperations
								|import bitub.geojson.encoders._
								|""".stripMargin