/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.debug;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Registers 255 mob effects that log every tick on the client. Used to test {@link net.minecraftforge.network.filters.ExtendedMobEffectChannel}
 */
@Mod(ManyMobEffectsTest.MODID)
public class ManyMobEffectsTest
{

    static final String MODID = "many_mob_effects_test";

    private static final boolean ENABLED = false;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);

    static
    {
        for (int i = 0; i < 255; i++)
        {
            final var index = i;
            MOB_EFFECTS.register("effect_" + i, () -> new MobEffect(MobEffectCategory.NEUTRAL, 0)
            {
                @Override
                public void applyEffectTick(LivingEntity p_19467_, int p_19468_)
                {
                    if (p_19467_.level.isClientSide)
                    {
                        LOGGER.info("Effect Tick for {} on the client", index);
                    }
                }

                @Override
                public boolean isDurationEffectTick(int p_19455_, int p_19456_)
                {
                    return true;
                }
            });
        }
    }

    public ManyMobEffectsTest()
    {
        if (!ENABLED) return;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        MOB_EFFECTS.register(modBus);
    }
}
