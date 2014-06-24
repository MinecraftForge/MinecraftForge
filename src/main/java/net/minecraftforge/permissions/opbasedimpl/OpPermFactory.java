package net.minecraftforge.permissions.opbasedimpl;

import static net.minecraftforge.permissions.api.RegisteredPermValue.FALSE;
import static net.minecraftforge.permissions.api.RegisteredPermValue.TRUE;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.permissions.api.IGroup;
import net.minecraftforge.permissions.api.PermBuilderFactory;
import net.minecraftforge.permissions.api.context.EntityContext;
import net.minecraftforge.permissions.api.context.EntityLivingContext;
import net.minecraftforge.permissions.api.context.IContext;
import net.minecraftforge.permissions.api.context.PlayerContext;
import net.minecraftforge.permissions.api.context.Point;
import net.minecraftforge.permissions.api.context.TileEntityContext;
import net.minecraftforge.permissions.api.context.WorldContext;

public class OpPermFactory implements PermBuilderFactory<Builder>
{
    static HashSet<String> opPerms      = new HashSet<String>();
    static HashSet<String> deniedPerms  = new HashSet<String>();
    static HashSet<String> allowedPerms = new HashSet<String>();
    static HashMap<String, IGroup> groups = new HashMap<String, IGroup>();

    public static final IContext GLOBAL = new IContext() {};
    private static int userPermissionLevel = 0;
    private static int opPermissionLevel = MinecraftServer.getServer().getOpPermissionLevel();
    
    @Override
    public String getName()
    {
        return "Forge";
    }
    
    @Override
    public void initialize()
    {
        String fileData;
        fileData = "This file will only be loaded and read if you have no permission framework installed.";
        fileData += "\n If you have installed a mod that provides a permission framework, use the mod's configs instead.";
        
        //dedicated server and single player have different commands available
        if (FMLCommonHandler.instance().getSide().isServer())
        {
            fileData += "\n To configure permission levels for ops, use op-permission-level in server.properties.";
            
            fileData += "\n Permission levels are as such:";
            
            fileData += "\n 0: Use commands /help, /kill, /me, /list, /tell";
            fileData += "\n 1: Use /broadcast and bypass spawn protection";
            fileData += "\n 2: Use most commands, and command blocks";
            fileData += "\n 3: Use player management commands (op, deop, ban, pardon) and /debug";
            fileData += "\n 4: Use /stop, /save-all, /save-on and /save-off";
            
        }
        else
        {
            fileData += "\n Permission levels are as such:";
            
            fileData += "\n 0: Use commands /help, /kill, /me, /list, /tell";
            fileData += "\n 1: Use /broadcast and bypass spawn protection";
            fileData += "\n 2: Use most commands, and command blocks";
            fileData += "\n 3: Use /debug";
            
        }
        
        Configuration config = new Configuration(new File(Loader.instance().getConfigDir(), "forgeOpBasedPerms.cfg"));
        
        config.addCustomCategoryComment("Help", fileData);
        
        Property prop;
        prop = config.get("Levels", "userPermissionLevel", 0, "Permission level for users");
        userPermissionLevel = prop.getInt(0);
        
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            prop = config.get("Levels", "opPermissionLevel", 0, "Permission level for ops. Only effective in singleplayer.");
            opPermissionLevel = prop.getInt(0);
        }
        
        config.save();
        
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
    public Collection<IGroup> getGroup(EntityPlayer player)
    {
        List<IGroup> reply = new ArrayList<IGroup>();
        Iterator it = groups.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            IGroup group = (IGroup)pairs.getValue();
            if (!group.getAllPlayers().contains(player.getPersistentID())){break;}
            else reply.add(group);
            it.remove();
        }
        return reply;
    }
 
    @Override
    public IGroup getGroup(String name)
    {
        return groups.get(name);
    }
 
    @Override
    public Collection<IGroup> getAllGroups()
    {
        Collection<IGroup> reply = new ArrayList<IGroup>();
        Iterator it = groups.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            IGroup group = (IGroup)pairs.getValue();
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
            if (isRegistered(entry.key))
                continue;

            if (entry.role.getLevel() <= opPermissionLevel)
                opPerms.add(entry.key);
            
            else if (entry.role == TRUE || entry.role.getLevel() <= userPermissionLevel)
                allowedPerms.add(entry.key);
            
            else if (entry.role == FALSE || entry.role.getLevel() > opPermissionLevel)
                deniedPerms.add(entry.key);
            
        }
    }

    private static boolean isRegistered(String node)
    {
        return opPerms.contains(node) || allowedPerms.contains(node) || deniedPerms.contains(node);
    }
    
}
