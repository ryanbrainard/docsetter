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
      entry <- XML.load(tocUrl(url)) \\ "TocEntry"
      title <- entry.attribute("Title")
      link  <- entry.attribute("Link")
      decs  <- entry.attribute("DescendantCount")
    } yield {
      val entryType = if (decs.text.toInt > 0) {
        EntryType.Section
      } else {
        title.text.trim match {
          case t if t.matches( """\w+:\w+""") => EntryType.Element
          case t if t.endsWith(" Methods") => EntryType.Class
          case t if t.endsWith(" Class") => EntryType.Class
          case t if t.endsWith(" Interface") => EntryType.Interface
          case t if t.endsWith(" Enums") => EntryType.Enum
          case t if t.contains("Sample") => EntryType.Sample
          case _ => EntryType.Guide
        }
      }

      IndexEntry(title.text, entryType, url + link.text)
    }
  }

  def tocUrl(url: URL) = {
    url + "/Data/Toc.xml"
  }
}
