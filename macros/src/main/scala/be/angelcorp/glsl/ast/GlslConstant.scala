package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

case class GlslConstant( value: String, typ: GlslType ) extends GlslNode