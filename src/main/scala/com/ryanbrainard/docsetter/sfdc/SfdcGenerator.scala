package com.ryanbrainard.docsetter.sfdc

import com.ryanbrainard.docsetter.{EntryType, IndexEntry, Generator}
import java.io.File
import java.net.URL
import xml._

class SfdcGenerator extends Generator {
  val name = "Salesforce"

  def detect(url: URL) = {
    url.toExternalForm.matches("https?://www.salesforce.com/us/developer/docs/\\w+$")
  }

  def indexPagePath(url: URL) = url + "/Content/Template/Splash.htm"

  def index(url: URL) = {
    for {
      entry <- XML.load(tocUrl(url)) \\ "TocEntry" // TODO: change to streaming XML
      title <- entry.attribute("Title")
      link  <- entry.attribute("Link")
    } yield {
      // TODO: handle type
      IndexEntry(title.head.text, EntryType.File, url + link.head.text)
    }
  }

  def tocUrl(url: URL) = {
    url + "/Data/Toc.xml"
  }
}
