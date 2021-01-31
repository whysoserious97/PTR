import akka.NotUsed
import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Source
class HelloAkka extends Actor{    // Extending actor trait

  def receive = {
//    implicit val mat: ActorMaterializer = ActorMaterializer()
    //  Receiving message
    case msg:String => {
      println(msg)
//      val my_map = msg.flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
    }

    case _ =>println("Unknown message")      // Default case
  }
}

object HttpClientSingleRequest {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem()
//    implicit val mat: ActorMaterializer = ActorMaterializer()
    var actor = system.actorOf(Props[HelloAkka],"HelloAkka")

    import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._
    import system.dispatcher

    Http()
      .singleRequest(Get("http://localhost:4000/tweets/1/"))
      .flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
      .foreach(_.runForeach(se => actor ! se.data ))
//    actor ! "{\"message\" : {\"field\": \"Hello\"}}"
  }

}