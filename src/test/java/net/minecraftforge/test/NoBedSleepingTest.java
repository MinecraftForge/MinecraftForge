package net.minecraftforge.test;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(modid = NoBedSleepingTest.MODID, name = "ForgeDebugNoBedSleeping", version = NoBedSleepingTest.VERSION, acceptableRemoteVersions = "*")
public class NoBedSleepingTest
{
    public static final String MODID = "forgedebugnobedsleeping";
    public static final String VERSION = "1.0";
    @CapabilityInject(IExtraSleeping.class)
    private static final Capability<IExtraSleeping> SLEEP_CAP = null;

    @SidedProxy
    public static CommonProxy proxy = null;

    public static abstract class CommonProxy
    {
        public void preInit(FMLPreInitializationEvent event)
        {
            GameRegistry.register(ItemSleepingPill.instance);
            CapabilityManager.INSTANCE.register(IExtraSleeping.class, new Storage(), DefaultImpl.class);
            MinecraftForge.EVENT_BUS.register(new EventHandler());
        }
    }

    public static final class ServerProxy extends CommonProxy {}

    public static final class ClientProxy extends CommonProxy
    {
        @Override
        public void preInit(FMLPreInitializationEvent event)
        {
            super.preInit(event);
            ModelLoader.setCustomModelResourceLocation(ItemSleepingPill.instance, 0, new ModelResourceLocation(new ResourceLocation(MODID, ItemSleepingPill.name), "inventory"));
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);
    }

    public static class EventHandler
    {
        @SubscribeEvent
        public void onEntityConstruct(AttachCapabilitiesEvent.Entity evt)
        {
            if (!(evt.getEntity() instanceof EntityPlayer))
                return;

            evt.addCapability(new ResourceLocation(MODID, "IExtraSleeping"), new ICapabilitySerializable<NBTPrimitive>()
            {
                IExtraSleeping inst = SLEEP_CAP.getDefaultInstance();
                @Override
                public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                    return capability == SLEEP_CAP;
                }

                @Override
                @Nullable
                public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                    return capability == SLEEP_CAP ? SLEEP_CAP.<T>cast(inst) : null;
                }

                @Override
                public NBTPrimitive serializeNBT() {
                    return (NBTPrimitive)SLEEP_CAP.getStorage().writeNBT(SLEEP_CAP, inst, null);
                }

                @Override
                public void deserializeNBT(NBTPrimitive nbt) {
                    SLEEP_CAP.getStorage().readNBT(SLEEP_CAP, inst, null, nbt);
                }
            });
        }

        @SubscribeEvent
        public void onBedCheck(SleepingLocationCheckEvent evt)
        {
            final IExtraSleeping sleep = evt.getEntityPlayer().getCapability(SLEEP_CAP, null);
            if (sleep != null && sleep.isSleeping())
                evt.setResult(Result.ALLOW);
        }

        @SubscribeEvent
        public void onWakeUp(PlayerWakeUpEvent evt)
        {
            final IExtraSleeping sleep = evt.getEntityPlayer().getCapability(SLEEP_CAP, null);
            if (sleep != null)
                sleep.setSleeping(false);
        }
    }

    public interface IExtraSleeping {
        boolean isSleeping();
        void setSleeping(boolean value);
    }

    public static class Storage implements IStorage<IExtraSleeping>
    {
        @Override
        public NBTBase writeNBT(Capability<IExtraSleeping> capability, IExtraSleeping instance, EnumFacing side)
        {
            return new NBTTagByte((byte)(instance.isSleeping() ? 1 : 0));
        }

        @Override
        public void readNBT(Capability<IExtraSleeping> capability, IExtraSleeping instance, EnumFacing side, NBTBase nbt)
        {
            instance.setSleeping(((NBTPrimitive)nbt).getByte() == 1);
        }
    }

    public static class DefaultImpl implements IExtraSleeping
    {
        private boolean isSleeping = false;
        @Override public boolean isSleeping() { return isSleeping; }
        @Override public void setSleeping(boolean value) { this.isSleeping = value; }
    }

    public static class ItemSleepingPill extends Item
    {
        public static final ItemSleepingPill instance = new ItemSleepingPill();
        public static final String name = "sleeping_pill";

        private ItemSleepingPill()
        {
            setCreativeTab(CreativeTabs.MISC);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }

        @Override
        public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
        {
            ItemStack stack = player.getHeldItem(hand);
            if (!world.isRemote)
            {
                final SleepResult result = player.trySleep(player.getPosition());
                if (result == SleepResult.OK)
                {
                    final IExtraSleeping sleep = player.getCapability(SLEEP_CAP, null);
                    if (sleep != null)
                        sleep.setSleeping(true);
                    return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                }
            }
            return ActionResult.newResult(EnumActionResult.PASS, stack);
        }
    }
}
