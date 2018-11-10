package bitub.map.geometry

trait DistanceMetrics {
	def distance(a:Coordinate, b:Coordinate): Double
	def lengths(c:Seq[Coordinate]): Seq[Double] = {
		var prev = c.head
		for {
			next <- c.tail
		} yield {
			val base = prev
			prev = next
			distance(base, next)
		}
	}
	def length(c:Seq[Coordinate]): Double = lengths(c).sum
}

object HaversineMetrics extends DistanceMetrics {
	override def distance(a: Coordinate, b: Coordinate): Double = a hDistance b
}

object EuclideanMetrics extends DistanceMetrics {
	override def distance(a: Coordinate, b: Coordinate): Double = a distance b
}