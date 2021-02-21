
import scala.util.hashing.MurmurHash3.{stringHash => h1}



class Hasher {

  def knuthHash(word: String, constant: Int): Int = {
    var hash = 0
    for (ch <- word.toCharArray)
      hash = ((hash << 5) ^ (hash >> 27)) ^ ch.toInt
    hash % constant
  }

  import scala.util.Random
  val seed: Int = Random.nextInt


  def getHashes(string: String): List[Int] ={
    val a = h1(string)
    val b = string.hashCode()
    val c = knuthHash(string,seed)
    var list = List[Int](a,b,c)
    list
  }

}
