/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.debug.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

@Mod(CustomElytraTest.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = CustomElytraTest.MOD_ID)
public class CustomElytraTest
{
    public static final String MOD_ID = "custom_elytra_test";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<Item> TEST_ELYTRA = ITEMS.register("test_elytra",() -> new CustomElytra(new Item.Properties().maxDamage(100).group(ItemGroup.MISC)));

    public CustomElytraTest()
    {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        modBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        registerElytraLayer();
    }

    @OnlyIn(Dist.CLIENT)
    private void registerElytraLayer()
    {
        Minecraft.getInstance().getRenderManager().getSkinMap().values().forEach(player -> player.addLayer(new CustomElytraLayer(player)));
    }

    public static class CustomElytra extends Item
    {

        public CustomElytra(Properties properties)
        {
            super(properties);
            DispenserBlock.registerDispenseBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
        }

        @Nullable
        @Override
        public EquipmentSlotType getEquipmentSlot(ItemStack stack)
        {
            return EquipmentSlotType.CHEST; //Or you could just extend ItemArmor
        }

        @Override
        public boolean canElytraFly(ItemStack stack, LivingEntity entity)
        {
            return true;
        }

        @Override
        public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks)
        {
            //Adding 1 to ticksElytraFlying prevents damage on the very first tick.
            if (!entity.world.isRemote && (flightTicks + 1) % 20 == 0)
            {
                stack.damageItem(1, entity, e -> e.sendBreakAnimation(EquipmentSlotType.CHEST));
            }
            return true;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class CustomElytraLayer extends ElytraLayer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>
    {
        private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation(MOD_ID, "textures/entity/custom_elytra.png");

        public CustomElytraLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> rendererIn)
        {
            super(rendererIn);
        }

        @Override
        public boolean shouldRender(ItemStack stack, AbstractClientPlayerEntity entity)
        {
            return stack.getItem() == TEST_ELYTRA.get();
        }

        @Override
        public ResourceLocation getElytraTexture(ItemStack stack, AbstractClientPlayerEntity entity)
        {
            return TEXTURE_ELYTRA;
        }
    }
}
