
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


object HttpClient {
  import akka.http.scaladsl.unmarshalling.Unmarshal
  implicit val system = ActorSystem("http-client")

  def main(args: Array[String]) : Unit = {

    var html = Await.result(get("http://akka.io"), 10.seconds)
    println(html)

    println("Shutting down...")
    Http().shutdownAllConnectionPools().foreach(_ => system.terminate)
  }

  def get(uri: String) = {
    val request = HttpRequest(HttpMethods.GET, uri)
    for {
      response <- Http().singleRequest(request)
      content <- Unmarshal(response.entity).to[String]
    } yield content
  }
}