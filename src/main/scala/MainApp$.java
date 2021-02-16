object MainApp{
        val actorSystem:ActorSystem=ActorSystem("ActorSystem")
        def main(args:Array[String]):Unit={

        var DS=actorSystem.actorOf(Props[DynamicSpawner],"DS")
        val Con=actorSystem.actorOf(Props[Connector],"C")
        var router=actorSystem.actorOf(Props[Router],"R")
        var autoScaler=actorSystem.actorOf(Props[AutoScaler],"AS")
        Con!"test"
//    val hasher = new Hasher
//    println(hasher.getHashes("Hello"))

        }
        }
