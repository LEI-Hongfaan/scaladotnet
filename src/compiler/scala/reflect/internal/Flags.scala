/* NSC -- new Scala compiler
 * Copyright 2005-2011 LAMP/EPFL
 * @author  Martin Odersky
 */

package scala.reflect
package internal

import api.Modifier
import scala.collection.{ mutable, immutable }

// Flags at each index of a flags Long.  Those marked with /M are used in
// Parsers/JavaParsers and therefore definitely appear on Modifiers; but the
// absence of /M on the other flags does not imply they aren't.
//
// Generated by mkFlagsTable() at Thu Feb 02 20:31:52 PST 2012
//
//  0:     PROTECTED/M
//  1:      OVERRIDE/M
//  2:       PRIVATE/M
//  3:      ABSTRACT/M
//  4:      DEFERRED/M
//  5:         FINAL/M
//  6:          METHOD
//  7:     INTERFACE/M
//  8:          MODULE
//  9:      IMPLICIT/M
// 10:        SEALED/M
// 11:          CASE/M
// 12:       MUTABLE/M
// 13:         PARAM/M
// 14:         PACKAGE
// 15:         MACRO/M
// 16:   BYNAMEPARAM/M      CAPTURED COVARIANT/M
// 17: CONTRAVARIANT/M INCONSTRUCTOR       LABEL
// 18:   ABSOVERRIDE/M
// 19:         LOCAL/M
// 20:          JAVA/M
// 21:       SYNTHETIC
// 22:          STABLE
// 23:        STATIC/M
// 24:  CASEACCESSOR/M
// 25:  DEFAULTPARAM/M       TRAIT/M
// 26:          BRIDGE
// 27:        ACCESSOR
// 28:   SUPERACCESSOR
// 29: PARAMACCESSOR/M
// 30:       MODULEVAR
// 31:          LAZY/M
// 32:        IS_ERROR
// 33:      OVERLOADED
// 34:          LIFTED
// 35:     EXISTENTIAL       MIXEDIN
// 36:    EXPANDEDNAME
// 37:       IMPLCLASS    PRESUPER/M
// 38:      TRANS_FLAG
// 39:          LOCKED
// 40:     SPECIALIZED
// 41:   DEFAULTINIT/M
// 42:         VBRIDGE
// 43:         VARARGS
// 44:    TRIEDCOOKING
// 45:
// 46:
// 47:
// 48:
// 49:
// 50:
// 51:    lateDEFERRED
// 52:       lateFINAL
// 53:      lateMETHOD
// 54:   lateINTERFACE
// 55:      lateMODULE
// 56:    notPROTECTED
// 57:     notOVERRIDE
// 58:      notPRIVATE
// 59:
// 60:
// 61:
// 62:
// 63:

/** Flags set on Modifiers instances in the parsing stage.
 */
class ModifierFlags {
  final val IMPLICIT      = 0x00000200
  final val FINAL         = 0x00000020
  final val PRIVATE       = 0x00000004
  final val PROTECTED     = 0x00000001

  final val SEALED        = 0x00000400
  final val OVERRIDE      = 0x00000002
  final val CASE          = 0x00000800
  final val ABSTRACT      = 0x00000008    // abstract class, or used in conjunction with abstract override.
                                          // Note difference to DEFERRED!
  final val DEFERRED      = 0x00000010    // was `abstract' for members | trait is virtual
  final val INTERFACE     = 0x00000080    // symbol is an interface (i.e. a trait which defines only abstract methods)
  final val MUTABLE       = 0x00001000    // symbol is a mutable variable.
  final val PARAM         = 0x00002000    // symbol is a (value or type) parameter to a method
  final val MACRO         = 0x00008000    // symbol is a macro definition

