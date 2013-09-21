package com.ryanbrainard.docsetter.heroku

import com.ryanbrainard.docsetter.{EntryType, IndexEntry, Generator}
import java.net.URL
import scala.io.Source
import scala.util.parsing.json.{JSONObject, JSONArray, JSON}
import java.io.{FileInputStream, FileOutputStream, PrintWriter, File}

class DevcenterGenerator extends Generator {

  val host = "devcenter.heroku.com"

  def id = "heroku"

  def name = "Heroku"

  def indexFilePath = url("/").toExternalForm

  def searchKey = "heroku"

  def iconUrl = new URL("https://raw.github.com/heroku/favicon/master/favicon.iconset/icon_32x32.png")

  val templateUrl= getClass.getClassLoader.getResource("heroku-devcenter/dynamic/layout.html")

  val staticAssetsResourcesDir = new File(getClass.getClassLoader.getResource("heroku-devcenter/static/assets").getFile)

  def index(docsDir: File) = {
    val articlesDir = new File(docsDir, "articles")
    val assetsDir = new File(docsDir, "assets")
    articlesDir.mkdirs()
    assetsDir.mkdirs()

    val template = Source.fromURL(templateUrl).getLines().mkString

    staticAssetsResourcesDir.listFiles().filter(_.isFile).foreach(f => copy(f, new File(assetsDir, f.getName)))

    loadJson(url("/api/v1/articles.json", secure = true)).collect {
      case articles: JSONArray =>
        articles.list.par.collect {
          case article: JSONObject =>
            val apiUrl = new URL(article.obj("api_url").toString)
            loadJson(apiUrl).collect {
              case data: JSONObject =>
                println("writing:" + Thread.currentThread() + apiUrl)

                val slug = data.obj("slug").toString + ".html"
                val title = data.obj("title").toString
                val meta = data.obj("meta_description").toString
                val htmlContent = data.obj("html_content").toString
                val path = Seq(articlesDir.getName, slug).mkString("/")
                val file = new File(articlesDir, slug)
                val writer = new PrintWriter(file)
                val html = template
                  .replaceAllLiterally("{{TITLE}}", title)
                  .replaceAllLiterally("{{META}}", meta)
                  .replaceAllLiterally("{{BODY}}", htmlContent)
                  .replaceAllLiterally("\"/articles/", "\"")

                writer.print(html)
                writer.close()
                println("wrote:" + Thread.currentThread() + file)
                IndexEntry(title, EntryType.Guide, path)
            }.getOrElse(sys.error("Could not load article from " + apiUrl))
        }.seq
    }.getOrElse(sys.error("Could not build Heroku Devcenter index"))
  }

  private def loadJson(url: URL) = {
    JSON.parseRaw(Source.fromURL(url).getLines().mkString)
  }

  private def url(path: String, secure: Boolean = false) = {
    new URL("http" + (if (secure) "s" else "") + "://" + host + path)
  }

  def copy(from: File, to: File) = {
    val fileInputStream = new FileInputStream(from)
    val fileOutputStream = new FileOutputStream(to)
    var buffer = -1
    while (-1 != {buffer = fileInputStream.read; buffer}) {
      fileOutputStream.write(buffer)
    }
    fileInputStream.close()
    fileOutputStream.close()
  }
}
