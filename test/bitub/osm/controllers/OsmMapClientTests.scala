package bitub.osm.controllers

import akka.actor.ActorSystem
import bitub.map.geometry.BoundingBox
import org.scalatestplus.play._
import play.api.test.WsTestClient

import scala.concurrent.Await
import scala.concurrent.duration._
import bitub.map.osm.OsmResponse
import bitub.map.osm.decoders._
import io.circe.{Decoder, Json}
import io.circe.parser._
import io.circe.syntax._
import io.circe.generic.semiauto._
import org.slf4j.LoggerFactory

class OsmMapClientTests extends PlaySpec {

	val testBounds = BoundingBox(51.249,7.148,51.251,7.152)

	s"OSM highwayNode($testBounds) query" should {
		"response" in {
			val actorSystem = ActorSystem("test")
			try {
				implicit val ec = actorSystem.dispatcher
				WsTestClient.withClient {
					client =>
						val result = Await.result( new OsmMapClient(client).loadRouteLinks(testBounds), 30.seconds )
						result match {
							case Left(e) =>
								throw e
							case Right(r) =>
								info(s"Response of version ${r.version} successfully decoded (${r.element.size} features)")
						}
				}
			} finally {
				actorSystem.terminate()
			}
		}
	}
}
