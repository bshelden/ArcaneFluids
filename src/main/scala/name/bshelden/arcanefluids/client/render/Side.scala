package name.bshelden.arcanefluids.client.render

/**
 * ADT for block sides
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
sealed trait Side {
  val sideId: Int
}
object Side {
  val Bottom = new Side { val sideId = 0 }
  val Top    = new Side { val sideId = 1 }
  val East   = new Side { val sideId = 2 }
  val West   = new Side { val sideId = 3 }
  val North  = new Side { val sideId = 4 }
  val South  = new Side { val sideId = 5 }
}
