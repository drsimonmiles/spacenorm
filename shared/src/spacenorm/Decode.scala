package spacenorm

object Decode:
  def decodeSchemaVersion(code: String): Option[String] =
    Some(code)

  def decodeConfiguration(code: String): Option[Configuration] =
    decodeStructure(
      decodeList5Tuple(decodeInt), decodeList2Tuple(decodeReal), decodeInfluence, decodeNetworker, decodeTransmission, decodeList(decodePosition), decodeList(decodePosition), {
        case ((spaceWidth, spaceHeight, numberAgents, numberBehaviours, obstacleSide),
              (threshold, maxMove), distanceInfluence, netConstruction, transmission, obstacleTopLefts, exits) =>
          Configuration(spaceWidth, spaceHeight, numberAgents, numberBehaviours, obstacleSide, threshold, distanceInfluence, netConstruction, transmission, maxMove, obstacleTopLefts, exits)
    })(code)

  def decodeState(code: String, config: Configuration): Option[State] =
    decodeMap(decodeAgent, decode4Tuple(decodeBehaviour, decodePosition, decodePosition, decodeReal, ';'))(code).map {
      agentStates =>
        val agents        = agentStates.keys.toList
        val behaviour     = agents.map(agent => (agent, agentStates(agent)._1)).toMap
        val position      = agents.map(agent => (agent, agentStates(agent)._2)).toMap
        val goal          = agents.map(agent => (agent, agentStates(agent)._3)).toMap
        val recentSuccess = agents.map(agent => (agent, agentStates(agent)._4)).toMap
        State(config, agents, behaviour, position, goal, None, recentSuccess)
    }

  def decodeAgent(code: String): Option[Agent] =
    Decode.decodeInt(code).map(Agent.apply)

  def decodeBehaviour(code: String): Option[Behaviour] =
    Decode.decodeInt(code).map(Behaviour.apply)

  def decodeInfluence(code: String): Option[Influence] = 
    Some(Influence.valueOf(code))

  def decodeNetworker(code: String): Option[Networker] = 
    Some(Networker.valueOf(code))

  def decodePosition(code: String): Option[Position] =
    (Decode.decodePair(Decode.decodeInt)(code)).map(xy => Position(xy._1, xy._2))

  def decodeTransmission(code: String): Option[Transmission] = 
    Some(Transmission.valueOf(code))

  def decodeList[Item](decodeItem: String => Option[Item], required: Option[Int] = None)(code: String): Option[List[Item]] =
    failOnAnyFail(code.split(" ").toList.map(decodeItem)).filterNot(list => required.exists(_ != list.size))

  def decodeMap[Key, Value](decodeKey: String => Option[Key], decodeValue: String => Option[Value])(code: String): Option[Map[Key, Value]] =
    decodeList(decode2Tuple(decodeKey, decodeValue, ':'))(code).map(_.toMap)

  def decodePair[Item](decodeItem: String => Option[Item])(code: String): Option[(Item, Item)] =
    decode2Tuple(decodeItem, decodeItem, ',')(code)

  def decode2Tuple[Item1, Item2](decodeItem1: String => Option[Item1],
                                 decodeItem2: String => Option[Item2],
                                 separator: Char)
                                 (code: String): Option[(Item1, Item2)] = {
    val parts = code.split(separator)
    if (parts.size == 2)
      for (item1 <- decodeItem1(parts(0));
           item2 <- decodeItem2(parts(1)))
        yield (item1, item2)
    else None
  }

  def decode4Tuple[Item1, Item2, Item3, Item4](decodeItem1: String => Option[Item1],
                                               decodeItem2: String => Option[Item2],
                                               decodeItem3: String => Option[Item3],
                                               decodeItem4: String => Option[Item4],
                                               separator: Char)
                                               (code: String): Option[(Item1, Item2, Item3, Item4)] = {
    val parts = code.split(separator)
    if (parts.size == 4)
      for (item1 <- decodeItem1(parts(0));
           item2 <- decodeItem2(parts(1));
           item3 <- decodeItem3(parts(2));
           item4 <- decodeItem4(parts(3)))
        yield (item1, item2, item3, item4)
    else None
  }

  def decode5Tuple[Item1, Item2, Item3, Item4, Item5]
    (decodeItem1: String => Option[Item1],
     decodeItem2: String => Option[Item2],
     decodeItem3: String => Option[Item3],
     decodeItem4: String => Option[Item4],
     decodeItem5: String => Option[Item5],
     separator: Char)
    (code: String): Option[(Item1, Item2, Item3, Item4, Item5)] = {

    val parts = code.split(separator)
    if (parts.size == 5)
      for (item1 <- decodeItem1(parts(0));
           item2 <- decodeItem2(parts(1));
           item3 <- decodeItem3(parts(2));
           item4 <- decodeItem4(parts(3));
           item5 <- decodeItem5(parts(4)))
        yield (item1, item2, item3, item4, item5)
    else None
  }

  def decode6Tuple[Item1, Item2, Item3, Item4, Item5, Item6]
    (decodeItem1: String => Option[Item1],
     decodeItem2: String => Option[Item2],
     decodeItem3: String => Option[Item3],
     decodeItem4: String => Option[Item4],
     decodeItem5: String => Option[Item5],
     decodeItem6: String => Option[Item6],
     separator: Char)
    (code: String): Option[(Item1, Item2, Item3, Item4, Item5, Item6)] = {

    val parts = code.split(separator)
    if (parts.size == 6)
      for (item1 <- decodeItem1(parts(0));
           item2 <- decodeItem2(parts(1));
           item3 <- decodeItem3(parts(2));
           item4 <- decodeItem4(parts(3));
           item5 <- decodeItem5(parts(4));
           item6 <- decodeItem6(parts(5)))
        yield (item1, item2, item3, item4, item5, item6)
    else None
  }

  def decode7Tuple[Item1, Item2, Item3, Item4, Item5, Item6, Item7]
    (decodeItem1: String => Option[Item1],
     decodeItem2: String => Option[Item2],
     decodeItem3: String => Option[Item3],
     decodeItem4: String => Option[Item4],
     decodeItem5: String => Option[Item5],
     decodeItem6: String => Option[Item6],
     decodeItem7: String => Option[Item7],
     separator: Char)
    (code: String): Option[(Item1, Item2, Item3, Item4, Item5, Item6, Item7)] = {

    val parts = code.split(separator)
    if (parts.size == 7)
      for (item1 <- decodeItem1(parts(0));
           item2 <- decodeItem2(parts(1));
           item3 <- decodeItem3(parts(2));
           item4 <- decodeItem4(parts(3));
           item5 <- decodeItem5(parts(4));
           item6 <- decodeItem6(parts(5));
           item7 <- decodeItem7(parts(6)))
        yield (item1, item2, item3, item4, item5, item6, item7)
    else None
  }

  def decodeStructure[Item1, Item2, Whole]
    (decodeItem1: String => Option[Item1],
     decodeItem2: String => Option[Item2],
     construct: ((Item1, Item2)) => Whole)
    (code: String): Option[Whole] =
    decode2Tuple(decodeItem1, decodeItem2, '\n')(code).map(construct)

  def decodeStructure[Item1, Item2, Item3, Item4, Whole]
    (decodeItem1: String => Option[Item1],
     decodeItem2: String => Option[Item2],
     decodeItem3: String => Option[Item3],
     decodeItem4: String => Option[Item4],
     construct: ((Item1, Item2, Item3, Item4)) => Whole)
    (code: String): Option[Whole] =
    decode4Tuple(decodeItem1, decodeItem2, decodeItem3, decodeItem4, '\n')(code).map(construct)

  def decodeStructure[Item1, Item2, Item3, Item4, Item5, Whole]
    (decodeItem1: String => Option[Item1],
     decodeItem2: String => Option[Item2],
     decodeItem3: String => Option[Item3],
     decodeItem4: String => Option[Item4],
     decodeItem5: String => Option[Item5],
     construct: ((Item1, Item2, Item3, Item4, Item5)) => Whole)
    (code: String): Option[Whole] =
    decode5Tuple(decodeItem1, decodeItem2, decodeItem3, decodeItem4, decodeItem5, '\n')(code).map(construct)

  def decodeStructure[Item1, Item2, Item3, Item4, Item5, Item6, Whole]
    (decodeItem1: String => Option[Item1],
     decodeItem2: String => Option[Item2],
     decodeItem3: String => Option[Item3],
     decodeItem4: String => Option[Item4],
     decodeItem5: String => Option[Item5],
     decodeItem6: String => Option[Item6],
     construct: ((Item1, Item2, Item3, Item4, Item5, Item6)) => Whole)
    (code: String): Option[Whole] =
    decode6Tuple(decodeItem1, decodeItem2, decodeItem3, decodeItem4, decodeItem5, decodeItem6, '\n')(code).map(construct)

  def decodeStructure[Item1, Item2, Item3, Item4, Item5, Item6, Item7, Whole]
    (decodeItem1: String => Option[Item1],
     decodeItem2: String => Option[Item2],
     decodeItem3: String => Option[Item3],
     decodeItem4: String => Option[Item4],
     decodeItem5: String => Option[Item5],
     decodeItem6: String => Option[Item6],
     decodeItem7: String => Option[Item7],
     construct: ((Item1, Item2, Item3, Item4, Item5, Item6, Item7)) => Whole)
    (code: String): Option[Whole] =
    decode7Tuple(decodeItem1, decodeItem2, decodeItem3, decodeItem4, decodeItem5, decodeItem6, decodeItem7, '\n')(code).map(construct)

  def decodeList2Tuple[Item](decodeItem: String => Option[Item])(code: String): Option[(Item, Item)] =
    decodeList(decodeItem, Some(2))(code).map(list => (list(0), list(1)))

  def decodeList5Tuple[Item](decodeItem: String => Option[Item])(code: String): Option[(Item, Item, Item, Item, Item)] =
    decodeList(decodeItem, Some(5))(code).map(list => (list(0), list(1), list(2), list(3), list(4)))

  val decodeInt: String => Option[Int] = _.toIntOption
  val decodeReal: String => Option[Double] = _.toDoubleOption

  def failOnAnyFail[Item](optionsList: List[Option[Item]]): Option[List[Item]] =
    optionsList.foldLeft[Option[List[Item]]](Some(Nil)) {
      (sofar, next) => next.flatMap(item => sofar.map(list => item :: list))
    }.map(_.reverse)
