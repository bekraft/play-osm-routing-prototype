package bitub

import bitub.map.PropertyMap
import bitub.map.decoders._
import bitub.map.encoders._
import bitub.map.geometry.{BoundingBox, Coordinate, Polyline}
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.syntax._

package object geojson {

	object decoders {
		implicit val decoderBoundingBox : Decoder[BoundingBox] = {
			Decoder[(Double,Double, Double, Double)].map(b => BoundingBox(b._1,b._2, b._3,b._4))
		}

		implicit val decoderCoordinate : Decoder[Coordinate] = {
			Decoder[(Double,Double)].map(p => Coordinate(p._1, p._2))
		}

		implicit val decoderPoint: Decoder[Point] = Decoder[Coordinate].prepare(
			_.downField("coordinates")
		).map(Point)

		implicit val decoderLineString: Decoder[LineString] = Decoder[List[Coordinate]].prepare(
			_.downField("coordinates")
		).map(Polyline(_:_*)).map(LineString)

		implicit val decoderPolygon: Decoder[Polygon] = Decoder[List[Coordinate]].prepare(
			_.downField("coordinates")
		).map(Polyline(_:_*)).map(Polygon(_))

		implicit val decoderGeometry: Decoder[Geometry] = Decoder.instance(g =>
			g.downField("type").as[String].flatMap {
				case "LineString" => g.as[LineString]
				case "Polygon" => g.as[Polygon]
				case "Point" => g.as[Point]
				case t: String => throw new IllegalArgumentException(s"Unknown type $t")
			}
		)

		implicit val decoderFeature: Decoder[Feature] = Decoder.instance(f =>
			for {
				id <- f.downField("id").as[Option[String]]
				p <- f.downField("properties").as[PropertyMap]
				g <- f.downField("geometry").as[Geometry]
			} yield Feature(id.getOrElse(""), p, g)
		)

		implicit val decoder: Decoder[Any] = Decoder.instance(h =>
			h.downField("type").as[String].flatMap {
				case "FeatureCollection" => h.as[FeatureCollection]
				case "Feature" => h.as[Feature]
				case t:String => throw new IllegalArgumentException(s"Unknown type $t")
			}
		)

		implicit val decoderFeatureCollection: Decoder[FeatureCollection] = {
			Decoder.forProduct2("bbox","features")((bbox:BoundingBox,features:List[Feature]) => FeatureCollection(features,Option(bbox)))
		}
	}


	object encoders {
		implicit val encoderBoundingBox : Encoder[BoundingBox] = (b: BoundingBox) => b.toArray.asJson

		implicit val encoderCoordinate : Encoder[Coordinate] = (c: Coordinate) => Array(c.x, c.y).asJson

		implicit val encoderPolyline : Encoder[Polyline] = (p: Polyline) => p.coordinates.asJson

		implicit val encoderPoint: Encoder[Point] = Encoder.forProduct2(
			"type", "coordinates")(p => ("Point", p.coordinate)
		)

		implicit val encoderLineString: Encoder[LineString] = Encoder.forProduct2(
			"type", "coordinates")(l => ("LineString", l.polyline)
		)

		implicit val encoderPolygon: Encoder[Polygon] = Encoder.forProduct2(
			"type", "coordinates")(p => ("Polygon", p.outerRing)
		)

		implicit val encoderGeometry: Encoder[Geometry] = Encoder.instance {
			case l: LineString => l.asJson
			case p: Polygon => p.asJson
			case c: Point => c.asJson
			case _ => None.asJson
		}

		implicit val encoderFeature: Encoder[Feature] = Encoder.forProduct4(
			"type", "id", "properties", "geometry")(f => ("Feature", f.id, f.properties, f.geometry)
		)

		implicit val encoderFeatureCollection: Encoder[FeatureCollection] = Encoder.forProduct3(
			"type", "bbox", "features")(c => ("FeatureCollection", c.bbox, c.features)
		)
	}

}
