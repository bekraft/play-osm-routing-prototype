package bitub.map

import bitub.map.geometry.Coordinate

trait Node {
	type A <: Arc[A]

	def outs: Stream[A]
	def ins: Stream[A]
	def coordinate: Coordinate
}
