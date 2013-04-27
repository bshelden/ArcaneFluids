package name.bshelden.arcanefluids.block

import java.util.Random

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.util.Icon
import net.minecraft.client.renderer.texture.IconRegister

/**
 * Block for the arcane pipe
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class BlockArcanePipe(blockId: Int) extends Block(blockId, Material.glass) {
  setCreativeTab(CreativeTabs.tabMisc)

  override def getUnlocalizedName: String = "arcanePipe"

  override def idDropped(metadata: Int, par2Random: Random, fortune: Int): Int = blockId

  override def getIcon(par1: Int, par2: Int): Icon = Block.stone.getIcon(par1, par2)

  override def registerIcons(ir: IconRegister) { }
}
