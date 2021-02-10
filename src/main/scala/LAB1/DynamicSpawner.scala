package LAB1

import akka.actor.Actor
import akka.event.Logging

class DynamicSpawner extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case "hello" => log.info("received test")
    case _      => log.info("received unknown message")
  }
}