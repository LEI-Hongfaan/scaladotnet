/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2006-2011, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */



package scala.util.parsing.input

/** Undefined position.
 *
 * @author Martin Odersky
 * @author Adriaan Moors
 */
object NoPosition extends Position {
  def line = 0
  def column = 0
  override def ToString = "<undefined position>"
  override def longString = ToString
  def lineContents = ""
}
