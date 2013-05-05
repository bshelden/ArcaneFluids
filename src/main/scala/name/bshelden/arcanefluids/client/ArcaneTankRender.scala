package name.bshelden.arcanefluids.client

import org.lwjgl.opengl.GL11

import cpw.mods.fml.client.registry.{ISimpleBlockRenderingHandler, RenderingRegistry}

import net.minecraft.block.Block
import net.minecraft.client.renderer.RenderBlocks
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.{World, IBlockAccess}
import net.minecraftforge.liquids.LiquidStack

import name.bshelden.arcanefluids.block.TileEntityArcaneTank
import name.bshelden.arcanefluids.client.render._
import name.bshelden.arcanefluids.model.Cuboid
import name.bshelden.arcanefluids.model.Models._
import name.bshelden.arcanefluids.client.render.Render._
import name.bshelden.arcanefluids.client.render.Liquids._

/**
 * Rendering for the arcane tank.
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
class ArcaneTankRender extends TileEntitySpecialRenderer with ISimpleBlockRenderingHandler {
  import ArcaneTankRender._

  def renderInventoryBlock(block: Block, metadata: Int, modelID: Int, renderer: RenderBlocks) {
    withPushedMatrix {
      withPushedAttrib(GL11.GL_ENABLE_BIT) {
        for {
          _ <- Render {
            GL11.glDisable(GL11.GL_LIGHTING)
            GL11.glEnable(GL11.GL_CULL_FACE)
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F)
          }
          _ <- draw(renderModelWithBlock(ArcaneTankFrameModel, block, 0))
        } yield (())
      }
    }.run()
  }

  def renderWorldBlock(world: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderer: RenderBlocks): Boolean = {
    ArcaneTankFrameModel.cuboids foreach { case c =>
      renderer.setRenderBounds(c.minX, c.minY, c.minZ, c.maxX, c.maxY, c.maxZ)
      renderer.renderStandardBlock(block, x, y, z)
    }

    // TODO determine why returning false makes everything appear correctly
    false
  }

  def shouldRender3DInInventory(): Boolean = true

  def getRenderId: Int = renderId

  def renderTileEntityAt(rawTE: TileEntity, x: Double, y: Double, z: Double, facing: Float) {
    val te = rawTE.asInstanceOf[TileEntityArcaneTank]
    for {
      liquid  <- te.getLiquid
      renders <- Option(getRendersForLiquid(liquid, te.worldObj))
    } {
      withPushedMatrix {
        withPushedAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT) {
          for {
            _ <- Render.liftIO { bindTextureByName(liquid.getTextureSheet()) }
            _ <- Render {
              GL11.glDisable(GL11.GL_LIGHTING)
              GL11.glEnable(GL11.GL_CULL_FACE)
              GL11.glEnable(GL11.GL_BLEND)
              GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
              GL11.glTranslatef(x.toFloat, y.toFloat, z.toFloat)
            }
            stage = ((liquid.amount.toFloat / te.capacity.toFloat) * (MAX_STAGES - 1)).toInt
            _ <- renders(stage)
            _ <- renderField()
          } yield (())
        }
      }.run()
    }
  }

  private def renderField(): Render[Unit] = {
    val fieldMin = 1d / 16d
    val fieldMax = 1 - fieldMin
    val fieldCuboid = Cuboid(fieldMin, fieldMin, fieldMin, fieldMax, fieldMax, fieldMax)

    for {
      _ <- Render { GL11.glColor4f(1f, 1f, 1f, 0.33f) }
      _ <- draw(renderCuboidWithBlock(fieldCuboid, Block.ice))
    } yield (())
  }
}
object ArcaneTankRender {
  val renderId = RenderingRegistry.getNextAvailableRenderId()
  private val MAX_STAGES = 100

  private var renderCache = Map.empty[LiquidStack, Array[Render[Unit]]]
  private def getRendersForLiquid(liquid: LiquidStack, world: World): Array[Render[Unit]] = {
    renderCache.get(liquid).getOrElse {
      val stages = for {
        stage <- 0 until MAX_STAGES
      } yield {
        val w = TANK_FRAME_STRUT_WIDTH
        val fill = (1-(2*w)) * (stage.toDouble / MAX_STAGES.toDouble)
        val cuboid = Cuboid(w, w, w, 1-w, w + fill, 1-w)

        draw(renderCuboid(cuboid, getTextureForLiquid(liquid))).compile()
      }

      val stageArray = stages.toArray
      renderCache += (liquid -> stageArray)
      stageArray
    }
  }
}
