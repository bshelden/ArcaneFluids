package name.bshelden.arcanefluids.flow

import net.minecraftforge.liquids.{LiquidStack, LiquidTank}
import name.bshelden.arcanefluids.misc.TGuid
import net.minecraft.nbt.NBTTagCompound
import name.bshelden.arcanefluids.ArcaneFluids
import java.util.logging.Level

/**
 * Contains the state of a flow network.
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class FlowNetwork(val id: TGuid[FlowNetwork], val tank: LiquidTank) {
  import FlowNetwork._

  def getLiquid: Option[LiquidStack] = Option(tank.getLiquid)

  def readFromNBT(nbt: NBTTagCompound) {
    try {
      if (nbt.getBoolean(NBT_HAS_LIQUID)) {
        val mNBT = if (nbt.hasKey(NBT_LIQUID_TAG)) {
          Some(nbt.getCompoundTag(NBT_LIQUID_TAG))
        } else None

        tank.setLiquid(new LiquidStack(
          nbt.getInteger(NBT_LIQUID_ID),
          nbt.getInteger(NBT_LIQUID_AMT),
          nbt.getInteger(NBT_LIQUID_META),
          mNBT.getOrElse(null)
        ))
      } else {
        tank.setLiquid(null)
      }
    } catch {
      case t: Throwable =>
        ArcaneFluids.logger.log(Level.WARNING, "Exception caught when reading the NBT for a flow network!  Discarding contents!", t)
        tank.setLiquid(null)
    }
  }

  def writeToNBT(nbt: NBTTagCompound) {

  }
}
object FlowNetwork {
  val NBT_HAS_LIQUID  = "hasLiquid"
  val NBT_LIQUID_ID   = "liquidId"
  val NBT_LIQUID_META = "liquidMeta"
  val NBT_LIQUID_TAG  = "tag"
  val NBT_LIQUID_AMT  = "liquidAmt"
}
