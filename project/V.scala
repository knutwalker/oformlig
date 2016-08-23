import sbt._

object V {
  val    caseApp = "1.1.0-RC3"
  val      circe = "0.5.0-M3"
  val  java8comp = "0.8.0-RC3"
  val    refined = "0.5.0"
  val  shapeless = "2.3.2"
  val      scala = "2.11.8"
  val  scalatest = "3.0.0"
  val validation = "0.2.0"
}

object L {
  val core = Seq(
    "com.github.alexarchambault" %% "case-app"           % V.caseApp                  ,
    "io.circe"                   %% "circe-generic"      % V.circe                    ,
    "eu.timepit"                 %% "refined"            % V.refined                  ,
    "com.chuusai"                %% "shapeless"          % V.shapeless                ,
    "org.scala-lang"              % "scala-compiler"     % V.scala       % "provided" ,
    "org.scala-lang.modules"     %% "scala-java8-compat" % V.java8comp                ,
    "org.scalatest"              %% "scalatest"          % V.scalatest   % "test"     ,
    "de.knutwalker"              %% "validation"         % V.validation
  )
}
