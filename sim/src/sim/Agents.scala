package sim

object Agents:
  opaque type Agent = Int

  private var nextID: Int = 0

  def nextAgent: Agent = {
    nextID += 1
    nextID
  }

  def nextAgents(count: Int): List[Agent] =
    List.fill(count)(nextAgent)