# Spatial norms agent-based model

An abstract agent-based model, written in Scala 3 with visualisation using Scala.js, of how physical space design could
affect the emergence of expected behaviour.

## Running the model

You will need to first have installed
- The language: [Scala 3](https://www.scala-lang.org/download/)
- The build tool: [Mill](https://com-lihaoyi.github.io/mill/mill/Intro_to_Mill.html)

The code is divided into four packages:
- **sim** is the code for running the simulation
- **viz** is the code for visualising a run trace
- **spacenorm** is the code for the specific model, required for both simulation and visualisation (found in the **shared** directory)
- **study** is the code for analysing (plotting) aggregate simulation output

On running a simulation(s), a configuration file must be given. One is provided in the repository, **config.toml**.
It has comments on each option which should be read after understanding what the model does from the description below.
Run simulations by doing:

`mill sim.run config.toml`

One option you can control is how many full simulation traces are recorded in simulating. Once you have a trace, you can visualise it
with the (very simple) UI. You first compile the visualisation code by doing:

`mill viz.fastOpt`

You can then open **index.html** (in the project root) and click the Load button to load a trace file to play.

## Agent-based models

An agent-based model (ABM) is a simulation of something constructed from components, agents, which decide how to act 
independently from each other within some environment. In particular, ABM is valuable for modelling social phenomena 
where the agents represent people or animals, as these are naturally autonomous and heterogenous (have a diversity of 
characteristics and behaviour). ABM are also used to simulate the interactions of businesses, biological cells, and more.

In using an ABM, we are interested in what emerges from the interaction of agents and the approach’s strength lies in
simulating complex systems where the choices of distinct individuals can have large eventual effects meaning that
predictions purely based on trends in aggregate statistics about a population would be inaccurate.

## This model

Our impetus for this model was in thinking about how the structure of public spaces in cities, such as city squares or
pedestrianised areas, affects how people might start to expect to behave in those spaces. People can adopt behaviour both
because they see what others are doing and take it to be what is expected of themselves and because it is often practical
to act similarly to coordinate and so use spaces in a way that is safe and lets them achieve what they want to. For example,
in post-pandemic times, groups of friends or families might take cues from other groups when deciding how close to others they
can stand when looking at a cultural display, while cyclists and pedestrians need to decide who gives way when their paths
cross to avoid accidents. By seeing or hearing what others are doing, people adjust their expectations and behaviour.

Commonly the spread of behaviour and emergence of social norms (behaviour that becomes the prevalent way of doing things) has
been explored on social networks, e.g. how influencers might cause their followers to behave a particular way, spilling over
to their friends, and so on. Physical space can affect norm emergence too. Spatial aspects we were interested in exploring are
things such as distance affecting how far you can reasonably see or hear how others are acting, movement affecting who comes in
contact with who, obstacles such as buildings restricting both observation and movement, and entry/exit points affecting where
people with different behaviour and expectations may enter the space.

While the ultimate aim is to model particular spaces in particular cities, existing or planned, we first wanted to build an 
abstract model where we can adjust parameters and see the emergent effects without the limitation of trying to match reality.

**Time**.
The simulation progresses from one state to another by time steps called ticks (as a common convention), starting from tick 0.
In general, a tick may denote any real time scale, but most intuitively corresponds to something like a minute or a fraction of a
minute in this model.

**Space**.
Agents are placed at positions in a rectangular grid area, denoting the space in which they move and interact. Parts of the
space are taken up by obstacles, which could represent buildings or other structures, and an agent cannot occupy those positions.
There are entry/exit points at positions in the space. These are placed only at edge positions of the space, representing leaving
the space for other areas of a city for example, and at positions on the boundaries of obstacles, representing doors into buildings.

**Interaction**.
At any given time, each agent has a behaviour which is a choice from a set of possible behaviours. At the simulation
start, agents have random behaviours. Each tick, an agent can interact with other agents. The probability of one agent interacting
with another depends on the distance between them, with interaction being less likely at further distances and impossible beyond a
threshold distance. If interaction is based on sight (a choice by a model parameter), it is also only possible to interact if there
is no obstruction to the line of sight between the agents. Agent interaction represents observation of each others’ behaviours or
coordination between agents based on their behaviour within the space. We model this interaction as a coordination game, meaning
that when two agents interact they both 'win' if they had the same behaviour on interacting and both 'lose' if they had different behaviours.

**Diffusion**.
After everyone has interacted, agents will look at those neighbours they can observe (i.e. those they could potentially
have interacted with) and copy the behaviour of the agent with the highest proportion of wins. This represents agents adjusting their
behaviour based on what appears to be conventional or allow easiest coordination.

**Movement**.
Each agent moves around the space over time so changing who they can interact with. At any given time, an agent has a goal
position that they move towards each tick as directly as they can while navigating around obstacles and other agents. Once they reach
one goal, they choose a new goal. If an agent reaches an exit position, they leave the space. When one agent leaves, a new agent with
a random initial behaviour joins through a random exit position, thus keeping the population in the space constant.


