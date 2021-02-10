package LAB1

import akka.actor.Actor
import akka.event.Logging

class Worker extends Actor{
  val log = Logging(context.system, this)
  def receive = {
    case str: String => {
      log.info(str)
    }
    case _      => log.info("received unknown message")
  }
}
