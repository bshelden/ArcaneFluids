package name.bshelden.arcanefluids.model

/**
 * ADT for rotation axes
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
sealed trait RotAxis

/**
 * Rotate the YZ plane CCW around the X axis
 *
 * 90 Degrees:
 *  1  0  0
 *  0  0  1
 *  0 -1  0
 **/
case object RotX extends RotAxis

/**
 * Rotate the XZ plane CCW around the Y axis
 *
 * 90 Degrees:
 *  0  0 -1
 *  0  1  0
 *  1  0  0
 **/
case object RotY extends RotAxis

/**
 * Rotate the XY plane CCW around the Z axis
 *
 * 90 Degrees:
 *  0  1  0
 * -1  0  0
 *  0  0  1
 **/
case object RotZ extends RotAxis
