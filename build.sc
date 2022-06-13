import mill._
import mill.api.Loose
import mill.define.Target
import mill.scalajslib.ScalaJSModule
import mill.scalalib._
import mill.scalalib.api.CompilationResult

val projectScalaVersion = "3.1.2"

/* The build module to run the simulation given a configuration file */
object sim extends ScalaModule {
  def scalaVersion = projectScalaVersion

  def sharedSources = T.sources { os.pwd / "shared" / "src" }
  override def sources = T.sources  { super.sources() ++ sharedSources() }

  object test extends Tests with TestModule.Munit {
    def ivyDeps = Agg(ivy"org.scalameta::munit::0.7.29")
  }
}

/* The build module to compile the JavaScript trace visualiser interface */
object viz extends ScalaJSModule {
  def scalaVersion = projectScalaVersion
  def scalaJSVersion = "1.10.0"

  def sharedSources = T.sources { os.pwd / "shared" / "src" }
  override def sources = T.sources  { super.sources() ++ sharedSources() }

  def ivyDeps: Target[Loose.Agg[Dep]] = super.ivyDeps() ++ Agg(
    ivy"org.scala-js::scalajs-dom::1.1.0".withDottyCompat(projectScalaVersion),
  )
}

/* The build module to analyse the statistics produced from running simulations */
object study extends ScalaModule {
  def scalaVersion = projectScalaVersion

  def sharedSources = T.sources { os.pwd / "shared" / "src" }
  def simSources = T.sources { os.pwd / "sim" / "src" }
  override def sources = T.sources  { super.sources() ++ sharedSources() ++ simSources() }

  def ivyDeps: Target[Loose.Agg[Dep]] = super.ivyDeps() ++ Agg(
    ivy"org.jfree:jfreechart:1.5.3",
  )

  def mainClass = Some("study.analyse")
}
