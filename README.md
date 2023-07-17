# Tyrian Chart.JS interop

This is a simple example of how to use [Chart.JS](https://www.chartjs.org)
leveraging [Scalably Typed](https://scalablytyped.org) mappings and getting it
to interop with [Tyrian](https://tyrian.indigoengine.io).

## Instructions

This project makes use of the ScalaJS Bundler plugin via ScalablyTyped so all
dependencies (NPM and Scala.JS) are managed by SBT.

Usually, you would just run `sbt fastOptJS::webpack` to generate the bundle and
then use a web server to serve the `index.html` file along with the generated JS
bundle. However, we use HTTP4S to serve the files from the resources folder.

In order to compile the JavaScript bundle and copy it to the resources folder of
the backend, run:

```bash
sbt fastOptCopy
```

You can then run the backend with:

```bash
sbt backend / run
```

Alternatively, you can run the copy task in watch mode so that the bundle is
automatically copied to the resources folder when it changes:

```bash
sbt ~fastOptCopy
```
