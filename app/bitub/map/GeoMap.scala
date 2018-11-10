package bitub.map

import bitub.map.geometry.BoundingBox

trait GeoMap[A <: Arc[A], N <: Node] {
	def arcsWithin(bounds:BoundingBox): Stream[A]
	def nodesWithin(bounds:BoundingBox): Stream[N]
}
