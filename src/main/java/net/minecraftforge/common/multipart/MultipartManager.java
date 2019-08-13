/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.common.multipart;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Arrays;

/**
 * Manages the current instance of {@link MultipartHandler} in use by the system and handles registration of Forge's
 * built-in {@link IBlockSlot block slots} and {@link NoOpMultipartHandler handler}.
 *
 * @see MultipartHandler
 */
public enum MultipartManager
{
    INSTANCE;

    private MultipartHandler handler;

    public MultipartHandler getHandler()
    {
        if (handler == null) updateActiveHandler();
        return handler;
    }

    public void updateActiveHandler()
    {
        ForgeConfigSpec.ConfigValue<String> cfg = ForgeConfig.SERVER.multipartHandler;
        String cfgValue = cfg.get().trim();

        // Try to find the specified multipart handler
        if(!cfgValue.isEmpty())
        {
            ResourceLocation handlerName = new ResourceLocation(cfgValue);
            handler = ForgeRegistries.MULTIPART_HANDLERS.getValue(handlerName);
            if (handler != null) return;
            cfg.set(""); // We couldn't find it, so for now we'll leave the config value empty
        }

        // Default to the no-op handler for now
        handler = NoOpMultipartHandler.INSTANCE;

        // If we find a handler that is not the no-op one, use that one instead
        for (MultipartHandler h : ForgeRegistries.MULTIPART_HANDLERS.getValues())
        {
            if (h == NoOpMultipartHandler.INSTANCE) continue;
            handler = h;
            cfg.set(h.getRegistryName().toString());
            break;
        }

        // Update the config value so we use this handler from now on
        cfg.save();
    }

    @SubscribeEvent
    public void registerMultipartSlots(RegistryEvent.Register<IBlockSlot> event)
    {
        IForgeRegistry<IBlockSlot> registry = event.getRegistry();
        registry.register(BlockSlot.FULL_BLOCK);
        registry.register(BlockSlot.CENTER);
        Arrays.stream(FaceSlot.values()).forEach(registry::register);
        Arrays.stream(EdgeSlot.values()).forEach(registry::register);
        Arrays.stream(CornerSlot.values()).forEach(registry::register);
    }

    @SubscribeEvent
    public void registerMultipartHandlers(RegistryEvent.Register<MultipartHandler> event)
    {
        event.getRegistry().register(NoOpMultipartHandler.INSTANCE);
    }
}
