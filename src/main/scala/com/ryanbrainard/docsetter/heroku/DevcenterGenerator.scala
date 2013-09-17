package com.ryanbrainard.docsetter.heroku

import com.ryanbrainard.docsetter.{EntryType, IndexEntry, Generator}
import java.net.URL
import scala.io.Source
import scala.util.parsing.json.{JSONObject, JSONArray, JSON}

class DevcenterGenerator extends Generator {

  val url = "http://devcenter.heroku.com"

  def id = "heroku"

  def name = "Heroku"

  def indexFilePath = url

  def searchKey = "heroku"

  def iconUrl = new URL("https://raw.github.com/heroku/favicon/master/favicon.iconset/icon_32x32.png")

  def index = JSON.parseRaw(Source.fromURL(url.replace("http://", "https://") + "/api/v1/articles.json").getLines().mkString).collect {
    case articles: JSONArray =>
      articles.list.collect {
        case article: JSONObject =>
          IndexEntry(article.obj("title").toString, EntryType.Guide, url + "/articles/" + article.obj("slug").toString)
      }
  }.getOrElse(sys.error("Could not build Heroku Devcenter index"))
}
