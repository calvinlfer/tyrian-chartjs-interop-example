import scala.sys.process._
import scala.language.postfixOps

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val myawesomewebapp =
  (project in file("."))
    .enablePlugins(ScalaJSPlugin, ScalablyTypedConverterPlugin)
    .settings(
      name         := "tyrian-chart-js-interop",
      version      := "0.0.1",
      scalaVersion := "3.3.0",
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
      scalafixOnCompile := true,
      semanticdbEnabled := true,
      semanticdbVersion := scalafixSemanticdb.revision,
      autoAPIMappings   := true,
      // Source maps seem to be broken with bundler
      Compile / fastOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      scalaJSUseMainModuleInitializer := true
    )
// use ~fastOptJS::webpack
// use fullOptJS::webpack for production
