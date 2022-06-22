package spacenorm

/** A choice of behaviour by an agent, with a distinguishing identifier. */
final case class Behaviour(choice: Int) extends AnyVal

object Behaviour:
  // A behaviour that an agent will never have, used as something that we know will not be equal to any actual behaviour
  val impossibleBehaviour = Behaviour(-1)