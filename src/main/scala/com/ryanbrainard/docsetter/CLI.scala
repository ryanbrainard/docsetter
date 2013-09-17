package com.ryanbrainard.docsetter

import java.io.{FileOutputStream, OutputStreamWriter, PrintWriter, File}
import org.rogach.scallop.ScallopConf
import java.util.ServiceLoader
import java.net.URL
import scala.io.Source
import java.util.regex.Pattern
import java.sql.DriverManager
import scala.sys.process.Process

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
    private implicit def fileConvertor = org.rogach.scallop.stringConverter.map(new File(_))
    version("Docsetter 1.0")
    val name = opt[String](required = true)
    val output = opt[File](descr = "Docset output parent directory", default = Some(new File("target/generated")))
    val verbose = toggle()
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

  def generate(config: Config) = generators.find(_.name.equalsIgnoreCase(config.name())).map { generator =>
    val baseDir = new File(config.output(), generator.id + ".docset")
    val contentsDir = new File(baseDir, "/Contents")
    val resourcesDir = new File(contentsDir, "/Resources")
    resourcesDir.mkdirs()

    val infoPlistTemplate = getClass.getClassLoader.getResource("Info-template.plist")
    val infoPListReplacements = Map(
      "{{CFBundleIdentifier}}"   -> generator.id,
      "{{CFBundleName}}"         -> generator.name,
      "{{DocSetPlatformFamily}}" -> generator.searchKey,
      "{{dashIndexFilePath}}"    -> generator.indexFilePath
    )
    val infoPlistReplacer = (line: String) => infoPListReplacements.foldLeft(line)((l,r) => l.replaceAll(Pattern.quote(r._1), r._2))
    val infoPlistFile = new File(contentsDir, "Info.plist")
    val infoPlistWriter = new PrintWriter(infoPlistFile)
    Source.fromURL(infoPlistTemplate).getLines().map(infoPlistReplacer).foreach(infoPlistWriter.println)
    infoPlistWriter.close()

    Option(generator.iconUrl).map { iconFileIn =>
      val iconFileOut = new File(baseDir, "icon.png")
      val iconFileInputStream = iconFileIn.openStream()
      val iconFileOutputStream = new FileOutputStream(iconFileOut)
      var buffer = -1
      while (-1 != {buffer = iconFileInputStream.read; buffer}) {
        iconFileOutputStream.write(buffer)
      }
    }

    Class.forName("org.sqlite.JDBC")
    val dbFile = new File(resourcesDir, "/docSet.dsidx")
    val db = DriverManager.getConnection("jdbc:sqlite:" + dbFile)
    db.setAutoCommit(true)
    db.createStatement().execute("CREATE TABLE searchIndex(id INTEGER PRIMARY KEY, name TEXT, type TEXT, path TEXT);")
    db.createStatement().execute("CREATE UNIQUE INDEX anchor ON searchIndex (name, type, path);")
    val inserts = db.prepareStatement("INSERT OR IGNORE INTO searchIndex(name, type, path) VALUES (?,?,?);")
    generator.index.map { e =>
      if (config.verbose()) println(e)
      inserts.setString(1, e.name)
      inserts.setString(2, e.entryType.toString)
      inserts.setString(3, e.path)
      inserts.addBatch()
    }
    inserts.executeBatch()
    inserts.close()
    db.close()

    Process("tar --exclude='.DS_Store' -czf " + generator.id + ".tgz " + generator.id + ".docset", config.output()).run()

    Right("Generated " + generator.name + " docset at " + baseDir)
  }.getOrElse {
    Left("No supported generator")
  }

  def printError(msg: String) {
    System.err.println("ERROR: " + msg)
  }
}
