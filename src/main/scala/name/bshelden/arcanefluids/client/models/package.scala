package name.bshelden.arcanefluids.client

import name.bshelden.arcanefluids.client.render.{Cuboid, Model}

/**
 * Created with IntelliJ IDEA.
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
package object models {
  val TANK_FRAME_STRUT_WIDTH = 1d / 8d

  val ArcaneTankFrameModel = {
    val min = 0d
    val max = 1d
    val w = TANK_FRAME_STRUT_WIDTH

    Model(
      // Top square
      Cuboid(min,     max - w, min,     min + w, max,     max    ),
      Cuboid(max - w, max - w, min,     max,     max,     max    ),
      Cuboid(min + w, max - w, min,     max - w, max,     min + w),
      Cuboid(min + w, max - w, max - w, max - w, max,     max    ),

      // Bottom square
      Cuboid(min,     min,     min,     min + w, min + w, max    ),
      Cuboid(max - w, min,     min,     max,     min + w, max    ),
      Cuboid(min + w, min,     min,     max - w, min + w, min + w),
      Cuboid(min + w, min,     max - w, max - w, min + w, max    ),

      // Vertical struts
      Cuboid(min,     min + w, min,     min + w, max - w, min + w),
      Cuboid(min,     min + w, max - w, min + w, max - w, max    ),
      Cuboid(max - w, min + w, min,     max,     max - w, min + w),
      Cuboid(max - w, min + w, max - w, max,     max - w, max    )
    )
  }
}
