package net.minecraftforge.permissions.implementation;

import net.minecraftforge.permissions.PermissionAttachment;
import net.minecraftforge.permissions.api.IPermissible;
import net.minecraftforge.permissions.api.IPermissionCalculator;

public class AdvancedPermissionAttachmentInfo extends PermissionAttachmentInfo
{
    protected IPermissionCalculator permissionCalculator;
    
    public AdvancedPermissionAttachmentInfo(IPermissible permissible, String permission, PermissionAttachment attachment, boolean value, IPermissionCalculator calculator)
    {
        super(permissible, permission, attachment, value);
        this.permissionCalculator = calculator;
    }

    
    @Override
    public boolean getValue() {
        return this.permissionCalculator.getValue(this);
    }
    
    @Override
    public boolean getValue(Object... varArgs)
    {
        return this.permissionCalculator.getValue(this);
    }
}
