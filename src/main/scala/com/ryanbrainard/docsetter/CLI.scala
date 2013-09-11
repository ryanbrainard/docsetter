package com.ryanbrainard.docsetter

import scala.io.Source
import spray.json._
import java.io.File
import org.rogach.scallop.ScallopConf
import java.util.ServiceLoader

object CLI {

  def main(args: Array[String]) {
    {
      val config = new Config(args)

      for {
        schemaFile  <- loadFile(config.file()).right
        schema      <- deserialize(schemaFile).right
        stdout      <- generate(config, schema).right
      } yield(stdout)
    }.fold(printError, println)
  }

  class Config(arguments: Seq[String]) extends ScallopConf(arguments) {
    private implicit def fileConverter = org.rogach.scallop.stringConverter.map(new File(_))
    version("Heroku.ANY 0.1 Super Alpha")
    val file = opt[File](required = true, descr = "File (doc.json) describing the API")
    val output = opt[File](descr = "Output directory of generated files", default = Some(new File("target/generated")))
    val generator = opt[String](descr = s"Generators name. Available generators: ${generators.map(_.name).mkString(", ")}", default = Option(generators.map(_.name).head))
    errorMessageHandler = { message: String =>
      printError(message + "\n")
      builder.printHelp()
      sys.exit(1)
    }
  }

  val generators = {
    import scala.collection.JavaConverters._
    ServiceLoader.load(classOf[com.heroku.any.Generator]).iterator().asScala.toSeq
  }

  def loadFile(file: File) = {
    if (file.exists()) Right(file)
    else Left(s"File not found: $file")
  }

  def deserialize(file: File) = {
    import com.heroku.any.schema.json._
    import com.heroku.any.HerokuApiProtocol._

    try {
      Right(
        Source.fromFile(file).mkString
          .asJson.asJsObject.convertTo[Schema]
          .addAttribute("App", "legacy_id", "Deprecated id format", "string", serialized = true, "app123@heroku.com")
          .addAttribute("App", "tier", "App tier", "string", serialized = true, "Production")
          .toRich(file.toString)
      )
    } catch {
      case e: DeserializationException => Left(s"Problem parsing schema:${e.getMessage}")
    }
  }

  def generate(config: Config, schema: com.heroku.any.schema.rich.Schema) = {
    generators.find(_.name == config.generator()).map { generator =>
      config.output().mkdirs()
      generator.generate(schema, config.output())
      Right(s"OK: ${generator.name}")
    }.getOrElse {
      Left(s"No generator named '${config.generator()}'")
    }
  }

  def printError(msg: String) {
    System.err.println(s"ERROR: $msg")
  }
}
