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

package net.minecraftforge.debug.mod;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "clientexceptiontest", version = "1.0", name = "Client Exception Test", clientSideOnly = true)
public class ClientLoadingExceptionTest
{

    // Disabled so other test mods can still work.
    public static boolean ENABLE_PREINIT = false;
    public static boolean ENABLE_INIT = false;
    public static boolean ENABLE_LOAD_COMPLETE = false;
    public static boolean ENABLE_SERVER_STARTED = false;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent e)
    {
        if (ENABLE_PREINIT)
        {
            MinecraftForge.EVENT_BUS.register(this);
            throwException("Thrown in Pre-Init");
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent<Item> itemRegistryEvent)
    {
        throw new RuntimeException("This should not be called because the mod threw an exception earlier in Pre-Init and is in a broken state.");
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent e)
    {
        throw new RuntimeException("This should not be called because the mod threw an exception earlier in Pre-Init and is in a broken state.");
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent e)
    {
        if (ENABLE_INIT)
        {
            throwException("Thrown in Init");
        }
    }

    @Mod.EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent e)
    {
        if (ENABLE_LOAD_COMPLETE)
        {
            throwException("Thrown in load complete");
        }
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent e)
    {
        if (ENABLE_SERVER_STARTED)
        {
            throw new RuntimeException("Server thread exception - should stop client");
        }
    }

    private void throwException(String runtimeMessage)
    {
        throw new CustomModLoadingErrorDisplayException("Custom Test Exception", new RuntimeException(runtimeMessage))
        {
            @Override
            public void initGui(GuiErrorScreen parent, FontRenderer fontRenderer) {}

            @Override
            public void drawScreen(GuiErrorScreen parent, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime)
            {
                parent.drawCenteredString(parent.mc.fontRenderer, "Custom Test Exception", parent.width / 2, 90, 16777215);
                parent.drawCenteredString(parent.mc.fontRenderer, runtimeMessage, parent.width / 2, 110, 16777215);
            }
        };
    }
}
