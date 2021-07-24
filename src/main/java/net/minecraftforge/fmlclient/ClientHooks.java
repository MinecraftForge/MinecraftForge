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

package net.minecraftforge.fmlclient;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fmllegacy.ForgeI18n;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.fmllegacy.packs.ModFileResourcePack;
import net.minecraftforge.registries.GameData;

public class ClientHooks
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker CLIENTHOOKS = MarkerManager.getMarker("CLIENTHOOKS");

    private static final ResourceLocation iconSheet = new ResourceLocation(ForgeVersion.MOD_ID, "textures/gui/icons.png");
    @Nullable

    public static void processForgeListPingData(ServerStatus packet, ServerData target)
    {
        if (packet.getForgeData() != null) {
            final Map<String, String> mods = packet.getForgeData().getRemoteModData();
            final Map<ResourceLocation, Pair<String, Boolean>> remoteChannels = packet.getForgeData().getRemoteChannels();
            final int fmlver = packet.getForgeData().getFMLNetworkVersion();

            boolean fmlNetMatches = fmlver == FMLNetworkConstants.FMLNETVERSION;
            boolean channelsMatch = NetworkRegistry.checkListPingCompatibilityForClient(remoteChannels);
            AtomicBoolean result = new AtomicBoolean(true);
            final List<String> extraClientMods = new ArrayList<>();
            ModList.get().forEachModContainer((modid, mc) ->
                    mc.getCustomExtension(IExtensionPoint.DisplayTest.class).ifPresent(ext-> {
                        boolean foundModOnServer = ext.remoteVersionTest().test(mods.get(modid), true);
                        result.compareAndSet(true, foundModOnServer);
                        if (!foundModOnServer) {
                            extraClientMods.add(modid);
                        }
                    })
            );
            boolean modsMatch = result.get();

            final Map<String, String> extraServerMods = mods.entrySet().stream().
                    filter(e -> !Objects.equals(FMLNetworkConstants.IGNORESERVERONLY, e.getValue())).
                    filter(e -> !ModList.get().isLoaded(e.getKey())).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            LOGGER.debug(CLIENTHOOKS, "Received FML ping data from server at {}: FMLNETVER={}, mod list is compatible : {}, channel list is compatible: {}, extra server mods: {}", target.ip, fmlver, modsMatch, channelsMatch, extraServerMods);

            String extraReason = null;

            if (!extraServerMods.isEmpty()) {
                extraReason = "fml.menu.multiplayer.extraservermods";
                LOGGER.info(CLIENTHOOKS, ForgeI18n.parseMessage(extraReason) + ": {}", extraServerMods.entrySet().stream()
                        .map(e -> e.getKey() + "@" + e.getValue())
                        .collect(Collectors.joining(", ")));
            }
            if (!modsMatch) {
                extraReason = "fml.menu.multiplayer.modsincompatible";
                LOGGER.info(CLIENTHOOKS, "Client has mods that are missing on server: {}", extraClientMods);
            }
            if (!channelsMatch) {
                extraReason = "fml.menu.multiplayer.networkincompatible";
            }

            if (fmlver < FMLNetworkConstants.FMLNETVERSION) {
                extraReason = "fml.menu.multiplayer.serveroutdated";
            }
            if (fmlver > FMLNetworkConstants.FMLNETVERSION) {
                extraReason = "fml.menu.multiplayer.clientoutdated";
            }
            target.forgeData = new ExtendedServerListData("FML", extraServerMods.isEmpty() && fmlNetMatches && channelsMatch && modsMatch, mods.size(), extraReason, packet.getForgeData().isTruncated());
        } else {
            target.forgeData = new ExtendedServerListData("VANILLA", NetworkRegistry.canConnectToVanillaServer(),0, null);
        }

    }

    public static void drawForgePingInfo(JoinMultiplayerScreen gui, ServerData target, PoseStack mStack, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
        int idx;
        String tooltip;
        if (target.forgeData == null)
            return;
        switch (target.forgeData.type) {
            case "FML":
                if (target.forgeData.isCompatible) {
                    idx = 0;
                    tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.compatible", target.forgeData.numberOfMods);
                } else {
                    idx = 16;
                    if(target.forgeData.extraReason != null) {
                        String extraReason = ForgeI18n.parseMessage(target.forgeData.extraReason);
                        tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.incompatible.extra", extraReason);
                    } else {
                        tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.incompatible");
                    }
                }
                if (target.forgeData.truncated)
                {
                    tooltip += "\n" + ForgeI18n.parseMessage("fml.menu.multiplayer.truncated");
                }
                break;
            case "VANILLA":
                if (target.forgeData.isCompatible) {
                    idx = 48;
                    tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.vanilla");
                } else {
                    idx = 80;
                    tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.vanilla.incompatible");
                }
                break;
            default:
                idx = 64;
                tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.unknown", target.forgeData.type);
        }

        RenderSystem.setShaderTexture(0, iconSheet);
        GuiComponent.blit(mStack, x + width - 18, y + 10, 16, 16, 0, idx, 16, 16, 256, 256);

        if(relativeMouseX > width - 15 && relativeMouseX < width && relativeMouseY > 10 && relativeMouseY < 26) {
            //this is not the most proper way to do it,
            //but works best here and has the least maintenance overhead
            gui.setToolTip(Arrays.stream(tooltip.split("\n")).map(TextComponent::new).collect(Collectors.toList()));
        }
    }

    public static String fixDescription(String description)
    {
        return description.endsWith(":NOFML§r") ? description.substring(0, description.length() - 8)+"§r" : description;
    }

    @SuppressWarnings("resource")
    static File getSavesDir()
    {
        return new File(Minecraft.getInstance().gameDirectory, "saves");
    }

    private static Connection getClientToServerNetworkManager()
    {
        return Minecraft.getInstance().getConnection()!=null ? Minecraft.getInstance().getConnection().getConnection() : null;
    }

    public static void handleClientWorldClosing(ClientLevel world)
    {
        Connection client = getClientToServerNetworkManager();
        // ONLY revert a non-local connection
        if (client != null && !client.isMemoryConnection())
        {
            GameData.revertToFrozen();
        }
    }


    private static SetMultimap<String,ResourceLocation> missingTextures = HashMultimap.create();
    private static Set<String> badTextureDomains = Sets.newHashSet();
    private static Table<String, String, Set<ResourceLocation>> brokenTextures = HashBasedTable.create();

    public static void trackMissingTexture(ResourceLocation resourceLocation)
    {
        badTextureDomains.add(resourceLocation.getNamespace());
        missingTextures.put(resourceLocation.getNamespace(),resourceLocation);
    }

    public static void trackBrokenTexture(ResourceLocation resourceLocation, String error)
    {
        badTextureDomains.add(resourceLocation.getNamespace());
        Set<ResourceLocation> badType = brokenTextures.get(resourceLocation.getNamespace(), error);
        if (badType == null)
        {
            badType = Sets.newHashSet();
            brokenTextures.put(resourceLocation.getNamespace(), MoreObjects.firstNonNull(error, "Unknown error"), badType);
        }
        badType.add(resourceLocation);
    }

    public static void logMissingTextureErrors()
    {
        if (missingTextures.isEmpty() && brokenTextures.isEmpty())
        {
            return;
        }
        Logger logger = LogManager.getLogger("FML.TEXTURE_ERRORS");
        logger.error(Strings.repeat("+=", 25));
        logger.error("The following texture errors were found.");
        Map<String, FallbackResourceManager> resManagers = ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, (SimpleReloadableResourceManager)Minecraft.getInstance().getResourceManager(), "f_1087"+"0_");
        for (String resourceDomain : badTextureDomains)
        {
            Set<ResourceLocation> missing = missingTextures.get(resourceDomain);
            logger.error(Strings.repeat("=", 50));
            logger.error("  DOMAIN {}", resourceDomain);
            logger.error(Strings.repeat("-", 50));
            logger.error("  domain {} is missing {} texture{}",resourceDomain, missing.size(),missing.size()!=1 ? "s" : "");
            FallbackResourceManager fallbackResourceManager = resManagers.get(resourceDomain);
            if (fallbackResourceManager == null)
            {
                logger.error("    domain {} is missing a resource manager - it is probably a side-effect of automatic texture processing", resourceDomain);
            }
            else
            {
                List<PackResources> resPacks = fallbackResourceManager.fallbacks;
                logger.error("    domain {} has {} location{}:",resourceDomain, resPacks.size(), resPacks.size() != 1 ? "s" :"");
                for (PackResources resPack : resPacks)
                {
                    if (resPack instanceof ModFileResourcePack) {
                        ModFileResourcePack modRP = (ModFileResourcePack) resPack;
                        List<IModInfo> mods = modRP.getModFile().getModInfos();
                        logger.error("      mod(s) {} resources at {}", mods.stream().map(IModInfo::getDisplayName).collect(Collectors.toList()), modRP.getModFile().getFilePath());
                    }
                    else if (resPack instanceof AbstractPackResources)
                    {
                        logger.error("      resource pack at path {}", ((AbstractPackResources)resPack).file.getPath());
                    }
                    else
                    {
                        logger.error("      unknown resourcepack type {} : {}", resPack.getClass().getName(), resPack.getName());
                    }
                }
            }
            logger.error(Strings.repeat("-", 25));
            if (missingTextures.containsKey(resourceDomain)) {
                logger.error("    The missing resources for domain {} are:", resourceDomain);
                for (ResourceLocation rl : missing) {
                    logger.error("      {}", rl.getPath());
                }
                logger.error(Strings.repeat("-", 25));
            }
            if (!brokenTextures.containsRow(resourceDomain))
            {
                logger.error("    No other errors exist for domain {}", resourceDomain);
            }
            else
            {
                logger.error("    The following other errors were reported for domain {}:",resourceDomain);
                Map<String, Set<ResourceLocation>> resourceErrs = brokenTextures.row(resourceDomain);
                for (String error: resourceErrs.keySet())
                {
                    logger.error(Strings.repeat("-", 25));
                    logger.error("    Problem: {}", error);
                    for (ResourceLocation rl : resourceErrs.get(error))
                    {
                        logger.error("      {}",rl.getPath());
                    }
                }
            }
            logger.error(Strings.repeat("=", 50));
        }
        logger.error(Strings.repeat("+=", 25));
    }

    public static void firePlayerLogin(MultiPlayerGameMode pc, LocalPlayer player, Connection networkManager) {
        MinecraftForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggedInEvent(pc, player, networkManager));
    }

    public static void firePlayerLogout(MultiPlayerGameMode pc, LocalPlayer player) {
        MinecraftForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggedOutEvent(pc, player, player != null ? player.connection != null ? player.connection.getConnection() : null : null));
    }

    public static void firePlayerRespawn(MultiPlayerGameMode pc, LocalPlayer oldPlayer, LocalPlayer newPlayer, Connection networkManager) {
        MinecraftForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.RespawnEvent(pc, oldPlayer, newPlayer, networkManager));
    }


}
