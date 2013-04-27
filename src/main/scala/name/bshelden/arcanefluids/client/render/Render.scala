package name.bshelden.arcanefluids.client.render

import org.lwjgl.opengl.GL11

import net.minecraft.block.Block
import net.minecraft.client.renderer.{GLAllocation, Tessellator, RenderBlocks}
import net.minecraft.util.Icon

/**
 * Library for working with rendering.
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
sealed trait Render[A] {
  def run(): A

  import Render._

  def compile(): Render[A] = Render.compile(this)

  def map[B](f: A => B) = mk { f(run()) }

  def flatMap[B](f: A => Render[B]) = mk { f(run()).run() }
}

object Render {
  private def mk[A](act: => A): Render[A] = new Render[A] { def run() = act }

  def apply[A](act: => A): Render[A] = mk(act)

  def liftIO[A](act: => A): Render[A] = mk(act)

  def pure[A](a: => A): Render[A] = mk(a)

  def getTextureForBlock(block: Block, metadata: Int = 0): (Side => Icon) = { side =>
    block.getIcon(side.sideId, metadata)
  }

  def renderCuboid(cuboid: Cuboid, getTexture: Side => Icon): Render[Unit] = mk {
    val r: RenderBlocks = new RenderBlocks

    r.renderMinX = cuboid.minX
    r.renderMinY = cuboid.minY
    r.renderMinZ = cuboid.minZ
    r.renderMaxX = cuboid.maxX
    r.renderMaxY = cuboid.maxY
    r.renderMaxZ = cuboid.maxZ
    r.enableAO = false

    r.renderBottomFace(null, 0, 0, 0, getTexture(Side.Bottom))
    r.renderTopFace   (null, 0, 0, 0, getTexture(Side.Top))
    r.renderEastFace  (null, 0, 0, 0, getTexture(Side.East))
    r.renderWestFace  (null, 0, 0, 0, getTexture(Side.West))
    r.renderNorthFace (null, 0, 0, 0, getTexture(Side.North))
    r.renderSouthFace (null, 0, 0, 0, getTexture(Side.South))
  }

  def renderCuboidWithBlock(cuboid: Cuboid, block: Block, metadata: Int = 0): Render[Unit] =
    renderCuboid(cuboid, getTextureForBlock(block, metadata))

  def renderModel(model: Model, getTexture: Side => Icon): Render[Unit] = mk {
    model.cuboids foreach { c => renderCuboid(c, getTexture).run() }
  }

  def renderModelWithBlock(model: Model, block: Block, metadata: Int = 0): Render[Unit] =
    renderModel(model, getTextureForBlock(block, metadata))

  def draw[A](r: Render[A]): Render[A] = mk {
    val t = Tessellator.instance

    t.startDrawingQuads()
    val a = r.run()
    t.draw()
    a
  }

  def withPushedMatrix[A](r: Render[A]): Render[A] = mk {
    GL11.glPushMatrix()
    val a = r.run()
    GL11.glPopMatrix()
    a
  }

  def withPushedAttrib[A](mask: Int)(r: Render[A]): Render[A] = mk {
    GL11.glPushAttrib(mask)
    val a = r.run()
    GL11.glPopAttrib()
    a
  }

  /**
   * Compiles the given render into a display list.
   *
   * Note that the yield from the compiled render is captured and used as the yield of the call action.
   *
   * @return a render action that invokes the compiled display list.
   */
  def compile[A](r: Render[A]): Render[A] = {
    val listIndex = GLAllocation.generateDisplayLists(1)
    GL11.glNewList(listIndex, GL11.GL_COMPILE)
    val a = r.run()
    GL11.glEndList()

    mk {
      GL11.glCallList(listIndex)
      a
    }
  }
}
