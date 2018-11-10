package bitub.map

import bitub.map.PropertyMap._
import bitub.map.decoders._
import bitub.map.encoders._
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.syntax._

package object osm {

	sealed trait OsmElement

	case class OsmResponse(version: String, generator:String, element: Seq[OsmElement]) extends OsmElement

	case class OsmNodeElement(id: Long, lon: Double, lat: Double) extends OsmElement

	case class OsmWayElement(id: Long, nodes: Seq[Long], props: PropertyMap) extends OsmElement

	object decoders {

		implicit val decoderResponse: Decoder[OsmResponse] = Decoder.instance { e =>
			for {
				version <- e.downField("version").as[Float]
				generator <- e.downField("generator").as[String]
				elements <- e.downField("elements").as[Seq[OsmElement]]
			} yield {
				OsmResponse(s"$version", generator, elements)
			}
		}

		implicit val decoderEntities: Decoder[OsmElement] = Decoder.instance(e =>
			e.downField("type").as[String].flatMap {
				case "node" => e.as[OsmNodeElement]
				case "way" => e.as[OsmWayElement]
				case t: String => throw new IllegalArgumentException(s"Unknown type $t")
			}
		)

		implicit val decoderOsmWay: Decoder[OsmWayElement] = Decoder.instance { e =>
			for {
				id <- e.downField("id").as[Long]
				nodes <- e.downField("nodes").as[Seq[Long]]
				props <- e.downField("tags").as[PropertyMap]
			} yield {
				OsmWayElement(id, nodes, props)
			}
		}

		implicit val decoderOsmNode: Decoder[OsmNodeElement] = Decoder.instance { e =>
			for {
				id <- e.downField("id").as[Long]
				lon <- e.downField("lon").as[Double]
				lat <- e.downField("lat").as[Double]
			} yield {
				OsmNodeElement(id, lon, lat)
			}
		}

	}

}
