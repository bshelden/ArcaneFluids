package name.bshelden.arcanefluids.model

/**
 * Model for rendering
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class Model(val cuboids: Seq[Cuboid]) extends AnyVal {
  /** Merges two models together */
  def +(other: Model) = new Model(cuboids ++ other.cuboids)

  /** Merges models together */
  def :+(others: Seq[Model]) = {
    new Model(cuboids ++ others.map(_.cuboids).flatten)
  }

  /** Merges models together */
  def +:(others: Seq[Model]) = {
    new Model(cuboids ++ others.map(_.cuboids).flatten)
  }

  /** Offsets this model by the given amount */
  def offset(dx: Double, dy: Double, dz: Double): Model = {
    new Model(cuboids.map(_.offset(dx, dy, dz)))
  }

  /**
   * Rotates this model around the origin (0,0,0).
   *
   * @param axis the axis to rotate around
   * @param steps the number of 90 degree CCW rotations to do
   */
  def rotate(axis: RotAxis, steps: Int): Model = {
    new Model(cuboids.map(_.rotate(axis, steps)))
  }

  /**
   * Rotates this model around an origin.
   *
   * @param axis the axis to rotate around
   * @param steps the number of 90 degree CCW rotations to do
   * @param oX the x coord of the origin around which to rotate
   * @param oY the y coord of the origin around which to rotate
   * @param oZ the z coord of the origin around which to rotate
   */
  def rotateOff(axis: RotAxis, steps: Int, oX: Double, oY: Double, oZ: Double): Model = {
    new Model(cuboids.map(_.rotateOff(axis, steps, oX, oY, oZ)))
  }
}

object Model {
  def apply(cuboids: Cuboid*): Model = new Model(cuboids)

  def fromSeq(ms: Seq[Model]): Model = {
    new Model((Seq.empty[Cuboid] /: ms.map(_.cuboids)) { _ ++ _ })
  }
}
