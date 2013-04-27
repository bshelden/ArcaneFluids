package name.bshelden.arcanefluids.client.render

/**
 * Model for rendering
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class Model(val cuboids: Seq[Cuboid]) extends AnyVal
object Model {
  def apply(cuboids: Cuboid*): Model = new Model(cuboids)
}
