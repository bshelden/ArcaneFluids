package name.bshelden.arcanefluids.client

import org.lwjgl.opengl.GL11

import cpw.mods.fml.client.registry.{RenderingRegistry, ISimpleBlockRenderingHandler}
import net.minecraft.block.Block
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.IBlockAccess

import name.bshelden.arcanefluids.client.render._
import name.bshelden.arcanefluids.client.render.Render._
import name.bshelden.arcanefluids.client.render.Liquids._
import name.bshelden.arcanefluids.model._
import name.bshelden.arcanefluids.model.Models._
import name.bshelden.arcanefluids.block.{BlockArcanePipe, TileEntityArcanePipe}
import net.minecraftforge.liquids.LiquidStack

/**
 * Rendering for the arcane pipe.
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class ArcanePipeRender extends TileEntitySpecialRenderer with ISimpleBlockRenderingHandler {
  import ArcanePipeRender._

  def renderInventoryBlock(block: Block, metadata: Int, modelID: Int, renderer: RenderBlocks) {
    withPushedMatrix {
      withPushedAttrib(GL11.GL_ENABLE_BIT) {
        for {
          _ <- Render {
            GL11.glDisable(GL11.GL_LIGHTING)
            GL11.glEnable(GL11.GL_CULL_FACE)
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F)
          }
          _ <- draw(renderModelWithBlock(ArcanePipeConnectionModel, Block.blockNetherQuartz))
        } yield (())
      }
    }.run()
  }

  def renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks): Boolean = {
    if (world.getBlockMetadata(x, y, z) == BlockArcanePipe.META_DIRECTOR) {

      for {
        rawTE <- Option(world.getBlockTileEntity(x, y, z))
        if (rawTE.isInstanceOf[TileEntityArcanePipe])
        te = rawTE.asInstanceOf[TileEntityArcanePipe]
      } {
        val model = Model.fromSeq(te.getConnections.toSeq.map { case (side, _) =>
          orientModelTo(ArcanePipeJunctionModel, side)
        })

        model.cuboids foreach { case c =>
          renderer.setRenderBounds(c.minX, c.minY, c.minZ, c.maxX, c.maxY, c.maxZ)
          renderer.renderStandardBlock(block, x, y, z)
        }
      }
    }

    false
  }

  def shouldRender3DInInventory(): Boolean = true

  def getRenderId: Int = renderId

  def renderTileEntityAt(rawTE: TileEntity, x: Double, y: Double, z: Double, facing: Float) {
    if (rawTE.isInstanceOf[TileEntityArcanePipe]) {
      val te = rawTE.asInstanceOf[TileEntityArcanePipe]

      for {
//        net <- te.getFlowNetwork
//        lq <- net.getLiquid
        lq <- Some(new LiquidStack(Block.lavaStill, 1000))
      } {
        val model = ArcanePipeLiquidNodeModel :+ te.getConnections.toSeq.map { case (side, _) =>
          orientModelTo(ArcanePipeLiquidFlowModel, side)
        }

        withPushedMatrix {
          withPushedAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT) {
            for {
              _ <- Render.liftIO { bindTextureByName("/terrain.png") }
              _ <- Render {
                GL11.glDisable(GL11.GL_LIGHTING)
                GL11.glEnable(GL11.GL_CULL_FACE)
                GL11.glTranslatef(x.toFloat, y.toFloat, z.toFloat)
              }
              _ <- draw(renderModel(model, getTextureForLiquid(lq)))
            } yield (())
          }
        }.run()
      }
    }
  }
}
object ArcanePipeRender {
  val renderId = RenderingRegistry.getNextAvailableRenderId()

  /**
   * This will reorient a model from up to face the requested side
   * @param side the side to orientate the model towards
   */
  private def orientModelTo(model: Model, side: BlockSide) = side match {
    case BlockSide.Bottom => model.rotateOff(RotX, 2, 0.5, 0.5, 0.5)
    case BlockSide.Top    => model
    case BlockSide.East   => model.rotateOff(RotZ, 1, 0.5, 0.5, 0.5)
    case BlockSide.West   => model.rotateOff(RotZ, 3, 0.5, 0.5, 0.5)
    case BlockSide.North  => model.rotateOff(RotX, 1, 0.5, 0.5, 0.5)
    case BlockSide.South  => model.rotateOff(RotX, 3, 0.5, 0.5, 0.5)
  }

  private def orient(m: Model) = new ModelW(m)
  private class ModelW(m: Model) {
    def toDown  = orientModelTo(m, BlockSide.Bottom)
    def toUp    = orientModelTo(m, BlockSide.Top)
    def toEast  = orientModelTo(m, BlockSide.East)
    def toWest  = orientModelTo(m, BlockSide.West)
    def toNorth = orientModelTo(m, BlockSide.North)
    def toSouth = orientModelTo(m, BlockSide.South)
  }
}
