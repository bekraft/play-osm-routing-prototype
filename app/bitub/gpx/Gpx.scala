package bitub.gpx

import java.io.InputStream

import better.files._
import better.files.Dsl.SymbolicOperations
import bitub.geojson.{Feature, FeatureCollection, LineString}
import bitub.map.PropertyValue
import bitub.map.geometry.{Coordinate, Polyline}

import scala.xml.XML

object Gpx {
	def apply(file:File): Gpx = Gpx(file.newInputStream)
	def apply(fileName:String): Gpx = Gpx(fileName.toFile.newInputStream)
}

case class Gpx(xmlStream:InputStream) {
	private val contents = XML.load(xmlStream)

	lazy val name : String = (contents \\ "rte" \\ "name") text

	lazy val trackPoints : Seq[Coordinate] = {
		(contents \\ "trk" \\ "trkseg" \\ "trkpt" ) map {
			n => Coordinate((n \ "@lon").text.toFloat, (n \ "@lat").text.toFloat)
		}
	}

	def toGeoJson: FeatureCollection = FeatureCollection(
		List(Feature("0", Map("name" -> PropertyValue(name)), LineString(Polyline(trackPoints: _*))))
	)

}
