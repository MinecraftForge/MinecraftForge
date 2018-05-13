package net.minecraftforge.debug.item;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IElytra;

@Mod.EventBusSubscriber(modid = CustomElytraItemTest.MODID)
@Mod(modid = CustomElytraItemTest.MODID, name = "CustomElytraItemTest", version = "0.0.0", acceptableRemoteVersions = "*")
public class CustomElytraItemTest
{

    public static final String MODID = "custom_elytra_item_test";

    public static class TestItem extends Item implements IElytra
    {
        public static Item testItem = new TestItem();

        @Override
        public boolean doesSlowOnUpwardsFlight(ItemStack stack, EntityLivingBase entity)
        {
            // Can fly upwards without slowing down
            return false;
        }

        @Override
        public boolean doesDamageWhileFlying(ItemStack stack, EntityLivingBase entity)
        {
            // Doesn't use durability upon flying
            return false;
        }

        @Override
        public boolean doesDamageUponHittingWall(ItemStack stack, EntityLivingBase entity)
        {
            // Doesn't deal damage when hitting a wall
            return false;
        }

        @Override
        public boolean checkFlight(ItemStack stack, EntityLivingBase entity)
        {
            // This just acts like a regular Elytra, but you could put custom fuel checks or something
            return true;
        }

        @Override
        public void changeAcceleration(ItemStack stack, EntityLivingBase entity, double motionX, double motionY, double motionZ)
        {
            // Goes a bit faster than normal. This escalates QUICKLY; it can be changed as you use it, but I'd recommend sticking to 1 for most things.
            motionX *= 1.1;
            motionY *= 1.1;
            motionZ *= 1.1;
        }

        @Override
        public void onHitWall(ItemStack stack, EntityLivingBase entity)
        {
            // I could put particle effects, explosions, fuel reductions, or really anything.
        }

        @Override
        public int getDamageRate(ItemStack stack, EntityLivingBase entity)
        {
            // Doesn't take damage upon flying
            return 1;
        }
    };

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(TestItem.testItem.setRegistryName("custom_elytra_item").setUnlocalizedName("CustomElytraItem").setMaxStackSize(1));
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            ModelBakery.registerItemVariants(TestItem.testItem);
        }
    }
}