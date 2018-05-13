import Dependencies._

enablePlugins(GitVersioning)

lazy val root = (project in file(".")).
  settings(

    inThisBuild(List(
      organization := "io.ceratech",
      scalaVersion := "2.12.6"
    )),

    autoScalaLibrary := true,

    name := "php-unserializer",

    bintrayOrganization := Some("ceratech"),
    licenses += ("gpl-3.0", url("https://www.gnu.org/licenses/gpl-3.0.en.html")),

    git.useGitDescribe := true,
    git.baseVersion := "0.1",

    libraryDependencies ++= Seq(
      scalaTest
    )
  )
