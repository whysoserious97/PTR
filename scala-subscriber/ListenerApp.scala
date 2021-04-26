import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorSystem}

object ListenerApp extends App  {
  val system = ActorSystem("RemoteActorSystem")
//  val local = new InetSocketAddress("localhost", 5005)
val local = new InetSocketAddress("localhost", 5015)
//  val remote = new InetSocketAddress("localhost", 5115)
val remote = new InetSocketAddress("localhost", 5005)

  val udp: ActorRef = system.actorOf(ListenerActor(local, remote), name = "Udp")
}