import akka.actor.Actor

class HelloAkka extends Actor { // Extending actor trait
  def receive = { //  Receiving message
    case msg: String => println(msg)
    case _ => println("Unknown message") // Default case
  }
}
