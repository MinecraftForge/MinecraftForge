package net.minecraftforge.debug;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import static org.lwjgl.opengl.GL11.*;

@Mod(modid = ItemTileDebug.MODID)
public class ItemTileDebug
{
    public static final String MODID = "ForgeDebugItemTile";

    private static String blockName = MODID.toLowerCase() + ":" + TestBlock.name;

    @SidedProxy
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) { proxy.preInit(event); }

    @EventHandler
    public void init(FMLInitializationEvent event) { proxy.init(event); }

    public static class CommonProxy
    {
        public void preInit(FMLPreInitializationEvent event)
        {
            GameRegistry.registerBlock(TestBlock.instance, TestBlock.name);
            GameRegistry.registerTileEntity(CustomTileEntity.class, MODID.toLowerCase() + ":custom_tile_entity");
        }

        public void init(FMLInitializationEvent event) {}
    }

    public static class ServerProxy extends CommonProxy {}

    public static class ClientProxy extends CommonProxy
    {
        private static ModelResourceLocation itemLocation = new ModelResourceLocation(blockName, "inventory");

        @SuppressWarnings("deprecation")
        @Override
        public void preInit(FMLPreInitializationEvent event)
        {
            super.preInit(event);
            Item item = Item.getItemFromBlock(TestBlock.instance);
            ForgeHooksClient.registerTESRItemStack(item, 0, CustomTileEntity.class);
            ModelLoader.setCustomModelResourceLocation(item, 0, itemLocation);
            MinecraftForge.EVENT_BUS.register(BakeEventHandler.instance);
        }

        @Override
        public void init(FMLInitializationEvent event) {
            ClientRegistry.bindTileEntitySpecialRenderer(CustomTileEntity.class, TestTESR.instance);
        }
    }

    public static class BakeEventHandler
    {
        public static final BakeEventHandler instance = new BakeEventHandler();

        private BakeEventHandler() {};

        @SubscribeEvent
        public void onModelBakeEvent(ModelBakeEvent event)
        {
            event.modelManager.getBlockModelShapes().registerBuiltInBlocks(TestBlock.instance);
        }
    }

    public static class TestTESR extends TileEntitySpecialRenderer<CustomTileEntity>
    {
        private static final TestTESR instance = new TestTESR();

        private TestTESR() {}

        @Override
        public void renderTileEntityAt(CustomTileEntity p_180535_1_, double x, double y, double z, float p_180535_8_, int p_180535_9_)
        {
            glPushMatrix();
            glTranslated(x, y, z);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            glColor4f(.2f, 1, .1f, 1);
            glBegin(GL_QUADS);
            glVertex3f(0, .5f, 0);
            glVertex3f(0, .5f, 1);
            glVertex3f(1, .5f, 1);
            glVertex3f(1, .5f, 0);
            glEnd();
            glPopMatrix();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
        }
    }

    public static class TestBlock extends BlockContainer
    {
        public static final TestBlock instance = new TestBlock();
        public static final String name = "custom_model_block";

        private TestBlock()
        {
            super(Material.iron);
            setCreativeTab(CreativeTabs.tabBlock);
            setUnlocalizedName(MODID + ":" + name);
        }

        @Override
        public boolean isOpaqueCube() { return false; }

        @Override
        public boolean isFullCube() { return false; }

        @Override
        public boolean isVisuallyOpaque() { return false; }

        @Override
        public TileEntity createNewTileEntity(World world, int meta)
        {
            return new CustomTileEntity();
        }
    }

    public static class CustomTileEntity extends TileEntity {}
}
