package net.minecraftforge.permissions.opbasedimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.permissions.api.Group;
import net.minecraftforge.permissions.api.PermBuilderFactory;
import net.minecraftforge.permissions.api.context.EntityContext;
import net.minecraftforge.permissions.api.context.EntityLivingContext;
import net.minecraftforge.permissions.api.context.IContext;
import net.minecraftforge.permissions.api.context.PlayerContext;
import net.minecraftforge.permissions.api.context.Point;
import net.minecraftforge.permissions.api.context.TileEntityContext;
import net.minecraftforge.permissions.api.context.WorldContext;
import static net.minecraftforge.permissions.api.RegisteredPermValue.*;

public class OpPermFactory implements PermBuilderFactory<Builder>
{
    static HashSet<String> opPerms      = new HashSet<String>();
    static HashSet<String> deniedPerms  = new HashSet<String>();
    static HashSet<String> allowedPerms = new HashSet<String>();
    static HashMap<String, Group> groups = new HashMap<String, Group>();

    public static final IContext GLOBAL = new IContext() {};
    
    @Override
    public void initialize()
    {
        groups.put("ALL", new OpBasedGroup("ALL"));
        groups.put("OP", new OpBasedGroup("OP"));
    }

    @Override
    public Builder builder()
    {
        return new Builder();
    }

    @Override
    public Builder builder(EntityPlayer player, String permNode)
    {
        return new Builder().setUser(player).setPermNode(permNode);
    }

    @Override
    public IContext getDefaultContext(EntityPlayer player)
    {
        return new PlayerContext(player);
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
    
    @Override
    public Collection<Group> getGroup(EntityPlayer player)
    {
        List<Group> reply = new ArrayList<Group>();
        Iterator it = groups.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            Group group = (Group)pairs.getValue();
            if (!group.getAllPlayers().contains(player.getPersistentID())){break;}
            else reply.add(group);
            it.remove();
        }
        return reply;
    }
 
    @Override
    public Group getGroup(String name)
    {
        return groups.get(name);
    }
 
    @Override
    public Collection<Group> getAllGroups()
    {
        Collection<Group> reply = new ArrayList<Group>();
        Iterator it = groups.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            Group group = (Group)pairs.getValue();
            reply.add(group);
            it.remove();
        }
        return reply;
    }

    @Override
    public void registerPermissions(List<PermReg> perms)
    {
        for (PermReg entry : perms)
        {
            // avoid duplicates that are already configured
            if (isRegisterred(entry.key))
                continue;

            if (entry.role.getLevel() <= MinecraftServer.getServer().getOpPermissionLevel())
                opPerms.add(entry.key);
            
            else if (entry.role == TRUE)
                allowedPerms.add(entry.key);
            
            else if (entry.role == FALSE || entry.role.getLevel() > MinecraftServer.getServer().getOpPermissionLevel())
                deniedPerms.add(entry.key);
            
        }
    }

    private static boolean isRegisterred(String node)
    {
        return opPerms.contains(node) || allowedPerms.contains(node) || deniedPerms.contains(node);
    }
    
}
