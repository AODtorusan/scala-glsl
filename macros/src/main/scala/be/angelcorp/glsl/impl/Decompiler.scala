package be.angelcorp.glsl.impl

import be.angelcorp.glsl._
import be.angelcorp.glsl.ast._
import be.angelcorp.glsl.util._
import org.slf4j.LoggerFactory

import scala.reflect.macros.whitebox.Context

/**
 * Decompiles the scala AST to GlslNodes
 */
class Decompiler[C <: Context](val c: C, val compilationUnitName: String) extends MacroUtils {
  private val logger = LoggerFactory.getLogger( getClass )
  import c.universe._

  private def getSimpleType( name: String, context: GlslContext ): GlslType = {
    context.findType( name ) match {
      case Some( t ) =>
        logger.trace(s"Using known type for '$name'.")
        t
      case _ =>
        new SimpleType(name, new GlslContext(None))
    }
  }

  private def getArrayType( subTyp: GlslType, context: GlslContext ): GlslType = {
    val name = subTyp.simpleName + "[]"
    context.findType( name ) match {
      case Some( t ) =>
        logger.trace(s"Using known type for '$name'.")
        t
      case _ =>
        logger.debug(s"No definition for '$name' in context: $context")
        new ArrayType( subTyp )
    }
  }

  private def extractTypeName(target: c.Tree, context: GlslContext): GlslType = {
    try {
      val typ = c.typecheck(q"(7.asInstanceOf[$target])").tpe
      GlslType(c)(typ)
    } catch {
      case e: Throwable =>
        // Try do deduce the meant type from the scala code
        target match {
          case Select( _, name ) =>
            getSimpleType( name.toString, context )
          case Ident( name ) =>
            getSimpleType( name.toString, context )
          case Apply(Select(New(Ident( name )), termNames.CONSTRUCTOR), _) =>
            getSimpleType( name.toString, context )
          case AppliedTypeTree(Ident(TypeName("Array")), List( tparam )) =>
            getArrayType( extractTypeName(tparam, context), context )
          case _ =>
            c.error( target.pos, "Failed to extract typename of '" + showRaw(target) + "'. Did you forget to explicitly define the variable or function type?" )
            new SimpleType("Any", context)
        }
    }
  }

  private def extractType(target: Tree): Type = {
    c.typecheck(q"(7.asInstanceOf[$target])").tpe
  }

  private def toParentGlsl( parent: c.Tree ) = {
    val externalSource = Select( Select( parent, TermName("source") ), TermName("toString"))
    new GlslExternalCode[c.type ](c)( externalSource )
  }

  private def importSymbols( typ: Type, context: GlslContext, namespace: String ) {
    for (sym <- typ.decls.sorted) {
      logger.trace(s"Importing type $sym into context")
      val glslCode = annotateWithGlslCode(sym)
      val rsa = annotateWithGlslRuntimeSymbolAnnotation(sym)
      if (glslCode || rsa) {
        importSymbol( sym, context, namespace )
      }
    }
  }
  private def importSymbol( sym: Symbol, context: GlslContext, namespace: String ) {
    sym match {
      case s if s.isClass =>
        val c = s.asClass
        val typ = new SimpleType( c.name.toString, new GlslContext(None) /* TODO; Import real context */, namespace )
        context.types += typ
        logger.debug(s"Imported type '${c.name.toString}' into context as $typ")
//      case vd if vd.isMethod =>
//        val scalaTyp = vd.asMethod.returnType
//        val typ = if (scalaTyp.erasure <:< typeOf[GlslRuntimeSymbol[_]]) {
//          val tparams = scalaTyp.typeParams
//          new GlslScalaType[c.type](c, tparams.head)
//        } else {
//          new GlslScalaType[c.type](c, scalaTyp)
//        }
//        context.types += typ
//        logger.debug(s"Imported type '${vd.name.toString}' into contex as $typ")
      case _ =>
        logger.debug(s"Dont know how to import the following symbol from $namespace: $sym")
    }
  }

