package name.bshelden.arcanefluids.client

import name.bshelden.arcanefluids.ArcaneFluidsCommonProxy
import cpw.mods.fml.client.registry.RenderingRegistry
import net.minecraft.client.renderer.tileentity.{TileEntitySpecialRenderer, TileEntityRenderer}
import name.bshelden.arcanefluids.block.{TileEntityArcanePipe, TileEntityArcaneTank}

/**
 * Client proxy for arcane fluids
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class ArcaneFluidsClientProxy extends ArcaneFluidsCommonProxy {
  override def registerRenderers() {
    RenderingRegistry.registerBlockHandler(new ArcaneTankRender)
    RenderingRegistry.registerBlockHandler(new ArcanePipeRender)

    val renderMap = TileEntityRenderer.instance.specialRendererMap.asInstanceOf[java.util.Map[Class[_], TileEntitySpecialRenderer]]

    val rs = Seq(
      (classOf[TileEntityArcaneTank], new ArcaneTankRender),
      (classOf[TileEntityArcanePipe], new ArcanePipeRender)
    )

    rs foreach { case (cls, render) =>
      renderMap.put(cls, render)
      render.setTileEntityRenderer(TileEntityRenderer.instance)
    }
  }
}
