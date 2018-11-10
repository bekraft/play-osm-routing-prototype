package bitub.geojson

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import bitub.Testbase

class GeoJsonTests extends Testbase {

	// Some formatted real world geojson
	val geoJsonResource =
		"""{
		  |  "type": "FeatureCollection",
		  |  "features": [{
		  |    "type": "Feature",
		  |    "geometry": {
		  |      "type": "LineString",
		  |      "coordinates": [[11.61482, 48.11096], [11.61495, 48.11073]]
		  |    },
		  |    "properties": {
		  |      "length": 27.6299991607666,
		  |      "highway": "motorway"
		  |    },
		  |    "id": "0"
		  |  }, {
		  |    "type": "Feature",
		  |    "geometry": {
		  |      "type": "LineString",
		  |      "coordinates": [[11.61495, 48.11073], [11.61501, 48.11056]]
		  |    },
		  |    "properties": {
		  |      "length": 19.739999771118164,
		  |    },
		  |    "id": "1"
		  |  }, {
		  |    "type": "Feature",
		  |    "geometry": {
		  |      "type": "Point",
		  |      "coordinates": [11.61495, 48.11073]
		  |    },
		  |    "properties": {
		  |      "label": "Some point"
		  |    },
		  |    "id": "3"
		  |  }]
		  |}
		""".stripMargin

	"GeoJson pretty-print resource" should "be decoded properly" in {
		val stringStream = new ByteArrayInputStream(geoJsonResource.getBytes(StandardCharsets.UTF_8))

	}

	"GeoJson roundtrip encoding/decoding" should "be return same results" in {

	}
}
