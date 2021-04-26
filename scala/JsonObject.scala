import akka.actor.ActorRef
import akka.io.Udp
import akka.util.ByteString

import java.io.{File, PrintWriter}
import java.net.InetSocketAddress
import java.nio.file.{Files, Paths}
import java.util.UUID.randomUUID

class JsonObject(var data:ByteString,var remote: InetSocketAddress,var broker:ListenerActor,send: ActorRef) {
  var ip :(String,Int) = null
  var objType :String = ""

  //OBJECT COMMANDS
  var connect :ConnectPubJSON = null
  var subscribe :Subscribe = null
  var disconnect :Disconnect = null
  var tweetJson:TweetJson = null
  var user:User = null
  var topicList:TopicList = null




  def deserialize(str:String): Unit ={

    objType = str.substring(0,str.indexOf("{"))
    if (str.startsWith("Tweet")){
      tweetReconstruct(str);
      for (ip <- this.broker.topicsMap("tweets")){
        send ! Udp.Send(ByteString(this.tweetJson.serialized), new InetSocketAddress(ip._1,ip._2)) // from key,get element adress and port
      }
      println("Message: " + this.tweetJson.serialized)
    }
    else if (str.startsWith("ConnectPub")){
      connectPubJSONReconstruct(str)
      val ip = (remote.getAddress.getHostAddress,remote.asInstanceOf[InetSocketAddress].getPort)
      var arrayIp:Array[String] = null
      if (broker.ipsmap.contains(ip)) {
        arrayIp = broker.ipsmap((remote.getAddress.getHostAddress,remote.asInstanceOf[InetSocketAddress].getPort))
      }else{
        arrayIp = new Array[String](0)
        broker.ipsmap += (ip -> arrayIp)
      }
      arrayIp ++= this.connect.array
      println(broker.ipsmap)
    }
    else if(str.startsWith("User")){
      userReconstruct(str)
      for (ip <- this.broker.topicsMap("users")){
        broker.uuidAckMap += (user.id -> this.user.serialized)
        send ! Udp.Send(ByteString(this.user.serialized), new InetSocketAddress(ip._1,ip._2)) // from key,get element adress and port
      }
      println("User is:" + this.user.username)

    }
    else if(str.startsWith("Subscribe")){
      subscribeReconstruct(str)
      val ip = (remote.getAddress.getHostAddress,remote.asInstanceOf[InetSocketAddress].getPort)
      var array = this.broker.ipTopicsMap(ip)
      array ++= this.subscribe.array
      this.broker.ipTopicsMap += (ip -> array)


      for (topic <- this.subscribe.array){
        var ipArray = this.broker.topicsMap(topic)
        ipArray = ipArray :+ ip
        this.broker.topicsMap += (topic -> ipArray)
      }
      println(this.broker.topicsMap)
    //  println(this.broker.topicsMap("users").mkString("Array(", ", ", ")"))
      println(this.broker.ipTopicsMap)
      println("Subscribed")
      // println("TO DO...")
    }
      else if (str.startsWith("TopicList")){
      this.topicList = new TopicList()
      val ip = (remote.getAddress.getHostAddress,remote.asInstanceOf[InetSocketAddress].getPort)
      var topics = broker.ipTopicsMap(ip)
      var msg = "Topics{"
      for (a <- topics.indices) {

        msg += topics(a)
        if (a != topics.length - 1){
          msg += ","
        }
      }
      msg += "}"
      send ! Udp.Send(ByteString(msg), remote)
    }
      else if (str.startsWith("Disconnect")){
      this.disconnect = new Disconnect()
      broker.ipsmap -= ((remote.getAddress.getHostAddress,remote.asInstanceOf[InetSocketAddress].getPort))
    }
      else if (str.startsWith("ACK")){
      println("Very nice , ACK :)")
      if (str.contains("UUID{")){
        val start = str.lastIndexOf("UUID{") + 5
        val end = str.lastIndexOf("}")
        val id = str.substring(start,end)
        println("ID to remove" + id)
        val path = "/persistent/" + id.substring(0,8) + "/" + id;
        new File(path).delete()
        println("Deleted " + path)
        this.broker.uuidAckMap -= id
      }
    }
    else {
      objType = "Error"
      println("ErrorMsg:" + str)
    }
  }

  def subscribeReconstruct(str:String): Unit ={
    val start = str.indexOf("topics:[") + 1 + 7
    val end = str.indexOf("]}")
    var content = str.substring(start,end)
    var splitedContent = content.split(",")
    this.subscribe = new Subscribe(splitedContent)
  }
  def connectPubJSONReconstruct(str:String): Unit ={
    val start = str.indexOf("topics:[") + 1 + 7
    val end = str.indexOf("]}")
    var content = str.substring(start,end)
    var splitedContent = content.split(",")
    this.connect = new ConnectPubJSON(splitedContent)
  }
  def userReconstruct(str:String):Unit = {
    //
    var start = str.indexOf("{") + 1
    var end = str.lastIndexOf("}")
    var body = str.substring(start,end)

    var field_Values = body.split('|')
    var fieldMap = Map[String,String]()
    for (field <- field_Values){
      var parts = field.split(":").toList

      fieldMap += (parts(0) -> parts(1))
    }
    //

    val user_start = str.indexOf("user_name:") + 1 + 9
    val user_end = str.lastIndexOf("}")
    val username = str.substring(user_start,user_end)
    var isPersistent = fieldMap("isPersistent").toBoolean
    var id = randomUUID().toString
    this.user = new User(username,id)

    if (isPersistent){
      val PATH = "./persistent/" + id.substring(0,8) + "/"
      val directory = new File(PATH)
      if(!directory.exists()){
        directory.mkdirs()
      }
      val file = new File(PATH + id + ".pers")
      file.createNewFile()
      val pw = new PrintWriter(file)
      pw.write(str)
      pw.close()
      this.user.serialized = str + "{UUID:"+this.user.id+"}"
      this.broker.uuidAckMap += (id -> this.user.serialized)
    }else{
      this.user.serialized = str
    }
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
    var isPersistent = fieldMap("isPersistent").toBoolean
    var id = randomUUID().toString
    this.tweetJson = new TweetJson(topic,engagement,isOriginal,msg,id);
    if (isPersistent){
      val PATH = "./persistent/" + id.substring(0,8) + "/"
      val directory = new File(PATH)
      if(!directory.exists()){
        directory.mkdirs()
      }
      val file = new File(PATH + id + ".pers")
      file.createNewFile()
        val pw = new PrintWriter(file)
      pw.write(str)
      pw.close()
      this.tweetJson.serialized = str + "{UUID:"+this.tweetJson.id+"}"
      this.broker.uuidAckMap += (id -> this.tweetJson.serialized)
    }else{
      this.tweetJson.serialized = str
    }

    println(tweetJson)
  }
}
class TweetJson(var topic:String,var engagement:Double,var isOriginal:Boolean,var message:String,var id:String){var serialized:String = ""}
class ConnectPubJSON(var array: Array[String])
class Subscribe(var array: Array[String])
class User(var username:String,var id:String){var serialized:String = ""}
class TopicList()
class Disconnect()