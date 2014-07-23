package be.angelcorp.glsl.glsl410

import be.angelcorp.glsl._

class Clipdistance extends GlslTest {

  "glsl410" should "compile the clipdistance sb6 vertex shaders correctly" in {

    val expectedVertexShader =
      """#version 410 core
        |
        |layout(location = 0) in vec4 position;
        |layout(location = 1) in vec3 normal;
        |
        |uniform mat4 mv_matrix;
        |uniform mat4 proj_matrix;
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
        |uniform vec4 clip_plane = vec4(1.0f, 1.0f, 0.0f, 0.85f);
        |uniform vec4 clip_sphere = vec4(0.0f, 0.0f, 0.0f, 4.0f);
        |
        |void main()
        |{
        |    vec4 P = mv_matrix * position;
        |
        |    vs_out.N = mat3(mv_matrix) * normal;
        |    vs_out.L = light_pos - P.xyz;
        |    vs_out.V = - P.xyz;
        |
        |    gl_ClipDistance[0] = dot(position, clip_plane);
        |    gl_ClipDistance[1] = length(position.xyz / position.w - clip_sphere.xyz) - clip_sphere.w;
        |
        |    gl_Position = proj_matrix * P;
        |}
      """.stripMargin

    test( expectedVertexShader, ClipdistanceVertexShader.source, Vertex )
  }

  it should "compile the clipdistance sb6 fragment shaders correctly" in {
    val expectedFragmentShader =
      """
      """.stripMargin

    //test( expectedFragmentShader, ClipdistanceFragmentShader.source, Fragment )
  }

}

@Glsl
object ClipdistanceVertexShader extends VS410Core {

  // Per-vertex inputs
  @layout(location = 0) @in val position: vec4 = __
  @layout(location = 1) @in val normal:   vec3 = __

  // Matrices we'll need
  @uniform val mv_matrix:   mat4 = __
  @uniform val proj_matrix: mat4 = __

  // Inputs from vertex shader
  @out
  object vs_out {
    var N: vec3 = __
    var L: vec3 = __
    var V: vec3 = __
  }

  // Position of light
  @uniform val light_pos: vec3 = vec3(100.0f, 100.0f, 100.0f)

  // Clip plane
  @uniform val clip_plane:  vec4 = vec4(1.0f, 1.0f, 0.0f, 0.85f)
  @uniform val clip_sphere: vec4 = vec4(0.0f, 0.0f, 0.0f, 4.0f)

  def main() {
    // Calculate view-space coordinate
    val P: vec4 = mv_matrix * position

    // Calculate normal in view-space
    vs_out.N = mat3(mv_matrix) * normal

    // Calculate light vector
    vs_out.L = light_pos - P.xyz

    // Calculate view vector
    vs_out.V = -P.xyz

    // Write clip distances
    gl_ClipDistance(0) = dot(position, clip_plane)
    gl_ClipDistance(1) = length(position.xyz / position.w - clip_sphere.xyz) - clip_sphere.w

    // Calculate the clip-space position of each vertex
    gl_Position = proj_matrix * P
  }

}