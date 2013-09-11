package com.ryanbrainard.docsetter

import java.io.File
import org.rogach.scallop.ScallopConf
import java.util.ServiceLoader
import java.net.URL

object CLI {

  def main(args: Array[String]) {
    {
      val config = new Config(args)

      for {
        stdout      <- generate(config).right
      } yield(stdout)
    }.fold(printError, println)
  }

  class Config(arguments: Seq[String]) extends ScallopConf(arguments) {
    private implicit def urlConvertor = org.rogach.scallop.stringConverter.map(new URL(_))
    private implicit def fileConvertor = org.rogach.scallop.stringConverter.map(new File(_))
    version("Docsetter 1.0")
    val url = opt[URL](required = true, descr = "URL of doc")
    val output = opt[File](descr = "Output directory of generated docset", default = Some(new File("target/generated")))
    val generator = opt[String](descr = "Generators name. Available generators: " + generators.map(_.name).mkString(", "), default = Option(generators.map(_.name).head))
    errorMessageHandler = { message: String =>
      printError(message + "\n")
      builder.printHelp()
      sys.exit(1)
    }
  }

  val generators = {
    import scala.collection.JavaConverters._
    ServiceLoader.load(classOf[com.ryanbrainard.docsetter.Generator]).iterator().asScala.toSeq
  }

  def generate(config: Config) = {
    generators.find(_.name == config.generator()).map { generator =>
      config.output().mkdirs()
      generator.generate(config.output())
      Right("OK: " + generator.name)
    }.getOrElse {
      Left("No generator named '" + config.generator() + "'")
    }
  }

  def printError(msg: String) {
    System.err.println("ERROR: " + msg)
  }
}
