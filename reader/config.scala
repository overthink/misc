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
    
    // Just makin' my method, don't care about no config!
    // UrlServiceClient => String => String
    def getBody(urlClient: UrlServiceClient)(url: String): String = {
      urlClient.getBody(url)
    }
    def getHeader(urlClient: UrlServiceClient)(url: String): String = {
      urlClient.getHeader(url)
    }
    def getAllTheData(url: String): String = {
      val urlClient = new UrlServiceClient(port=5432)
      getBody(urlClient, url) + getHeader(urlClient, url)
      
    }
    def sayHello: String = {
      "hello: " + getAllTheData("http://realycool.geocities.com/toky/shrine/23424")
      
    }
    
    // OH NOZ!!! UrlClient now depends on configuration parameters!!!!!
    def getAllTheData2(url: String): ConfigReader[String] = {
      portReader.map { urlClient => getBody(urlClient, url) + getHeader(urlClient, url) }
    }
    // getAllTheData has now defered the passing of configuration.
    // Say hello now becomes:
    def sayHello2: String = {
      val config = GlobalConfigGrossness.getConfig
      "hello: " + getAllTheData2("http://reallycool.geocities.com/tokyo/shrine/23424")(config)
    }
    
    // If we needed to defer computation, we could just keep using map, pushing it all the way up to main
    def sayHelloThenGoodBye: ConfigReader[String] = {
      sayHello2.map { str => str + ", goodbye" }
    }
    
    def main(args: Array[String]) {
      // Look! Caring about config was pushed all the way up to main
      val config = GlobalConfigGrossness.getConfig
      sayHelloThenGoodBye(config)
    }
    
    
    

  }
}

