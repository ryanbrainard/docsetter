package com.ryanbrainard.docsetter

import java.io.{PrintWriter, File}
import org.rogach.scallop.ScallopConf
import java.util.ServiceLoader
import java.net.URL
import scala.io.Source
import java.util.regex.Pattern
import java.sql.DriverManager

object CLI {

  def main(args: Array[String]) {
    {
      val config = new Config(args)

      for {
        stdout      <- generate(config).right
      } yield stdout
    }.fold(printError, println)
  }

  class Config(arguments: Seq[String]) extends ScallopConf(arguments) {
    private implicit def urlConvertor = org.rogach.scallop.stringConverter.map(new URL(_))
    private implicit def fileConvertor = org.rogach.scallop.stringConverter.map(new File(_))
    version("Docsetter 1.0")
    val name = opt[String](required = true)
    val url = opt[URL](required = true, descr = "Doc URL")
    val output = opt[File](descr = "Docset output parent directory", default = Some(new File("target/generated")))
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

  def generate(config: Config) = generators.find(_.detect(config.url())).map { generator =>
    val baseDir = new File(config.output(), config.name() + ".docset")
    val contentsDir = new File(baseDir, "/Contents")
    val resourcesDir = new File(contentsDir, "/Resources")
    resourcesDir.mkdirs()

    val infoPlistTemplate = getClass.getClassLoader.getResource("Info-template.plist")
    val infoPListReplacements = Map("{{NAME}}" -> config.name(), "{{INDEX_PAGE_PATH}}" -> generator.indexPagePath(config.url()))
    val infoPlistReplacer = (line: String) => infoPListReplacements.foldLeft(line)((l,r) => l.replaceAll(Pattern.quote(r._1), r._2))
    val infoPlist = new File(contentsDir, "Info.plist")
    val infoPlistWriter = new PrintWriter(infoPlist)
    Source.fromURL(infoPlistTemplate).getLines().map(infoPlistReplacer).foreach(infoPlistWriter.println)
    infoPlistWriter.close()

    Class.forName("org.sqlite.JDBC")
    val dbFile = new File(resourcesDir, "/docSet.dsidx")
    val db = DriverManager.getConnection("jdbc:sqlite:" + dbFile)
    db.setAutoCommit(true)
    db.createStatement().execute("CREATE TABLE searchIndex(id INTEGER PRIMARY KEY, name TEXT, type TEXT, path TEXT);")
    db.createStatement().execute("CREATE UNIQUE INDEX anchor ON searchIndex (name, type, path);")
    val inserts = db.prepareStatement("INSERT OR IGNORE INTO searchIndex(name, type, path) VALUES (?,?,?);")
    generator.index(config.url()).map { e =>
      inserts.setString(1, e.name)
      inserts.setString(2, e.entryType.toString)
      inserts.setString(3, e.path)
      inserts.addBatch()
    }
    inserts.executeBatch()
    inserts.close()
    db.close()

    Right("Generated " + generator.name + " docset at " + baseDir)
  }.getOrElse {
    Left("No supported generator")
  }

  def printError(msg: String) {
    System.err.println("ERROR: " + msg)
  }
}
