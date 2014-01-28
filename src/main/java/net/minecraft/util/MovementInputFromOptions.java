package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.GameSettings;

@SideOnly(Side.CLIENT)
public class MovementInputFromOptions extends MovementInput
{
    private GameSettings gameSettings;
    private static final String __OBFID = "CL_00000937";

    public MovementInputFromOptions(GameSettings par1GameSettings)
    {
        this.gameSettings = par1GameSettings;
    }

    public void updatePlayerMoveState()
    {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.func_151470_d())
        {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.func_151470_d())
        {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.func_151470_d())
        {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.func_151470_d())
        {
            --this.moveStrafe;
        }

        this.jump = this.gameSettings.keyBindJump.func_151470_d();
        this.sneak = this.gameSettings.keyBindSneak.func_151470_d();

        if (this.sneak)
        {
            this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
            this.moveForward = (float)((double)this.moveForward * 0.3D);
        }
    }
}