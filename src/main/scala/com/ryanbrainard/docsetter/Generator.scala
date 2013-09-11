package com.ryanbrainard.docsetter

import java.io.File

trait Generator {
  def name: String
  def generate(output: File)
}
