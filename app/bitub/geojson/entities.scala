package bitub.geojson

import bitub.map.{PropertyMap, PropertyValue}
import bitub.map.geometry.{BoundingBox, Coordinate, Polyline}

sealed trait Geometry

case class Point(coordinate: Coordinate) extends Geometry

case class LineString(polyline: Polyline) extends Geometry

case class Polygon(outerRing: Polyline, innerRings: Polyline*) extends Geometry

case class FeatureCollection(features:List[Feature], bbox: Option[BoundingBox] = None)

object Feature {
	def apply(id:String, props:PropertyMap, geometry: Geometry): Feature = Feature(id).withProps(props).withGeometry(geometry)
}

case class Feature(id: String) {
	var properties: PropertyMap = Map()
	var geometry: Option[Geometry] = None

	def withGeometry(g:Geometry): this.type = {
		geometry = Option(g)
		this
	}

	def withProps(props: PropertyMap): this.type = {
		properties = properties ++ props
		this
	}

	def withProps(props: (String, PropertyValue)*): this.type = {
		properties = properties ++ props
		this
	}

	def withProp(key: String, value: PropertyValue): this.type = {
		properties = properties + (key -> value)
		this
	}
}
