/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.server.command.world;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class NewWorldJob extends AbstractWorldJob
{

    private long seed;
    private WorldType type;
    private String generatorOptions;

    NewWorldJob(MinecraftServer server, ICommandSender sender, String folderName, int delayS, long seed, WorldType type, String generatorOptions)
    {
        super(server, sender, folderName, delayS);
        this.seed = seed;
        this.type = type;
        this.generatorOptions = generatorOptions;
    }

    @Override
    public void executeWorldJob()
    {
        switchWorld(folderName, folderName, seed, type, generatorOptions);
    }

    protected void switchWorld(String saveName, String worldNameIn, long seed, WorldType type, String generatorOptions)
    {
        Logger logger = LogManager.getLogger();

        PlayerList playerList = server.getPlayerList();
        if (playerList != null)
        {
            logger.info("Saving players");
            playerList.saveAllPlayerData();
            playerList.removeAllPlayers();
        }

        if (server.worlds != null)
        {
            logger.info("Saving worlds");

            for (WorldServer worldserver : server.worlds)
            {
                if (worldserver != null)
                {
                    worldserver.disableLevelSaving = false;
                }
            }

            server.saveAllWorlds(false);

            for (WorldServer worldserver1 : server.worlds)
            {
                if (worldserver1 != null)
                {
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(worldserver1));
                    worldserver1.flush();
                }
            }

            WorldServer[] tmp = server.worlds;
            for (WorldServer world : tmp)
            {
                net.minecraftforge.common.DimensionManager.setWorld(world.provider.getDimension(), null, server);
            }
        }

        server.loadAllWorlds(saveName, worldNameIn, seed, type, generatorOptions);
    }
}
