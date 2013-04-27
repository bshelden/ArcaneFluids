package name.bshelden.arcanefluids.block

import java.util.Random

import net.minecraft.block.{BlockContainer, Block}
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.client.renderer.texture.IconRegister
import net.minecraft.util.Icon
import net.minecraft.world.{World, IBlockAccess}
import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.liquids.LiquidContainerRegistry
import net.minecraft.item.ItemStack
import name.bshelden.arcanefluids.client.ArcaneTankRender
import cpw.mods.fml.relauncher.{SideOnly, Side}

/**
 * Block for the arcane tank
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class BlockArcaneTank(blockId: Int) extends BlockContainer(blockId, Material.iron) {
  setCreativeTab(CreativeTabs.tabMisc)
  setHardness(1.5f)
  setResistance(20f)
  setStepSound(Block.soundGlassFootstep)

  def createNewTileEntity(world: World): TileEntity = new TileEntityArcaneTank

  override def createTileEntity(world: World, metadata: Int): TileEntity = new TileEntityArcaneTank

  override def onBlockActivated(world: World,
                                    x: Int,
                                    y: Int,
                                    z: Int,
                               player: EntityPlayer,
                                 side: Int,
                               clickX: Float,
                               clickY: Float,
                               clickZ: Float)
                                     : Boolean = {
    val te: TileEntityArcaneTank = world.getBlockTileEntity(x, y, z).asInstanceOf[TileEntityArcaneTank]

    Option(player.getCurrentEquippedItem) match {
      case Some(curItem) =>
        Option(LiquidContainerRegistry.getLiquidForFilledItem(curItem)) match {
          case Some(liquid) =>
            val amt = te.fill(0, liquid, false)
            if (amt == liquid.amount) {
              te.fill(0, liquid, true)
              if (!player.capabilities.isCreativeMode) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, consumeItem(curItem))
              }
            }
            world.markBlockForUpdate(x, y, z)
            true

          case None =>
            if (LiquidContainerRegistry.isBucket(curItem)) {
              for {
                liquid <- te.getLiquid
                filledItem <- Option(LiquidContainerRegistry.fillLiquidContainer(liquid, curItem))
              } {
                val amt = LiquidContainerRegistry.getLiquidForFilledItem(filledItem).amount
                te.drain(0, amt, true)
                if (!player.capabilities.isCreativeMode) {
                  if (curItem.stackSize == 1) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, filledItem)
                  } else {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, consumeItem(curItem))

                    if (!player.inventory.addItemStackToInventory(filledItem)) {
                      player.dropPlayerItem(filledItem)
                    }
                  }
                }
              }
              world.markBlockForUpdate(x, y, z)
              true
            } else {
              false
            }
        }

      case None => false
    }
  }

  private def consumeItem(is: ItemStack): ItemStack = {
    if (is.stackSize == 1) {
      if (is.getItem.hasContainerItem) {
        is.getItem.getContainerItemStack(is)
      } else {
        null
      }
    } else {
      is.splitStack(1) // Discard the split, we're consuming it anyway
      is
    }
  }

  override def getLightValue(world: IBlockAccess, x: Int, y: Int, z: Int): Int = {
    val mLiquidLight = for {
      rawTE <- Option(world.getBlockTileEntity(x, y, z))
      if (rawTE.isInstanceOf[TileEntityArcaneTank])
      te = rawTE.asInstanceOf[TileEntityArcaneTank]
    } yield te.getBrightness

    mLiquidLight.getOrElse(0)
  }

  override def getUnlocalizedName: String = "arcaneTank"

  override def idDropped(metadata: Int, random: Random, fortune: Int): Int = blockId

  override def renderAsNormalBlock(): Boolean = false

  override def isOpaqueCube: Boolean = false

  override def shouldSideBeRendered(par1IBlockAccess: IBlockAccess, par2: Int, par3: Int, par4: Int, par5: Int): Boolean = true

  override def getRenderType: Int = ArcaneTankRender.renderId

  override def getIcon(side: Int, metadata: Int): Icon = Block.blockIron.getIcon(side, metadata)

  @SideOnly(Side.CLIENT)
  override def registerIcons(it: IconRegister) { }
}
