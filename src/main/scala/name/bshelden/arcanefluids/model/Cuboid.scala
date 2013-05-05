package name.bshelden.arcanefluids.model

import scala.annotation.tailrec

/**
 * Describes a single cuboid
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
case class Cuboid(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double) {
  import Cuboid._

  /**
   * Normalizes this cuboid, ensuring that min <= max for each axis
   */
  def normalize: Cuboid = {
    val (newMinX, newMaxX) = if (minX <= maxX) { (minX, maxX) } else { (maxX, minX) }
    val (newMinY, newMaxY) = if (minY <= maxY) { (minY, maxY) } else { (maxY, minY) }
    val (newMinZ, newMaxZ) = if (minZ <= maxZ) { (minZ, maxZ) } else { (maxZ, minZ) }

    Cuboid(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ)
  }

  /** Offsets this cuboid by the given amount */
  def offset(dx: Double, dy: Double, dz: Double): Cuboid = {
    Cuboid(
      minX + dx, minY + dy, minZ + dz,
      maxX + dx, maxY + dy, maxZ + dz
    )
  }

  /**
   * Rotates this cuboid around the origin (0,0,0).
   *
   * @param axis the axis to rotate around
   * @param steps the number of 90 degree CCW rotations to do
   */
  def rotate(axis: RotAxis, steps: Int): Cuboid = {
    val s = steps match {
      case n if n == 0 => n
      case n if n < 0 => (4 + (n % 4)) % 4
      case n if n > 0 => n % 4
    }

    rotN(this, axis, s)
  }

  /**
   * Rotates this cuboid around an origin.
   *
   * @param axis the axis to rotate around
   * @param steps the number of 90 degree CCW rotations to do
   * @param oX the x coord of the origin around which to rotate
   * @param oY the y coord of the origin around which to rotate
   * @param oZ the z coord of the origin around which to rotate
   */
  def rotateOff(axis: RotAxis, steps: Int, oX: Double, oY: Double, oZ: Double): Cuboid = {
    val s = steps match {
      case n if n == 0 => n
      case n if n < 0 => (4 + (n % 4)) % 4
      case n if n > 0 => n % 4
    }

    rotN(offset(-oX, -oY, -oZ), axis, s).offset(oX, oY, oZ)
  }
}
object Cuboid {
  /** A cuboid spanning (0,0,0) to (1,1,1) */
  def basicBlock = Cuboid(0, 0, 0, 1, 1, 1)

  /**
   * Rotates this cuboid around the origin (0, 0, 0).
   *
   * Private as we expect the steps to be 0-4 at this point, and also to assist in tail recursion.
   *
   * @param axis the axis to rotate around
   * @param steps the number of 90 degree CCW rotations to do
   */
  @tailrec
  private def rotN(c: Cuboid, axis: RotAxis, steps: Int): Cuboid = {
    if (steps > 0) {
      rotN(rot1(c, axis), axis, steps - 1)
    } else {
      c
    }
  }

  /**
   * Rotates this cuboid once CCW around the given axis.
   *
   * @param axis the axis to rotate around
   */
  private def rot1(c: Cuboid, axis: RotAxis): Cuboid = axis match {
    case RotX =>
      val x1 =  c.minX
      val y1 =  c.minZ
      val z1 = -c.minY

      val x2 =  c.maxX
      val y2 =  c.maxZ
      val z2 = -c.maxY

      Cuboid(x1, y1, z1, x2, y2, z2).normalize

    case RotY =>
      val x1 = -c.minZ
      val y1 =  c.minY
      val z1 =  c.minX

      val x2 = -c.maxZ
      val y2 =  c.maxY
      val z2 =  c.maxX

      Cuboid(x1, y1, z1, x2, y2, z2).normalize

    case RotZ =>
      val x1 =  c.minY
      val y1 = -c.minX
      val z1 =  c.minZ

      val x2 =  c.maxY
      val y2 = -c.maxX
      val z2 =  c.maxZ

      Cuboid(x1, y1, z1, x2, y2, z2).normalize
  }
}
