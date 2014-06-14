package net.minecraftforge.permissions.api;

import java.util.Collection;
import java.util.List;

import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.permissions.api.context.IContext;
import net.minecraftforge.permissions.opbasedimpl.OpPermFactory;

public final class PermissionsManager
{
    private PermissionsManager()
    {
        // no touch
    }

    private static       boolean            wasSet  = false;
    private static final PermBuilderFactory DEFAULT = new OpPermFactory(); //Forge init takes this as default permissions API provider
    private static PermBuilderFactory FACTORY;

    /**
     * Check a permission
     * @param player The EntityPlayer being checked
     * @param node The permission node to be checked
     * @return true if permission allowed, false if not
     */
    public static boolean checkPerm(EntityPlayer player, String node)
    {
        if (player instanceof FakePlayer)
            throw new IllegalArgumentException("You cannot check permissions with a fake player. Use PermManager.getPerm(username, node)");

        IContext context = FACTORY.getDefaultContext(player);
        return FACTORY.builder(player, node)
                      .setUserContext(context)
                      .setTargetContext(context)
                      .check();
    }

    /**
     * Check a permission
     * @param player The EntityPlayer being checked
     * @param node The permission node to be checked
     * @param targetContext An Entity to be checked
     * @return true if permission allowed, false if not
     */
    public static boolean checkPerm(EntityPlayer player, String node, Entity targetContext)
    {
        if (player instanceof FakePlayer)
            throw new IllegalArgumentException("You cannot check permissions with a fake player. Use PermManager.getPerm(username, node)");

        return FACTORY.builder(player, node)
                      .setUserContext(FACTORY.getDefaultContext(player))
                      .setTargetContext(FACTORY.getDefaultContext(targetContext))
                      .check();
    }

    /**
     * Check a permission
     * @param player The EntityPlayer being checked
     * @param node The permission node to be checked
     * @param targetContext An ILocation to be checked
     * @return true if permission allowed, false if not
     */
    public static boolean checkPerm(EntityPlayer player, String node, ILocation targetContext)
    {
        if (player instanceof FakePlayer)
            throw new IllegalArgumentException("You cannot check permissions with a fake player. Use PermManager.getPerm(username, node)");

        return FACTORY.builder(player, node)
                      .setUserContext(FACTORY.getDefaultContext(player))
                      .setTargetContext(FACTORY.getDefaultContext(targetContext))
                      .check();
    }

    public static PermBuilder getPerm(EntityPlayer player, String node)
    {
        if (player instanceof FakePlayer)
            throw new IllegalArgumentException("You cannot check permissions with a fake player. Use PermManager.getPerm(username, node)");

        IContext context = FACTORY.getDefaultContext(player);
        return FACTORY.builder(player, node)
                      .setUserContext(context)
                      .setTargetContext(context);
    }

    public static PermBuilder getPerm(EntityPlayer player, String node, Entity targetContext)
    {
        if (player instanceof FakePlayer)
            throw new IllegalArgumentException("You cannot check permissions with a fake player. Use PermManager.getPerm(username, node)");

        return FACTORY.builder(player, node)
                      .setUserContext(FACTORY.getDefaultContext(player))
                      .setTargetContext(FACTORY.getDefaultContext(targetContext));
    }

    public static PermBuilder getPerm(EntityPlayer player, String node, ILocation targetContext)
    {
        if (player instanceof FakePlayer)
            throw new IllegalArgumentException("You cannot check permissions with a fake player. Use PermManager.getPerm(username, node)");

        return FACTORY.builder(player, node)
                      .setUserContext(FACTORY.getDefaultContext(player))
                      .setTargetContext(FACTORY.getDefaultContext(targetContext));
    }

    public static PermBuilder getPerm(EntityPlayer player, String node, TileEntity userContext)
    {
        return FACTORY.builder(player, node).setUserContext(FACTORY.getDefaultContext(userContext));
    }

    public static PermBuilder getPermWithNoContexts(EntityPlayer player, String node)
    {
        return FACTORY.builder(player, node);
    }
    
    public static Collection<Group> getGroupsForPlayer(EntityPlayer player)
    {
        return FACTORY.getGroup(player);
    }
    
    public static Collection<Group> getAllGroups()
    {
        return FACTORY.getAllGroups();
    }
    
    public static Group getGroupForName(String name)
    {
        return FACTORY.getGroup(name);
    }

    /**
     * Get the factory
     * FOR ADVANCED OPERATIONS
     * @return the current permission implementor
     */
    public static PermBuilderFactory getPermFactory()
    {
        if (FACTORY != null)
        return FACTORY;
        else return DEFAULT;
    }

    public static void setPermFactory(PermBuilderFactory factory) throws IllegalStateException
    {
        if (factory == null)
        {
            FACTORY = DEFAULT;
            wasSet = false;
        }
        else if (wasSet)
        {
            throw new IllegalStateException("Two Mods are trying to register permissions systems!");
        }
        else
        {
            FACTORY = factory;
            wasSet = true;
        }
    }
}
