package be.angelcorp.glsl.glsl410

import be.angelcorp.glsl._

class Bumpmapping extends GlslTest {

  "glsl410" should "compile the bumpmapping sb6 vertex shaders correctly" in {

    val expectedVertexShader =
      """#version 410 core
        |
        |layout(location = 0) in vec4 position;
        |layout(location = 1) in vec3 normal;
        |layout(location = 2) in vec3 tangent;
        |layout(location = 4) in vec2 texcoord;
        |
        |out Vs_outInterfaceBlock {
        |  vec2 texcoord;
        |  vec3 eyeDir;
        |  vec3 lightDir;
        |  vec3 normal;
        |} vs_out;
        |
        |uniform mat4 mv_matrix;
        |uniform mat4 proj_matrix;
        |uniform vec3 light_pos = vec3(0.0f, 0.0f, 100.0f);
        |
        |void main()
        |{
        |  vec4 P = mv_matrix * position;
        |
        |  vec3 V = P.xyz;
        |  vec3 N = normalize(mat3(mv_matrix) * normal);
        |  vec3 T = normalize(mat3(mv_matrix) * tangent);
        |  vec3 B = cross(N, T);
        |
        |  vec3 L = light_pos - P.xyz;
        |  vs_out.lightDir = normalize(vec3(dot(L, T), dot(L, B), dot(L, N)));
        |
        |  V = - P.xyz;
        |  vs_out.eyeDir = normalize(vec3(dot(V, T), dot(V, B), dot(V, N)));
        |
        |  vs_out.texcoord = texcoord;
        |
        |  vs_out.normal = N;
        |
        |  gl_Position = proj_matrix * P;
        |}
      """.stripMargin

    test( expectedVertexShader, BumpmappingVertexShader.source, Vertex )
  }

  it should "compile the bumpmapping sb6 fragment shaders correctly" in {
    val expectedFragmentShader =
      """#version 410 core
        |#extension GL_ARB_shading_language_420pack : enable
        |
        |out vec4 color;
        |
        |layout(binding = 0) uniform sampler2D tex_color;
        |layout(binding = 1) uniform sampler2D tex_normal;
        |
        |in Fs_inInterfaceBlock
        |{
        |  vec2 texcoord;
        |  vec3 eyeDir;
        |  vec3 lightDir;
        |  vec3 normal;
        |} fs_in;
        |
        |void main()
        |{
        |  vec3 V = normalize(fs_in.eyeDir);
        |  vec3 L = normalize(fs_in.lightDir);
        |  vec3 N = normalize(texture(tex_normal, fs_in.texcoord).rgb * 2.0f - vec3(1.0f));
        |  vec3 R = reflect(- L, N);
        |
        |  vec3 diffuse_albedo = texture(tex_color, fs_in.texcoord).rgb;
        |  vec3 diffuse = max(dot(N, L), 0.0f) * diffuse_albedo;
        |
        |  vec3 specular_albedo = vec3(1.0f);
        |  vec3 specular = max(pow(dot(R, V), 20.0f), 0.0f) * specular_albedo;
        |
        |  color = vec4(diffuse + specular, 1.0f);
        |}
     """.stripMargin

    test( expectedFragmentShader, BumpmappingFragmentShader.source, Fragment )
  }

}

@Glsl
object BumpmappingVertexShader extends VS410Core {

  @layout(location = 0) @in val position: vec4 = ???
  @layout(location = 1) @in val normal:   vec3 = ???
  @layout(location = 2) @in val tangent:  vec3 = ???
  @layout(location = 4) @in val texcoord: vec2 = ???

  @out object vs_out {
    var texcoord: vec2 = ???
    var eyeDir:   vec3 = ???
    var lightDir: vec3 = ???
    var normal:   vec3 = ???
  }

  @uniform val mv_matrix:   mat4 = ???
  @uniform val proj_matrix: mat4 = ???
  @uniform val light_pos: vec3 = vec3(0.0f, 0.0f, 100.0f)

  def main() {
    val P: vec4 = mv_matrix * position

    var V: vec3 = P.xyz
    val N: vec3 = normalize(mat3(mv_matrix) * normal)
    val T: vec3 = normalize(mat3(mv_matrix) * tangent)
    val B: vec3 = cross(N, T)

    val L: vec3 = light_pos - P.xyz
    vs_out.lightDir = normalize(vec3(dot(L, T), dot(L, B), dot(L, N)))

    V = -P.xyz
    vs_out.eyeDir = normalize(vec3(dot(V, T), dot(V, B), dot(V, N)))

    vs_out.texcoord = texcoord

    vs_out.normal = N

    gl_Position = proj_matrix * P
  }

}

@Glsl
object BumpmappingFragmentShader extends FS410Core with extensions.GL_ARB_shading_language_420pack {

  @out var color: vec4 = ???

  @layout(binding = 0) @uniform val tex_color:  sampler2D = ???
  @layout(binding = 1) @uniform val tex_normal: sampler2D = ???

  @in object fs_in {
    val texcoord: vec2 = ???
    val eyeDir:   vec3 = ???
    val lightDir: vec3 = ???
    val normal:   vec3 = ???
  }

  def main() {
    val V: vec3 = normalize(fs_in.eyeDir)
    val L: vec3 = normalize(fs_in.lightDir)
    val N: vec3 = normalize(texture(tex_normal, fs_in.texcoord).rgb * 2.0f - vec3(1.0f))
    val R: vec3 = reflect(-L, N);

    val diffuse_albedo: vec3 = texture(tex_color, fs_in.texcoord).rgb
    val diffuse: vec3 = max(dot(N, L), 0.0f) * diffuse_albedo

    val specular_albedo: vec3 = vec3(1.0f)
    val specular: vec3 = max(pow(dot(R, V), 20.0f), 0.0f) * specular_albedo

    color = vec4(diffuse + specular, 1.0f)
  }



}
