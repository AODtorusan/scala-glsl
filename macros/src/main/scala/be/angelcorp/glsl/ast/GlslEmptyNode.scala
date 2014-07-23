package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

object GlslEmptyNode extends GlslEmpty with GlslStatementLike {

  override lazy val toString: String = getClass.getSimpleName
  override def typ: GlslType = GlslType.Unit

}
