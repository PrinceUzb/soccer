import Dependencies.Libraries._
import sbt._

object Dependencies {
  object Versions {
    val cats          = "2.7.0"
    val catsEffect    = "3.3.8"
    val circe         = "0.14.1"
    val fs2           = "3.2.5"
    val http4s        = "0.23.11"
    val log4cats      = "2.2.0"
    val skunk         = "0.3.1"
    val logback       = "1.2.11"
    val ciris         = "2.3.2"
    val refined       = "0.9.28"
    val http4sJwtAuth = "1.0.0"
    val fontAwesome   = "6.1.0"
    val scalaJsReact  = "2.0.1"
    val scalaCss      = "1.0.0"
    val reactJs       = "17.0.2"
    val redis4cats    = "1.1.1"
    val newtype       = "0.4.4"
    val derevo        = "0.13.0"

    val weaver = "0.7.11"
  }

  object Libraries {
    def circe(artifact: String): ModuleID = "io.circe" %% artifact % Versions.circe

    def skunk(artifact: String): ModuleID = "org.tpolecat" %% artifact % Versions.skunk

    def ciris(artifact: String): ModuleID = "is.cir" %% artifact % Versions.ciris

    def http4s(artifact: String): ModuleID = "org.http4s" %% artifact % Versions.http4s

    def refined(artifact: String): ModuleID = "eu.timepit" %% artifact % Versions.refined

    def derevo(artifact: String): ModuleID = "tf.tofu"    %% s"derevo-$artifact" % Versions.derevo

    val circeCore    = circe("circe-core")
    val circeGeneric = circe("circe-generic")
    val circeParser  = circe("circe-parser")
    val circeRefined = circe("circe-refined")

    val skunkCore    = skunk("skunk-core")
    val skunkCirce   = skunk("skunk-circe")
    val skunkRefined = skunk("refined")

    val cirisCore    = ciris("ciris")
    val cirisRefined = ciris("ciris-refined")

    val derevoCore  = derevo("core")
    val derevoCats  = derevo("cats")
    val derevoCirce = derevo("circe-magnolia")

    val http4sDsl    = http4s("http4s-dsl")
    val http4sCore   = http4s("http4s-core")
    val http4sServer = http4s("http4s-blaze-server")
    val http4sClient = http4s("http4s-blaze-client")
    val http4sCirce  = http4s("http4s-circe")

    val refinedType  = refined("refined")
    val refinedCats  = refined("refined-cats")

    val redis4catsEffects = "dev.profunktor" %% "redis4cats-effects" % Versions.redis4cats

    val http4sJwtAuth = "dev.profunktor" %% "http4s-jwt-auth" % Versions.http4sJwtAuth
    val cats       = "org.typelevel"      %% "cats-core"   % Versions.cats
    val catsEffect = "org.typelevel"      %% "cats-effect" % Versions.catsEffect
    val fs2        = "co.fs2"             %% "fs2-core"    % Versions.fs2
    val newtype  = "io.estatico"   %% "newtype"        % Versions.newtype

    val log4cats = "org.typelevel" %% "log4cats-slf4j"  % Versions.log4cats
    val logback  = "ch.qos.logback" % "logback-classic" % Versions.logback

    // Test
    val log4catsNoOp      = "org.typelevel"       %% "log4cats-noop"      % Versions.log4cats
    val refinedScalacheck = "eu.timepit"          %% "refined-scalacheck" % Versions.refined
    val weaverCats        = "com.disneystreaming" %% "weaver-cats"        % Versions.weaver
    val weaverDiscipline  = "com.disneystreaming" %% "weaver-discipline"  % Versions.weaver
    val weaverScalaCheck  = "com.disneystreaming" %% "weaver-scalacheck"  % Versions.weaver
  }

  val circeLibs = Seq(circeCore, circeGeneric, circeParser, circeRefined)

  val catsLibs = Seq(cats, catsEffect)

  val http4sLibs = Seq(http4sDsl, http4sCore, http4sServer, http4sClient, http4sCirce)

  val cirisLibs = Seq(cirisRefined, cirisCore)

  val logLibs = Seq(log4cats, logback)

  val skunkLibs = Seq(skunkCore, skunkCirce, skunkRefined)

  val derevoLibs = Seq(derevoCore, derevoCats, derevoCirce)

  val coreLibraries: Seq[ModuleID] =
    catsLibs ++ cirisLibs ++ circeLibs ++ skunkLibs ++ http4sLibs ++ logLibs ++ derevoLibs ++
      Seq(
        fs2,
        refinedType,
        redis4catsEffects,
        http4sJwtAuth,
        newtype
      )

  val testLibraries = Seq(
    log4catsNoOp,
    refinedScalacheck,
    weaverCats,
    weaverDiscipline,
    weaverScalaCheck
  )
}
