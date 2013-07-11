package net.minecraftforge.permissions.api;

import net.minecraftforge.permissions.implementation.PermissionAttachmentInfo;

public interface IPermissionCalculator 
{
    public boolean getValue(IPermissionAttachmentInfo info);
    
    public boolean getValue(IPermissionAttachmentInfo info, Object... varArgs);

}
