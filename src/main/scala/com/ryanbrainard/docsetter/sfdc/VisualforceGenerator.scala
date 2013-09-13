package com.ryanbrainard.docsetter.sfdc

import com.ryanbrainard.docsetter.EntryType
import scala.collection.immutable.ListMap

class VisualforceGenerator extends AbstractSfdcGenerator {
  override val sfdcId = "pages"
  override val name   = "Visualforce"
  override val searchKey = "vf"
  override val entryTypeRegexMappings = ListMap(
    "Customizing a Flow’s User Interface"  -> defaultEntryType,
    """\w+:\w+"""  -> EntryType.Element
  ) ++ super.entryTypeRegexMappings
}
