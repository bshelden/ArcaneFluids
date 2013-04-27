package name.bshelden.arcanefluids.client

import name.bshelden.arcanefluids.ArcaneFluidsCommonProxy
import cpw.mods.fml.client.registry.RenderingRegistry
import net.minecraft.client.renderer.tileentity.{TileEntitySpecialRenderer, TileEntityRenderer}
import name.bshelden.arcanefluids.block.TileEntityArcaneTank

/**
 * Client proxy for arcane fluids
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class ArcaneFluidsClientProxy extends ArcaneFluidsCommonProxy {
  override def registerRenderers() {
    RenderingRegistry.registerBlockHandler(new ArcaneTankRender)

    val renderMap = TileEntityRenderer.instance.specialRendererMap.asInstanceOf[java.util.Map[Class[_], TileEntitySpecialRenderer]]

    val renderer = new ArcaneTankRender
    renderMap.put(classOf[TileEntityArcaneTank], renderer)
    renderer.setTileEntityRenderer(TileEntityRenderer.instance)
  }
}
