/* NSC -- new Scala compiler
 * Copyright 2005-2011 LAMP/EPFL
 * @author Paul Phillips
 */

package scala.tools.nsc
package interpreter

import util.stringFromWriter

trait Formatting {
  def prompt: String

  def spaces(code: String): String = {
    /** Heuristic to avoid indenting and thereby corrupting """-strings and XML literals. */
    val tokens = List("\"\"\"", "</", "/>")
    val noIndent = (_root_.java.lang.String.instancehelper_contains(code, "\n")) && (tokens exists { t => _root_.java.lang.String.instancehelper_contains(code, t)} )

    if (noIndent) ""
    else prompt drop 1 map (_ => ' ')
  }
  /** Indent some code by the width of the scala> prompt.
   *  This way, compiler error messages read better.
   */
  def indentCode(code: String) = {
    val indent = spaces(code)
    stringFromWriter(str =>
      for (line <- code.lines) {
        str print indent
        str print (line + "\n")
        str.flush()
      }
    )
  }
}
