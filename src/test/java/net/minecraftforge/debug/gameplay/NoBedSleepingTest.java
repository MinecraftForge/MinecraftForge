/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.gameplay;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
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
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(modid = NoBedSleepingTest.MODID, name = "ForgeDebugNoBedSleeping", version = NoBedSleepingTest.VERSION, acceptableRemoteVersions = "*")
public class NoBedSleepingTest
{
    public static final String MODID = "forgedebugnobedsleeping";
    public static final String VERSION = "1.0";
    @CapabilityInject(IExtraSleeping.class)
    private static final Capability<IExtraSleeping> SLEEP_CAP = null;
    @ObjectHolder(ItemSleepingPill.name)
    public static final Item SLEEPING_PILL = null;

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            event.getRegistry().register(new ItemSleepingPill());
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            ModelLoader.setCustomModelResourceLocation(SLEEPING_PILL, 0, new ModelResourceLocation(SLEEPING_PILL.getRegistryName(), "inventory"));
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        CapabilityManager.INSTANCE.register(IExtraSleeping.class, new Storage(), DefaultImpl::new);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static class EventHandler
    {
        @SubscribeEvent
        public void onEntityConstruct(AttachCapabilitiesEvent<Entity> evt)
        {
            if (!(evt.getObject() instanceof EntityPlayer))
            {
                return;
            }

            evt.addCapability(new ResourceLocation(MODID, "IExtraSleeping"), new ICapabilitySerializable<NBTPrimitive>()
            {
                IExtraSleeping inst = SLEEP_CAP.getDefaultInstance();

                @Override
                public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
                {
                    return capability == SLEEP_CAP;
                }

                @Override
                @Nullable
                public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
                {
                    return capability == SLEEP_CAP ? SLEEP_CAP.<T>cast(inst) : null;
                }

                @Override
                public NBTPrimitive serializeNBT()
                {
                    return (NBTPrimitive) SLEEP_CAP.getStorage().writeNBT(SLEEP_CAP, inst, null);
                }

                @Override
                public void deserializeNBT(NBTPrimitive nbt)
                {
                    SLEEP_CAP.getStorage().readNBT(SLEEP_CAP, inst, null, nbt);
                }
            });
        }

        @SubscribeEvent
        public void onBedCheck(SleepingLocationCheckEvent evt)
        {
            final IExtraSleeping sleep = evt.getEntityPlayer().getCapability(SLEEP_CAP, null);
            if (sleep != null && sleep.isSleeping())
            {
                evt.setResult(Result.ALLOW);
            }
        }

        @SubscribeEvent
        public void onWakeUp(PlayerWakeUpEvent evt)
        {
            final IExtraSleeping sleep = evt.getEntityPlayer().getCapability(SLEEP_CAP, null);
            if (sleep != null)
            {
                sleep.setSleeping(false);
            }
        }
    }

    public interface IExtraSleeping
    {
        boolean isSleeping();

        void setSleeping(boolean value);
    }

    public static class Storage implements IStorage<IExtraSleeping>
    {
        @Override
        public NBTBase writeNBT(Capability<IExtraSleeping> capability, IExtraSleeping instance, EnumFacing side)
        {
            return new NBTTagByte((byte) (instance.isSleeping() ? 1 : 0));
        }

        @Override
        public void readNBT(Capability<IExtraSleeping> capability, IExtraSleeping instance, EnumFacing side, NBTBase nbt)
        {
            instance.setSleeping(((NBTPrimitive) nbt).getByte() == 1);
        }
    }

    public static class DefaultImpl implements IExtraSleeping
    {
        private boolean isSleeping = false;

        @Override
        public boolean isSleeping()
        {
            return isSleeping;
        }

        @Override
        public void setSleeping(boolean value)
        {
            this.isSleeping = value;
        }
    }

    public static class ItemSleepingPill extends Item
    {
        public static final String name = "sleeping_pill";

        private ItemSleepingPill()
        {
            setCreativeTab(CreativeTabs.MISC);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }

        @Override
        @Nonnull
        public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand)
        {
            ItemStack stack = player.getHeldItem(hand);
            if (!world.isRemote)
            {
                final SleepResult result = player.trySleep(player.getPosition());
                if (result == SleepResult.OK)
                {
                    final IExtraSleeping sleep = player.getCapability(SLEEP_CAP, null);
                    if (sleep != null)
                    {
                        sleep.setSleeping(true);
                    }
                    return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                }
            }
            return ActionResult.newResult(EnumActionResult.PASS, stack);
        }
    }
}