  def parentToGlsl(code: c.Tree, context: GlslContext): GlslNode = {
    try {
      val typ = extractType( code )
      def toTermName( t: c.Tree ): c.Tree = t match {
        case Ident(nm) => Ident(nm.toTermName)
        case Select( q, nm ) => Select(q, nm.toTermName)
      }
      typ.companion match {
        case NoType => GlslEmptyNode
        case companion if annotateWithGlslCode(companion.typeSymbol.asClass) =>
          // A parent with GLSL code attached
          importSymbols( companion, context, code.toString() )

          // Return it's GLSL code
          toParentGlsl( toTermName(code) )
        case _ => GlslEmptyNode
      }
    } catch {
      case e: Throwable =>
        logger.warn("Failed extract type of parent: " + code, e )
        GlslEmptyNode
    }
  }

  def codeToGlsl(code: c.Tree, context: GlslContext): GlslNode = {
    code match {
      case EmptyTree      => GlslEmptyNode

      case q"$from.until($until).foreach($v => $f)"                                         => toRangedFor( context, from, until, Literal(Constant(1)), v, f, false )
      case q"$from.until($until).withFilter($autofilterblubber).foreach($v => $f)"          => toRangedFor( context, from, until, Literal(Constant(1)), v, f, false )
      case q"$from.until($until).by($by).foreach($v => $f)"                                 => toRangedFor( context, from, until, by,                   v, f, false )
      case q"$from.until($until).by($by).withFilter($autofilterblubber).foreach($v => $f)"  => toRangedFor( context, from, until, by,                   v, f, false )
      case q"$from.to($to).foreach($v => $f)"                                               => toRangedFor( context, from, to,    Literal(Constant(1)), v, f, true  )
      case q"$from.to($to).withFilter($autofilterblubber).foreach($v => $f)"                => toRangedFor( context, from, to,    Literal(Constant(1)), v, f, true  )
      case q"$from.to($to).by($by).foreach($v => $f)"                                       => toRangedFor( context, from, to,    by,                   v, f, true  )
      case q"$from.to($to).by($by).withFilter($autofilterblubber).foreach($v => $f)"        => toRangedFor( context, from, to,    by,                   v, f, true  )

      case r: Return      => toReturn( r, context )
      case l: Literal     => toConstant( l, context )
      case a: Apply       => toApply( a, context )
      case a: Assign      => toAssign(a, context )

      case Ident( t: TermName ) => toSymbolReference( t, context, code.pos )
      case d: DefDef      => if (annotateWithGlslExcluded(d)) toFunction( d, context ) else  GlslEmptyNode
      case v: ValDef      => if (annotateWithGlslExcluded(v)) toVariable( v, context ) else  GlslEmptyNode

      case i: Import      => toImport(i, context)
      case b: Block       => toBlock(b, context)
      case cd: ClassDef   => toStruct( cd, context )
      case md: ModuleDef  => toInterfaceBlock(md, context)

      case m: Match       => toSwitch( m, context )
      case s: Select      => toSelect( s, context )
      case i: If          => toIf( i, context )

      case cde =>
        throw new Exception( s"Cannot convert '${showRaw(cde)}' to glsl nodes!"  )
    }
  }

  def statement( node: GlslNode ): GlslStatementLike = {
    node match {
      case s: GlslStatementLike => s
      case _ => new GlslStatement( node )
    }
  }

  def block( node: GlslNode ): GlslBlock = {
    node match {
      case s: GlslBlock => s
      case _ => new GlslBlock( List(node) )
    }
  }

  private def toReturn( r: Return, context: GlslContext ) = {
    new GlslReturn( statement(codeToGlsl(r.expr, context)) )
  }

  private def toConstant( l: Literal, context: GlslContext ): GlslNode = l match {
    case Literal( Constant(str: String   ) ) if str.startsWith("#") => new GlslMacro( str )
    case Literal( Constant(value: String ) ) => new GlslConstant(value, GlslType(c)( typeOf[String] ) )
    case Literal( Constant(value: Boolean) ) => new GlslConstant(value.toString, GlslType(c)( typeOf[Boolean] ) )
    case Literal( Constant(value: Byte   ) ) => new GlslConstant(value.toString, GlslType(c)( typeOf[Byte] ) )
    case Literal( Constant(value: Short  ) ) => new GlslConstant(value.toString, GlslType(c)( typeOf[Short] ) )
    case Literal( Constant(value: Int    ) ) => new GlslConstant(value.toString, GlslType(c)( typeOf[Int] ) )
    case Literal( Constant(value: Float  ) ) => new GlslConstant(value.toString + "f", GlslType(c)( typeOf[Float] ) )
    case Literal( Constant(value: Double ) ) => new GlslConstant(value.toString + "d", GlslType(c)( typeOf[Double] ) )
    case q"()" => GlslEmptyNode
    case _ =>
      throw new Exception( s"Failed to deduce literal type: ${showRaw(l)}" )
  }