  final val COVARIANT     = 0x00010000    // symbol is a covariant type variable
  final val BYNAMEPARAM   = 0x00010000    // parameter is by name
  final val CONTRAVARIANT = 0x00020000    // symbol is a contravariant type variable
  final val ABSOVERRIDE   = 0x00040000    // combination of abstract & override
  final val LOCAL         = 0x00080000    // symbol is local to current class (i.e. private[this] or protected[this]
                                          // pre: PRIVATE or PROTECTED are also set
  final val JAVA          = 0x00100000    // symbol was defined by a Java class
  final val STATIC        = 0x00800000    // static field, method or class
  final val CASEACCESSOR  = 0x01000000    // symbol is a case parameter (or its accessor)
  final val TRAIT         = 0x02000000    // symbol is a trait
  final val DEFAULTPARAM  = 0x02000000    // the parameter has a default value
  final val PARAMACCESSOR = 0x20000000    // for field definitions generated for primary constructor
                                          //   parameters (no matter if it's a 'val' parameter or not)
                                          // for parameters of a primary constructor ('val' or not)
                                          // for the accessor methods generated for 'val' or 'var' parameters
  final val LAZY          = 0x80000000L   // symbol is a lazy val. can't have MUTABLE unless transformed by typer
  final val PRESUPER      = 0x2000000000L // value is evaluated before super call
  final val DEFAULTINIT   = 0x20000000000L// symbol is initialized to the default value: used by -Xcheckinit

  // Overridden.
  def flagToString(flag: Long): String = ""

  final val PrivateLocal: Long   = PRIVATE | LOCAL
  final val ProtectedLocal: Long = PROTECTED | LOCAL
  final val AccessFlags: Long    = PRIVATE | PROTECTED | LOCAL
}
object ModifierFlags extends ModifierFlags

/** All flags and associated operatins */
class Flags extends ModifierFlags {
  final val METHOD        = 0x00000040    // a method
  final val MODULE        = 0x00000100    // symbol is module or class implementing a module
  final val PACKAGE       = 0x00004000    // symbol is a java package

  final val CAPTURED      = 0x00010000    // variable is accessed from nested function.  Set by LambdaLift.
  final val LABEL         = 0x00020000    // method symbol is a label. Set by TailCall
  final val INCONSTRUCTOR = 0x00020000    // class symbol is defined in this/superclass constructor.
  final val SYNTHETIC     = 0x00200000    // symbol is compiler-generated
  final val STABLE        = 0x00400000    // functions that are assumed to be stable
                                          // (typically, access methods for valdefs)
                                          // or classes that do not contain abstract types.
  final val BRIDGE        = 0x04000000    // function is a bridge method. Set by Erasure
  final val ACCESSOR      = 0x08000000    // a value or variable accessor (getter or setter)

  final val SUPERACCESSOR = 0x10000000    // a super accessor
  final val MODULEVAR     = 0x40000000    // for variables: is the variable caching a module value

  final val IS_ERROR      = 0x100000000L  // symbol is an error symbol
  final val OVERLOADED    = 0x200000000L  // symbol is overloaded
  final val LIFTED        = 0x400000000L  // class has been lifted out to package level
                                          // local value has been lifted out to class level
                                          // todo: make LIFTED = latePRIVATE?
  final val MIXEDIN       = 0x800000000L  // term member has been mixed in
  final val EXISTENTIAL   = 0x800000000L  // type is an existential parameter or skolem
  final val EXPANDEDNAME  = 0x1000000000L // name has been expanded with class suffix
  final val IMPLCLASS     = 0x2000000000L // symbol is an implementation class
  final val TRANS_FLAG    = 0x4000000000L // transient flag guaranteed to be reset after each phase.

  final val LOCKED        = 0x8000000000L // temporary flag to catch cyclic dependencies
  final val SPECIALIZED   = 0x10000000000L// symbol is a generated specialized member
  final val VBRIDGE       = 0x40000000000L// symbol is a varargs bridge

  final val VARARGS       = 0x80000000000L// symbol is a Java-style varargs method
  final val TRIEDCOOKING  = 0x100000000000L // ``Cooking'' has been tried on this symbol
                                            // A Java method's type is ``cooked'' by transforming raw types to existentials

  final val SYNCHRONIZED  = 0x200000000000L // symbol is a method which should be marked ACC_SYNCHRONIZED
  // ------- shift definitions -------------------------------------------------------

