                  name := "oformlig"
          organization := "de.knutwalker"
             startYear := Some(2016)
           description := "Shapless Talk"
          scalaVersion := V.scala
           logBuffered := false
           shellPrompt := prompt
        scalacOptions ++= scalacFlags
  libraryDependencies ++= L.core
        scalacOptions  in (Compile, console) ~= (_ filterNot (x => x == "-Xfatal-warnings" || x.startsWith("-Ywarn")))
        scalacOptions  in (Test, console)    ~= (_ filterNot (x => x == "-Xfatal-warnings" || x.startsWith("-Ywarn")))
        scalacOptions  in Test               ~= (xs => xs.filterNot(x => x == "-Xfatal-warnings" || x.startsWith("-Ywarn")) :+ "-Yrangepos")
                  fork in run                := true
          connectInput in run                := true


lazy val scalacFlags = Seq(
  "-deprecation",
  "-explaintypes",
  "-feature",
  "-unchecked",
  "-encoding", "UTF-8",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Xlint:_",
  "-Xfuture",
  "-Xfatal-warnings",
  "-Yclosure-elim",
  "-Yconst-opt",
  "-Ydead-code",
  "-Yno-adapted-args",
  "-Ypatmat-exhaust-depth", "42",
  "-Ywarn-inaccessible",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Ywarn-dead-code",
  "-Ywarn-infer-any",
  "-Ywarn-adapted-args",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-target:jvm-1.8",
  "-Xexperimental",
  "-Ybackend:GenBCode",
  "-Ydelambdafy:method"
)

lazy val prompt: State => String = state => {
  import scala.Console._
  val extracted = Project.extract(state)
  val reader = extracted.get(_root_.com.typesafe.sbt.SbtGit.GitKeys.gitReader)
  val dir = extracted.get(baseDirectory)
  val name = extracted.get(Keys.name)
  val branch = reader.withGit(_.branch)
  s"[$CYAN$name$RESET] ($MAGENTA$branch$RESET)> "
}

enablePlugins(GitVersioning, GitBranchPrompt)
