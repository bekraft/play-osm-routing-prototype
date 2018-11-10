package bitub

import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.syntax._

package object map {

	type PropertyMap = Map[String, PropertyValue]
	type PropertyValue = Either[String, Double]

	object PropertyMap {
		def apply(elems: (String, PropertyValue)*): PropertyMap = Map(elems: _*)
	}

	object PropertyValue {
		private val boolDomain = List("false","true")

		def apply(b: Boolean): PropertyValue = Left(if(b) boolDomain.last else boolDomain.head)
		def apply(n: Number): PropertyValue = Right(n.doubleValue())

		def apply(s: String): PropertyValue = Left(s)

		implicit class Converter(p:PropertyValue) {
			def isDouble: Boolean = p.isRight
			def asDouble: Double = p.right.get
			def isString: Boolean = p.isLeft
			def asString: String = p.left.get
			def isBool: Boolean = p.isLeft && p.left.forall(s => boolDomain.contains(s.toLowerCase))
			def asBool : Boolean = if( 1 != boolDomain.indexOf(p.left.get) ) false else true
		}
	}

	object decoders {
		implicit val decoderPropertyValue: Decoder[PropertyValue] = decodeEither[String, Double]

		def decodeEither[A, B](implicit
							   decoderA: Decoder[A],
							   decoderB: Decoder[B]
							  ): Decoder[Either[A, B]] = {
			c: HCursor => c.as[A] match {
				case Right(a) => Right(Left(a))
				case _ => c.as[B].map(Right(_))
			}
		}
	}

	object encoders{
		implicit val encoderPropertValue: Encoder[PropertyValue] = encodeEither[String, Double]

		def encodeEither[A, B](implicit
							   encoderA: Encoder[A],
							   encoderB: Encoder[B]
							  ): Encoder[Either[A, B]] = {
			o: Either[A, B] => o.fold(_.asJson, _.asJson)
		}
	}

	trait LinkId extends Serializable {
		def serialize: Seq[(String, Any)]
		def opposed: Option[LinkId]
	}

}
