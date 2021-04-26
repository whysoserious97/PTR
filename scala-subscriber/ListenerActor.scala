import java.net.InetSocketAddress
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.{IO, Udp}
import akka.util.ByteString

import scala.collection.mutable.ListBuffer

class ListenerActor(local: InetSocketAddress, remote: InetSocketAddress) extends Actor with ActorLogging {

  import context.system
  IO(Udp) ! Udp.Bind(self, local)
//  var ips = Set[(String,Int)]()

  def receive = {
    case Udp.Bound(_) ⇒
      context.become(ready(sender()))
      println("Subscriber is up")
      var subscribe = new Subscribe
      subscribe.topics =  subscribe.topics :+ "users"
      subscribe.topics =  subscribe.topics :+ "tweets"
      self ! subscribe.stringify()
  }

  def ready(send: ActorRef): Receive = {
    case msg: String ⇒{
      send ! Udp.Send(ByteString(msg), remote)
      println("Sender: send:"+msg+ "remote:"+remote)
    }
    case Udp.Received(data, remoteAddress) ⇒
      val ipAddress = remoteAddress.getAddress.getHostAddress
      val port = remoteAddress.asInstanceOf[InetSocketAddress].getPort
      val str = data.utf8String
      val ackStart = str.lastIndexOf("UUID{") + 5
      val ackEnd = str.lastIndexOf("}")
      val id = str.substring(ackStart,ackEnd)
      println("Id = " + id)
      send ! Udp.Send(ByteString("ACK{"+id+"}"), remote)
      process(data,remoteAddress,this,send);
  }

  def process(data:ByteString,remoteAddress:InetSocketAddress,broker:ListenerActor,send: ActorRef): Unit ={
      var jsonObject = new JsonObject(data,remoteAddress,broker,send)
    jsonObject.deserialize(data.utf8String)
  }
}

object ListenerActor {
  def apply(local: InetSocketAddress, remote: InetSocketAddress) = Props(classOf[ListenerActor], local, remote)
}