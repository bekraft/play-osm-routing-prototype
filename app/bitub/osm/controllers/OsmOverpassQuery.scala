package bitub.osm.controllers

import bitub.map.geometry.BoundingBox

trait OsmOverpassQuery {
	def bounds:BoundingBox
	def timeOutSeconds:Int

	def bodyQL: String
	def toQL: String =
		s"""
		   |[out:json][timeout:$timeOutSeconds];
		   |(${bodyQL});
		   |out body;
		 """.stripMargin
}

object OsmOverpassQuery {
	def highwayNode(bounds:BoundingBox, timeOutSecs:Int = 30): OsmOverpassQuery = {
		OsmCompoundQuery(
			OsmHighwayQuery(bounds,timeOutSecs),
			new OsmStripDownQuery
		)
	}
}

case class OsmCompoundQuery(queries:OsmOverpassQuery*) extends OsmOverpassQuery {
	override def bounds: BoundingBox = queries.map(_.bounds).reduce(_ union _)
	override def timeOutSeconds: Int = queries.map(_.timeOutSeconds).max

	override def bodyQL: String = queries.map(_.bodyQL).mkString
}

case class OsmNodeQuery(bounds:BoundingBox,
						timeOutSeconds: Int = 30) extends OsmOverpassQuery {

	override def bodyQL: String = s"node(${bounds.toArray.map(_.toString).mkString(",")});"
}

sealed class OsmStripDownQuery extends OsmOverpassQuery {
	override def bounds: BoundingBox = BoundingBox.empty()
	override def timeOutSeconds: Int = Int.MinValue
	override def bodyQL: String = ">;"
}

case class OsmHighwayQuery(bounds:BoundingBox,
						   timeOutSeconds: Int = 30) extends OsmOverpassQuery {

	override def bodyQL: String = s"""way["highway"](${bounds.toArray.map(_.toString).mkString(",")});"""
}
