package name.bshelden.arcanefluids.block

import java.util.logging.Level

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.INetworkManager
import net.minecraft.network.packet.{Packet, Packet132TileEntityData}
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.ForgeDirection
import net.minecraftforge.liquids._

import name.bshelden.arcanefluids.ArcaneFluids
import net.minecraft.block.Block

/**
 * Tile entity for the arcane tank.
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class TileEntityArcaneTank() extends TileEntity with ITankContainer {
  val capacity = LiquidContainerRegistry.BUCKET_VOLUME * 16

  val NBT_HAS_LIQUID  = "hasLiquid"
  val NBT_LIQUID_ID   = "liquidId"
  val NBT_LIQUID_META = "liquidMeta"
  val NBT_LIQUID_TAG  = "tag"
  val NBT_LIQUID_AMT  = "liquidAmt"

  private val tank: LiquidTank = new LiquidTank(capacity)

  private var tick: Int = 0
  private var mSavedLiquid: Option[LiquidStack] = None

  override def canUpdate: Boolean = true

  override def updateEntity() {
    if (!worldObj.isRemote) {
      tick += 1
      if (tick % 10 == 0) {
        tick = 0

        Option(tank.getLiquid) match {
          case Some(current) =>
            mSavedLiquid match {
              case Some(saved) =>
                if (!current.isLiquidEqual(saved)) {
                  worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
                  mSavedLiquid = Some(current.copy())
                }

              case None =>
                mSavedLiquid = Some(current.copy())
            }

          case None =>
            if (mSavedLiquid.isDefined) {
              worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
              mSavedLiquid = None
            }
        }
      }
    }
  }

  def fill(from: ForgeDirection, resource: LiquidStack, doFill: Boolean): Int = tank.fill(resource, doFill)

  def fill(tankIndex: Int, resource: LiquidStack, doFill: Boolean): Int = tank.fill(resource, doFill)

  def drain(from: ForgeDirection, maxDrain: Int, doDrain: Boolean): LiquidStack = tank.drain(maxDrain, doDrain)

  def drain(tankIndex: Int, maxDrain: Int, doDrain: Boolean): LiquidStack = tank.drain(maxDrain, doDrain)

  def getTanks(direction: ForgeDirection): Array[ILiquidTank] = Array(tank)

  def getTank(direction: ForgeDirection, `type`: LiquidStack): ILiquidTank = tank

  def getLiquid: Option[LiquidStack] = Option(tank.getLiquid)

  def getBrightness: Int = {
    val mLiquidBrightness = for {
      lq <- getLiquid
      if (lq.itemID < Block.lightValue.length)
    } yield (Block.lightValue(lq.itemID))

    mLiquidBrightness.getOrElse(0)
  }

  override def readFromNBT(nbt: NBTTagCompound) {
    super.readFromNBT(nbt)

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
        ArcaneFluids.logger.log(Level.WARNING, "Exception caught when reading the NBT for an arcane tank!  Discarding contents!", t)
        tank.setLiquid(null)
    }
  }

  override def writeToNBT(nbt: NBTTagCompound) {
    super.writeToNBT(nbt)

    Option(tank.getLiquid) match {
      case Some(liquid) =>
        nbt.setBoolean(NBT_HAS_LIQUID,  true)
        nbt.setInteger(NBT_LIQUID_ID,   liquid.itemID)
        nbt.setInteger(NBT_LIQUID_META, liquid.itemMeta)
        nbt.setInteger(NBT_LIQUID_AMT,  liquid.amount)

        Option(liquid.extra) foreach { tag =>
          nbt.setCompoundTag(NBT_LIQUID_TAG, tag)
        }

      case None =>
        nbt.setBoolean(NBT_HAS_LIQUID, false)
    }
  }

  override def getDescriptionPacket: Packet = {
    val tag: NBTTagCompound = new NBTTagCompound
    writeToNBT(tag)
    new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag)
  }

  override def onDataPacket(net: INetworkManager, pkt: Packet132TileEntityData) {
    readFromNBT(pkt.customParam1)
  }
}
