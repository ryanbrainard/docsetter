package com.ryanbrainard.docsetter

import java.net.URL

trait Generator {
  def id: String
  def name: String
  def indexFilePath: String
  def searchKey: String
  def iconUrl: URL
  def index: Seq[IndexEntry]
}

case class IndexEntry(name: String, entryType: EntryType, path: String)

trait EntryType
object EntryType {
  case object Attribute extends EntryType
  case object Binding extends EntryType
  case object Callback extends EntryType
  case object Category extends EntryType
  case object Class extends EntryType
  case object Command extends EntryType
  case object Constant extends EntryType
  case object Constructor extends EntryType
  case object Define extends EntryType
  case object Directive extends EntryType
  case object Element extends EntryType
  case object Entry extends EntryType
  case object Enum extends EntryType
  case object Error extends EntryType
  case object Event extends EntryType
  case object Exception extends EntryType
  case object Field extends EntryType
  case object File extends EntryType
  case object Filter extends EntryType
  case object Framework extends EntryType
  case object Function extends EntryType
  case object Global extends EntryType
  case object Guide extends EntryType
  case object Instance extends EntryType
  case object Instruction extends EntryType
  case object Interface extends EntryType
  case object Keyword extends EntryType
  case object Library extends EntryType
  case object Literal extends EntryType
  case object Macro extends EntryType
  case object Method extends EntryType
  case object Mixin extends EntryType
  case object Module extends EntryType
  case object Namespace extends EntryType
  case object Notation extends EntryType
  case object Object extends EntryType
  case object Operator extends EntryType
  case object Option extends EntryType
  case object Package extends EntryType
  case object Parameter extends EntryType
  case object Property extends EntryType
  case object Protocol extends EntryType
  case object Record extends EntryType
  case object Sample extends EntryType
  case object Section extends EntryType
  case object Service extends EntryType
  case object Struct extends EntryType
  case object Style extends EntryType
  case object Tag extends EntryType
  case object Trait extends EntryType
  case object Type extends EntryType
  case object Union extends EntryType
  case object Value extends EntryType
  case object Variable extends EntryType
}