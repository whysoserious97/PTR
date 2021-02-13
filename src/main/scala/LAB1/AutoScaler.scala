package LAB1

import akka.actor.Actor
import akka.event.Logging

class AutoScaler extends Actor{
  val log = Logging(context.system, this)
  implicit  val system = context.system;
  var ds = system.actorSelection("user/DS")
  def receive = {
    case str => {
      //ds ! str
    }
//    case integer: Integer => {
//
//    }
    case _      => log.info("received unknown message")
  }
}