  final val InitialFlags  = 0x0001FFFFFFFFFFFFL // flags that are enabled from phase 1.
  final val LateFlags     = 0x00FE000000000000L // flags that override flags in 0x1FC.
  final val AntiFlags     = 0x7F00000000000000L // flags that cancel flags in 0x07F
  final val LateShift     = 47L
  final val AntiShift     = 56L

  // ------- late flags (set by a transformer phase) ---------------------------------
  //
  // Summary of when these are claimed to be first used.
  // You can get this output with scalac -Xshow-phases -Ydebug.
  //
  //     refchecks   7  [START] <latemethod>
  //    specialize  13  [START] <latefinal> <notprivate>
  // explicitouter  14  [START] <notprotected>
  //       erasure  15  [START] <latedeferred> <lateinterface>
  //         mixin  20  [START] <latemodule> <notoverride>
  //
  // lateMETHOD set in RefChecks#transformInfo.
  // lateFINAL set in Symbols#makeNotPrivate.
  // notPRIVATE set in Symbols#makeNotPrivate, IExplicitOuter#transform, Inliners.
  // notPROTECTED set in ExplicitOuter#transform.
  // lateDEFERRED set in AddInterfaces, Mixin, etc.
  // lateINTERFACE set in AddInterfaces#transformMixinInfo.
  // lateMODULE set in Mixin#transformInfo.
  // notOVERRIDE set in Mixin#preTransform.

  final val lateDEFERRED  = (DEFERRED: Long) << ((LateShift) & 0x3f)
  final val lateFINAL     = (FINAL: Long) << ((LateShift) & 0x3f)
  final val lateINTERFACE = (INTERFACE: Long) << ((LateShift) & 0x3f)
  final val lateMETHOD    = (METHOD: Long) << ((LateShift) & 0x3f)
  final val lateMODULE    = (MODULE: Long) << ((LateShift) & 0x3f)

  final val notOVERRIDE   = (OVERRIDE: Long) << ((AntiShift) & 0x3f)
  final val notPRIVATE    = (PRIVATE: Long) << ((AntiShift) & 0x3f)
  final val notPROTECTED  = (PROTECTED: Long) << ((AntiShift) & 0x3f)

  // ------- masks -----------------------------------------------------------------------

  /** These flags can be set when class or module symbol is first created.
   *  They are the only flags to survive a call to resetFlags().
   */
  final val TopLevelCreationFlags: Long =
    MODULE | PACKAGE | FINAL | JAVA

  /** These modifiers can be set explicitly in source programs.  This is
   *  used only as the basis for the default flag mask (which ones to display
   *  when printing a normal message.)
   */
  final val ExplicitFlags: Long =
    PRIVATE | PROTECTED | ABSTRACT | FINAL | SEALED |
    OVERRIDE | CASE | IMPLICIT | ABSOVERRIDE | LAZY

  /** These modifiers appear in TreePrinter output. */
  final val PrintableFlags: Long =
    ExplicitFlags | LOCAL | SYNTHETIC | STABLE | CASEACCESSOR | MACRO |
    ACCESSOR | SUPERACCESSOR | PARAMACCESSOR | BRIDGE | STATIC | VBRIDGE | SPECIALIZED | SYNCHRONIZED

  /** The two bridge flags */
  final val BridgeFlags = BRIDGE | VBRIDGE
  final val BridgeAndPrivateFlags = BridgeFlags | PRIVATE

  /** When a symbol for a field is created, only these flags survive
   *  from Modifiers.  Others which may be applied at creation time are:
   *  PRIVATE, LOCAL.
   */
  final val FieldFlags: Long =
    MUTABLE | CASEACCESSOR | PARAMACCESSOR | STATIC | FINAL | PRESUPER | LAZY

  /** Masks for getters and setters, where the flags are derived from those
   *  on the field's modifiers.  Both getters and setters get the ACCESSOR flag.
   *  Getters of immutable values also get STABLE.
   */
  final val GetterFlags = ~(PRESUPER | MUTABLE)
  final val SetterFlags = ~(PRESUPER | MUTABLE | STABLE | CASEACCESSOR)

  /** When a symbol for a default getter is created, it inherits these
   *  flags from the method with the default.  Other flags applied at creation
   *  time are SYNTHETIC, DEFAULTPARAM, and possibly OVERRIDE.
   */
  final val DefaultGetterFlags: Long =
    PRIVATE | PROTECTED | FINAL

