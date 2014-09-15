package net.minecraftforge.permissions;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

/**
 * An interface for a Context class. Contexts are not meant to be saved and never should be.
 * Framework authors, do not implement this interface directly. Subclass PermissionContext.
 *
 */
public interface IContext
{
    EntityPlayer getPlayer();
    
    EntityPlayer getTargetPlayer();
    
    ICommand getCommand();
    
    ICommandSender getCommandSender();
    
    Vec3 getSourceLocationStart();
    
    Vec3 getSourceLocationEnd();
    
    Vec3 getTargetLocationStart();
    
    Vec3 getTargetLocationEnd();
    
    Entity getSourceEntity();
    
    Entity getTargetEntity();
}