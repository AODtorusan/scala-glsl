package be.angelcorp.glsl.glsl410

import be.angelcorp.glsl._

class Blinnphong extends GlslTest {

  "glsl410" should "compile the blinnphong sb6 vertex shaders correctly" in {

    val expectedVertexShader =
      """#version 410 core
        |
        |layout(location = 0) in vec4 position;
        |layout(location = 1) in vec3 normal;
        |
        |layout(std140) uniform ConstantsInterfaceBlock
        |{
        |    mat4 mv_matrix;
        |    mat4 view_matrix;
        |    mat4 proj_matrix;
        |} constants;
        |
        |out Vs_outInterfaceBlock
        |{
        |    vec3 N;
        |    vec3 L;
        |    vec3 V;
        |} vs_out;
        |
        |uniform vec3 light_pos = vec3(100.0f, 100.0f, 100.0f);
        |
        |void main()
        |{
        |    vec4 P = constants.mv_matrix * position;
        |    vs_out.N = mat3(constants.mv_matrix) * normal;
        |    vs_out.L = light_pos - P.xyz;
        |    vs_out.V = - P.xyz;
        |    gl_Position = constants.proj_matrix * P;
        |}
      """.stripMargin

    test( expectedVertexShader, BlinnphongVertexShader.source, Vertex )
  }

  it should "compile the blinnphong sb6 fragment shaders correctly" in {
    val expectedFragmentShader =
      """#version 410 core
        |
        |layout(location = 0) out vec4 color;
        |
        |in Fs_inInterfaceBlock
        |{
        |    vec3 N;
        |    vec3 L;
        |    vec3 V;
        |} fs_in;
        |
        |uniform vec3 diffuse_albedo = vec3(0.5f, 0.2f, 0.7f);
        |uniform vec3 specular_albedo = vec3(0.7f);
        |uniform float specular_power = 200.0f;
        |
        |void main()
        |{
        |    vec3 N = normalize(fs_in.N);
        |    vec3 L = normalize(fs_in.L);
        |    vec3 V = normalize(fs_in.V);
        |    vec3 H = normalize(L + V);
        |
        |    vec3 diffuse = max(dot(N, L), 0.0f) * diffuse_albedo;
        |    vec3 specular = pow(max(dot(N, H), 0.0f), specular_power) * specular_albedo;
        |
        |    color = vec4(diffuse + specular, 1.0f);
        |}
     """.stripMargin

    test( expectedFragmentShader, BlinnphongFragmentShader.source, Fragment )
  }

}

@Glsl
object BlinnphongFragmentShader extends FS410Core {

  @layout(location = 0) @out var color: vec4 = ???

  @in
  object fs_in {
    var N: vec3 = ???
    var L: vec3 = ???
    var V: vec3 = ???
  }

  // Material properties
  @uniform val diffuse_albedo:  vec3 = vec3(0.5f, 0.2f, 0.7f)
  @uniform val specular_albedo: vec3 = vec3(0.7f)
  @uniform val specular_power: Float = 200.0f

  def main() {
    // Normalize the incoming N, L and V vectors
    val N: vec3 = normalize(fs_in.N)
    val L: vec3 = normalize(fs_in.L)
    val V: vec3 = normalize(fs_in.V)
    val H: vec3 = normalize(L + V)

    // Compute the diffuse and specular components for each fragment
    val diffuse: vec3 = max(dot(N, L), 0.0f) * diffuse_albedo
    val specular: vec3 = pow(max(dot(N, H), 0.0f), specular_power) * specular_albedo

    // Write final color to the framebuffer
    color = vec4(diffuse + specular, 1.0f)
  }


}

@Glsl
object BlinnphongVertexShader extends VS410Core {

  @layout(location = 0) @in var position: vec4 = ???
  @layout(location = 1) @in var normal: vec3   = ???

  @layout(std140​) @uniform
  object constants {
    var mv_matrix:   mat4 = ???
    var view_matrix: mat4 = ???
    var proj_matrix: mat4 = ???
  }

  @out
  object vs_out {
    var N: vec3 = ???
    var L: vec3 = ???
    var V: vec3 = ???
  }

  @uniform val light_pos: vec3 = vec3(100.0f, 100.0f, 100.0f)

  def main() {
    val P: vec4 = constants.mv_matrix * position
    vs_out.N    = mat3(constants.mv_matrix) * normal
    vs_out.L    = light_pos - P.xyz
    vs_out.V    = -P.xyz
    gl_Position = constants.proj_matrix * P
  }

}