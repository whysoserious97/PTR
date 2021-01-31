
import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source




object HttpClientSingleRequest {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val mat = ActorMaterializer()

    import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._
    import system.dispatcher
    Http()
      .singleRequest(Get(s"http://localhost:4000/tweets/1"))
      .flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
      .foreach(source => source.runForeach(elem => println(prettyPrint(elem)) ))
  }

  def prettyPrint(event:ServerSentEvent):String =
    s"""
       |ID: ${event.id.getOrElse("No ID")}
       |DATA: ${event.data}
       |EVENT_TYPE: ${event.eventType.getOrElse("?")}
       |RETRY: ${event.retry.getOrElse("")}
    """.stripMargin
}