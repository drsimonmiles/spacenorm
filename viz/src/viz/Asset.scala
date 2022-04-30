package viz

import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.Document
import org.scalajs.dom.Event
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.HTMLImageElement
import viz.Images.*

enum AssetKey:
  case StrandMap, SaveIcon, LoadIcon

case class Asset (element: HTMLImageElement, source: String) {
  private var loaded = false
  element.src = source
  element.onload = (event: Event) => {
    loaded = true
  }

  def isLoaded: Boolean = loaded
}

class AssetsSet {
  private var assets:     Map[AssetKey, Asset]            = Map()
  private var withColour: Map[(AssetKey, String), Canvas] = Map()

  def add (key: AssetKey, document: Document, source: String): Unit =
    assets = assets + (key -> Asset (document.createElement("img").asInstanceOf[HTMLImageElement], source))

  def draw (key: AssetKey, point: Point, size: Option[Size], draw: CanvasRenderingContext2D): Unit =
    size match {
      case Some(sized) => get(key).foreach(draw.drawImage(_, point.x, point.y, sized.width, sized.height))
      case None        => get(key).foreach(draw.drawImage(_, point.x, point.y))
    }

  def drawColoured (key: AssetKey, colour: String, point: Point, size: Option[Size], draw: CanvasRenderingContext2D, document: Document): Unit =
    size match {
      case Some(sized) => 
        getColoured(key, colour, document).foreach(draw.drawImage(_, point.x, point.y, sized.width, sized.height))
      case None =>
        getColoured(key, colour, document).foreach(draw.drawImage(_, point.x, point.y))
    }

  def get (key: AssetKey): Option[HTMLImageElement] =
    assets.get (key).map (_.element)

  def getColoured (key: AssetKey, colour: String, document: Document): Option[Canvas] =
    withColour.get ((key, colour)) match {
      case Some(version) => Some(version)
      case None =>
        get(key).map { element => 
          val version = coloured(element, document, colour)
          withColour = withColour + ((key, colour) -> version)
          version
        }
    }

  def loaded =
    assets.values.forall (_.isLoaded)
}
