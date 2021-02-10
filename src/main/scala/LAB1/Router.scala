package LAB1

import akka.actor.Actor
import akka.event.Logging

class Router extends Actor{
  val log = Logging(context.system, this)
  def receive = {
    case str: String => {
        val data=ujson.read(str)
      println(data("message")("tweet")("text"));
    }
    case _      => log.info("received unknown message")
  }
}
