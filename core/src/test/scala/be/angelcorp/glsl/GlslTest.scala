package be.angelcorp.glsl

import org.scalatest.{FlatSpec, Matchers}

trait GlslTest extends FlatSpec with Matchers {

  def reformat( str: String ): String =
    str.replaceAll("""\s+""", " ").trim

  def test( expected: => String, real: => String, typ: ShaderType ) {
    val expectedString = expected
    val realString = real


    assertResult( reformat(expectedString) ) {
      reformat( realString )
    }

    val (status, output) = Glslang.runValidator(realString, typ)
    require( status == 0, output )
  }

 }
