package LAB1

import akka.actor.Actor
import akka.pattern.ask
import akka.util.Timeout

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationInt

class Router extends Actor{
  implicit  val system = context.system;
  import context.dispatcher
  var ds = system.actorSelection("user/DS")

  implicit val timeout = Timeout(5 seconds)

  def receive = {
    case str: String => {
      val paths = ds ? "workers"
      paths.foreach(el =>{
        val path = el.asInstanceOf[ListBuffer[akka.actor.ActorPath]];
        //println(path(1))
        val worker = system.actorSelection(path(1))
        worker ! str
       // println("Hello")
      }

      )

    }

  }
}
