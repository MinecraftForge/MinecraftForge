/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Registers 255 mob effects that log every tick on the client.
 * Used to test the patches for saving MobEffects to NBT.
 *
 * To verify correct function:
 * - Check that the Potion item and suspicious stew item provided via the creative tab function correctly
 * - Check that the effect given by the above items can persist on an entity as well as on ItemStacks in inventories
 * - Right click a Mushroom Cow using the above given Potion Item. Verify that it obtained the effect by right-clicking
 *   it again using an empty hand before and after reloading the world.
 */
@Mod(ManyMobEffectsTest.MODID)
public class ManyMobEffectsTest
{

    static final String MODID = "many_mob_effects_test";

    private static final boolean ENABLED = true;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(MODID)
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(Items.POTION);
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> stacks)
        {
            super.fillItemList(stacks);
            var stack = new ItemStack(Items.POTION);
            PotionUtils.setCustomEffects(stack, List.of(new MobEffectInstance(LAST_EFFECT.get(), 1000)));
            stacks.add(stack);

            stack = new ItemStack(Items.SUSPICIOUS_STEW);
            SuspiciousStewItem.saveMobEffect(stack, LAST_EFFECT.get(), 1000);
            stacks.add(stack);
        }
    };

    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    private static final RegistryObject<MobEffect> LAST_EFFECT;

    static
    {
        RegistryObject<MobEffect> effect = null;
        for (int i = 0; i < 255; i++)
        {
            final var index = i;
            effect = MOB_EFFECTS.register("effect_" + i, () -> new MobEffect(MobEffectCategory.NEUTRAL, 0xFF0000)
            {
                @Override
                public void applyEffectTick(LivingEntity entity, int amplifier)
                {
                    if (entity.level.isClientSide)
                    {
                        LOGGER.info("Effect Tick for {} on the client", index);
                    }
                }

                @Override
                public boolean isDurationEffectTick(int duration, int amplifier)
                {
                    return true;
                }
            });
        }
        LAST_EFFECT = effect;
    }

    public ManyMobEffectsTest()
    {
        if (!ENABLED) return;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        MOB_EFFECTS.register(modBus);
        MinecraftForge.EVENT_BUS.addListener(ManyMobEffectsTest::mobInteract);
    }

    private static void mobInteract(PlayerInteractEvent.EntityInteract event)
    {
        if (!event.getTarget().getLevel().isClientSide() && event.getTarget() instanceof MushroomCow cow)
        {
            var heldItem = event.getEntity().getItemInHand(event.getHand());
            if (heldItem.is(Items.POTION))
            {
                var effects = PotionUtils.getMobEffects(heldItem);
                if (!effects.isEmpty())
                {
                    ObfuscationReflectionHelper.setPrivateValue(MushroomCow.class, cow,  effects.get(0).getEffect(), "f_289" + "09_");
                    ObfuscationReflectionHelper.setPrivateValue(MushroomCow.class, cow,  effects.get(0).getDuration(), "f_289" + "10_");
                }
            }
            else if (heldItem.isEmpty())
            {
                var effect = ((MobEffect) ObfuscationReflectionHelper.getPrivateValue(MushroomCow.class, cow, "f_289" + "09_"));
                if (effect != null)
                {
                    event.getEntity().sendSystemMessage(Component.literal(String.valueOf(ForgeRegistries.MOB_EFFECTS.getKey(effect))));
                }
            }
        }
    }

}
