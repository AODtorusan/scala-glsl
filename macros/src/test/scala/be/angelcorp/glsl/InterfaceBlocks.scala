package be.angelcorp.glsl

class InterfaceBlocks extends GlslTest {

  "interface blocks" should "be formed from scala objects" in {
    val expected =
      """Interface1InterfaceBlock {
        |   int x;
        |   float y;
        |} interface1;
        |interface1.y = interface1.x;
        |
        |Interface2InterfaceBlock {
        |   int x;
        |   float y;
        |} interface2[5];
        |
        |interface2[3].x = interface1.x;
        |interface2[1] = interface2[2];
      """.stripMargin
    assertResult( reformat(expected) ) {
      reformat( InterfaceBlocksGlsl.source )
    }
  }
}

@Glsl
object InterfaceBlocksGlsl extends GlslDSL {

  object interface1 extends InterfaceBlock {
    var x: Int = ???
    var y: Float = ???
  }
  interface1.y = interface1.x

  object interface2 extends InterfaceBlockArray(5) {
    var x: Int = ???
    var y: Float = ???
  }
  interface2(3).x = interface1.x
  interface2(1) = interface2(2)

}
