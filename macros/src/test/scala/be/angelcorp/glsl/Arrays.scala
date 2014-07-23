package be.angelcorp.glsl

class Arrays extends GlslTest {

  "arrays" should "be formed from scala arrays" in {
    val expected =
      """float[3]         frequencies;
        |int[]            light;
        |int              numLights = 2;
        |float[2]         lights;
        |
        |float[5] a = float[5](3.4f, 4.2f, 5.0f, 5.2f, 1.1f);
        |float[] b = a;
        |
      """.stripMargin
    assertResult( reformat(expected) ) {
      reformat( ArraysGlsl.source )
    }
  }

}

@Glsl
object ArraysGlsl extends GlslDSL {

  val frequencies: Array[Float] = Array.ofDim[Float](3)
  var light: Array[Int] = ???
  val numLights: Int = 2
  val lights: Array[Float] = Array.ofDim[Float]( numLights )

  val a: Array[Float] = Array[Float]( 3.4f, 4.2f, 5.0f, 5.2f, 1.1f )
  val b: Array[Float] = a;

}
