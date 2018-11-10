package bitub.map.osm

import bitub.map.GeoMap
import bitub.map.geometry.BoundingBox
import bitub.osm.controllers.OsmMapClient

case class OsmMapProvider(client: OsmMapClient) extends GeoMap[OsmArc, OsmNode] {

	override def arcsWithin(bounds: BoundingBox): Stream[OsmArc] = ???

	override def nodesWithin(bounds: BoundingBox): Stream[OsmNode] = ???
}
