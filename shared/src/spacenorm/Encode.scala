package spacenorm

import spacenorm.Agents.*
import spacenorm.Behaviours.*
import spacenorm.Configuration.*
import spacenorm.Positions.*

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
    encodeStructure(
      encodeList(encodeAgent, state.agents),
      encodeList(encodePair(encodeAgent), state.edges),
      encodeMap(encodeAgent, encodeBehaviour, state.behaviour),
      encodeMap(encodeAgent, encodePosition, state.position),
      encodeMap(encodeAgent, encodePosition, state.goal),
      encodeMap(encodeAgent, encodeReal, state.recentSuccess)
    )

  def encodeAll[Item](encodeItem: Item => String, items: Item*): String =
    encodeList(encodeItem, items.toList)

  def encodeList[Item](encodeItem: Item => String, list: List[Item]): String =
    list.map(encodeItem).mkString(" ")

  def encodeMap[Key, Value](encodeKey: Key => String, encodeValue: Value => String, map: Map[Key, Value]): String =
    encodeList[(Key, Value)](entry => s"${encodeKey(entry._1)}:${encodeValue(entry._2)}", map.toList)

  def encodePair[Item](encodeItem: Item => String)(pair: (Item, Item)): String =
    s"${encodeItem(pair._1)},${encodeItem(pair._2)}"

  def encodeStructure(parts: String*): String =
    parts.mkString("\n")

  val encodeInt: Int => String = _.toString
  val encodeReal: Double => String = real => f"$real%1.5f"
