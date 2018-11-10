package bitub.map.geometry

object Polyline {
	def apply(dm: DistanceMetrics, coordinates: Coordinate*): Polyline = {
		val line = Polyline(dm)
		line.segment = line.mapLocation(coordinates)
		line
	}

	def apply(coordinates: Coordinate*): Polyline = apply(HaversineMetrics, coordinates: _*)
}

case class Polyline(dm: DistanceMetrics) extends LinearGeometry {

	private var segment: Seq[PolylineLocation] = Seq()

	case class PolylineLocation(coordinate: Coordinate, index: Int, position: Double, offset: Double) extends Location[Polyline] {
		override def geometry: Polyline = Polyline.this
		override def tail: Seq[Location[Polyline]] = segment.drop(index + 1)
	}

	private object LinearProjection {
		def apply(p: Coordinate, index: Int): LinearProjection = {
			val s = clamped(index)
			val e = clamped(index + 1)

			// Vector se = s -> e
			val dx = e.coordinate.x - s.coordinate.x
			val dy = e.coordinate.y - s.coordinate.y
			// Vector sp = s -> scale
			val psx = p.x - s.coordinate.x
			val psy = p.y - s.coordinate.y

			val pow2 = dx * dx + dy * dy
			val length = Math.sqrt(pow2)

			var offset: Double = 0
			var scale: Double = 0
			if (length < EPSILON) { // Catch numerical problems
				offset = s.coordinate.distance(p)
			}
			else { // Otherwise do dot product s->e . s->p
				scale = (dx * psx + dy * psy) / pow2
				offset = Math.abs(dy * psx + (-dx) * psy) / length
			}
			if (0 >= scale || 1 <= scale) {
				offset = Math.min(s.coordinate.distance(p), e.coordinate.distance(p))
			}
			LinearProjection(p, s.index, dx, dy, scale, offset, length)
		}
	}

	private case class LinearProjection(p: Coordinate, index: Int, dx: Double, dy: Double, scale: Double, offset: Double, length: Double) {
		lazy val segmentPosition: Double = (clamped(index + 1).position - segment(index).position) * scale

		def polyline: Polyline = Polyline.this

		def position: Double = {
			val location = clamped(index)
			if (scale < 0) {
				location.position
			} else if (scale > 1) {
				val nxtLocation = clamped(index + 1)
				nxtLocation.position
			} else {
				location.position + segmentPosition
			}
		}

		def projectedCoordinate: Coordinate = {
			val s: PolylineLocation = clamped(index)
			if (0 >= scale) { // If before start (scale less than or equal to zero)
				s.coordinate
			} else if (1 <= scale) { // If beyond end (scale greater or equal than one)
				clamped(index + 1).coordinate
			} else { // Otherwise somewhere in between
				new Coordinate(s.coordinate.x + dx * scale, s.coordinate.y + dy * scale)
			}
		}

		def location: PolylineLocation = {
			PolylineLocation(p, index, position, dm.distance(p, projectedCoordinate))
		}
	}

	override def apply(index: Int): Coordinate = segment(index).coordinate

	override def size: Int = segment.size

	override def start: Coordinate = segment.head.coordinate

	override def end: Coordinate = segment.last.coordinate

	override def isClosed: Boolean = segment.head.coordinate.distance(segment.last.coordinate) < EPSILON

	def coordinates: Seq[Coordinate] = segment.map(_.coordinate)

	def hLength: Double = Coordinate.hLength(segment.map(_.coordinate): _ *).sum

	def length: Double = segment.last.position

	def nearest(position: Double): PolylineLocation = {
		val index = find(position)
		if (0 > index)
			segment.head
		else {
			val predecessor = segment(index)
			val successor = clamped(index + 1)
			if ((position - predecessor.position) <= (successor.position - position))
				predecessor
			else
				successor
		}
	}

	def projection(c: Coordinate,
				   lengthAlongRouteThreshold: Double,
				   startLocation: PolylineLocation,
				   offsetThreshold: Double = Double.PositiveInfinity): PolylineLocation = {
		assert(startLocation.geometry == this)
		projectSequential(c, startLocation.index, lengthAlongRouteThreshold, offsetThreshold).map(_.location).orNull
	}

	def projection(c: Coordinate): PolylineLocation = projection(c, segment.head)

	def projection(c: Coordinate, startLocation: PolylineLocation): PolylineLocation = {
		assert(startLocation.geometry == this)
		segment.drop(startLocation.index - 1)
				.par
				.map(s => LinearProjection(c, s.index))
				.filter(_.position >= startLocation.position)
				.minBy(_.offset).location
	}

	private def clamped(index: Int): PolylineLocation = {
		segment(if (0 > index) 0 else if (segment.size <= index) segment.size - 1 else index)
	}

	private def find(position: Double): Int = {
		// Pre check boundaries
		if (segment.head.position > position)
			return -1
		else if (segment.last.position < position)
			return segment.size
		else if (0 == position)
			return 0
		else if (segment.last.position == position)
			return segment.size - 1

		// Otherwise use binary search
		var s = 0
		var e = segment.size
		while (e - s > 1) {
			// As long there start and end index are not directly adjacent
			val m = (s + e) / 2
			if (segment(m).position < position)
				s = m
			else if (segment(m).position > position)
				e = m
			else
			// If found, return exact index
				return m
		}
		// By default current predecessor
		s
	}

	private def projectSequential(c: Coordinate,
								  startIndex: Int,
								  lengthAlongRouteThreshold: Double,
								  offsetThreshold: Double): Option[LinearProjection] = {
		var minLs: Option[LinearProjection] = None
		var index: Int = Math.max(0, startIndex)
		var totalLength: Double = .0
		do {
			// Compute next linear projection
			val ls = LinearProjection(c, index)
			totalLength += ls.length

			// Check offset for minimum
			if (minLs.forall(_.offset > ls.offset))
				// Find the nearest reference
				minLs = Some(ls)

			index += 1
			// As long as we don't leave sequence range, don't hold the offset threshold or reach the estimated length
		} while (index < segment.size
				&& (minLs.forall(_.offset > offsetThreshold) || totalLength < lengthAlongRouteThreshold))

		minLs
	}

	def head(endLocation: PolylineLocation): Polyline = {
		assert(endLocation.geometry == this)
		val newPolyline = Polyline(dm)
		newPolyline.segment = newPolyline.mapLocation(segment.take(endLocation.index + 1).map(_.coordinate))
		newPolyline
	}

	def tail(startLocation: PolylineLocation): Polyline = {
		assert(startLocation.geometry == this)
		val newPolyline = Polyline(dm)
		newPolyline.segment = newPolyline.mapLocation(segment.drop(startLocation.index).map(_.coordinate))
		newPolyline
	}

	private def mapLocationTail(coordinate: Seq[Coordinate]): Seq[PolylineLocation] = {
		var position: Double = 0
		var index: Int = 0
		for {
			l <- dm.lengths(coordinate)
		} yield {
			position += l
			index += 1
			PolylineLocation(coordinate(index), index, position, 0)
		}
	}

	private def mapLocation(coordinate: Seq[Coordinate]): Seq[PolylineLocation] = {
		Seq(PolylineLocation(coordinate.head, 0, 0, 0)) ++ mapLocationTail(coordinate)
	}
}