  private def toApply( a: Apply, context: GlslContext ): GlslNode = a match {
    case Apply( Ident(TermName("raw")), List(arg) ) =>
      new GlslExternalCode(c)( arg )

    case Apply( Select( lhs, func ), List( rhs ) ) if GlslApplyBinary.mappings.contains( func.toString ) =>
      val lhsNode    = codeToGlsl(lhs, context)
      val lhsContext = lhsNode.typ.context
      val function   = lhsContext.findFunction( func.toString ).getOrElse( new GlslUnknownSymbol( func.toString ) )
      new GlslApplyBinary(lhsNode, function, GlslApplyBinary.mappings(func.toString), codeToGlsl(rhs, context) )

    // Array.ofDim( x: Int ) => [array size hint]
    case Apply(TypeApply(Select(Ident(TermName("Array")), TermName("ofDim")), List(typArg)), List(Literal(Constant(size: Int)))) =>
      val typ = new ArrayType( extractTypeName( typArg, context ) )
      new GlslArraySizeHint(size, typ.asInstanceOf[ArrayType] )
    // Array.ofDim( var ) => [array size hint]
    case Apply(TypeApply(Select(Ident(TermName("Array")), TermName("ofDim")), List(typArg)), List( tree )) if context.findVariable(tree.toString()).isDefined =>
      val typ = new ArrayType( extractTypeName( typArg, context ) )
      val variable = context.findVariable(tree.toString()).get
      val size = variable.initializer match {
        case Some( GlslStatement( GlslConstant(value, _) ) ) => value.toInt
        case rhs => throw new Exception( "Cannot create array using Array.ofDim[T]( var ) where var is not a constant! (var: " + variable + ")" )
      }
      new GlslArraySizeHint(size, typ.asInstanceOf[ArrayType] )
    // Array[T]( x, y, z ) => T[3](x, y, z)
    case Apply(TypeApply(Ident(TermName("Array")), List(typArg)), args) =>
      val typ = new ArrayType( extractTypeName( typArg, context ) )
      new GlslArrayConstructor( args.map( a => codeToGlsl(a, context) ), typ )

    case Apply(Select(lhs, t: TermName), List(idx, rhs)) if t.toString == "update" =>
      val qualifier = codeToGlsl(lhs, context)
      val index     = codeToGlsl(idx, context)
      val newValue  = codeToGlsl(rhs, context)
      if ( qualifier.isInstanceOf[GlslSelect] || qualifier.typ.isArray ) {
        new GlslAssign( new GlslArrayAccess(qualifier, index), newValue )
      } else {
        new GlslApply( toSymbolReference(TermName(lhs.toString()), context, a.pos) , List(index, newValue))
      }

    case Apply( fun, args ) =>
      val qualifier = codeToGlsl(fun, context)
      qualifier match {
        case t if args.length == 1 && t.typ.isArray =>
          new GlslArrayAccess(qualifier, codeToGlsl(args.head, context))
        case s: GlslSelect =>
          new GlslArrayAccess(qualifier, codeToGlsl(args.head, context))
        case _ =>
          new GlslApply( toSymbolReference(TermName(fun.toString()), context, fun.pos), args.map( a => codeToGlsl( a, context ) ) )
      }
  }

  private def toAssign( a: Assign, context: GlslContext ): GlslNode = {
    new GlslAssign( codeToGlsl(a.lhs, context), codeToGlsl(a.rhs, context) )
  }

  private def decodeCasePattern( t: c.Tree, context: GlslContext ): List[String] = {
    t match {
      case Ident(termNames.WILDCARD) =>
        List( "#default#" )
      case Alternative( trees ) =>
        trees.map( tree => decodeCasePattern(tree, context) ).flatten
      case tree =>
        List( new Compiler[c.type](c).serialize( codeToGlsl( tree, context ) ).toString )
    }
  }

