package name.bshelden.arcanefluids

import cpw.mods.fml.common.{FMLLog, FMLCommonHandler, Mod}
import cpw.mods.fml.common.network.NetworkMod
import cpw.mods.fml.common.event.{FMLPostInitializationEvent, FMLInitializationEvent, FMLPreInitializationEvent}
import cpw.mods.fml.common.Mod.{PostInit, Init, PreInit}
import cpw.mods.fml.relauncher.Side
import java.util.logging.{Logger, Level}
import net.minecraftforge.common.Configuration
import cpw.mods.fml.common.registry.{GameRegistry, LanguageRegistry}
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.block.Block
import name.bshelden.arcanefluids.block.{TileEntityArcaneTank, BlockArcaneTank, BlockArcanePipe}

/**
 * Primary mod object for Arcane Fluids
 *
 * (c) 2013 Byron Shelden
 * See COPYING for details
 */
@Mod(modid="ArcaneFluids", modLanguage="scala")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
object ArcaneFluids {
  private[arcanefluids] val logger: Logger = Logger.getLogger("ArcaneFluids")
  logger.setParent(FMLLog.getLogger())

  private var _config: ArcaneFluidsConfig = null
  private var _blocks: ArcaneFluidsBlocks = null

  private val CFG_ID_TANK_DEFAULT = 2850
  private val CFG_ID_PIPE_DEFAULT = 2851

  private val proxy = findProxy()

  def config: ArcaneFluidsConfig = _config
  def blocks: ArcaneFluidsBlocks = _blocks

  @PreInit
  def preInit(ev: FMLPreInitializationEvent) {
    _config = loadConfig(ev)
    _blocks = ArcaneFluidsBlocks(
      new BlockArcaneTank(_config.tankId),
      new BlockArcanePipe(_config.pipeId)
    )
  }

  @Init
  def init(ev: FMLInitializationEvent) {
    FMLLog.log(Level.INFO,"Initialization")

    FMLLog.log(Level.FINE, "Registering renderers")
    proxy.registerRenderers()

    FMLLog.log(Level.FINE, "Registering blocks")
    registerBlocks()

    FMLLog.log(Level.FINE, "Registering localizations")
    initTranslations()

    FMLLog.log(Level.FINE, "Registering recipes")
    registerRecipes()
  }

  @PostInit
  def postInit(ev: FMLPostInitializationEvent) {
  }

  private def registerBlocks() {
    GameRegistry.registerBlock(blocks.tank, "arcaneTank")
    GameRegistry.registerTileEntity(classOf[TileEntityArcaneTank], "arcaneTank")
//    GameRegistry.registerBlock(blocks.pipe, "arcanePipe")
//    GameRegistry.registerTileEntity(classOf[TileEntityArcanePipe], "arcanePipe")
  }

  private def initTranslations() {
    LanguageRegistry.addName(blocks.tank, "Arcane Tank")
//    LanguageRegistry.addName(blocks.pipe, "Arcane Pipe")
  }

  private def registerRecipes() {
    GameRegistry.addRecipe(
      new ItemStack(blocks.tank, 1),
      "QQQ", "Q Q", "QQQ",
      new Character('Q'), new ItemStack(Item.netherQuartz))

/*
    GameRegistry.addRecipe(
      new ItemStack(blocks.pipe, 1),
      "GGG", "   ", "GGG",
      new Character('G'), new ItemStack(Block.glass))
*/
  }

  private def findProxy(): ArcaneFluidsCommonProxy = {
    FMLCommonHandler.instance().getEffectiveSide match {
      case Side.CLIENT =>
        Class.forName("name.bshelden.arcanefluids.client.ArcaneFluidsClientProxy").newInstance().asInstanceOf[ArcaneFluidsCommonProxy]
      case _ =>
        new ArcaneFluidsCommonProxy
    }
  }

  private def loadConfig(ev: FMLPreInitializationEvent): ArcaneFluidsConfig = {
    logger.log(Level.FINE, "Loading configuration")

    val cfg = new Configuration(ev.getSuggestedConfigurationFile)

    try {
      cfg.load()

      val tankId = cfg.getBlock(
        "tank",
        CFG_ID_TANK_DEFAULT
      ).getInt(CFG_ID_TANK_DEFAULT)

      val pipeId = cfg.getBlock(
        "pipe",
        CFG_ID_PIPE_DEFAULT
      ).getInt(CFG_ID_PIPE_DEFAULT)

      val clientRenderLiquidAsTop = cfg.get(
        "client",
        "clientRenderLiquidAsTop",
        true,
        "If true, liquids will render their top face on all sides, avoiding the flowing look some liquids show on their sides."
      ).getBoolean(true)

      ArcaneFluidsConfig(tankId, pipeId, clientRenderLiquidAsTop)
    } catch {
      case e: Exception =>
        logger.log(Level.SEVERE, "Exception caught when trying to load the configuration for Arcane Fluids!  Abort!")
        throw e
    } finally {
      cfg.save()
    }
  }
}

case class ArcaneFluidsConfig(tankId: Int, pipeId: Int, clientRenderLiquidAsTop: Boolean)

case class ArcaneFluidsBlocks(tank: BlockArcaneTank, pipe: BlockArcanePipe)