  /** When a symbol for a method parameter is created, only these flags survive
   *  from Modifiers.  Others which may be applied at creation time are:
   *  SYNTHETIC.
   */
  final val ValueParameterFlags: Long = BYNAMEPARAM | IMPLICIT | DEFAULTPARAM
  final val BeanPropertyFlags         = DEFERRED | OVERRIDE | STATIC
  final val VarianceFlags             = COVARIANT | CONTRAVARIANT

  /** These appear to be flags which should be transferred from owner symbol
   *  to a newly created constructor symbol.
   */
  final val ConstrFlags: Long         = JAVA

  /** Module flags inherited by their module-class */
  final val ModuleToClassFlags: Long = AccessFlags | MODULE | PACKAGE | CASE | SYNTHETIC | JAVA | FINAL

  def getterFlags(fieldFlags: Long): Long = ACCESSOR + (
    if ((fieldFlags & MUTABLE) != 0) fieldFlags & ~MUTABLE & ~PRESUPER
    else fieldFlags & ~PRESUPER | STABLE
  )

  def setterFlags(fieldFlags: Long): Long =
    getterFlags(fieldFlags) & ~STABLE & ~CASEACCESSOR

 // ------- pickling and unpickling of flags -----------------------------------------------

  // The flags from 0x001 to 0x800 are different in the raw flags
  // and in the pickled format.

  private final val IMPLICIT_PKL   = (1 << 0)
  private final val FINAL_PKL      = (1 << 1)
  private final val PRIVATE_PKL    = (1 << 2)
  private final val PROTECTED_PKL  = (1 << 3)
  private final val SEALED_PKL     = (1 << 4)
  private final val OVERRIDE_PKL   = (1 << 5)
  private final val CASE_PKL       = (1 << 6)
  private final val ABSTRACT_PKL   = (1 << 7)
  private final val DEFERRED_PKL   = (1 << 8)
  private final val METHOD_PKL     = (1 << 9)
  private final val MODULE_PKL     = (1 << 10)
  private final val INTERFACE_PKL  = (1 << 11)

  private final val PKL_MASK       = 0x00000FFF

  final val PickledFlags: Long  = 0xFFFFFFFFL

  private def rawPickledCorrespondence = Array(
    (IMPLICIT, IMPLICIT_PKL),
    (FINAL, FINAL_PKL),
    (PRIVATE, PRIVATE_PKL),
    (PROTECTED, PROTECTED_PKL),
    (SEALED, SEALED_PKL),
    (OVERRIDE, OVERRIDE_PKL),
    (CASE, CASE_PKL),
    (ABSTRACT, ABSTRACT_PKL),
    (DEFERRED, DEFERRED_PKL),
    (METHOD, METHOD_PKL),
    (MODULE, MODULE_PKL),
    (INTERFACE, INTERFACE_PKL)
  )
  private val rawFlags: Array[Int]     = rawPickledCorrespondence map (_._1)
  private val pickledFlags: Array[Int] = rawPickledCorrespondence map (_._2)

  private def r2p(flags: Int): Int = {
    var result = 0
    var i      = 0
    while (i < rawFlags.length) {
      if ((flags & rawFlags(i)) != 0)
        result |= pickledFlags(i)

      i += 1
    }
    result
  }
  private def p2r(flags: Int): Int = {
    var result = 0
    var i      = 0
    while (i < rawFlags.length) {
      if ((flags & pickledFlags(i)) != 0)
        result |= rawFlags(i)

      i += 1
    }
    result
  }

  // ------ displaying flags --------------------------------------------------------

