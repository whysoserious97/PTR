import akka.actor.{ActorSystem, Props}

object SimpleActorExample {
  def main(args: Array[String]) {
    var actorSystem = ActorSystem("ActorSystem"); // Creating ActorSystem
    var actor = actorSystem.actorOf(Props[HelloAkka], "HelloAkka") //Creating actor
    actor ! "Hello Akka" // Sending messages by using !
    actor ! 100.52
  }
}
