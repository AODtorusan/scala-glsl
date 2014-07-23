package be.angelcorp.glsl.impl

import scala.reflect.macros.whitebox.Context

trait CodeSimplifier[C <: Context] {

  type T

  def c: C

  def simplify( tree: T ): T

}

class Simplifier[C <: Context](val c: C) extends CodeSimplifier[C] {
  type T = c.universe.Tree

  def simplify( tree: c.Tree ): c.Tree =
    doSimplify( tree )._1

  def doSimplify( tree: c.Tree ): (c.Tree, Boolean) = {
    import c.universe._
    val res: (c.Tree, Boolean) = tree match {
      case q" ${str1: String} + ${str2: String} " =>
        q" ${str1 + str2} " -> true
      case q" ${str1: String}.+(${str2: String} + $rest)" =>
        simplify( q" ${str1 + str2} + $rest " ) -> true
      case q" $rest.+(${str1: String}).+(${str2: String})" =>
        simplify( q" $rest + ${str1 + str2} " ) -> true

      case t: Ident   => t -> false
      case t: Literal => t -> false
      case Select(q, n) =>
        val (newQ, qSimplified) = doSimplify(q)
        if (qSimplified) {
          val newtree = doSimplify(Select(newQ.asInstanceOf[c.Tree], n))
          newtree._1 -> true
        } else Select(q, n) -> false
      case Apply(fun, args) =>
        val (newFun, funSimplified) = doSimplify(fun)
        val transformedArgs = args.map(a => doSimplify(a))
        val (newArgs, argsSimplified) = transformedArgs.map(_._1) -> transformedArgs.exists(_._2)
        if (funSimplified || argsSimplified) {
          val newTree = doSimplify(Apply(newFun.asInstanceOf[c.Tree], newArgs.asInstanceOf[List[c.Tree]]))
          newTree._1 -> true
        } else Apply(newFun.asInstanceOf[c.Tree], newArgs.asInstanceOf[List[c.Tree]]) -> false
      case t =>
        throw new Exception( "Dont know how to simplify: " + showRaw(t) )
    }
    res
  }

}

class DummySimplifier[C <: Context](val c: C) extends CodeSimplifier[C] {
  type T = c.universe.Tree
  def simplify( tree: c.Tree ): c.Tree = tree

}
