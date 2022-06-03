package spacenorm

object Encode:
  def encodeConfiguration(config: Configuration): String = {
    import config.*
    encodeStructure(
      encodeAll(encodeInt, spaceWidth, spaceHeight, numberAgents, numberBehaviours, obstacleSide),
      encodeAll(encodeReal, threshold, maxMove),
      encodeList(encodePosition, obstacleTopLefts),
      encodeList(encodePosition, exits)
    )
  }

  def encodeState(state: State): String =
    encodeMap(encodeAgent, encode4Tuple(encodeBehaviour, encodePosition, encodePosition, encodeReal, ';'), collateAgents(state))

  def collateAgents(state: State): Map[Agent, (Behaviour, Position, Position, Double)] =
    state.agents.map(agent =>
      (agent, (state.behaviour(agent), state.position(agent), state.goal(agent), state.recentSuccess(agent)))
    ).toMap

  def encodeAgent(agent: Agent): String =
    Encode.encodeInt(agent.id)

  def encodeBehaviour(behaviour: Behaviour): String =
    Encode.encodeInt(behaviour.choice)

  def encodePosition(position: Position): String =
    Encode.encodePair(Encode.encodeInt)((position.x, position.y))

  def encodeAll[Item](encodeItem: Item => String, items: Item*): String =
    encodeList(encodeItem, items.toList)

  def encodeList[Item](encodeItem: Item => String, list: List[Item]): String =
    list.map(encodeItem).mkString(" ")

  def encodeMap[Key, Value](encodeKey: Key => String, encodeValue: Value => String, map: Map[Key, Value]): String =
    encodeList[(Key, Value)](entry => s"${encodeKey(entry._1)}:${encodeValue(entry._2)}", map.toList)

  def encodePair[Item](encodeItem: Item => String)(pair: (Item, Item)): String =
    s"${encodeItem(pair._1)},${encodeItem(pair._2)}"

  def encode4Tuple[Item1, Item2, Item3, Item4]
    (encode1: Item1 => String, encode2: Item2 => String, encode3: Item3 => String, encode4: Item4 => String, separator: Char)
    (items: (Item1, Item2, Item3, Item4)): String =
    s"${encode1(items._1)}$separator${encode2(items._2)}$separator${encode3(items._3)}$separator${encode4(items._4)}"

  def encodeStructure(parts: String*): String =
    parts.mkString("\n")

  val encodeInt: Int => String = _.toString
  val encodeReal: Double => String = real => f"$real%1.5f"
