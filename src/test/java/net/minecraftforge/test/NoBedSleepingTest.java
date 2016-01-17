package net.minecraftforge.test;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = NoBedSleepingTest.MODID, version = NoBedSleepingTest.VERSION)
public class NoBedSleepingTest
{
    public static final String MODID = "ForgeDebugNoBedSleeping";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        GameRegistry.registerItem(ItemSleepingPill.instance, ItemSleepingPill.name);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static class EventHandler
    {
        @SubscribeEvent
        public void onEntityConstruct(EntityConstructing evt)
        {
            evt.entity.registerExtendedProperties(ExtendedPropertySleeping.name, new ExtendedPropertySleeping());
        }

        @SubscribeEvent
        public void onBedCheck(SleepingLocationCheckEvent evt)
        {
            final IExtendedEntityProperties property = evt.entityPlayer.getExtendedProperties(ExtendedPropertySleeping.name);
            if (property instanceof ExtendedPropertySleeping && ((ExtendedPropertySleeping) property).isSleeping)
                evt.setResult(Result.ALLOW);
        }

        @SubscribeEvent
        public void onWakeUp(PlayerWakeUpEvent evt)
        {
            final IExtendedEntityProperties property = evt.entityPlayer.getExtendedProperties(ExtendedPropertySleeping.name);
            if (property instanceof ExtendedPropertySleeping)
                ((ExtendedPropertySleeping) property).isSleeping = false;
        }
    }

    public static class ExtendedPropertySleeping implements IExtendedEntityProperties
    {
        private static final String TAG_NAME = "IsSleepingExt";

        public static final String name = "is_sleeping";

        public boolean isSleeping;

        @Override
        public void saveNBTData(NBTTagCompound compound)
        {
            compound.setBoolean(TAG_NAME, isSleeping);
        }

        @Override
        public void loadNBTData(NBTTagCompound compound)
        {
            this.isSleeping = compound.getBoolean(TAG_NAME);
        }

        @Override
        public void init(Entity entity, World world)
        {
        }

    }

    public static class ItemSleepingPill extends Item
    {
        public static final ItemSleepingPill instance = new ItemSleepingPill();
        public static final String name = "sleeping_pill";

        private ItemSleepingPill()
        {
            setCreativeTab(CreativeTabs.tabMisc);
            setUnlocalizedName(MODID + ":" + name);
        }

        public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
        {
            if (!worldIn.isRemote)
            {
                final EnumStatus result = playerIn.trySleep(playerIn.getPosition());
                if (result == EnumStatus.OK)
                {
                    final IExtendedEntityProperties property = playerIn.getExtendedProperties(ExtendedPropertySleeping.name);
                    if (property instanceof ExtendedPropertySleeping)
                        ((ExtendedPropertySleeping) property).isSleeping = true;
                }
            }
            return itemStackIn;
        }
    }
}
