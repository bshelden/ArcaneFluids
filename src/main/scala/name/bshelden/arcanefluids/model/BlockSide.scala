package name.bshelden.arcanefluids.model

/**
 * ADT for block sides
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
sealed trait BlockSide {
  val sideId: Int
}

object BlockSide {
  case object Bottom extends BlockSide { val sideId = 0 }
  case object Top    extends BlockSide { val sideId = 1 }
  case object East   extends BlockSide { val sideId = 2 }
  case object West   extends BlockSide { val sideId = 3 }
  case object North  extends BlockSide { val sideId = 4 }
  case object South  extends BlockSide { val sideId = 5 }

  def sideForCoords(x: Float, y: Float, z: Float): BlockSide = {
    (x, y, z) match {
      case (_,  1f, _ ) => Top
      case (_,  0f, _ ) => Bottom
      case (1f, _,  _ ) => East
      case (0f, _,  _ ) => West
      case (_,  _,  0d) => North
      case (_,  _,  1d) => South
    }
  }
}