  private def getModifiers(mods: Modifiers, context: GlslContext): List[String] = {
    def isGlslModifier( typ: Type ) = typ <:< c.universe.typeOf[GlslModifier]

    mods.annotations.flatMap {
      case Apply( Select(New(name), _), args ) if args.nonEmpty =>
        val typ = extractType(name)
        if (isGlslModifier( typ )) {
          val params = args map {
            case AssignOrNamedArg(Ident(TermName("memoryLayout")), rhs) => rhs.toString()
            case AssignOrNamedArg( param, rhs ) => param.toString() + " = " + rhs.toString
            case Ident( n ) => n.toString
          }
          Some(typ.typeSymbol.name.toString + params.map( _.filter( _.toInt < 0xff ) ).mkString( "(", ", ", ")" ))
        } else
          None
      case ann =>
        val typ = extractType(ann)
        if (isGlslModifier( typ )) {
          Some(typ.typeSymbol.name.toString)
        } else
          None
    }
  }

  private def getImplementation[T](rhs: c.Tree, context: GlslContext): Option[GlslNode] = {
    rhs match {
      case EmptyTree | Ident(TermName("$qmark$qmark$qmark")) | Ident(TermName("__")) => None
      case code => Some(codeToGlsl( code, context ))
    }
  }

  private def toSymbolReference( term: Name, context: GlslContext, pos: Position = c.enclosingPosition ): GlslSymbolReference = {
    val name = term.toTermName.toString
    context.findVariable( name ) match {
      case Some( variable ) =>
        new GlslSymbolReference( variable )
      case _ =>
        //c.warning( pos, s"Failed to find symbol matching with name '$name' in context '$context'" )
        logger.info( s"Failed to find symbol matching with name '$name' in context '$context'" )
        new GlslSymbolReference( new GlslUnknownSymbol(name) )
    }
  }

  private def toVariable(v: c.universe.ValDef, context: GlslContext): GlslVariable = {
    val mods = getModifiers( v.mods, context )
    val implementation = getImplementation(v.rhs, context)

    val variable = new GlslVariable( v.name.toString, extractTypeName(v.tpt, context), new GlslContext(None), implementation.map( statement ), mods )
    context.variables += variable
    logger.trace( s"Added variable '${variable.name}' to context $context" )
    variable
  }

  private def toFunction(d: c.universe.DefDef, context: GlslContext): GlslFunction= {
    val mods     = getModifiers( d.mods, context )
    val fContext = context.fork()
    val params   = d.vparamss.flatten.map( v => toVariable(v, fContext) )
    val implementation = getImplementation(d.rhs, fContext)

    val func = new GlslFunction( d.name.toString, extractTypeName(d.tpt, context), params, fContext, implementation.map( block ), mods )
    context.functions += func
    logger.trace( s"Added function '${func.name}' to context $context" )
    func
  }

  private def toBlock( b: Block, context: GlslContext ): GlslBlock = {
    val expressions = (b.stats ::: List(b.expr)).map( s => statement( codeToGlsl( s, context )) )
    new GlslBlock( expressions )
  }

  private def toImport( i: Import, context: GlslContext ): GlslNode = {
    val imports =
      for ( selector <- i.selectors ) yield {
        selector match {
          case ImportSelector(termNames.WILDCARD, _, _, _) =>
            val typ = extractType( i.expr )
            // Import the symbols
            importSymbols( typ, context, i.expr.toString() )

            // Return the GLSL code
            if ( typ.typeSymbol.asClass.annotations.exists( a =>  a.tree.tpe == typeOf[GlslCode] ) ) {
              toParentGlsl( i.expr )
            } else {
              logger.trace(s"Import was not tagged with GlslCode, so no glsl source attached! (typ: ${showRaw(typ)} | annotations: ${typ.typeSymbol.asClass.annotations.toList} ): ")
              GlslEmptyNode
            }
          case _ =>
            val invocation = Select( i.expr, selector.name )
            val typ = c.typecheck( invocation )
            ???
        }
      }
    GlslElements( imports )
  }

  private def toStruct( cd: ClassDef, context: GlslContext ): GlslStruct = {
    val sContext = context.fork()
    val name = cd.name.toString
    cd.impl match {
      case Template(_, _, body) =>
        val args = body flatMap {
          case v: ValDef =>
            Some(v)
          case DefDef(_, termNames.CONSTRUCTOR, _, _, _, Block(List(c.universe.pendingSuperCall), _)) => None // Don't show a warning on the super call
          case ast =>
            c.warning( ast.pos, "A struct/case class only converts 'var' or 'val' definitions into glsl code!" )
            None
        }

        context.types += new SimpleType( name, sContext, compilationUnitName )
        new GlslStruct( name, args.map( v => toVariable(v, sContext) ), sContext )
    }
  }

