package bitub.gpx

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import better.files._
import bitub.Testbase

class GpxTests extends Testbase {

	val gpxExample =
		"""
		  |<gpx xmlns="http://www.topografix.com/GPX/1/1" version="1.1" creator="Who knows">
		  |    <rte>
		  |        <name>Some example route</name>
		  |    </rte>
		  |    <trk>
		  |        <name>Some example route</name>
		  |        <trkseg>
		  |            <trkpt lat="48.19156" lon="11.5671"/>
		  |            <trkpt lat="48.1916" lon="11.56832"/>
		  |            <trkpt lat="48.19158" lon="11.5695"/>
		  |            <trkpt lat="48.19158" lon="11.56982"/>
		  |            <trkpt lat="48.19158" lon="11.57027"/>
		  |            <trkpt lat="48.1916" lon="11.57137"/>
		  |       </trkseg>
		  |    </trk>
		  |</gpx>
		""".stripMargin

	"Gpx" should "read XML" in {
		val stringStream = new ByteArrayInputStream(gpxExample.getBytes(StandardCharsets.UTF_8))
		val gpx = Gpx(stringStream)

		gpx.name shouldEqual "Some example route"
		gpx.trackPoints.size shouldEqual 6
	}
}
