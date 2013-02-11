// Reader Monad example, via some Tony Morris slides
import java.net.URL

case class Configuration(hostname: String, port: Int, path: String)

abstract class ConfigReader[A] {
  def apply(config: Configuration): A

  final def map[B](f: A => B): ConfigReader[B] =
    new ConfigReader[B] {
      def apply(c: Configuration) = f(ConfigReader.this.apply(c))
    }

  final def flatMap[B](f: A => ConfigReader[B]): ConfigReader[B] =
    new ConfigReader[B] {
      def apply(c: Configuration) = f(ConfigReader.this.apply(c))(c)
    }
}

object ConfigReader {

  // you have a bunch of ConfigReader[A]s around, but some functions that work
  // in terms of plain values.  Lift said functions into the ConfigReader monad.
  // Here's a 3-arg version:
  def lift3ConfigReader[A, B, C, D](f: A => B => C => D):
    ConfigReader[A] => ConfigReader[B] => ConfigReader[C] => ConfigReader[D] =
      a => b => c =>
        for {
          aa <- a // get the values "out of" the given ConfigReaders
          bb <- b
          cc <- c
        } yield f(aa)(bb)(cc)

}

object TestApp {

  // Produces a ConfigReader that just returns `value` when invoked.
  private def const[A](value: A) = new ConfigReader[A] { def apply(c: Configuration) = value }

  def main(args: Array[String]) {
    val conf = Configuration("tralfamadore.org", 80, "/beings/salo")

//    val url= 
//      new ConfigReader[URL] { 
//        def apply(c: Configuration) = new URL("http", c.hostname, c.port, c.path)
//      }

    // let's say the proto to use doesn't come from config for some reason
    val protoReader = const("http")
    // other stuff is built from config (trivially in this case)
    val hostnameReader = new ConfigReader[String] { def apply(c: Configuration) = c.hostname }
    val portReader = new ConfigReader[Int] { def apply(c: Configuration) = c.port }
    val pathReader = new ConfigReader[String] { def apply(c: Configuration) = c.path }

    val urlReader =
      for {
        proto <- protoReader
        host <- hostnameReader
        port <- portReader
        path <- pathReader
      } yield new URL(proto, host, port, path)

    println(urlReader(conf))

    // just add up lengths of string version of args
    val dummyLen: String => Int => String => Int =
      a => b => c => a.length + b.toString.length + c.length

    val lifted = ConfigReader.lift3ConfigReader(dummyLen)
    // now what?

  }
}

