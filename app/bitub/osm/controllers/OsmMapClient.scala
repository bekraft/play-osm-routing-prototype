package bitub.osm.controllers

import bitub.map.geometry.BoundingBox
import javax.inject.Inject
import play.api.libs.ws.{WSClient, WSRequest}
import play.mvc.Controller
import bitub.map.osm.OsmResponse
import bitub.map.osm.decoders._
import io.circe._
import io.circe.parser._
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class OsmMapClient @Inject()(ws: WSClient)(implicit ec:ExecutionContext) extends Controller {

	private val log = LoggerFactory.getLogger(getClass)

	val overpassApi: WSRequest = ws.url("http://overpass-api.de/api/interpreter")

	def loadRouteLinks(box: BoundingBox): Future[Either[Error,OsmResponse]] =
		overpassApi
				.withQueryStringParameters("data" -> OsmOverpassQuery.highwayNode(box).toQL)
				.get()
				.filter(_.status == 200)
				.flatMap { response =>
						Future.fromTry(Try(decode[OsmResponse](response.body)))
				}
}
