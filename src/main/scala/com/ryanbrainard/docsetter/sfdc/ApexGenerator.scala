package com.ryanbrainard.docsetter.sfdc

import com.ryanbrainard.docsetter.EntryType
import scala.collection.immutable.ListMap

class ApexGenerator extends AbstractSfdcGenerator {
  override val sfdcId = "apexcode"
  override val name   = "Apex"
  override val entryTypeRegexMappings = ListMap(
    "Exposing Data with Apex REST Web Service Methods"  -> defaultEntryType,
    "Exposing Data with WebService Methods"  -> defaultEntryType,
    "Mixed DML Operations in Test Methods"  -> defaultEntryType,
    "Overloading Web Service Methods"  -> defaultEntryType,
    ".* Annotation"  -> EntryType.Notation
  ) ++ super.entryTypeRegexMappings
}
