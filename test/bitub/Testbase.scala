package bitub

import org.scalatest.prop.PropertyChecks
import org.scalatest.{FlatSpec, Inside, Matchers}

trait Testbase extends FlatSpec
		with PropertyChecks
		with Matchers
		with Inside
