package com.ryanbrainard.docsetter

import org.scalatest.FunSuite
import com.ryanbrainard.docsetter.sfdc.SfdcGenerator
import java.net.URL

class SfdcGeneratorTest extends FunSuite {

  test("index") {
    val gen = new SfdcGenerator() {
      override def tocUrl(url: URL) = this.getClass.getClassLoader.getResource("apex-toc.xml").toString
    }

    val index = gen.index(new URL("http://dummy"))
    println(index)
    assert(true)
  }
}