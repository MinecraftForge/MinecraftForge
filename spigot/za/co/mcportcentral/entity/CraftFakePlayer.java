package za.co.mcportcentral.entity;

import cpw.mods.fml.common.FMLCommonHandler;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.world.World;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class CraftFakePlayer extends CraftPlayer
{
    public CraftFakePlayer(CraftServer server, EntityPlayerMP entity)
    {
        super(server, entity);
    }

    // Map of all active fake player usernames to their entities
    private static Map<String, EntityPlayerMP> fakePlayers = new HashMap<String, EntityPlayerMP>();

    // Map mod EntityPlayer class name to username (default or as configured) and doLogin settings
    private static Map<String, String> fakePlayersUsername = new HashMap<String, String>();
    private static Map<String, Boolean> fakePlayersDoLogin = new HashMap<String, Boolean>();

    /**
     * Get a fake player, creating if necessary
     * @param world The world to login the player from. This is only used when the player is first created;
     *              fake players can cross worlds without issue.
     * @param modFakePlayer The mod's fake player instance, if available. Note this is an EntityPlayer, which
     *                      is insufficient for Bukkit's purposes, hence can be 'converted' to an EntityPlayerMP
     *                      through CraftFakePlayer. The class name is used for naming the new fake player.
     * @return
     */
    public static EntityPlayerMP get(World world, EntityPlayer modFakePlayer)
    {
        String className;

        if (modFakePlayer != null)
        {
            // Fake player is configured via its class name
            className = modFakePlayer.getClass().getName().replace('.', '/');
        }
        else
        {
            className = "default";
        }

        if (!fakePlayersUsername.containsKey(className))
        {
            String defaultName;

            if (modFakePlayer == null)
            {
                // Global fake player name, if mod cannot be identified
                defaultName = "[FakePlayer]";
            } else {
                // Default to either the mod's fake player username, or class name if unspecified
                if (modFakePlayer.username != null && !modFakePlayer.username.equals("")) {
                    defaultName = "[" + modFakePlayer.username + "]";
                } else {
                    defaultName = "[" + className + "]";
                }
            }

            // Use custom name defined by administrator, if any
            String username = ((CraftServer)Bukkit.getServer()).configuration.getString("mcpc.fake-players." + className + ".username", defaultName);

            System.out.println("[FakePlayer] Initializing fake player for " + className + ": " + username);

            boolean doLogin = ((CraftServer)Bukkit.getServer()).configuration.getBoolean("mcpc.fake-players." + className + ".do-login", false);

            fakePlayersUsername.put(className, username);
            fakePlayersDoLogin.put(className, doLogin);
        }

        return get(world, fakePlayersUsername.get(className), fakePlayersDoLogin.get(className));
    }

    /**
     * Get a fake player with a given username
     */
    public static EntityPlayerMP get(World world, String username, boolean doLogin)
    {
        if (!fakePlayers.containsKey(username))
        {
            EntityPlayerMP fakePlayer = new EntityPlayerMP(FMLCommonHandler.instance().getMinecraftServerInstance(), world,
                    username, new ItemInWorldManager(world));

            if (doLogin)
            {
                PlayerLoginEvent ple = new PlayerLoginEvent(fakePlayer.getBukkitEntity(), "", null);
                world.getServer().getPluginManager().callEvent(ple);
                if (ple.getResult() != PlayerLoginEvent.Result.ALLOWED)
                {
                    System.err.println("[FakePlayer] Warning: Login event was disallowed for "+username+". Ignoring, but this may cause confused plugins.");
                }

                PlayerJoinEvent pje = new PlayerJoinEvent(fakePlayer.getBukkitEntity(), "");
                world.getServer().getPluginManager().callEvent(pje);
            }

            fakePlayers.put(username, fakePlayer);
        }

        return fakePlayers.get(username);
    }

    public static EntityPlayerMP get(World world)
    {
        return get(world, null);
    }

    // Bukkit wrappers

    public static CraftPlayer getBukkitEntity(World world, EntityPlayer modFakePlayer)
    {
        EntityPlayerMP player = get(world, modFakePlayer);
        if (player != null)
        {
            return player.getBukkitEntity();
        }
        return null;
    }

    public static CraftPlayer getBukkitEntity(World world)
    {
        return getBukkitEntity(world, null);
    }

    public static CraftPlayer getBukkitEntity(World world, String username, boolean doLogin)
    {
        EntityPlayerMP player = get(world, username, doLogin);
        if (player != null)
        {
            return player.getBukkitEntity();
        }
        return null;
    }

    public static Player getPossiblyRealPlayerBukkitEntity(World world, String username, boolean doLogin)
    {
        if (username.startsWith("[") && username.endsWith("]"))
        {
            // always a fake player
        }
        else
        {
            Player player = Bukkit.getServer().getPlayer(username);
            if (player != null)
            {
                // real player is logged in, use it
                return player;
            }
            // offline player, they still need a fake player
        }

        return getBukkitEntity(world, username, doLogin);
    }
}
