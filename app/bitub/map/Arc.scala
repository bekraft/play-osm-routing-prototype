package bitub.map

trait Arc[Self <: Arc[Self]] {
	type N <: Node
	type L <: Link

	def id: LinkId

	def out: Option[Self]
	def fan: Self
	def peer: Option[Self]
	def link: L

	def startNode: N
	def endNode: N

	// Cycling around start node in an infinite loop
	def fanCycle: Stream[Self] = fan  #:: fan.fanCycle
	// Finite loop terminating at this arc
	def fans: Stream[Self] = fanCycle.takeWhile(_ != this)
	def outCycle: Stream[Self] = out.map(_.fanCycle).getOrElse(Stream.empty)
	def outs: Stream[Self] = outCycle.takeWhile(_.link != link)
	def inCycle: Stream[Self] = fanCycle.flatMap(f => f.peer.toStream)
	def ins: Stream[Self] = inCycle.takeWhile(_.link != link)
}

trait MiniArc[Self <: MiniArc[Self]] extends Arc[Self] {
	override def peer : Option[Self] = outs.find(_.link == link)
}
