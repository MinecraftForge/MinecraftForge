package net.minecraftforge.debug;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderItemOverlayEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = RenderItemOverlayEventTest.MODID, name = "RenderItemOverlayEventTest", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class RenderItemOverlayEventTest {
    public static final boolean ENABLE = true;
    public static final String MODID = "renderitemoverlayeventtest";
    @SidedProxy
    public static CommonProxy proxy = null;

    public static abstract class CommonProxy
    {
        public void registerItem(RegistryEvent.Register<Item> event)
        {
            ItemTest.instance = new ItemTest();
            event.getRegistry().register(ItemTest.instance);
        }
    }

    public static final class ServerProxy extends CommonProxy
    {
    }

    public static final class ClientProxy extends CommonProxy
    {
        @Override
        public void registerItem(RegistryEvent.Register<Item> event)
        {
            super.registerItem(event);
            ModelLoader.setCustomModelResourceLocation(ItemTest.instance, 0, new ModelResourceLocation(new ResourceLocation(MODID, "test_item"), "inventory"));

        }
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event)
    {
        if (ENABLE)
        {
            proxy.registerItem(event);
        }
    }

    @SubscribeEvent
    public static void renderItemOverlay(RenderItemOverlayEvent event) {
        if (event.getItem().getItem() == ItemTest.instance) {
            ItemStack stack = event.getItem();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            int testValue = stack.getTagCompound().getInteger("test");
            int rgbfordisplay = stack.getItem().getRGBDurabilityForDisplay(stack);
            int i = Math.min(testValue, 13);
            int j = rgbfordisplay;
            draw(bufferbuilder, event.getxPosition() + 2, event.getyPosition() + 10, 13, 2, 0, 0, 0, 255);
            draw(bufferbuilder, event.getxPosition() + 2, event.getyPosition() + 10, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }
    }

    private static void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha)
    {
        renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos((double)(x + 0), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((double)(x + 0), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((double)(x + width), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((double)(x + width), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }

    public static class ItemTest extends ItemSword {

        public static ItemTest instance = null;

        public ItemTest() {
            super(ToolMaterial.DIAMOND);
            setCreativeTab(CreativeTabs.TOOLS);
            setRegistryName(MODID, "test_sword");
            setUnlocalizedName(MODID + ":test_sword");
        }

        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
            ItemStack stack = playerIn.getHeldItem(handIn);
            int x = 0;
            NBTTagCompound tag = new NBTTagCompound();
            if (stack.hasTagCompound()) {
                x = stack.getTagCompound().getInteger("test");
            }
            x++;
            tag.setInteger("test", x);
            stack.setTagCompound(tag);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        @Override
        public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
            if (stack.hasTagCompound()) {
                stack.setTagCompound(null);
            }
            return super.onLeftClickEntity(stack, player, entity);
        }

        @Override
        public boolean shouldRenderAdditionalOverlay(ItemStack stack) {
            return stack.hasTagCompound();
        }
    }
}
