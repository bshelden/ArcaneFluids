package name.bshelden.arcanefluids.model


/**
 * Collection of models for rendering.
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
object Models {
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

  val PIPE_STRUT_WIDTH = 1d / 8d
  val LIQUID_FLOW_WIDTH = 2d / 8d

  /** Connection of a pipe network to something.  Aligned to connect up. */
  val ArcanePipeConnectionModel = {
    val min = 2d / 8d
    val max = 1d - min
    val yMax = 1d
    val w = PIPE_STRUT_WIDTH

    Model(
      Cuboid(min,     yMax - w, min,     min + w, yMax,     max    ),
      Cuboid(max - w, yMax - w, min,     max,     yMax,     max    ),
      Cuboid(min + w, yMax - w, min,     max - w, yMax,     min + w),
      Cuboid(min + w, yMax - w, max - w, max - w, yMax,     max    )
    )
  }
  /** Relay for a pipe network.  Aligned to connect up to down. */
  val ArcanePipeRelayModel = {
    val min = 2d / 8d
    val max = 1d - min
    val yMin = 7d / 16d
    val yMax = 9d / 16d
    val w = PIPE_STRUT_WIDTH

    Model(
      Cuboid(min,     yMin, min,     min + w, yMax, max    ),
      Cuboid(max - w, yMin, min,     max,     yMax, max    ),
      Cuboid(min + w, yMin, min,     max - w, yMax, min + w),
      Cuboid(min + w, yMin, max - w, max - w, yMax, max    )
    )
  }
  /** Segment of a junction.  This is the segment towards up. */
  val ArcanePipeJunctionModel = {
    val min = 2d / 8d
    val max = 1d - min
    val yMin = 5d / 8d
    val yMax = 6d / 8d
    val w = PIPE_STRUT_WIDTH

    Model(
      Cuboid(min,     yMin, min,     min + w, yMax, max    ),
      Cuboid(max - w, yMin, min,     max,     yMax, max    ),
      Cuboid(min + w, yMin, min,     max - w, yMax, min + w),
      Cuboid(min + w, yMin, max - w, max - w, yMax, max    )
    )
  }

  /** Connection of a pipe network to something.  Aligned to connect up. */
  val ArcanePipeLiquidFlowModel = {
    val xzMin = 3d / 8d
    val xzMax = 5d / 8d
    val max = 1d

    Model(
      Cuboid(
        xzMin, 5d / 8d, xzMin,
        xzMax, max,     xzMax
      )
    )
  }

  /** Connection of a pipe network to something.  Centered. */
  val ArcanePipeLiquidNodeModel = {
    val min = 3d / 8d
    val max = 5d / 8d

    Model(
      Cuboid(
        min, min, min,
        max, max, max
      )
    )
  }
}