  // Generated by mkFlagToStringMethod() at Thu Feb 02 20:31:52 PST 2012
  @annotation.switch override def flagToString(flag: Long): String = flag match {
    case           PROTECTED => "protected"                           // (1L << 0)
    case            OVERRIDE => "override"                            // (1L << 1)
    case             PRIVATE => "private"                             // (1L << 2)
    case            ABSTRACT => "abstract"                            // (1L << 3)
    case            DEFERRED => "<deferred>"                          // (1L << 4)
    case               FINAL => "final"                               // (1L << 5)
    case              METHOD => "<method>"                            // (1L << 6)
    case           INTERFACE => "<interface>"                         // (1L << 7)
    case              MODULE => "<module>"                            // (1L << 8)
    case            IMPLICIT => "implicit"                            // (1L << 9)
    case              SEALED => "sealed"                              // (1L << 10)
    case                CASE => "case"                                // (1L << 11)
    case             MUTABLE => "<mutable>"                           // (1L << 12)
    case               PARAM => "<param>"                             // (1L << 13)
    case             PACKAGE => "<package>"                           // (1L << 14)
    case               MACRO => "<macro>"                             // (1L << 15)
    case         BYNAMEPARAM => "<bynameparam/captured/covariant>"    // (1L << 16)
    case       CONTRAVARIANT => "<contravariant/inconstructor/label>" // (1L << 17)
    case         ABSOVERRIDE => "absoverride"                         // (1L << 18)
    case               LOCAL => "<local>"                             // (1L << 19)
    case                JAVA => "<java>"                              // (1L << 20)
    case           SYNTHETIC => "<synthetic>"                         // (1L << 21)
    case              STABLE => "<stable>"                            // (1L << 22)
    case              STATIC => "<static>"                            // (1L << 23)
    case        CASEACCESSOR => "<caseaccessor>"                      // (1L << 24)
    case        DEFAULTPARAM => "<defaultparam/trait>"                // (1L << 25)
    case              BRIDGE => "<bridge>"                            // (1L << 26)
    case            ACCESSOR => "<accessor>"                          // (1L << 27)
    case       SUPERACCESSOR => "<superaccessor>"                     // (1L << 28)
    case       PARAMACCESSOR => "<paramaccessor>"                     // (1L << 29)
    case           MODULEVAR => "<modulevar>"                         // (1L << 30)
    case                LAZY => "lazy"                                // (1L << 31)
    case            IS_ERROR => "<is_error>"                          // (1L << 32)
    case          OVERLOADED => "<overloaded>"                        // (1L << 33)
    case              LIFTED => "<lifted>"                            // (1L << 34)
    case         EXISTENTIAL => "<existential/mixedin>"               // (1L << 35)
    case        EXPANDEDNAME => "<expandedname>"                      // (1L << 36)
    case           IMPLCLASS => "<implclass/presuper>"                // (1L << 37)
    case          TRANS_FLAG => "<trans_flag>"                        // (1L << 38)
    case              LOCKED => "<locked>"                            // (1L << 39)
    case         SPECIALIZED => "<specialized>"                       // (1L << 40)
    case         DEFAULTINIT => "<defaultinit>"                       // (1L << 41)
    case             VBRIDGE => "<vbridge>"                           // (1L << 42)
    case             VARARGS => "<varargs>"                           // (1L << 43)
    case        TRIEDCOOKING => "<triedcooking>"                      // (1L << 44)
    case        SYNCHRONIZED => "<synchronized>"                      // (1L << 45)
    case     0x400000000000L => ""                                    // (1L << 46)
    case     0x800000000000L => ""                                    // (1L << 47)
    case    0x1000000000000L => ""                                    // (1L << 48)
    case    0x2000000000000L => ""                                    // (1L << 49)
    case    0x4000000000000L => ""                                    // (1L << 50)
    case      `lateDEFERRED` => "<latedeferred>"                      // (1L << 51)
    case         `lateFINAL` => "<latefinal>"                         // (1L << 52)
    case        `lateMETHOD` => "<latemethod>"                        // (1L << 53)
    case     `lateINTERFACE` => "<lateinterface>"                     // (1L << 54)
    case        `lateMODULE` => "<latemodule>"                        // (1L << 55)
    case      `notPROTECTED` => "<notprotected>"                      // (1L << 56)
    case       `notOVERRIDE` => "<notoverride>"                       // (1L << 57)
    case        `notPRIVATE` => "<notprivate>"                        // (1L << 58)
    case  0x800000000000000L => ""                                    // (1L << 59)
    case 0x1000000000000000L => ""                                    // (1L << 60)
    case 0x2000000000000000L => ""                                    // (1L << 61)
    case 0x4000000000000000L => ""                                    // (1L << 62)
    case 0x8000000000000000L => ""                                    // (1L << 63)
    case _ => ""
  }

