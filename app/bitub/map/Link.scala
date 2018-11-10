package bitub.map

import bitub.map.geometry.Polyline

object LinkType {
	val ROAD = LinkType("Road")
	val RAILWAY = LinkType("Railway")
	val RAILWAY_FERRY = LinkType("Railway Ferry")
	val FERRY = LinkType("Offshore Ferry")
	val NONTRAFFIC = LinkType("Non traffic")
}

case class LinkType(name:String) extends AnyVal

trait Link {
	def line: Polyline
	def names : Seq[String]
	def frc: Option[Int]
	def length: Double
	def navigable: Boolean
	def fow: FormOfWay
	def linkType: LinkType
}
