                  name  := "oformlig"
           description  := "Shapless Talk"
           logBuffered  := false
           shellPrompt  := prompt
          scalaVersion  := V.scala
          organization  := "de.knutwalker"
         scalacOptions  += "-Yliteral-types"
         scalacOptions ++= scalacFlags
       initialCommands  := "import demo._"
     scalaOrganization  := "org.typelevel"
   libraryDependencies ++= L.core


lazy val scalacFlags = Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-encoding", "UTF-8",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Xlint:_",
  "-Xfuture",
  "-Yclosure-elim",
  "-Yconst-opt",
  "-Ydead-code",
  "-Yno-adapted-args",
  "-Ypatmat-exhaust-depth", "42",
  "-Ywarn-infer-any",
  "-Ywarn-adapted-args",
  "-target:jvm-1.8",
  "-Xexperimental",
  "-Ybackend:GenBCode",
  "-Ydelambdafy:method"
)

lazy val prompt: State => String = state => {
  import scala.Console._
  val extracted = Project.extract(state)
  val reader = extracted.get(_root_.com.typesafe.sbt.SbtGit.GitKeys.gitReader)
  val name = extracted.get(Keys.name)
  val branch = reader.withGit(_.branch)
  s"[$CYAN$name$RESET] ($MAGENTA$branch$RESET)> "
}

enablePlugins(GitVersioning, GitBranchPrompt)
