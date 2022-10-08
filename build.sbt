// *****************************************************************************
// Build settings
// *****************************************************************************

inThisBuild(
  Seq(
    scalaVersion     := "2.13.9",
    organization     := "com.nachinius",
    organizationName := "nachinius",
    startYear        := Some(2022),
    licenses += ("MPL-2.0", url("https://www.mozilla.org/en-US/MPL/2.0/")),
    testFrameworks += new TestFramework("munit.Framework"),
    Test / parallelExecution := false,
    dynverSeparator          := "_", // the default `+` is not compatible with docker tags
    scalacOptions ++= Seq(
      "-deprecation",
//      "-explain-types",
      "-feature",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-unchecked",
      "-Xfatal-warnings" // Should be enabled if feasible.
//      "-Ykind-projector"
    ),
    scalafmtOnCompile := false,
    Compile / console / scalacOptions --= Seq("-Xfatal-warnings"),
    Test / console / scalacOptions --= Seq("-Xfatal-warnings"),
    Test / fork := true
  )
)

// *****************************************************************************
// Projects
// *****************************************************************************

lazy val jsonvalidatorservice =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .configs(IntegrationTest)
    .settings(
      name := "json-validator-service"
    )
    .settings(scalafmtSettings)
    .settings(
      Defaults.itSettings,
      headerSettings(IntegrationTest),
      inConfig(IntegrationTest)(scalafmtSettings),
      IntegrationTest / console / scalacOptions --= Seq("-Xfatal-warnings"),
      IntegrationTest / parallelExecution := false
    )
    .settings(
      libraryDependencies ++= Seq(
        library.catsCore,
        library.circeCore,
        library.circeGeneric,
        library.circeParser,
        library.doobieCore,
        library.doobieHikari,
        library.doobiePostgres,
        library.flywayCore,
        library.http4sCirce,
        library.http4sDsl,
        library.http4sEmberClient,
        library.http4sEmberServer,
        library.jsonSchemaValidator,
        library.logback,
        library.monixNewType,
        library.postgresql,
        library.pureConfig,
        library.sttpApiSpecCirceYaml,
        library.tapirCats,
        library.tapirCirce,
        library.tapirCore,
        library.tapirHttp4s,
        library.tapirMonixNewType,
        library.tapirOpenApiDocs,
        library.tapirSwaggerUi,
        library.munit           % IntegrationTest,
        library.munitCatsEffect % IntegrationTest,
        library.munitScalaCheck % IntegrationTest,
        library.scalaCheck      % IntegrationTest,
        library.munit           % Test,
        library.munitCatsEffect % Test,
        library.munitScalaCheck % Test,
        library.scalaCheck      % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val cats            = "2.8.0"
      val circe           = "0.14.3"
      val doobie          = "1.0.0-RC2"
      val flyway          = "9.4.0"
      val http4s          = "0.23.14"
      val jsonSchemaValidator = "2.2.14"
      val logback         = "1.2.11"
      val munit           = "0.7.29"
      val munitCatsEffect = "1.0.7"
      val postgresql      = "42.5.0"
      val pureConfig      = "0.17.1"
      val scalaCheck      = "1.15.4"
      val sttpApiSpec     = "0.2.1"
      val tapir           = "1.1.2"
      val monixNewType    = "0.2.3"
    }
    val catsCore             = "org.typelevel"                 %% "cats-core"           % Version.cats
    val circeCore            = "io.circe"                      %% "circe-core"          % Version.circe
    val circeGeneric         = "io.circe"                      %% "circe-generic"       % Version.circe
    val circeParser          = "io.circe"                      %% "circe-parser"        % Version.circe
    val doobieCore           = "org.tpolecat"                  %% "doobie-core"         % Version.doobie
    val doobieHikari         = "org.tpolecat"                  %% "doobie-hikari"       % Version.doobie
    val doobiePostgres       = "org.tpolecat"                  %% "doobie-postgres"     % Version.doobie
    val doobieScalaTest      = "org.tpolecat"                  %% "doobie-scalatest"    % Version.doobie
    val flywayCore           = "org.flywaydb"                   % "flyway-core"         % Version.flyway
    val http4sCirce          = "org.http4s"                    %% "http4s-circe"        % Version.http4s
    val http4sDsl            = "org.http4s"                    %% "http4s-dsl"          % Version.http4s
    val http4sEmberServer    = "org.http4s"                    %% "http4s-ember-server" % Version.http4s
    val http4sEmberClient    = "org.http4s"                    %% "http4s-ember-client" % Version.http4s
    val jsonSchemaValidator  = "com.github.java-json-tools"     % "json-schema-validator" % Version.jsonSchemaValidator
    val logback              = "ch.qos.logback"                 % "logback-classic"     % Version.logback
    val monixNewType         = "io.monix"                      %% "newtypes-core"       % Version.monixNewType
    val munit                = "org.scalameta"                 %% "munit"               % Version.munit
    val munitCatsEffect      = "org.typelevel"                 %% "munit-cats-effect-3" % Version.munitCatsEffect
    val munitScalaCheck      = "org.scalameta"                 %% "munit-scalacheck"    % Version.munit
    val postgresql           = "org.postgresql"                 % "postgresql"          % Version.postgresql
    val pureConfig           = "com.github.pureconfig"         %% "pureconfig-core"     % Version.pureConfig
    val scalaCheck           = "org.scalacheck"                %% "scalacheck"          % Version.scalaCheck
    val sttpApiSpecCirceYaml = "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml"  % Version.sttpApiSpec
    val tapirCats            = "com.softwaremill.sttp.tapir"   %% "tapir-cats"          % Version.tapir
    val tapirCirce           = "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"    % Version.tapir
    val tapirCore            = "com.softwaremill.sttp.tapir"   %% "tapir-core"          % Version.tapir
    val tapirHttp4s          = "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server" % Version.tapir
    val tapirMonixNewType = "com.softwaremill.sttp.tapir" %% "tapir-monix-newtype" % Version.tapir
    val tapirOpenApiDocs     = "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"  % Version.tapir
    val tapirSwaggerUi       = "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui"    % Version.tapir


  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := false
  )
