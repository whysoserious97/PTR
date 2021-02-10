//package akka
//
//import akka.SSE.actorSystem
//import akka.SSE.actorSystem.dispatcher
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.client.RequestBuilding.Get
//import akka.http.scaladsl.model.sse.ServerSentEvent
//import akka.http.scaladsl.unmarshalling.Unmarshal
//import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._
//import akka.stream.scaladsl.Source
//
//
//class SourceTry {
//// def main(args: Array[String]): Unit = {
////
////   Http()
////     .singleRequest(Get("http://localhost:8000/tweets/1"))
////     .flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
////     .foreach(_.runForeach(println))
//// }
//}
//object Run {
//  def main(args: Array[String]): Unit = {
//
//    Http()
//      .singleRequest(Get("http://localhost:4000/tweets/1"))
//      .flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
//      .foreach(_.runForeach(println))
//  }
//}
