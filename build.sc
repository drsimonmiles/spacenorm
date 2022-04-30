import mill._
import mill.api.Loose
import mill.define.Target
import mill.scalajslib.ScalaJSModule
import mill.scalalib._
import mill.scalalib.api.CompilationResult

val projectScalaVersion = "3.1.2"

object sim extends ScalaModule {
  def scalaVersion = projectScalaVersion

  def sharedSources = T.sources { os.pwd / "shared" / "src" }
  override def sources = T.sources  { super.sources() ++ sharedSources() }
}

object viz extends ScalaJSModule {
  def scalaVersion = projectScalaVersion
  def scalaJSVersion = "1.10.0"

  def sharedSources = T.sources { os.pwd / "shared" / "src" }
  override def sources = T.sources  { super.sources() ++ sharedSources() }

  def ivyDeps: Target[Loose.Agg[Dep]] = super.ivyDeps() ++ Agg(
    ivy"org.scala-js::scalajs-dom::1.1.0".withDottyCompat(projectScalaVersion),
    ivy"com.lihaoyi::scalatags:0.11.1".withDottyCompat(projectScalaVersion),
  )
}
