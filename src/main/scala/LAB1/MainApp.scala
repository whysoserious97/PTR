package LAB1

import akka.actor.{ActorSystem, Props}


object MainApp {
  val actorSystem = ActorSystem("ActorSystem")
  def main(args: Array[String]): Unit = {

    var DS = actorSystem.actorOf(Props[DynamicSpawner],"DS")
    var Con = actorSystem.actorOf(Props[Connector],"Con")
    var router = actorSystem.actorOf(Props[Router],"Router")
    
    Con ! "test"
  }
}
