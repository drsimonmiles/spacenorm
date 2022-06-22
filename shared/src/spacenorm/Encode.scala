package spacenorm

/** Functionality to encode to Strings simulation traces for visulisation, specifically the static configuration and
 * each tick's state.
 */
object Encode:
  def encodeSchemaVersion = "1.2"

  def encodeConfiguration(config: Configuration): String = {
    import config.*
    encodeStructure(
      encodeAll(encodeInt, spaceWidth, spaceHeight, numberAgents, numberBehaviours, obstacleSide),
      encodeAll(encodeReal, distanceThreshold, linearThreshold, maxMove),
      encode4Tuple(encodeInfluence, encodeDiffusion, encodeNetworker, encodeTransmission, ' ')(distanceInfluence, diffusion, netConstruction, transmission),
      encodeList(encodePosition, obstacleTopLefts),
      encodeList(encodePosition, exits),
      encodeOption(encodeMapToList[Agent, Agent](encodeAgent, encodeAgent, _), network)
    )
  }

  def encodeState(state: State): String =
    encodeMap(encodeAgent, encode3Tuple(encodeBehaviour, encodePosition, encodePosition, ';'), collateAgents(state))

  def collateAgents(state: State): Map[Agent, (Behaviour, Position, Position)] =
    state.agents.map(agent =>
      (agent, (state.behaviour(agent), state.position(agent), state.goal(agent)))
    ).toMap

  def encodeAgent(agent: Agent): String =
    Encode.encodeInt(agent.id)

  def encodeBehaviour(behaviour: Behaviour): String =
    Encode.encodeInt(behaviour.choice)

  def encodeDiffusion(diffusion: Diffusion): String =
    diffusion.toString

  def encodeInfluence(influence: Influence): String =
    influence.toString

  def encodeNetworker(netConstruction: Networker): String =
    netConstruction.toString

  def encodeTransmission(transmission: Transmission): String =
    transmission.toString

  def encodePosition(position: Position): String =
    Encode.encodePair(Encode.encodeInt)((position.x, position.y))

  def encodeAll[Item](encodeItem: Item => String, items: Item*): String =
    encodeList(encodeItem, items.toList)

  def encodeList[Item](encodeItem: Item => String, list: List[Item], separator: String = " "): String =
    list.map(encodeItem).mkString(separator)

  def encodeMap[Key, Value](encodeKey: Key => String, encodeValue: Value => String, map: Map[Key, Value]): String =
    encodeList[(Key, Value)](entry => s"${encodeKey(entry._1)}:${encodeValue(entry._2)}", map.toList, "~")

  def encodeMapToList[Key, Item](encodeKey: Key => String, encodeItem: Item => String, map: Map[Key, List[Item]]): String =
    encodeMap(encodeKey, encodeList[Item](encodeItem, _), map)

  def encodeOption[Item](encodeItem: Item => String, item: Option[Item]): String =
    item.map(encodeItem).getOrElse("X")

  def encodePair[Item](encodeItem: Item => String)(pair: (Item, Item)): String =
    s"${encodeItem(pair._1)},${encodeItem(pair._2)}"

  def encode3Tuple[Item1, Item2, Item3]
    (encode1: Item1 => String, encode2: Item2 => String, encode3: Item3 => String, separator: Char)
    (items: (Item1, Item2, Item3)): String =
    s"${encode1(items._1)}$separator${encode2(items._2)}$separator${encode3(items._3)}"

  def encode4Tuple[Item1, Item2, Item3, Item4]
    (encode1: Item1 => String, encode2: Item2 => String, encode3: Item3 => String, encode4: Item4 => String, separator: Char)
    (items: (Item1, Item2, Item3, Item4)): String =
    s"${encode1(items._1)}$separator${encode2(items._2)}$separator${encode3(items._3)}$separator${encode4(items._4)}"

  def encodeStructure(parts: String*): String =
    parts.mkString("\n")

  val encodeInt: Int => String = _.toString
  val encodeReal: Double => String = real => f"$real%1.5f"
