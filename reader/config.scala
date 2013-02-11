// Reader Monad example, via some Tony Morris slides

case class Configuration(hostname: String, port: Int, outfile: String)

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
  def main(args: Array[String]) {
    val conf = Configuration("tralfamadore.org", 80, "foo.txt")

    // dummy function, just add up lengths of string version of args
    val lengthAll: String => Int => String => Int =
      a => b => c => a.length + b.toString.length + c.length

    val lifted = ConfigReader.lift3ConfigReader(lengthAll)
    // now what?
  }
}

