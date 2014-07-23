package be.angelcorp.glsl.util

import be.angelcorp.glsl.ast._

import scala.collection.mutable

class GlslContext(parent: Option[GlslContext]) {

  val variables = mutable.ListBuffer[GlslVariable]()
  val functions = mutable.ListBuffer[GlslFunction]()
  val types     = mutable.ListBuffer[GlslType    ]()

  def fork() = new GlslContext( Some(this) )

  def findFunction( name: String ): Option[GlslFunction] =
    functions.find( _.name == name ).orElse( parent.flatMap( _.findFunction(name) ) )

  def findVariable( name: String ): Option[GlslVariable] =
    variables.find( _.name == name ).orElse( parent.flatMap( _.findVariable(name) ) )

  def find( name: String ): Option[GlslSymbol] =
    variables.find( _.name == name ).orElse(functions.find( _.name == name )).orElse( parent.flatMap( _.find(name) ) )

  def findType( name: String ): Option[GlslType] =
    types.find( _.simpleName == name ).orElse( parent.flatMap( _.findType(name) ) )

  override def toString: String = s"<variables=${variables.map(v => v.name + ": " + v.typ)} functions=${functions.map(v => v.name + ": " + v.typ)} types=${types.toList}>"

}
