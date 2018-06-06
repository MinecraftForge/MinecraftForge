/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.List;

public class ClientModLoader
{
    /**
     * Mod loading entrypoint for the client
     * @param minecraft
     * @param defaultResourcePacks
     * @param mcResourceManager
     * @param metadataSerializer_
     */
    public static void begin(Minecraft minecraft, List<IResourcePack> defaultResourcePacks, IReloadableResourceManager mcResourceManager, MetadataSerializer metadataSerializer_)
    {
        SplashProgress.start();
        FMLLoader.getModLoader().loadMods();
        minecraft.refreshResources();
    }

    public static void end()
    {
        SplashProgress.finish();
    }

    public static ForgeVersion.Status checkForUpdates()
    {
        return ForgeVersion.Status.UP_TO_DATE;
    }

    public static void complete()
    {
        GlStateManager.disableTexture2D();
        GlStateManager.enableTexture2D();
    }
}
