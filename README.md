scala-glsl
==========

Scala-glsl is essentially a source-to-source compiler that transforms a Scala DSL almost directly into GLSL code (based on macro annotations). This allows you to take advantage of common Scala practices such as inheritance, compile time validation and runtime interoperation between Scala and OpenGL/GLSL. 

Using scala-glsl in your project requires that you add the scala-glsl core library dependency. Furthermore, since this project requires macro annotations, it depends on the 'Macro Paradise' Scala compiler plugin to transform annotated traits and objects. More information on paradise and how to enable it can be found on: http://docs.scala-lang.org/overviews/macros/paradise.html . 

```
libraryDependencies += "be.angelcorp.scala-glsl" % "core" % "1.0.0-SNAPSHOT",
addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.0" cross CrossVersion.full)
```

The best way to start with the library is to go over the following example:

```
@Glsl
object MyFragmentShader extends FS410Core {

  @layout(location = 0) @out var color: vec4 = __

  @in
  object fs_in {
    var N: vec3 = __
    var L: vec3 = __
    var V: vec3 = __
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
```

Upon compilation, the object MyFragmentShader is rewritten to include a method 'source' that returns the following string:

```
#version 410 core

layout(location = 0) out vec4 color;

in Fs_inInterfaceBlock {
    vec3 N;
    vec3 L;
    vec3 V;
} fs_in;

uniform vec3 diffuse_albedo = vec3(0.5f, 0.2f, 0.7f);
uniform vec3 specular_albedo = vec3(0.7f);
uniform float specular_power = 200.0f;

void main() {
    vec3 N = normalize(fs_in.N);
    vec3 L = normalize(fs_in.L);
    vec3 V = normalize(fs_in.V);
    vec3 H = normalize(L + V);

    vec3 diffuse = max(dot(N, L), 0.0f) * diffuse_albedo;
    vec3 specular = pow(max(dot(N, H), 0.0f), specular_power) * specular_albedo;

    color = vec4(diffuse + specular, 1.0f);
}
```

EXPLAIN CODE GENERATION

For more examples of generated shaders and there corresponding glsl code, consult the core test package (contains most example shaders of the OpenGL SuperBible 6):
scala-glsl/core/src/test/scala/be/angelcorp/glsl

