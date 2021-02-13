package LAB1

import akka.actor.{Actor, Props}
import akka.event.Logging

import scala.collection.mutable.ListBuffer

class DynamicSpawner extends Actor {
  implicit  val system = context.system;
  val log = Logging(context.system, this)
  val workers = ListBuffer[akka.actor.ActorRef]()
  val workerPaths = ListBuffer[akka.actor.ActorPath]()
  for (a <- 1 to 10){
    val worker = system.actorOf(Props[Worker],"Worker" + a)
    workers += worker
    workerPaths += worker.path
  }

  def receive = {
    case "workers" => {
      val children = workerPaths
      sender() ! children
    }
    case integer: Integer => {

    }
    case _      => log.info("received unknown message")
  }
}