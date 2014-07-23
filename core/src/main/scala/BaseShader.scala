import be.angelcorp.glsl.glsl330.Glsl330
import be.angelcorp.glsl._

@Glsl
trait BaseShader extends Glsl330 with extensions.GL_ARB_shading_language_packing {

  case class light_t( position: vec3, pad0: uint, color: vec3, pad1: uint )

  case class fragment_info( var color: vec3, var normal: vec3, var specular_power: Float, var ws_coord: vec3, var material_id: uint )

  def fragmentOperation( fIn: fragment_info ): fragment_info = return fIn

}
