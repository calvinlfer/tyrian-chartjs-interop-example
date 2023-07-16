# Tyrian Chart.JS interop

This is a simple example of how to use [Chart.JS](https://www.chartjs.org)
leveraging [Scalably Typed](https://scalablytyped.org) mappings and getting it
to interop with [Tyrian](https://tyrian.indigoengine.io).

## Instructions

This project makes use of the ScalaJS Bundler plugin via ScalablyTyped so all
dependencies (NPM and Scala.JS) are managed by SBT. To transpile Scala JS code
to JavaScript, run:

```bash
sbt fastOptJS::webpack
```

This will generate
`target/scala-3.3.0/scalajs-bundler/main/myawesomewebapp-fastopt-bundle.js`
which is used by index.html

You can use the following to automatically recompile when you change the code:

```bash
sbt ~fastOptJS::webpack
```

To run the web application, run:

```bash
npx http-server -c-1
```

This starts up a web server on port 8080 and does not cache any files.

Visit the root page to see the application
