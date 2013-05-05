package name.bshelden.arcanefluids.client.render

import net.minecraftforge.liquids.LiquidStack
import net.minecraft.block.Block
import net.minecraft.util.Icon

import name.bshelden.arcanefluids.ArcaneFluids
import name.bshelden.arcanefluids.model.BlockSide

/**
 * Render functions for dealing with liquids
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
object Liquids {
  /** Builds a getTexture function for the given liquid that may then be passed to the various render functions */
  def getTextureForLiquid(liquid: LiquidStack): (BlockSide => Icon) = {
    val block = if (liquid.itemID < Block.blocksList.length && Block.blocksList(liquid.itemID) != null) { Block.blocksList(liquid.itemID) } else { Block.waterStill }
    val mTexture = Option(liquid.canonical().getRenderingIcon)

    { side: BlockSide => mTexture.getOrElse {
      val texSide = if (ArcaneFluids.config.clientRenderLiquidAsTop) { BlockSide.Top.sideId } else { side.sideId }
      block.getIcon(texSide, liquid.itemMeta)
    }}
  }
}
