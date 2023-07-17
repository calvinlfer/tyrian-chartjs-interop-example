import scala.sys.process._
import scala.language.postfixOps

Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  List(
    scalaVersion      := "3.3.0",
    scalafixOnCompile := true,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision
  )
)

lazy val root =
  project
    .in(file("."))
    .aggregate(frontend, backend)

lazy val frontend =
  (project in file("frontend"))
    .enablePlugins(ScalaJSPlugin, ScalablyTypedConverterPlugin)
    .settings(
      name         := "tyrian-chart-js-interop",
      organization := "myorg",
      Compile / npmDependencies ++= Seq(
        "@types/chart.js" -> "2.9.11",
        "chart.js"        -> "2.9.3"
      ),
      libraryDependencies ++= Seq(
        "io.indigoengine" %%% "tyrian-io" % "0.7.1",
        "com.armanbilge"  %%% "fs2-dom"   % "0.2.1",
        "org.scalameta"   %%% "munit"     % "0.7.29" % Test
      ),
      testFrameworks += new TestFramework("munit.Framework"),
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
      autoAPIMappings := true,
      // Source maps seem to be broken with bundler
      Compile / fastOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      scalaJSUseMainModuleInitializer := true
    )
// use ~fastOptJS::webpack
// use fullOptJS::webpack for production

lazy val backend =
  project
    .in(file("backend"))
    .settings(
      name := "tyrian-chart-js-interop-backend",
      libraryDependencies ++= {
        val http4s        = "org.http4s"
        val http4sVersion = "0.23.22"
        Seq(
          http4s          %% "http4s-ember-server" % http4sVersion,
          http4s          %% "http4s-dsl"          % http4sVersion,
          "ch.qos.logback" % "logback-classic"     % "1.4.8"
        )
      }
    )

lazy val fastOptCopy =
  taskKey[Unit]("Copies the output of Scala.js to the backend resources")

fastOptCopy := compileFrontendAndPlaceInBackendResource(fastOptJS).value

lazy val fullOptCopy =
  taskKey[Unit]("Copies the output of Scala.js to the backend resources")

fullOptCopy := compileFrontendAndPlaceInBackendResource(fullOptJS).value

def compileFrontendAndPlaceInBackendResource(
    optJs: TaskKey[Attributed[File]]
) = Def.task {
  val scalaJsOutput = (frontend / Compile / optJs / webpack).value
    .filter(_.data.name.contains("bundle"))
    .head
  val backendResources = (backend / Compile / resourceDirectory).value
  val targetFile       = backendResources / "main.js"
  IO.copyFile(scalaJsOutput.data, targetFile)
}
