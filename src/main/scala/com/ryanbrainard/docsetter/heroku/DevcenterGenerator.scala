package com.ryanbrainard.docsetter.heroku

import com.ryanbrainard.docsetter.{EntryType, IndexEntry, Generator}
import java.net.URL
import scala.io.Source
import scala.util.parsing.json.{JSONObject, JSONArray, JSON}
import java.io.{PrintWriter, File}

class DevcenterGenerator extends Generator {

  val host = "devcenter.heroku.com"

  def id = "heroku"

  def name = "Heroku"

  def indexFilePath = url("/").toExternalForm

  def searchKey = "heroku"

  def iconUrl = new URL("https://raw.github.com/heroku/favicon/master/favicon.iconset/icon_32x32.png")

  def index(docsDir: File) = loadJson(url("/api/v1/articles.json", secure = true)).collect {
    case articles: JSONArray =>
      articles.list.collect {
        case article: JSONObject =>
          val apiUrl = new URL(article.obj("api_url").toString)
          loadJson(apiUrl).collect {
            case data: JSONObject =>
              val slug = data.obj("slug")
              val title = data.obj("title")
              val htmlContent = data.obj("html_content")
              val path = slug + ".html"
              val file = new File(docsDir, path)
              val writer = new PrintWriter(file)
              writer.print(htmlContent)
              writer.close()
              IndexEntry(title.toString, EntryType.Guide, path.toString)
          }.getOrElse(sys.error("Could not load article from " + apiUrl))
      }
  }.getOrElse(sys.error("Could not build Heroku Devcenter index"))

  private def loadJson(url: URL) = {
    JSON.parseRaw(Source.fromURL(url).getLines().mkString)
  }

  private def url(path: String, secure: Boolean = false) = {
    new URL("http" + (if (secure) "s" else "") + "://" + host + path)
  }
}
