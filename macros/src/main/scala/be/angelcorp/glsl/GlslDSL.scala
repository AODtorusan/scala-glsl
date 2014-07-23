package be.angelcorp.glsl

class GlslDSL {

  /** Used to indicate that only a glsl definition is required, and thus is uninitialized.
   *
   * This syntax is an alternative to '???'
   *
   * <pre>
   *   def foo: Int = &#95;&#95;                 =>      int foo;
   *   def foo(bar: Float): Int = &#95;&#95;     =>      int foo(float bar);
   * </pre>
   */
  def __ = throw new NotImplementedError

  /**
   * Used to insert raw GLSL code into the compiled glsl class.
   *
   * @param str GLSL code in insert
   */
  def raw( str: String ) = __

  /** Dummy method to allow IDE's to typecheck on the generated source method */
  def source:  String = __

}
