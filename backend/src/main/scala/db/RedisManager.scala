package db
import redis.clients.jedis.UnifiedJedis;

object Main {
  def main(args: Array[String]): Unit = {
    val jedis = new UnifiedJedis("redis://localhost:6379")

    val res1 = jedis.set("bike:1", "Deimos")
    println(res1) // OK

    val res2 = jedis.get("bike:1")
    println(res2) // Deimos

//

    jedis.set("bike:2", "Test")
    val res3 = jedis.get("bike:2")
    println("Result of bike:2: " + res3) // Test

    jedis.close()
  }
}
