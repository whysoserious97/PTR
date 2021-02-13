package LAB1

import akka.actor.Actor
import akka.event.Logging

import scala.collection.mutable.ListBuffer
import scala.io.Source

class Worker extends Actor{
  val log = Logging(context.system, this)
  val filename = "C:\\Users\\wildw\\IdeaProjects\\PTR\\Akka-SBT TRY\\src\\main\\scala\\LAB1\\em.txt"
  val lines = Source.fromFile(filename).getLines.toList
  var emotion = Map[String, Int]()
  for (line <- lines){
    val splited = line.split("\t")
    emotion = emotion + (splited(0) -> splited(1).toInt)
  }

  def receive = {
    case str: String => {

      if (!str.contains("panic")){
        val scoredWords = ListBuffer[String]()
        val data=ujson.read(str)
        val input:String = data("message")("tweet")("text").toString();
        var chunks = input.split("[ ,\\.\\!\\?@#\"]+")
        chunks=chunks.filter(_.nonEmpty)
        var result = 0
        for (chunk <- chunks){
          if (emotion.contains(chunk)){
            scoredWords += chunk
            result = result + emotion(chunk)
          }
        }
      println("Input"+input)
        println("Scored words: "+ scoredWords)
        println("Result "+result)
      }
      else {
        println("Here is a panic")
      }



    }
    case _      => log.info("received unknown message")
  }
}
