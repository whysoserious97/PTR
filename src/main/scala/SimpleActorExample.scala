import akka.actor.Actor
import akka.stream.scaladsl.Source

class Translator extends Actor {
  def receive = {
    case word: String =>
      // ... process message
      val reply = word.toUpperCase
      sender() ! reply // reply to the ask
  }
}

object HttpClientSingleRequest {
  def main(args: Array[String]): Unit = {
//    var html = Await.result(get("http://localhost:4000/tweets/1"), 10.seconds)
//    println(html)

//    def func(x:Int) : Future[Int] = ???
    val src = Source("localhost:4000")
    println(src)
  }
}