  def flagsToString(flags: Long, privateWithin: String): String = {
    var f = flags
    val pw =
      if (privateWithin == "") {
        if ((flags & PrivateLocal) == PrivateLocal) {
          f &= ~PrivateLocal
          "private[this]"
        } else if ((flags & ProtectedLocal) == ProtectedLocal) {
          f &= ~ProtectedLocal
          "protected[this]"
        } else {
          ""
        }
      } else if ((f & PROTECTED) != 0L) {
        f &= ~PROTECTED
        "protected[" + privateWithin + "]"
      } else {
        "private[" + privateWithin + "]"
      }
    List(flagsToString(f), pw) filterNot (_ == "") mkString " "
  }

  // List of the raw flags, in pickled order
  protected final val MaxBitPosition = 62

  def flagsToString(flags: Long): String = {
    // Fast path for common case
    if (flags == 0L) "" else {
      var sb: StringBuilder = null
      var i = 0
      while (i <= MaxBitPosition) {
        val mask = rawFlagPickledOrder(i)
        if ((flags & mask) != 0L) {
          val s = flagToString(mask)
          if (s.Length > 0) {
            if (sb eq null) sb = new StringBuilder append s
            else if (sb.length == 0) sb append s
            else sb append " " append s
          }
        }
        i += 1
      }
      if (sb eq null) "" else sb.ToString
    }
  }

  def rawFlagsToPickled(flags: Long): Long =
    (flags & ~PKL_MASK) | r2p(flags.toInt & PKL_MASK)

  def pickledToRawFlags(pflags: Long): Long =
    (pflags & ~PKL_MASK) | p2r(pflags.toInt & PKL_MASK)

  protected final val pickledListOrder: List[Long] = {
    val all   = 0 to MaxBitPosition map (1L << _)
    val front = rawFlags map (_.toLong)

    front.toList ++ (all filterNot (front contains _))
  }
  protected final val rawFlagPickledOrder: Array[Long] = pickledListOrder.toArray

  def flagOfModifier(mod: Modifier): Long = mod match {
    case Modifier.`protected` => PROTECTED
    case Modifier.`private` => PRIVATE
    case Modifier.`override` => OVERRIDE
    case Modifier.`abstract` => ABSTRACT
    case Modifier.`final`=> FINAL
    case Modifier.`sealed`=> SEALED
    case Modifier.`implicit`=> IMPLICIT
    case Modifier.`lazy`=> LAZY
    case Modifier.`case`=> CASE
    case Modifier.`trait`=> TRAIT
    case Modifier.deferred => DEFERRED
    case Modifier.interface => INTERFACE
    case Modifier.mutable => MUTABLE
    case Modifier.parameter => PARAM
    case Modifier.`macro` => MACRO
    case Modifier.covariant => COVARIANT
    case Modifier.contravariant => CONTRAVARIANT
    case Modifier.preSuper => PRESUPER
    case Modifier.abstractOverride => ABSOVERRIDE
    case Modifier.local => LOCAL
    case Modifier.java => JAVA
    case Modifier.static => STATIC
    case Modifier.caseAccessor => CASEACCESSOR
    case Modifier.defaultParameter => DEFAULTPARAM
    case Modifier.defaultInit => DEFAULTINIT
    case Modifier.paramAccessor => PARAMACCESSOR
    case Modifier.bynameParameter => BYNAMEPARAM
  }

  def flagsOfModifiers(mods: List[Modifier]): Long =
    (mods :\ 0L) { (mod, curr) => curr | flagOfModifier(mod) }

  def modifierOfFlag(flag: Long): Option[Modifier] =
    Modifier.values find { mod => flagOfModifier(mod) == flag }

  def modifiersOfFlags(flags: Long): List[Modifier] =
    pickledListOrder map (mask => modifierOfFlag(flags & mask)) flatMap { mod => mod }
}

object Flags extends Flags { }
