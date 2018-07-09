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

package net.minecraftforge.debug.gameplay.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Most of the real work is in the advancements directory of this mod.
 * This mod tests ore-dict advancement triggers.
 */
@Mod.EventBusSubscriber
@Mod(modid = OredictTriggerTest.MODID, name = "Oredict Item Predicate Test", version = "1.0", acceptableRemoteVersions = "*")
public class OredictTriggerTest
{
    public static final String MODID = "oredict_predicate";

    static final boolean ENABLED = false;

    private static final MethodHandle ctRegister;
    static
    {
        try
        {
            final Method tmp = CriteriaTriggers.class.getDeclaredMethod("register"/* func_192118_a */, ICriterionTrigger.class);
            tmp.setAccessible(true);
            ctRegister = MethodHandles.lookup().unreflect(tmp);
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }

    public static final EnabledTrigger ENABLED_TRIGGER;
    static
    {
        try
        {
            ENABLED_TRIGGER = (EnabledTrigger)(ICriterionTrigger)ctRegister.invokeExact((ICriterionTrigger)new EnabledTrigger());
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }

    // trigger the enabled advancement on player entry
    @SubscribeEvent
    public static void triggerAdv(EntityJoinWorldEvent event)
    {
        if (event.getEntity() instanceof EntityPlayerMP)
        {
            final EntityPlayerMP player = (EntityPlayerMP)event.getEntity();
            ENABLED_TRIGGER.trigger(player, player.inventory);
        }
    }


    /**
     * Far more work than expected…oh well.
     */
    public static class EnabledTrigger implements ICriterionTrigger<EnabledTrigger.Instance>
    {
        public static final ResourceLocation ID = new ResourceLocation(MODID, "is_enabled");
        private final Map<PlayerAdvancements, Listeners> listeners = new HashMap<>();

        @Override
        public ResourceLocation getId()
        {
            return ID;
        }

        @Override
        public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener)
        {
            Listeners listeners = this.listeners.computeIfAbsent(playerAdvancementsIn, Listeners::new);

            listeners.add(listener);
        }

        @Override
        public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener)
        {
            Listeners listeners = this.listeners.get(playerAdvancementsIn);

            if (listeners != null)
            {
                listeners.remove(listener);

                if (listeners.isEmpty())
                {
                    this.listeners.remove(playerAdvancementsIn);
                }
            }
        }

        @Override
        public void removeAllListeners(PlayerAdvancements playerAdvancementsIn)
        {
            this.listeners.remove(playerAdvancementsIn);
        }

        @Override
        public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context)
        {
            return new Instance();
        }

        public void trigger(EntityPlayerMP player, InventoryPlayer inventory)
        {
            Listeners listeners = this.listeners.get(player.getAdvancements());

            if (listeners != null)
            {
                listeners.trigger(inventory);
            }
        }

        public static class Instance extends AbstractCriterionInstance
        {
            public Instance()
            {
                super(ID);
            }
        }

        static class Listeners
        {
            private final PlayerAdvancements playerAdvancements;
            private final Set<Listener<Instance>> listeners = new HashSet<>();

            public Listeners(PlayerAdvancements playerAdvancementsIn)
            {
                this.playerAdvancements = playerAdvancementsIn;
            }

            public boolean isEmpty()
            {
                return this.listeners.isEmpty();
            }

            public void add(ICriterionTrigger.Listener<Instance> listener)
            {
                this.listeners.add(listener);
            }

            public void remove(ICriterionTrigger.Listener<Instance> listener)
            {
                this.listeners.remove(listener);
            }

            public void trigger(InventoryPlayer inventory)
            {
                List<Listener<Instance>> list = null;

                for (ICriterionTrigger.Listener<Instance> listener : this.listeners)
                {
                    if (ENABLED)
                    {
                        if (list == null)
                        {
                            list = new ArrayList<>();
                        }

                        list.add(listener);
                    }
                }

                if (list != null)
                {
                    for (ICriterionTrigger.Listener<Instance> listener1 : list)
                    {
                        listener1.grantCriterion(this.playerAdvancements);
                    }
                }
            }
        }
    }
}
