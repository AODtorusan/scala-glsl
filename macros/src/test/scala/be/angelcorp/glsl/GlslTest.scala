package be.angelcorp.glsl

import org.scalatest.{Matchers, FlatSpec}

trait GlslTest extends FlatSpec with Matchers {

  def reformat( str: String ): String =
    str.replaceAll("""\s+""", " ").trim

}
