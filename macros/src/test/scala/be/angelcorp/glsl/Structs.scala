package be.angelcorp.glsl

class Structs extends GlslTest {

  "structs" should "be formed from scala case classes" in {
    val expected =
      """struct S {
        |   float f;
        |   int i;
        |};
        |S s1;
      """.stripMargin
    assertResult( reformat(expected) ) {
      reformat( StructsGlsl.source )
    }
  }
}

@Glsl
object StructsGlsl extends GlslDSL {

  case class S( var f: Float, var i: Int )

  val s1: S = ???

}