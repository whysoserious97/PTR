
import SSE.actorSystem.dispatcher
import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.alpakka.sse.scaladsl.EventSource
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, ThrottleMode}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt


object HttpClientSingleRequest {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem()
    implicit val mat    = ActorMaterializer()

//    Http()
//      .singleRequest(Get("http://localhost:4000/tweets/1/"))
//      .flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
//      .foreach(_.runForeach(se => println(se)))
val send: HttpRequest => Future[HttpResponse] = Http().singleRequest(_)
   val eventSource: Source[ServerSentEvent, NotUsed] = EventSource(
  uri = Uri(s"http://localhost:4000/tweets/1"),
  send,
  initialLastEventId = None,
  retryDelay = 1.second
)
    //while (true){
      val events = eventSource.throttle(1, 500.milliseconds, 1, ThrottleMode.Shaping).take(1).runWith(Sink.seq)

      events.onComplete(se => println(se))
    // }

  }

}