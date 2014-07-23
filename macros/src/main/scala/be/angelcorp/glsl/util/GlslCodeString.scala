package be.angelcorp.glsl.util

import scala.collection.mutable.ListBuffer
import scala.collection.{GenTraversableOnce, mutable}
import scala.reflect.macros.whitebox.Context

class GlslCodeString {

  val lines = mutable.ListBuffer[ GlslCodeLine ]()

  private var indent = 0

  def +=( str: String ): GlslCodeString = add(str)
  def add( str: String ): GlslCodeString = {
    lines += GlslCodeLine( new GlslStringSnippet(str), indent)
    this
  }

  def +=( code: GenTraversableOnce[String] ): GlslCodeString = this.add(code)
  def add( code: GenTraversableOnce[String] ): GlslCodeString = {
    for (line <- code)
      lines += GlslCodeLine( new GlslStringSnippet(line), indent )
    this
  }

  def +=( code: GlslCodeString ): GlslCodeString = this.add(code)
  def add( code: GlslCodeString ): GlslCodeString = {
    for (line <- code.lines)
      add( line )
    this
  }

  def +=( line: GlslCodeLine ): GlslCodeString = this.add(line)
  def add( line: GlslCodeLine ): GlslCodeString = {
    lines += GlslCodeLine( line.snippets, line.tabs + indent )
    this
  }

  def +=(c: Context)(tree: c.Tree): GlslCodeString = this.add(c)(tree)
  def add(c: Context)(tree: c.Tree): GlslCodeString = {
    lines += GlslCodeLine( new GlslTreeSnippet(c)(tree), indent )
    this
  }

  def append( str: String ): GlslCodeString = {
    if (lines.isEmpty)
      add(str)
    else
      lines.last.snippets += new GlslStringSnippet(str)
    this
  }

  def append( line: GlslCodeLine ): GlslCodeString = {
    if (lines.isEmpty)
      add(line)
    else
      lines.last.snippets ++= line.snippets
    this
  }

  def append( str: GlslCodeString ): GlslCodeString = {
    if (str.lines.nonEmpty) {
      append( str.lines.head )
      tabIn()
      for (l <- str.lines.drop(1))
        add(l)
      tabOut()
    }
    this
  }

  def appendNoIndent( str: GlslCodeString ): GlslCodeString = {
    if (str.lines.nonEmpty) {
      append( str.lines.head )
      for (l <- str.lines.drop(1))
        add(l)
    }
    this
  }

  def appendAll( strs: Seq[String], start: String, sep: String, end: String ): GlslCodeString = {
    append(start)
    var first = true
    for (str <- strs) {
      if (first) {
        append(str)
        first = false
      } else {
        append(sep)
        append(str)
      }
    }
    append(end)
    this
  }

  def appendAll( strs: GenTraversableOnce[GlslCodeString], start: String, sep: String, end: String ): GlslCodeString = {
    append(start)
    var first = true
    for (str <- strs) {
      if (first) {
        append(str)
        first = false
      } else {
        append(sep)
        append(str)
      }
    }
    append(end)
    this
  }

  def tabIn() = {
    indent += 1
    this
  }

  def tabOut() = {
    indent -= 1
    this
  }

  override def toString: String = {
    lines.map( l => {
      (0 until l.tabs).map( t => "  " ).mkString + l
    } ).mkString( "\n" )
  }

  def toTree[C <: Context](c: C): c.Tree = {
    import c.universe._

    val codeSnippets = ( for ( line <- lines ) yield {
      line.snippets.prepend( new GlslStringSnippet( (0 until line.tabs).map( t => "  " ).mkString ) )
      line.snippets += new GlslStringSnippet( "\n" )
      line.snippets.toList
    } ).flatten

    val simpleCodeSnippets = ListBuffer[GlslCodeSnippet]()
    var lastIsString = false
    for ( snippet <- codeSnippets ) {
      snippet match {
        case s: GlslStringSnippet if lastIsString =>
          simpleCodeSnippets( simpleCodeSnippets.size - 1 ) = new GlslStringSnippet( simpleCodeSnippets.last.toString + s.str )
        case s: GlslStringSnippet =>
          lastIsString = true
          simpleCodeSnippets += snippet
        case _ =>
          lastIsString = false
          simpleCodeSnippets += snippet
      }
    }
//    val simpleCodeSnippets = codeSnippets

    // Convert snippets to a scala AST
    val code = simpleCodeSnippets.map{
      case s: GlslStringSnippet  =>
        Literal( Constant( s.str ) )
      case t: GlslTreeSnippet[_] =>
        t.tree.asInstanceOf[c.Tree]
    }
    if (code.isEmpty)
      q""" "" """
    else
      code.reduce( (a, b) => q" $a + $b " )
  }

}

object GlslCodeString {

  def apply() = new GlslCodeString

  def apply( code: String ) = {
    val str = new GlslCodeString()
    str += code
  }

  def apply( code:  GenTraversableOnce[String] ) = {
    val str = new GlslCodeString()
    str += code
  }

}

class GlslCodeLine( val snippets: mutable.ListBuffer[GlslCodeSnippet], val tabs: Int ) {
  override def toString: String = snippets.mkString("")
}
object GlslCodeLine {

  def apply( snip: GlslCodeSnippet, tabs: Int ) =
    new GlslCodeLine( toList(snip), tabs )

  def apply( lines: Seq[GlslCodeSnippet], tabs: Int ) =
    new GlslCodeLine( toList(lines), tabs )

  private def toList( snip: GlslCodeSnippet ) =
    mutable.ListBuffer[GlslCodeSnippet]( snip )

  private def toList( lines: Seq[GlslCodeSnippet] ) =
    mutable.ListBuffer[GlslCodeSnippet]( lines: _* )

}

trait GlslCodeSnippet
class GlslStringSnippet( val str: String ) extends GlslCodeSnippet {
  override def toString: String = str
}
class GlslTreeSnippet[T](c: Context)( val tree: T ) extends GlslCodeSnippet {
  override def toString: String = c.universe.showRaw( tree.asInstanceOf[c.Tree] )
}
