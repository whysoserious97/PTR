import akka.actor.ActorPath

import scala.collection.mutable.ListBuffer

class Tweet (var content:String){
  var workers: ListBuffer[ActorPath] = ListBuffer[ActorPath]()
  var isExecuted = false
  var input :String = ""
  var scoredWords :ListBuffer[String] = ListBuffer[String]()
  var result: Int  = 0

  def toExecute(): Boolean ={
    this.synchronized{
      if (!isExecuted){
        isExecuted = true
        true
      }
      else {
        false
      }
    }

  }
}
