package bitub.map.geometry

object ParseOp {
	implicit class Strings(val s:String) {
		def parseDouble = try { Some(s.toDouble) } catch { case _ : Throwable => None }
		def parseInt = try { Some(s.toInt) } catch { case _ : Throwable => None }
	}
}

object Coordinate {
	def apply(c: Coordinate): Coordinate = Coordinate(c.x, c.y)

	def hLength(coordinate: Coordinate*): Seq[Double] = {
		var prev = coordinate.head
		for {
			next <- coordinate.tail
		} yield {
			val base = prev
			prev = next
			base hDistance next
		}
	}

	def length(coordinate: Coordinate*): Seq[Double] = {
		var prev = coordinate.head
		for {
			next <- coordinate.tail
		} yield {
			val base = prev
			prev = next
			base distance next
		}
	}
}

case class Coordinate(x:Double, y:Double) {

	def isUndefined: Boolean = x.isInfinite || y.isInfinite

	/**
	  * Euclidean distance
	  * @param c Some other coordinate
	  * @return A distance in planar projection CRS
	  */
	def distance(c:Coordinate): Double = {
		val dx = x - c.x
		val dy = y - c.y
		Math.sqrt(dx*dx + dy*dy)
	}

	/**
	  * Haversine distance
	  * @param c Some other coordinate
	  * @return A approximated distance at spherical CRS
	  */
	def hDistance(c:Coordinate): Double = {
		val rad : Seq[Double] = Seq(x,c.x,y,c.y).map(Math.toRadians).toSeq
		val havdlat = Math.sin((rad(3) - rad(2)) / 2.0)
		val havdlon = Math.sin((rad(1) - rad(0)) / 2.0)
		val dist = havdlat*havdlat + Math.cos(rad(2)) * Math.cos(rad(3)) * havdlon * havdlon
		// Some radius between WGS84 equatorial 6378137m and pole 6356752m
		2.0 * 6367000.0 * Math.asin(Math.sqrt(dist))
	}
}

trait Envelope {
	def center: Coordinate
	def isEmpty: Boolean
	def union(other:Envelope): Envelope
	def intersect(other:Envelope): Envelope
	def contains(c:Coordinate): Boolean
	def covers(c:Coordinate, tolerance: Double): Boolean
}

trait LinearGeometry {
	val EPSILON = 1e-8

	def apply(index: Int): Coordinate
	def size: Int
	def start: Coordinate
	def end: Coordinate
	def isClosed: Boolean
}

trait Location[G <: LinearGeometry] {
	def geometry: G
	def coordinate: Coordinate
	def position: Double
	def index: Int
	def offset: Double
	def tail: Seq[Location[G]]
}