package bitub.map.osm

import bitub.map._
import bitub.map.geometry.{Coordinate, Polyline}

case class OsmWayId(id:Long, aligned:Boolean) extends LinkId {
	override def serialize: Seq[(String, Any)] = Seq("id" -> id, "aligned" -> aligned)
	override def opposed: Option[LinkId] = Some(OsmWayId(id, !aligned))
}

trait OsmCache {
	val nodeOnDemand : Long => OsmNode
	val wayOnDemand: Long => OsmWay
	def node(id:Long): OsmNode
	def ways(id:LinkId): OsmWay
}

case class OsmNode(id:Long, coordinate: Coordinate) extends Node {
	override type A = OsmArc
	override def outs: Stream[OsmArc] = ???
	override def ins: Stream[OsmArc] = ???
}

case class OsmWay(id:Long, props: PropertyMap, nodes:Seq[OsmNode]) extends Link {
	override def line: Polyline = ???
	override def names: Seq[String] = ???
	override def frc: Option[Int] = ???
	override def length: Double = ???
	override def navigable: Boolean = ???
	override def fow: FormOfWay = ???
	override def linkType: LinkType = ???
}

case class OsmArc(id:LinkId) extends Arc[OsmArc] {
	override type N = OsmNode
	override type L = OsmWay

	override def out: Option[OsmArc] = ???
	override def fan: OsmArc = ???
	override def peer: Option[OsmArc] = ???
	override def link: OsmWay = ???
	override def startNode: OsmNode = ???
	override def endNode: OsmNode = ???
}
