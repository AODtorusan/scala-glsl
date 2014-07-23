package be.angelcorp.glsl

trait mat3Like[M, V] extends  matLike[M, V] {

}

trait mat3 extends mat3Like[mat3, vec3] {

}

object mat3 {

  def apply( col0_row0: Float, col0_row1: Float, col0_row2: Float,
             col1_row0: Float, col1_row1: Float, col1_row2: Float,
             col2_row0: Float, col2_row1: Float, col2_row2: Float): mat3 = ???

  def apply( diag: Float ): mat3 = ???

  def apply( col0: vec3, col1: vec3, col2: vec3 ): mat3 = ???

  def apply( mtx4: mat4 ): mat3 = ???

}

trait mat4Like[M, V] extends  matLike[M, V] {

}

trait mat4 extends mat4Like[mat4, vec4] {

}

