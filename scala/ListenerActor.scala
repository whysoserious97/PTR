import java.net.InetSocketAddress
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.{IO, Udp}
import akka.util.ByteString
import scala.collection.mutable.ListBuffer

class ListenerActor(local: InetSocketAddress, remote: InetSocketAddress) extends Actor with ActorLogging {

  import context.system
  IO(Udp) ! Udp.Bind(self, local)
  var ipsmap = Map[(String,Int),Array[String]]()
  var topicsMap = Map[String,Array[(String,Int)]]().withDefaultValue(Array[(String,Int)]())
  var ipTopicsMap = Map[(String,Int),Array[String]]().withDefaultValue(Array[String]())
  var uuidAckMap = Map[String,String]() // UUID and the message to send again
  def receive = {
    case Udp.Bound(_) ⇒
      context.become(ready(sender()))
      println("Brocker is up")
  }

  def ready(send: ActorRef): Receive = {
    case msg: String ⇒
      send ! Udp.Send(ByteString(msg), remote)

    case Udp.Received(data, remoteAddress) ⇒
      val ipAddress = remoteAddress.getAddress.getHostAddress
      val port = remoteAddress.asInstanceOf[InetSocketAddress].getPort
      if (!data.utf8String.contains("ACK")){

        send ! Udp.Send(ByteString("ACK{"+data.utf8String+"}"), remote)
        println("sent")
      }
      process(data,remoteAddress,this,send);
     // log.info(s"we received ${data.utf8String} from IP Address: $ipAddress and port number: $port")
     // println("Listener: send" + send+" remote: "+ remote + "remote Adress: "+remoteAddress)
     // send ! Udp.Send(ByteString("Hello back"), remote)
  }
  def process(data:ByteString,remoteAddress:InetSocketAddress,broker:ListenerActor,send: ActorRef): Unit ={
      var jsonObject = new JsonObject(data,remoteAddress,broker,send)
      jsonObject.deserialize(data.utf8String)

  }
}

object ListenerActor {
  def apply(local: InetSocketAddress, remote: InetSocketAddress) = Props(classOf[ListenerActor], local, remote)
}