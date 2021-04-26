class Subscribe {
  var topics = Array[String]()
  def stringify():String = {
    var result:String = ""
    result = s"Subscribe{topics:["
    for (i <- topics.indices){
      result += topics(i)
      if(i != topics.length - 1){
        result += ','
      }
    }
    result += s"]}"

    result
  }
}
