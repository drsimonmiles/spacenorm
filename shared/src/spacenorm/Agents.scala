package spacenorm

object Agents:
  opaque type Agent = Int

  private var nextID: Int = 0

  def nextAgent: Agent = {
    nextID += 1
    nextID
  }

  def nextAgents(count: Int): List[Agent] =
    List.fill(count)(nextAgent)

  def decodeAgent(code: String): Option[Agent] =
    Decode.decodeInt(code)

  def encodeAgent(agent: Agent): String =
    Encode.encodeInt(agent)
