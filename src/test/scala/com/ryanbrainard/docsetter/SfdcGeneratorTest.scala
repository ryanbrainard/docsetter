package com.ryanbrainard.docsetter

import org.scalatest.FunSuite
import com.ryanbrainard.docsetter.sfdc.AbstractSfdcGenerator
import java.net.URL
import java.io.File

class SfdcGeneratorTest extends FunSuite {

  test("index") {
    val gen = new AbstractSfdcGenerator() {
      override def tocUrl = this.getClass.getClassLoader.getResource("sample-toc.xml")

      def sfdcId: String = "test"

      def name: String = "test"
    }

    val index = gen.index(new File("/dev/null"))
    println(index)
    assert(true)
  }
}