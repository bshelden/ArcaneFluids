package name.bshelden.arcanefluids.block

import cpw.mods.fml.relauncher.{SideOnly, Side}

import net.minecraft.block.{BlockContainer, Block}
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.client.renderer.texture.IconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.Icon
import net.minecraft.world.{World, IBlockAccess}

import net.minecraftforge.common.MinecraftForge

import name.bshelden.arcanefluids.client.ArcanePipeRender

/**
 * Block for the arcane pipe.
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class BlockArcanePipe(blockId: Int) extends BlockContainer(blockId, Material.glass) {
  setCreativeTab(CreativeTabs.tabMisc)
  setHardness(1.5f)
  setResistance(20f)
  setStepSound(Block.soundGlassFootstep)
  MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 2)

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
    val mHandled = for {
      rawTE <- Option(world.getBlockTileEntity(x, y, z))
      if (rawTE.isInstanceOf[TileEntityArcanePipe])
      te = rawTE.asInstanceOf[TileEntityArcanePipe]
    } yield {
      val side = name.bshelden.arcanefluids.model.BlockSide.sideForCoords(clickX, clickY, clickZ)

      te.toggleConnection(side)
      world.markBlockForUpdate(x, y, z)

      true
    }

    mHandled.getOrElse(false)
  }

  def createNewTileEntity(world: World): TileEntity = new TileEntityArcanePipe

  override def createTileEntity(world: World, metadata: Int): TileEntity = new TileEntityArcanePipe

  override def getUnlocalizedName: String = "arcanePipe"

  override def damageDropped(metadata: Int): Int = metadata

  override def renderAsNormalBlock(): Boolean = false

  override def isOpaqueCube: Boolean = false

  override def shouldSideBeRendered(par1IBlockAccess: IBlockAccess, par2: Int, par3: Int, par4: Int, par5: Int): Boolean = true

  override def getRenderType: Int = ArcanePipeRender.renderId

  override def getIcon(side: Int, metadata: Int): Icon = Block.blockNetherQuartz.getIcon(side, 1)

  @SideOnly(Side.CLIENT)
  override def registerIcons(ir: IconRegister) { }
}
object BlockArcanePipe {
  val META_DIRECTOR = 0
  val META_LENGTH = 1
}
