package com.ryanbrainard.docsetter.sfdc

import com.ryanbrainard.docsetter.EntryType
import scala.collection.immutable.ListMap

class ApiGenerator extends AbstractSfdcGenerator {
  override val sfdcId = "api"
  override val name   = "sfdc-api"
  override val entryTypeRegexMappings = ListMap(
    "Step 1: Obtain a Salesforce Developer Edition Organization"  -> defaultEntryType,
    "Primitive Data Types"  -> defaultEntryType,
    "API Data Types and Salesforce Field Types"  -> defaultEntryType,
    "System Fields"  -> defaultEntryType,
    ".* Annotation"  -> EntryType.Notation
  ) ++ super.entryTypeRegexMappings
}
