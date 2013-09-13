package com.ryanbrainard.docsetter.sfdc

import com.ryanbrainard.docsetter.{EntryType, IndexEntry, Generator}
import xml._
import scala.collection.immutable.ListMap
import java.net.URL

abstract class AbstractSfdcGenerator extends Generator {

  def sfdcId: String

  def id = "sfdc_" + sfdcId

  def name: String

  def searchKey = name

  def url: URL = new URL("http://www.salesforce.com/us/developer/docs/" + sfdcId + "/")

  def tocUrl = new URL(url, "Data/Toc.xml")

  def indexFilePath = new URL(url, "Content/Template/Splash.htm").toExternalForm

  def iconUrl = getClass.getClassLoader.getResource(id + ".png")

  def defaultEntryType = EntryType.Guide

  def index = {
    for {
      entry     <- XML.load(tocUrl) \\ "TocEntry"
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
      entryTypeRegexMappings
        .find(r => title.matches(r._1))
        .map(_._2)
        .getOrElse(defaultEntryType)
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
}