import SSE.actorSystem
import SSE.actorSystem.dispatcher
import akka.NotUsed
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Source

import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._


class SourceTry {
 def main(args: Array[String]): Unit = {

   Http()
     .singleRequest(Get("http://localhost:8000/events"))
     .flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
     .foreach(_.runForeach(println))
 }
}
