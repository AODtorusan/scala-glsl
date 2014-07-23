package be.angelcorp.glsl

class BasicTypes extends GlslTest {

  "primitive scala types" should "be converted to equivalent glsl ones" in {
    val expected =
      """void voidVariable;
        |void voidFunction();
        |bool success;
        |bool done = false;
        |int a;
        |int b = 15;
        |float c;
        |float d = 15.0f;
        |double e;
        |double f = 15.0d;
      """.stripMargin
    assertResult( reformat(expected) ) {
      reformat( PrimitiveTypes.source )
    }
  }
}

@Glsl
object PrimitiveTypes extends GlslDSL {
  val voidVariable: Unit = ???
  def voidFunction: Unit = ???
  val success: Boolean = ???
  val done: Boolean = false
  val a: Int = ???
  val b: Int = 15
  val c: Float = ???
  val d: Float = 15f
  val e: Double = ???
  val f: Double = 15.0
}