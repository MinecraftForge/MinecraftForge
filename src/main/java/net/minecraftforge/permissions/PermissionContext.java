package net.minecraftforge.permissions;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

/**
 * This is the base class for a context. A permission framework
 * <i><b>might</b></i> subclass this context, to allow additional properties for
 * the context.
 */
public class PermissionContext implements IContext{

    private EntityPlayer player;

    private EntityPlayer targetPlayer;

    private ICommand command;

    private ICommandSender commandSender;

    private Vec3 sourceLocationStart;

    private Vec3 sourceLocationEnd;

    private Vec3 targetLocationStart;

    private Vec3 targetLocationEnd;

    private Entity sourceEntity;

    private Entity targetEntity;
    
    private World world;

    public EntityPlayer getPlayer()
    {
        return player;
    }

    public EntityPlayer getTargetPlayer()
    {
        return targetPlayer;
    }

    public ICommand getCommand()
    {
        return command;
    }

    public ICommandSender getCommandSender()
    {
        return commandSender;
    }

    public Vec3 getSourceLocationStart()
    {
        return sourceLocationStart;
    }

    public Vec3 getSourceLocationEnd()
    {
        return sourceLocationEnd;
    }

    public Vec3 getTargetLocationStart()
    {
        return targetLocationStart;
    }

    public Vec3 getTargetLocationEnd()
    {
        return targetLocationEnd;
    }

    public Entity getSourceEntity()
    {
        return sourceEntity;
    }

    public Entity getTargetEntity()
    {
        return targetEntity;
    }
    
    public World getWorld()
    {
        return world;
    }

    public PermissionContext setPlayer(EntityPlayer player)
    {
        this.player = player;
        return this;
    }

    public PermissionContext setTargetPlayer(EntityPlayer player)
    {
        this.targetPlayer = player;
        return this;
    }

    public PermissionContext setCommand(ICommand command)
    {
        this.command = command;
        return this;
    }

    public PermissionContext setCommandSender(ICommandSender sender)
    {
        this.commandSender = sender;
        return this;
    }

    public PermissionContext setSourceLocationStart(Vec3 location)
    {
        this.sourceLocationStart = location;
        return this;
    }

    public PermissionContext setSourceLocationEnd(Vec3 location)
    {
        this.sourceLocationEnd = location;
        return this;
    }

    public PermissionContext setTargetLocationStart(Vec3 location)
    {
        this.targetLocationStart = location;
        return this;
    }

    public PermissionContext setTargetLocationEnd(Vec3 location)
    {
        this.targetLocationEnd = location;
        return this;
    }

    public PermissionContext setSourceEntity(Entity entity)
    {
        this.sourceEntity = entity;
        return this;
    }

    public PermissionContext setTargetEntity(Entity entity)
    {
        this.targetEntity = entity;
        return this;
    }
    
    public PermissionContext setWorld(int dimID)
    {
        this.world = DimensionManager.getWorld(dimID);
        return this;
    }
    
    public PermissionContext setWorld(World world)
    {
        this.world = world;
        return this;
    }

    public PermissionContext(){}

    public boolean isConsole()
    {
        return player == null && commandSender != null && !(commandSender instanceof EntityPlayer);
    }

    public boolean isPlayer()
    {
        return (player instanceof EntityPlayer) || (commandSender instanceof EntityPlayer);
    }

}