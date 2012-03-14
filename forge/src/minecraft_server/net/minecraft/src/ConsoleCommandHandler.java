package net.minecraft.src;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.forge.DimensionManager;

public class ConsoleCommandHandler
{
    private static Logger minecraftLogger = Logger.getLogger("Minecraft");

    /** Stores a reference to the Server */
    private MinecraftServer minecraftServer;

    public ConsoleCommandHandler(MinecraftServer par1MinecraftServer)
    {
        this.minecraftServer = par1MinecraftServer;
    }

    /**
     * handles the command that was issued by an Op/Console
     */
    public synchronized void handleCommand(ServerCommand par1ServerCommand)
    {
        String var2 = par1ServerCommand.command;
        ICommandListener var3 = par1ServerCommand.commandListener;
        String var4 = var3.getUsername();
        ServerConfigurationManager var5 = this.minecraftServer.configManager;

        if (!var2.toLowerCase().startsWith("help") && !var2.toLowerCase().startsWith("?"))
        {
            if (var2.toLowerCase().startsWith("list"))
            {
                var3.log("Connected players: " + var5.getPlayerList());
            }
            else if (var2.toLowerCase().startsWith("stop"))
            {
                this.sendNoticeToOps(var4, "Stopping the server..");
                this.minecraftServer.initiateShutdown();
            }
            else
            {
                int var6;
                WorldServer var7;

                if (var2.toLowerCase().startsWith("save-all"))
                {
                    this.sendNoticeToOps(var4, "Forcing save..");

                    if (var5 != null)
                    {
                        var5.savePlayerStates();
                    }

                    for (World world : DimensionManager.getWorlds())
                    {
                        world.saveWorld(true, null);
                    }

                    this.sendNoticeToOps(var4, "Save complete.");
                }
                else if (var2.toLowerCase().startsWith("save-off"))
                {
                    this.sendNoticeToOps(var4, "Disabling level saving..");

                    for (World world : DimensionManager.getWorlds())
                    {
                        ((WorldServer)world).levelSaving = true;
                    }
                }
                else if (var2.toLowerCase().startsWith("save-on"))
                {
                    this.sendNoticeToOps(var4, "Enabling level saving..");

                    for (World world : DimensionManager.getWorlds())
                    {
                        ((WorldServer)world).levelSaving = false;
                    }
                }
                else
                {
                    String var16;

                    if (var2.toLowerCase().startsWith("op "))
                    {
                        var16 = var2.substring(var2.indexOf(" ")).trim();
                        var5.addOp(var16);
                        this.sendNoticeToOps(var4, "Opping " + var16);
                        var5.sendChatMessageToPlayer(var16, "\u00a7eYou are now op!");
                    }
                    else if (var2.toLowerCase().startsWith("deop "))
                    {
                        var16 = var2.substring(var2.indexOf(" ")).trim();
                        var5.removeOp(var16);
                        var5.sendChatMessageToPlayer(var16, "\u00a7eYou are no longer op!");
                        this.sendNoticeToOps(var4, "De-opping " + var16);
                    }
                    else if (var2.toLowerCase().startsWith("ban-ip "))
                    {
                        var16 = var2.substring(var2.indexOf(" ")).trim();
                        var5.banIP(var16);
                        this.sendNoticeToOps(var4, "Banning ip " + var16);
                    }
                    else if (var2.toLowerCase().startsWith("pardon-ip "))
                    {
                        var16 = var2.substring(var2.indexOf(" ")).trim();
                        var5.pardonIP(var16);
                        this.sendNoticeToOps(var4, "Pardoning ip " + var16);
                    }
                    else
                    {
                        EntityPlayerMP var17;

                        if (var2.toLowerCase().startsWith("ban "))
                        {
                            var16 = var2.substring(var2.indexOf(" ")).trim();
                            var5.banPlayer(var16);
                            this.sendNoticeToOps(var4, "Banning " + var16);
                            var17 = var5.getPlayerEntity(var16);

                            if (var17 != null)
                            {
                                var17.playerNetServerHandler.kickPlayer("Banned by admin");
                            }
                        }
                        else if (var2.toLowerCase().startsWith("pardon "))
                        {
                            var16 = var2.substring(var2.indexOf(" ")).trim();
                            var5.pardonPlayer(var16);
                            this.sendNoticeToOps(var4, "Pardoning " + var16);
                        }
                        else
                        {
                            int var8;

                            if (var2.toLowerCase().startsWith("kick "))
                            {
                                var16 = var2.substring(var2.indexOf(" ")).trim();
                                var17 = null;

                                for (var8 = 0; var8 < var5.playerEntities.size(); ++var8)
                                {
                                    EntityPlayerMP var9 = (EntityPlayerMP)var5.playerEntities.get(var8);

                                    if (var9.username.equalsIgnoreCase(var16))
                                    {
                                        var17 = var9;
                                    }
                                }

                                if (var17 != null)
                                {
                                    var17.playerNetServerHandler.kickPlayer("Kicked by admin");
                                    this.sendNoticeToOps(var4, "Kicking " + var17.username);
                                }
                                else
                                {
                                    var3.log("Can\'t find user " + var16 + ". No kick.");
                                }
                            }
                            else
                            {
                                EntityPlayerMP var18;
                                String[] var21;

                                if (var2.toLowerCase().startsWith("tp "))
                                {
                                    var21 = var2.split(" ");

                                    if (var21.length == 3)
                                    {
                                        var17 = var5.getPlayerEntity(var21[1]);
                                        var18 = var5.getPlayerEntity(var21[2]);

                                        if (var17 == null)
                                        {
                                            var3.log("Can\'t find user " + var21[1] + ". No tp.");
                                        }
                                        else if (var18 == null)
                                        {
                                            var3.log("Can\'t find user " + var21[2] + ". No tp.");
                                        }
                                        else if (var17.dimension != var18.dimension)
                                        {
                                            var3.log("User " + var21[1] + " and " + var21[2] + " are in different dimensions. No tp.");
                                        }
                                        else
                                        {
                                            var17.playerNetServerHandler.teleportTo(var18.posX, var18.posY, var18.posZ, var18.rotationYaw, var18.rotationPitch);
                                            this.sendNoticeToOps(var4, "Teleporting " + var21[1] + " to " + var21[2] + ".");
                                        }
                                    }
                                    else
                                    {
                                        var3.log("Syntax error, please provice a source and a target.");
                                    }
                                }
                                else
                                {
                                    int var19;
                                    String var20;

                                    if (var2.toLowerCase().startsWith("give "))
                                    {
                                        var21 = var2.split(" ");

                                        if (var21.length != 3 && var21.length != 4 && var21.length != 5)
                                        {
                                            return;
                                        }

                                        var20 = var21[1];
                                        var18 = var5.getPlayerEntity(var20);

                                        if (var18 != null)
                                        {
                                            try
                                            {
                                                var19 = Integer.parseInt(var21[2]);

                                                if (Item.itemsList[var19] != null)
                                                {
                                                    this.sendNoticeToOps(var4, "Giving " + var18.username + " some " + var19);
                                                    int var10 = 1;
                                                    int var11 = 0;

                                                    if (var21.length > 3)
                                                    {
                                                        var10 = this.tryParse(var21[3], 1);
                                                    }

                                                    if (var21.length > 4)
                                                    {
                                                        var11 = this.tryParse(var21[4], 1);
                                                    }

                                                    if (var10 < 1)
                                                    {
                                                        var10 = 1;
                                                    }

                                                    if (var10 > 64)
                                                    {
                                                        var10 = 64;
                                                    }

                                                    var18.func_48348_b(new ItemStack(var19, var10, var11));
                                                }
                                                else
                                                {
                                                    var3.log("There\'s no item with id " + var19);
                                                }
                                            }
                                            catch (NumberFormatException var14)
                                            {
                                                var3.log("There\'s no item with id " + var21[2]);
                                            }
                                        }
                                        else
                                        {
                                            var3.log("Can\'t find user " + var20);
                                        }
                                    }
                                    else if (var2.toLowerCase().startsWith("xp"))
                                    {
                                        var21 = var2.split(" ");

                                        if (var21.length != 3)
                                        {
                                            return;
                                        }

                                        var20 = var21[1];
                                        var18 = var5.getPlayerEntity(var20);

                                        if (var18 != null)
                                        {
                                            try
                                            {
                                                var19 = Integer.parseInt(var21[2]);
                                                var19 = var19 > 5000 ? 5000 : var19;
                                                this.sendNoticeToOps(var4, "Giving " + var19 + " orbs to " + var18.username);
                                                var18.addExperience(var19);
                                            }
                                            catch (NumberFormatException var13)
                                            {
                                                var3.log("Invalid orb count: " + var21[2]);
                                            }
                                        }
                                        else
                                        {
                                            var3.log("Can\'t find user " + var20);
                                        }
                                    }
                                    else if (var2.toLowerCase().startsWith("gamemode "))
                                    {
                                        var21 = var2.split(" ");

                                        if (var21.length != 3)
                                        {
                                            return;
                                        }

                                        var20 = var21[1];
                                        var18 = var5.getPlayerEntity(var20);

                                        if (var18 != null)
                                        {
                                            try
                                            {
                                                var19 = Integer.parseInt(var21[2]);
                                                var19 = WorldSettings.validGameType(var19);

                                                if (var18.itemInWorldManager.getGameType() != var19)
                                                {
                                                    this.sendNoticeToOps(var4, "Setting " + var18.username + " to game mode " + var19);
                                                    var18.itemInWorldManager.toggleGameType(var19);
                                                    var18.playerNetServerHandler.sendPacket(new Packet70Bed(3, var19));
                                                }
                                                else
                                                {
                                                    this.sendNoticeToOps(var4, var18.username + " already has game mode " + var19);
                                                }
                                            }
                                            catch (NumberFormatException var12)
                                            {
                                                var3.log("There\'s no game mode with id " + var21[2]);
                                            }
                                        }
                                        else
                                        {
                                            var3.log("Can\'t find user " + var20);
                                        }
                                    }
                                    else if (var2.toLowerCase().startsWith("time "))
                                    {
                                        var21 = var2.split(" ");

                                        if (var21.length != 3)
                                        {
                                            return;
                                        }

                                        var20 = var21[1];

                                        try
                                        {
                                            var8 = Integer.parseInt(var21[2]);
                                            WorldServer var22;

                                            if ("add".equalsIgnoreCase(var20))
                                            {
                                                for (World world : DimensionManager.getWorlds())
                                                {
                                                    world.advanceTime(world.getWorldTime() + (long)var8);
                                                }

                                                this.sendNoticeToOps(var4, "Added " + var8 + " to time");
                                            }
                                            else if ("set".equalsIgnoreCase(var20))
                                            {
                                                for (World world : DimensionManager.getWorlds())
                                                {
                                                    world.advanceTime((long)var8);
                                                }

                                                this.sendNoticeToOps(var4, "Set time to " + var8);
                                            }
                                            else
                                            {
                                                var3.log("Unknown method, use either \"add\" or \"set\"");
                                            }
                                        }
                                        catch (NumberFormatException var15)
                                        {
                                            var3.log("Unable to convert time value, " + var21[2]);
                                        }
                                    }
                                    else if (var2.toLowerCase().startsWith("say "))
                                    {
                                        var2 = var2.substring(var2.indexOf(" ")).trim();
                                        minecraftLogger.info("[" + var4 + "] " + var2);
                                        var5.sendPacketToAllPlayers(new Packet3Chat("\u00a7d[Server] " + var2));
                                    }
                                    else if (var2.toLowerCase().startsWith("tell "))
                                    {
                                        var21 = var2.split(" ");

                                        if (var21.length >= 3)
                                        {
                                            var2 = var2.substring(var2.indexOf(" ")).trim();
                                            var2 = var2.substring(var2.indexOf(" ")).trim();
                                            minecraftLogger.info("[" + var4 + "->" + var21[1] + "] " + var2);
                                            var2 = "\u00a77" + var4 + " whispers " + var2;
                                            minecraftLogger.info(var2);

                                            if (!var5.sendPacketToPlayer(var21[1], new Packet3Chat(var2)))
                                            {
                                                var3.log("There\'s no player by that name online.");
                                            }
                                        }
                                    }
                                    else if (var2.toLowerCase().startsWith("whitelist "))
                                    {
                                        this.handleWhitelist(var4, var2, var3);
                                    }
                                    else if (var2.toLowerCase().startsWith("toggledownfall"))
                                    {
                                        ((WorldServer)DimensionManager.getWorld(0)).commandToggleDownfall();
                                        var3.log("Toggling rain and snow, hold on...");
                                    }
                                    else if (var2.toLowerCase().startsWith("banlist"))
                                    {
                                        var21 = var2.split(" ");

                                        if (var21.length == 2)
                                        {
                                            if (var21[1].equals("ips"))
                                            {
                                                var3.log("IP Ban list:" + this.func_40648_a(this.minecraftServer.getBannedIPsList(), ", "));
                                            }
                                        }
                                        else
                                        {
                                            var3.log("Ban list:" + this.func_40648_a(this.minecraftServer.getBannedPlayersList(), ", "));
                                        }
                                    }
                                    else
                                    {
                                        minecraftLogger.info("Unknown console command. Type \"help\" for help.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else
        {
            this.printHelp(var3);
        }
    }

    /**
     * Handles the whitelist command
     */
    private void handleWhitelist(String par1Str, String par2Str, ICommandListener par3ICommandListener)
    {
        String[] var4 = par2Str.split(" ");

        if (var4.length >= 2)
        {
            String var5 = var4[1].toLowerCase();

            if ("on".equals(var5))
            {
                this.sendNoticeToOps(par1Str, "Turned on white-listing");
                this.minecraftServer.propertyManagerObj.setProperty("white-list", true);
            }
            else if ("off".equals(var5))
            {
                this.sendNoticeToOps(par1Str, "Turned off white-listing");
                this.minecraftServer.propertyManagerObj.setProperty("white-list", false);
            }
            else if ("list".equals(var5))
            {
                Set var6 = this.minecraftServer.configManager.getWhiteListedIPs();
                String var7 = "";
                String var9;

                for (Iterator var8 = var6.iterator(); var8.hasNext(); var7 = var7 + var9 + " ")
                {
                    var9 = (String)var8.next();
                }

                par3ICommandListener.log("White-listed players: " + var7);
            }
            else
            {
                String var10;

                if ("add".equals(var5) && var4.length == 3)
                {
                    var10 = var4[2].toLowerCase();
                    this.minecraftServer.configManager.addToWhiteList(var10);
                    this.sendNoticeToOps(par1Str, "Added " + var10 + " to white-list");
                }
                else if ("remove".equals(var5) && var4.length == 3)
                {
                    var10 = var4[2].toLowerCase();
                    this.minecraftServer.configManager.removeFromWhiteList(var10);
                    this.sendNoticeToOps(par1Str, "Removed " + var10 + " from white-list");
                }
                else if ("reload".equals(var5))
                {
                    this.minecraftServer.configManager.reloadWhiteList();
                    this.sendNoticeToOps(par1Str, "Reloaded white-list from file");
                }
            }
        }
    }

    /**
     * Print help on server commands
     */
    private void printHelp(ICommandListener par1ICommandListener)
    {
        par1ICommandListener.log("To run the server without a gui, start it like this:");
        par1ICommandListener.log("   java -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui");
        par1ICommandListener.log("Console commands:");
        par1ICommandListener.log("   help  or  ?               shows this message");
        par1ICommandListener.log("   kick <player>             removes a player from the server");
        par1ICommandListener.log("   ban <player>              bans a player from the server");
        par1ICommandListener.log("   pardon <player>           pardons a banned player so that they can connect again");
        par1ICommandListener.log("   ban-ip <ip>               bans an IP address from the server");
        par1ICommandListener.log("   pardon-ip <ip>            pardons a banned IP address so that they can connect again");
        par1ICommandListener.log("   op <player>               turns a player into an op");
        par1ICommandListener.log("   deop <player>             removes op status from a player");
        par1ICommandListener.log("   tp <player1> <player2>    moves one player to the same location as another player");
        par1ICommandListener.log("   give <player> <id> [num]  gives a player a resource");
        par1ICommandListener.log("   tell <player> <message>   sends a private message to a player");
        par1ICommandListener.log("   stop                      gracefully stops the server");
        par1ICommandListener.log("   save-all                  forces a server-wide level save");
        par1ICommandListener.log("   save-off                  disables terrain saving (useful for backup scripts)");
        par1ICommandListener.log("   save-on                   re-enables terrain saving");
        par1ICommandListener.log("   list                      lists all currently connected players");
        par1ICommandListener.log("   say <message>             broadcasts a message to all players");
        par1ICommandListener.log("   time <add|set> <amount>   adds to or sets the world time (0-24000)");
        par1ICommandListener.log("   gamemode <player> <mode>  sets player\'s game mode (0 or 1)");
    }

    /**
     * sends a notice to all online ops.
     */
    private void sendNoticeToOps(String par1Str, String par2Str)
    {
        String var3 = par1Str + ": " + par2Str;
        this.minecraftServer.configManager.sendChatMessageToAllOps("\u00a77(" + var3 + ")");
        minecraftLogger.info(var3);
    }

    /**
     * Parses First argument if possible; if not returns second argument
     */
    private int tryParse(String par1Str, int par2)
    {
        try
        {
            return Integer.parseInt(par1Str);
        }
        catch (NumberFormatException var4)
        {
            return par2;
        }
    }

    private String func_40648_a(String[] par1ArrayOfStr, String par2Str)
    {
        int var3 = par1ArrayOfStr.length;

        if (0 == var3)
        {
            return "";
        }
        else
        {
            StringBuilder var4 = new StringBuilder();
            var4.append(par1ArrayOfStr[0]);

            for (int var5 = 1; var5 < var3; ++var5)
            {
                var4.append(par2Str).append(par1ArrayOfStr[var5]);
            }

            return var4.toString();
        }
    }
}
