// val x: Option[Int] = None
val x: Option[Int] = Some(1)
val y: Option[Int] = Some(2)
//val z: Option[Int] = Some(3)
val z: Option[Int] = None

def toRight[Ok, Err](x: Option[Ok], orElse: => Err): Either.RightProjection[Err, Ok] = {
  val either =
    x match {
      case Some(x) => Right(x)
      case None => Left(orElse)
    }
  // Confusingly, you have to project this to a RightProjection (chosing Right
  // vs Left is simply convention) since Either itself isn't a monad (no
  // flatMap).  Gory details and a fix proposal:
  // http://robsscala.blogspot.ca/2012/06/fixing-scalaeither-unbiased-vs-biased.html
  either.right
}

val result: Either[String, Int] =
  for {
    a <- toRight(x, { println("side effect!"); "x was None" })
    b <- toRight(y, "y was None")
    c <- toRight(z, "z was None")
  } yield {
    a + b + c
  }

println(result)

result match {
  case Right(x) => println("Result was %d".format(x))
  case Left(msg) => println("Error, msg was %s".format(msg))
}

