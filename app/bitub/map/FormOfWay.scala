package bitub.map

object FormOfWay {
	// Aligned with OpenLR
	val UNDEFINED = FormOfWay("Undefined")
	val MOTORWAY = FormOfWay("Motorway")
	val MULTI_CARRIAGEWAY = FormOfWay("Multi Carriageway")
	val SINGLE_CARRIAGEWAY = FormOfWay("Single Carriageway")
	val ROUNDABOUT = FormOfWay("Roundabout")
	val TRAFFIC_SQUARE = FormOfWay("Traffic Square")
	val SLIP_ROAD = FormOfWay("Slip Road")
	val OTHER = FormOfWay("Other")

	// Special FOWs
	val PARKING_PLACE = FormOfWay("Parking Place")
	val WALKWAY = FormOfWay("Walkway")
}

case class FormOfWay(name:String) extends AnyVal
