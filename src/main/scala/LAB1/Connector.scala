package LAB1

import akka.NotUsed
import akka.actor.Actor
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.alpakka.sse.scaladsl.EventSource
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, ThrottleMode}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class Connector extends Actor {

  implicit val mat    = ActorMaterializer()
  import context.dispatcher
  implicit  val system = context.system;

  def receive = {
    case "test" => {

      val send: HttpRequest => Future[HttpResponse] =
        Http().singleRequest(_)

      val eventSource: Source[ServerSentEvent, NotUsed] = EventSource(
        uri = Uri(s"http://localhost:4000/tweets/1"),
        send,
        initialLastEventId = None,
        retryDelay = 1.second
      )
      while (true){
        val events = eventSource.throttle(1, 1.milliseconds, 1, ThrottleMode.Shaping).take(1).runWith(Sink.seq)
        events.foreach(se => println(se.foreach(
          ev => {
           val temp = ev.getData();
            println(temp)
          })
        )) /// .getData()
        while (!events.isCompleted){}
      }

    }

  }
}