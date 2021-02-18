
import akka.actor.{Actor, ActorSelection, ActorSystem}
import akka.event.{Logging, LoggingAdapter}
import akka.pattern.ask
import akka.util.Timeout
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration.DurationInt

class Router extends Actor{

  implicit  val system: ActorSystem = context.system;
  implicit val dispatcher: ExecutionContextExecutor = context.dispatcher
  implicit val timeout: Timeout = Timeout(5 seconds)

  val log: LoggingAdapter = Logging(context.system, this)
  var ds: ActorSelection = system.actorSelection("user/DS")
  var paths: ListBuffer[akka.actor.ActorPath] = ListBuffer[akka.actor.ActorPath]()

  var future: Future[Any] = ds ? "workers"
  future.foreach(f => paths = f.asInstanceOf[ListBuffer[akka.actor.ActorPath]])

  def receive = {
    case str: String => {
      val worker = system.actorSelection(paths.head)
      val tweet = new Tweet(str)
        worker ! tweet
        paths += paths.head
        paths.remove(0)
    }
    case workers: ListBuffer[akka.actor.ActorPath] => {
      paths = workers.clone()
    }
    case _ => log.info("received unknown message")
  }
}