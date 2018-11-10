package bitub.map.geometry

object BoundingBox {
	import ParseOp._

	def apply(bounds: Double*): BoundingBox = {
		assert(bounds.size == 4)
		BoundingBox(bounds(0), bounds(1), bounds(2), bounds(3))
	}

	def of(minMax:String): Option[BoundingBox] = {
		val bb = minMax.split(",").map(_.parseDouble).filter(_.isDefined).map(_.get).toArray
		if(4 == bb.size) Some(BoundingBox(bb(0), bb(1), bb(2), bb(3))) else None
	}

	def empty() = {
		BoundingBox(Double.PositiveInfinity, Double.PositiveInfinity, Double.NegativeInfinity, Double.NegativeInfinity)
	}
}

case class BoundingBox(x1: Double, y1: Double, x2: Double, y2: Double) {
	def toArray = Array(x1,y1,x2,y2)
	def center = Coordinate((x1+x2)/2, (y1+y2)/2)
	def isEmpty: Boolean = x1 >= x2 || y1 >= y2
	def union(other:BoundingBox): BoundingBox = {
		BoundingBox(
			Math.min(x1,other.x1),
			Math.min(y1,other.y1),
			Math.max(x2,other.x2),
			Math.max(y2,other.y2)
		)
	}
	def intersect(other:BoundingBox): BoundingBox = ???
	def contains(c:Coordinate): Boolean = ???
	def covers(c:Coordinate, tolerance: Double): Boolean = ???
}