  private def toInterfaceBlock(m: c.universe.ModuleDef, context: GlslContext): GlslInterfaceBlock = {
    val mContext  = context.fork()
    val modifiers = getModifiers(m.mods, context)
    val name      = m.name.toString
    m.impl match{
      case Template(parents, _, body) =>
        if ( parents.collectFirst {
          case Ident(TypeName("InterfaceBlock")) => true
          case Apply(Ident(TypeName("InterfaceBlockArray")), _) => true
        }.isEmpty ) c.warning(m.pos, s"Creating interface block without extending from InterfaceBlock (on object $name)")

        val args = body flatMap {
          case v: ValDef =>
            Some(v)
          case DefDef(_, termNames.CONSTRUCTOR, _, _, _, Block(List(c.universe.pendingSuperCall), _)) => None // Don't show a warning on the super call
          case ast =>
            c.warning( ast.pos, "An interface block only converts 'var' or 'val' definitions into glsl code! " )
            None
        }

        val sizer = parents.collectFirst{
          case Apply(Ident(TypeName("InterfaceBlockArray")), List(Literal(Constant( size: Int )))) => size
        }
        val glslInterfaceBlock = new GlslInterfaceBlock( name, args.map( v => toVariable(v, mContext) ), mContext, modifiers, sizer )
        context.variables += glslInterfaceBlock.asVariable
        logger.trace( s"Added inteface block variable '$name' to context $context" )
        glslInterfaceBlock
    }
  }

  private def toSwitch( m: Match, context: GlslContext ): GlslSwitch = {
    val sym = context.find(m.selector.toString()).getOrElse( new GlslUnknownSymbol(m.selector.toString()) )
    val entries = m.cases map {
      case CaseDef(pattern, _, rhs) =>
        val cases = decodeCasePattern(pattern, context)
        new GlslSwitchEntry(cases, statement(codeToGlsl( rhs, context.fork() )) )
    }
    new GlslSwitch( sym, entries )
  }

  private def toSelect( s: Select, context: GlslContext ) = {
    s match {
      case Select( qualifier, funcName: TermName ) if funcName.toString.startsWith("unary_") =>
        new GlslApplyUnary( codeToGlsl(qualifier, context), funcName.decodedName.toString.substring(6) )
      case _ =>
        val qualifier = codeToGlsl(s.qualifier, context)
        new GlslSelect( qualifier, toSymbolReference(s.name, qualifier.typ.context, s.pos) )
    }
  }

  private def toIf( i: If, context: GlslContext ): GlslIf = {
    val condNode  = codeToGlsl(i.cond, context)
    val trueNode  = codeToGlsl(i.thenp, context.fork())
    val falseNode = codeToGlsl(i.elsep, context.fork())
    val f = falseNode match {
      case e: GlslEmpty => None
      case elseNode => Some( block(elseNode) )
    }
    new GlslIf( condNode, block(trueNode), f )
  }

  private def toRangedFor( context: GlslContext, from: Tree, end: Tree, by: Tree, v: ValDef, f: Tree, isTo: Boolean ): GlslNode = {
    val fContext = context.fork()
    val variable = toVariable( v, fContext )
    val fromNode = codeToGlsl( from, context )
    val endNode  = codeToGlsl( end,  context )
    val byNode   = codeToGlsl( by,   context )
    val funcNode = codeToGlsl( f,    context )
    GlslRangeFor( variable, fromNode, endNode, byNode, block(funcNode), isTo )
  }

  def annotateWithGlslCode( s: Symbol ): Boolean = {
    s.annotations.exists( a =>  a.tree.tpe == typeOf[GlslCode] )
  }

  def annotateWithGlslRuntimeSymbolAnnotation( s: Symbol ): Boolean = {
    s.annotations.exists( a =>  a.tree.tpe == typeOf[GlslRuntimeSymbolAnnotation] )
  }

  def annotateWithGlslExcluded( vdd: ValOrDefDef ): Boolean = {
    vdd.mods.annotations.collectFirst { case q"new GlslExcluded()" => false } getOrElse true
  }

}
