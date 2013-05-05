package name.bshelden.arcanefluids.block

import net.minecraft.tileentity.TileEntity
import net.minecraft.nbt.NBTTagCompound
import name.bshelden.arcanefluids.flow.FlowNetwork
import name.bshelden.arcanefluids.ArcaneFluids
import java.util.logging.Level
import net.minecraft.network.packet.{Packet, Packet132TileEntityData}
import net.minecraft.network.INetworkManager
import name.bshelden.arcanefluids.model.BlockSide

/**
 * Tile entity for a single length in an arcane pipe network.
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class TileEntityArcanePipe extends TileEntity {
  import TileEntityArcanePipe._

  private var _mFlowNetwork: Option[FlowNetwork] = None
  private var _connections: Map[BlockSide, Unit] = Map.empty // Map(BlockSide.North -> (), BlockSide.South -> (), BlockSide.West -> (), BlockSide.Top -> ())

  def getFlowNetwork: Option[FlowNetwork] = _mFlowNetwork
  def getConnections: Map[BlockSide, Unit] = _connections

  def toggleConnection(side: BlockSide) {
    if (_connections.contains(side)) {
      _connections -= side
    } else {
      _connections += side -> ()
    }
  }

  override def canUpdate: Boolean = true

  override def updateEntity() { }

  def getBrightness: Int = 0

  override def readFromNBT(nbt: NBTTagCompound) {
    super.readFromNBT(nbt)

    try {
      if (nbt.hasKey(NBT_FLOW_NETWORK)) {
        // TODO implement loading networks
        _mFlowNetwork = None
      } else {
        _mFlowNetwork = None
      }
    } catch {
      case t: Throwable =>
        ArcaneFluids.logger.log(Level.WARNING, "Exception caught when reading the NBT for a pipe length!  Pipe will load without a network.", t)
        _mFlowNetwork = None
        _connections = Map.empty
    }
  }

  override def writeToNBT(nbt: NBTTagCompound) {
    super.writeToNBT(nbt)

    _mFlowNetwork foreach { case network =>
      nbt.setString(NBT_FLOW_NETWORK, network.id.guid)
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
object TileEntityArcanePipe {
  val NBT_FLOW_NETWORK = "flowNetwork"
}
