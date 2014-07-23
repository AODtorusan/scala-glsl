import be.angelcorp.glsl._
import be.angelcorp.glsl.glsl330._

object Test extends App {

  @Glsl
  object MyShader extends FS330Core with BaseShader {

    @layout( location = 0 ) @out var color_out: vec4 = ???
    @uniform val gbuf_tex0: usampler2D = ???
    @uniform val gbuf_tex1: sampler2D  = ???

    @uniform val num_lights: Int = 64
    @uniform val vis_mode: Int = 1

    @uniform
    @layout( memoryLayout = std140​ )
    object light_block {
      val light: Array[light_t] = Array.ofDim[light_t](64)
    }

    def unpackGBuffer(coord: ivec2, @out fragment: fragment_info) {
      val data0: uvec4 = texelFetch(gbuf_tex0, ivec2(coord), 0)
      val data1: vec4  = texelFetch(gbuf_tex1, ivec2(coord), 0)

      light_block.light(2) = light_block.light(1)

      val temp: vec2 = unpackHalf2x16(data0.y)
      fragment.color = vec3(unpackHalf2x16(data0.x), temp.x)
      fragment.normal = normalize(vec3(temp.y, unpackHalf2x16(data0.z)))
      fragment.material_id = data0.w

      fragment.ws_coord = data1.xyz
      fragment.specular_power = data1.w
    }

    def vis_fragment(fragment: fragment_info): vec4 = {
      var result: vec4 = vec4(0f)

      var myFrag: fragment_info = fragment_info( vec3(0f), vec3(0f), 0, vec3(0f), 0 )

      var j: Int = ???
      for (i: Int <- 0 until 5;
           k: Int <- 5 to 0 by -1) {
        j = i
        myFrag = myFrag
      }



      return result
    }

    def main() {
      var fragment: fragment_info = ???

      unpackGBuffer(ivec2(gl_FragCoord.xy), fragment)

      color_out = vis_fragment(fragment)
    }
  }

  println( "MyShader:" )
  println( MyShader.source )

}