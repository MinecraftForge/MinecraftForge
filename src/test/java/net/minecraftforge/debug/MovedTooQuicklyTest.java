package net.minecraftforge.debug;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.MovementCheckEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = MovedTooQuicklyTest.MOD_ID, name = "Moved Too Quickly Test", version = "1.0")
public class MovedTooQuicklyTest
{
    static final String MOD_ID = "movedtooquickly";
    static final boolean ENABLED = false;
    static Item pogostick = new ItemPogoStick();

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onMovementCheck(MovementCheckEvent event)
    {
        if (ENABLED && event.getEntity() instanceof EntityPlayer && ((EntityPlayer) event.getEntity()).getHeldItemMainhand().getItem() == pogostick)
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void registerItem(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(pogostick);
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void setupModels(ModelRegistryEvent event)
        {
            if (ENABLED)
            {
                ModelLoader.setCustomModelResourceLocation(pogostick, 0, new ModelResourceLocation(new ResourceLocation("minecraft", "stick"), "inventory"));
            }
        }
    }

    public static class ItemPogoStick extends Item
    {
        public ItemPogoStick()
        {

            this.setCreativeTab(CreativeTabs.MISC);
            this.setUnlocalizedName(MOD_ID + ".pogostick");
            this.setRegistryName("pogostick");
        }

        public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
        {
            playerIn.motionY += 5d;
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
        }
    }
}
