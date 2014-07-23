package be.angelcorp.glsl

class Traits extends GlslTest {

  "structs" should "be formed from scala case classes" in {
    val expected1 = """#version 330
                      |int foo();
                    """.stripMargin
    assertResult( reformat(expected1) ) {
      reformat( TraitsTrait.source )
    }

    val expected2 =
      """#version 330
        |int foo();
        |
        |int foo() {
        |  return 1;
        |}
      """.stripMargin
    assertResult( reformat(expected2) ) {
      reformat( TraitsGlsl.source )
    }
  }
}

@Glsl
trait TraitsTrait {
  "#version 330"

  def foo: Int
}
// The following is not required but helps the IDE out a bit
object TraitsTrait extends GlslDSL

@Glsl
object TraitsGlsl extends GlslDSL with TraitsTrait {

  def foo: Int = return 1

}