package bitub.map.geometry

import bitub.Testbase

class PolylineTests extends Testbase {
	// Some test polyline
	val coordinates = Seq(Coordinate(12.5, 52.5), Coordinate(12.65,	52.55), Coordinate(12.7, 52.5))
	val polyline = Polyline(HaversineMetrics, coordinates: _*)

	"Polyline" should "have a correct length" in {
		polyline.length shouldEqual 18070.0 +- 5.0
	}

	"Polyline" should "have a correct nearest neighbor projection" in {
		val location = polyline.projection(Coordinate(12.656, 52.563))
		location.offset shouldEqual 1500.0 +- 5.0
		location.index shouldEqual 0
	}

	"Polyline" should "have a correct perpendicular projection" in {
		val location = polyline.projection(Coordinate(12.663, 52.545))
		location.offset shouldEqual 520.0 +- 5.0
		location.index shouldEqual 1
	}

}
