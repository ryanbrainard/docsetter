package com.ryanbrainard.docsetter.sfdc

import com.ryanbrainard.docsetter.{EntryType, IndexEntry, Generator}
import java.io.File
import java.net.URL
import xml._
import scala.collection.immutable.ListMap

class SfdcGenerator extends Generator {
  val name = "Salesforce"

  def detect(url: URL) = {
    url.toExternalForm.matches("https?://www.salesforce.com/us/developer/docs/\\w+$")
  }

  def indexPagePath(url: URL) = url + "/Content/Template/Splash.htm"

  def index(url: URL) = {
    for {
      entry     <- XML.load(tocUrl(url)) \\ "TocEntry"
      titleNode <- entry.attribute("Title")
      linkNode  <- entry.attribute("Link")
      decsNode  <- entry.attribute("DescendantCount")
    } yield {
      val title = titleNode.text.trim
      val link  =  linkNode.text.trim
      val decs  = decsNode.text.trim.toInt

      IndexEntry(title, mapEntryType(title, decs), url + link)
    }
  }

  def mapEntryType(title: String, decs: Int): EntryType = {
    if (decs > 0) {
      EntryType.Section
    } else {
      entryTypeRegexMappings.find(r => title.matches(r._1)).map(_._2).getOrElse(defaultEntryType)
    }
  }

  val entryTypeRegexMappings: Map[String, EntryType] = ListMap (
    """\w+:\w+"""  -> EntryType.Element,
    ".* Methods"   -> EntryType.Class,
    ".* Class"     -> EntryType.Class,
    ".* Interface" -> EntryType.Interface,
    ".* Enums"     -> EntryType.Enum,
    ".*Sample.*"   -> EntryType.Sample
  )

  val defaultEntryType = EntryType.Guide

  def tocUrl(url: URL) = {
    url + "/Data/Toc.xml"
  }
}
