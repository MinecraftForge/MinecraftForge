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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.StartupQuery;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.registries.GameData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ClientHooks
{
    private static final Logger LOGGER = LogManager.getLogger("FML");
    private static final Marker CLIENTHOOKS = MarkerManager.getMarker("CLIENTHOOKS");

    private static Map<ServerStatusResponse,JsonObject> extraServerListData;
    private static Map<ServerData, ExtendedServerListData> serverDataTag;

    public static void setupServerList()
    {
        extraServerListData = Collections.synchronizedMap(new HashMap<>());
        serverDataTag = Collections.synchronizedMap(new HashMap<>());
    }

    public static void captureAdditionalData(ServerStatusResponse serverstatusresponse, JsonObject jsonobject)
    {
        if (jsonobject.has("modinfo"))
        {
            JsonObject fmlData = jsonobject.get("modinfo").getAsJsonObject();
            extraServerListData.put(serverstatusresponse, fmlData);
        }
    }
    public static void bindServerListData(ServerData data, ServerStatusResponse originalResponse)
    {
        if (extraServerListData.containsKey(originalResponse))
        {
            JsonObject jsonData = extraServerListData.get(originalResponse);
            String type = jsonData.get("type").getAsString();
            JsonArray modDataArray = jsonData.get("modList").getAsJsonArray();
            boolean moddedClientAllowed = !jsonData.has("clientModsAllowed") || jsonData.get("clientModsAllowed").getAsBoolean();
            ImmutableMap.Builder<String, String> modListBldr = ImmutableMap.builder();
            for (JsonElement obj : modDataArray)
            {
                JsonObject modObj = obj.getAsJsonObject();
                modListBldr.put(modObj.get("modid").getAsString(), modObj.get("version").getAsString());
            }

            Map<String,String> modListMap = modListBldr.build();
            String modRejections = FMLNetworkHandler.checkModList(modListMap, LogicalSide.SERVER);
            serverDataTag.put(data, new ExtendedServerListData(type, modRejections == null, modListMap, !moddedClientAllowed));
        }
        else
        {
            String serverDescription = data.serverMOTD;
            boolean moddedClientAllowed = true;
            if (!Strings.isNullOrEmpty(serverDescription))
            {
                moddedClientAllowed = !serverDescription.endsWith(":NOFML§r");
            }
            serverDataTag.put(data, new ExtendedServerListData("VANILLA", false, ImmutableMap.of(), !moddedClientAllowed));
        }
        startupConnectionData.countDown();
    }

    private static final ResourceLocation iconSheet = new ResourceLocation("fml:textures/gui/icons.png");
    private static final CountDownLatch startupConnectionData = new CountDownLatch(1);

    @Nullable
    public static String enhanceServerListEntry(ServerListEntryNormal serverListEntry, ServerData serverEntry, int x, int width, int y, int relativeMouseX, int relativeMouseY)
    {
        String tooltip;
        int idx;
        boolean blocked = false;
        if (serverDataTag.containsKey(serverEntry))
        {
            ExtendedServerListData extendedData = serverDataTag.get(serverEntry);
            if ("FML".equals(extendedData.type) && extendedData.isCompatible)
            {
                idx = 0;
                tooltip = String.format("Compatible FML modded server\n%d mods present", extendedData.modData.size());
            }
            else if ("FML".equals(extendedData.type) && !extendedData.isCompatible)
            {
                idx = 16;
                tooltip = String.format("Incompatible FML modded server\n%d mods present", extendedData.modData.size());
            }
            else if ("BUKKIT".equals(extendedData.type))
            {
                idx = 32;
                tooltip = String.format("Bukkit modded server");
            }
            else if ("VANILLA".equals(extendedData.type))
            {
                idx = 48;
                tooltip = String.format("Vanilla server");
            }
            else
            {
                idx = 64;
                tooltip = String.format("Unknown server data");
            }
            blocked = extendedData.isBlocked;
        }
        else
        {
            return null;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(iconSheet);
        Gui.drawModalRectWithCustomSizedTexture(x + width - 18, y + 10, 0, (float)idx, 16, 16, 256.0f, 256.0f);
        if (blocked)
        {
            Gui.drawModalRectWithCustomSizedTexture(x + width - 18, y + 10, 0, 80, 16, 16, 256.0f, 256.0f);
        }

        return relativeMouseX > width - 15 && relativeMouseX < width && relativeMouseY > 10 && relativeMouseY < 26 ? tooltip : null;
    }

    public static String fixDescription(String description)
    {
        return description.endsWith(":NOFML§r") ? description.substring(0, description.length() - 8)+"§r" : description;
    }

    static File getSavesDir()
    {
        return new File(Minecraft.getMinecraft().mcDataDir, "saves");
    }

    public static void tryLoadExistingWorld(GuiWorldSelection selectWorldGUI, WorldSummary comparator)
    {
        try
        {
            Minecraft.getMinecraft().launchIntegratedServer(comparator.getFileName(), comparator.getDisplayName(), null);
        }
        catch (StartupQuery.AbortedException e)
        {
            // ignore
        }
    }

    private static NetworkManager getClientToServerNetworkManager()
    {
        return Minecraft.getMinecraft().getConnection()!=null ? Minecraft.getMinecraft().getConnection().getNetworkManager() : null;
    }

    public static void handleClientWorldClosing(WorldClient world)
    {
        NetworkManager client = getClientToServerNetworkManager();
        // ONLY revert a non-local connection
        if (client != null && !client.isLocalChannel())
        {
            GameData.revertToFrozen();
        }
    }

}
