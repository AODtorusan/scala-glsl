package be.angelcorp.glsl

import java.nio.charset.Charset
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.{Files, Path, Paths}

import scala.collection.JavaConversions

object Glslang {

  val isWin = System.getProperty("os.name").startsWith("Windows")

  lazy val exec = {
    val execLoc = if (isWin) {
      "/glslang/Windows/glslangValidator.exe"
    } else {
      "/glslang/Linux/glslangValidator"
    }
    val exec =  Paths.get( getClass.getResource(execLoc).toURI )
    if (!isWin) {
      val perms = Files.getPosixFilePermissions(exec)
      perms.add(PosixFilePermission.OWNER_EXECUTE)
      Files.setPosixFilePermissions(exec, perms)
    }
    exec.toFile.getAbsolutePath
  }

  def runValidator( shaderCode: String, typ: ShaderType ): (Int, String) = {
    val f: Path = Files.createTempFile("shadercode", typ.extension)
    Files.write(f, JavaConversions.asJavaIterable(shaderCode.split('\n').toIterable), Charset.defaultCharset())
    f.toFile.deleteOnExit()

    import scala.sys.process._
    val outputListener = new ProcessLogger {
      private val buffer = new StringBuilder()

      def output: String = buffer.toString()

      override def out(s: => String) = buffer.append(s)

      override def buffer[T](f: => T) = f

      override def err(s: => String) = buffer.append(s)
    }
    val status = Seq(exec, f.toFile.getAbsolutePath).!(outputListener)

    status -> outputListener.output
  }

}

sealed abstract class ShaderType( val extension: String )
object Vertex extends  ShaderType( ".vert" )
object Fragment extends  ShaderType( ".frag" )
