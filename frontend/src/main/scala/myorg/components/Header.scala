package myorg.components

import tyrian.Html.*
import tyrian.*

object Header:
  val view =
    val style =
      `class` := "font-sans flex flex-col text-center sm:flex-row sm:text-left sm:justify-between py-4 px-6 bg-white shadow sm:items-baseline w-full"

    nav(style)(
      img(
        src    := "/static/img/logo.png",
        alt    := "Tyrian logo",
        width  := "32",
        height := "32"
      ),
      div(
        link("Home", "/"),
        link("Pause", "/pause")
      )
    )

  private def link(displayName: String, path: String): Html[Nothing] =
    val style =
      `class` := "text-lg no-underline text-grey-darkest hover:text-blue-dark ml-2"

    a(href := path, id := s"header-$displayName", style)(displayName)
