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

package net.minecraftforge.fmlclient.registry;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistry
{
    private static Map<Class<? extends Entity>, ResourceLocation> entityShaderMap = new ConcurrentHashMap<>();

    /**
     * Registers a KeyBinding.
     * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
     * This method is safe to call during parallel mod loading.
     */
    public static synchronized void registerKeyBinding(KeyMapping key)
    {
        Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, key);
    }

    /**
     * Register a shader for an entity. This shader gets activated when a spectator begins spectating an entity.
     * Vanilla examples of this are the green effect for creepers and the invert effect for endermen.
     * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
     * This method is safe to call during parallel mod loading.
     */
    public static void registerEntityShader(Class<? extends Entity> entityClass, ResourceLocation shader)
    {
        entityShaderMap.put(entityClass, shader);
    }

    public static ResourceLocation getEntityShader(Class<? extends Entity> entityClass)
    {
        return entityShaderMap.get(entityClass);
    }
}
