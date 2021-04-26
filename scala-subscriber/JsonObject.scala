import akka.actor.ActorRef
import akka.util.ByteString

import java.net.InetSocketAddress

class JsonObject(var data:ByteString,var remote: InetSocketAddress,var broker:ListenerActor,send: ActorRef) {

  var tweetJson:TweetJson = null
  var user:User = null

  def deserialize(str:String): Unit ={
    if (str.startsWith("Tweet")){
      tweetReconstruct(str);
      println("Message " + this.tweetJson.message)
    }
    else if(str.startsWith("User")){
      userReconstruct(str)
      println("User is:" + this.user.username)
      //println("TO DO...")
    }
    else {
      println("Default:" + str)
    }
  }

  def userReconstruct(str:String):Unit = {
    val start = str.indexOf("user_name:") + 1 + 9
    val end = str.lastIndexOf("}")
    val username = str.substring(start,end)
    this.user = new User(username)
  }

  def tweetReconstruct(str:String): Unit ={
      var start = str.indexOf("{") + 1
      var end = str.lastIndexOf("}")
      var body = str.substring(start,end)
    println(body)
      val separator = "message:\""
      var startmsg = body.indexOf(separator) + 1
      end =  body.lastIndexOf("\"")
      var msg = body.substring(startmsg + separator.length - 1  ,end)
      println(msg)

      // now we need to take out message and split fields from json
      body = body.substring(0,startmsg - 1)
    var field_Values = body.split('|')
    var fieldMap = Map[String,String]()
    for (field <- field_Values){
      var parts = field.split(":").toList

      fieldMap += (parts(0) -> parts(1))
    }

    var topic = fieldMap("topic")
    var engagement = fieldMap("engagement").toDouble
    var isOriginal = fieldMap("isOriginal").toBoolean
    this.tweetJson = new TweetJson(topic,engagement,isOriginal,msg);
    println(tweetJson)
  }
}
class TweetJson(var topic:String,var engagement:Double,var isOriginal:Boolean,var message:String)
class User(var username:String)