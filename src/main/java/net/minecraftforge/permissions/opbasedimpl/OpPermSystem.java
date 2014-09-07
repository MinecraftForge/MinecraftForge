package net.minecraftforge.permissions.opbasedimpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.UnregisteredPermissionException;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;
import net.minecraftforge.permissions.api.IPermissionsProvider;
import net.minecraftforge.permissions.api.context.EntityContext;
import net.minecraftforge.permissions.api.context.EntityLivingContext;
import net.minecraftforge.permissions.api.context.IContext;
import net.minecraftforge.permissions.api.context.PlayerContext;
import net.minecraftforge.permissions.api.context.Point;
import net.minecraftforge.permissions.api.context.TileEntityContext;
import net.minecraftforge.permissions.api.context.WorldContext;
import cpw.mods.fml.common.Loader;

/**
 * This class acts as merely a wrapper around the currently existing ops system in minecraft.
 * It does not provide any additional functionality.
 * You are not recommended to use this in your mods, as this class is not guaranteed to be in use, especially
 * when another permissions framework is installed. Use {@link PermissionsManager.getPermProvider()} instead.
 *
 */
public class OpPermSystem implements IPermissionsProvider
{
    static HashSet<String> opPerms      = new HashSet<String>();
    static HashSet<String> deniedPerms  = new HashSet<String>();
    static HashSet<String> allowedPerms = new HashSet<String>();

    public static final IContext GLOBAL = new IContext() {};
    
    @Override
    public String getName()
    {
        return "Forge";
    }
    
    public boolean checkPerm(EntityPlayer player, String node, Map<String, IContext> contextInfo)
    {
        if (deniedPerms.contains(node))
            return false;
        else if (allowedPerms.contains(node))
            return true;
        else if (opPerms.contains(node))
            return MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile());
        else
            throw new UnregisteredPermissionException(node);
    }

    @Override
    public IContext getDefaultContext(EntityPlayer player)
    {
        IContext context = new PlayerContext(player);
        return context;
    }

    @Override
    public IContext getDefaultContext(TileEntity te)
    {
        return new TileEntityContext(te);
    }

    @Override
    public IContext getDefaultContext(ILocation loc)
    {
        return new Point(loc);
    }

    @Override
    public IContext getDefaultContext(Entity entity)
    {
        return new EntityContext(entity);
    }

    @Override
    public IContext getDefaultContext(World world)
    {
        return new WorldContext(world);
    }

    @Override
    public IContext getGlobalContext()
    {
        return GLOBAL;
    }

    @Override
    public IContext getDefaultContext(Object entity)
    {
        if (entity instanceof EntityLiving)
        {
            return new EntityLivingContext((EntityLiving) entity);
        }
        else
        {
            return GLOBAL;
        }
    }

    private static boolean isRegistered(String node)
    {
        return opPerms.contains(node) || allowedPerms.contains(node) || deniedPerms.contains(node);
    }

    @Override
    public void registerPermission(String node, RegisteredPermValue allow)
    {
        switch (allow)
        {
        case TRUE: allowedPerms.add(node);
        break;
        case FALSE: deniedPerms.add(node);
        break;
        case OP: opPerms.add(node);
        break;
        }
    }
    
}
