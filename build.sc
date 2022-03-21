import mill._, scalalib._

object sim extends ScalaModule {
  def scalaVersion = "3.1.1"

  def additionalSources1 = T.sources { os.pwd / "shared" / "src" }
  override def sources = T.sources  { super.sources() ++ additionalSources1() }
}