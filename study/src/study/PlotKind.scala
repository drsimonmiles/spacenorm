package study

import java.io.File

enum PlotKind:
  case Prevalence, Neigbourhood, Diversity

  def extract(tick: TickStatistics): Double = 
    this match {
      case Prevalence   => tick.highestPrevalence
      case Neigbourhood => tick.neighbourhood
      case Diversity    => tick.diversity
    }

  def dataFileName(results: ResultsFile): String =
    this match {
      case Prevalence   => s"prevalence-${results.prefix}.csv"
      case Neigbourhood => s"neighbourhood-${results.prefix}.csv"
      case Diversity    => s"diversity-${results.prefix}.csv"
    }

  def plotFile(plotsFolder: File, comparison: ResultsComparison): File =
    this match {
      case Prevalence   => File(plotsFolder, s"prevalence-${comparison.prefix}.png")
      case Neigbourhood => File(plotsFolder, s"neighbourhood-${comparison.prefix}.png")
      case Diversity    => File(plotsFolder, s"diversity-${comparison.prefix}.png")
    }

  def yRange: String =
    this match {
      case Prevalence   => "[0.5:1.0]"
      case Neigbourhood => "[0.5:1.0]"
      case Diversity    => "[0.0:1.0]"
    }

  def yLabel: String =
    this match {
      case Prevalence   => "Highest norm prevalence"
      case Neigbourhood => "Neighbourhood correlation"
      case Diversity    => "Global diversity"
    }
