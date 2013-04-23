import org.fressian._
import java.io.{ByteArrayOutputStream, File}
import scala.collection.JavaConverters._

object Fressian {

  val mapper = new com.fasterxml.jackson.databind.ObjectMapper()

  // run with: sbt "run arg1 arg2 ..."
  def main(args: Array[String]) {
    // read the entire input json doc
    val node = mapper.readTree(new File(args(0)))

    val baos = new ByteArrayOutputStream
    val fw = new FressianWriter(baos)

    node.fieldNames.asScala.foreach(println)


    fw.close
    baos.close
  }

}
