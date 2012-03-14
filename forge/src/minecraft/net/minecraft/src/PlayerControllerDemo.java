package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class PlayerControllerDemo extends PlayerControllerSP
{
    private boolean field_48518_c = false;
    private int field_48517_d = 0;

    public PlayerControllerDemo(Minecraft par1Minecraft)
    {
        super(par1Minecraft);
    }

    public void updateController()
    {
        super.updateController();
        long var1 = this.mc.theWorld.getWorldTime();
        long var3 = var1 / 24000L + 1L;
        this.field_48518_c = var1 > 120500L;

        if (this.field_48518_c)
        {
            ++this.field_48517_d;
        }

        if (var1 % 24000L == 500L)
        {
            if (var3 <= 6L)
            {
                this.mc.ingameGUI.addChatMessageTranslate("demo.day." + var3);
            }
        }
        else if (var3 == 1L)
        {
            GameSettings var5 = this.mc.gameSettings;
            StringTranslate var6 = StringTranslate.getInstance();
            String var7 = null;

            if (var1 == 100L)
            {
                var7 = var6.translateKey("demo.help.movement");
                var7 = String.format(var7, new Object[] {Keyboard.getKeyName(var5.keyBindForward.keyCode), Keyboard.getKeyName(var5.keyBindLeft.keyCode), Keyboard.getKeyName(var5.keyBindBack.keyCode), Keyboard.getKeyName(var5.keyBindRight.keyCode)});
            }
            else if (var1 == 175L)
            {
                var7 = var6.translateKey("demo.help.jump");
                var7 = String.format(var7, new Object[] {Keyboard.getKeyName(var5.keyBindJump.keyCode)});
            }
            else if (var1 == 250L)
            {
                var7 = var6.translateKey("demo.help.inventory");
                var7 = String.format(var7, new Object[] {Keyboard.getKeyName(var5.keyBindInventory.keyCode)});
            }

            if (var7 != null)
            {
                this.mc.ingameGUI.addChatMessage(var7);
            }
        }
        else if (var3 == 5L && var1 % 24000L == 22000L)
        {
            this.mc.ingameGUI.addChatMessageTranslate("demo.day.warning");
        }
    }

    private void func_48516_j()
    {
        if (this.field_48517_d > 100)
        {
            this.mc.ingameGUI.addChatMessageTranslate("demo.reminder");
            this.field_48517_d = 0;
        }
    }

    /**
     * Called by Minecraft class when the player is hitting a block with an item. Args: x, y, z, side
     */
    public void clickBlock(int par1, int par2, int par3, int par4)
    {
        if (this.field_48518_c)
        {
            this.func_48516_j();
        }
        else
        {
            super.clickBlock(par1, par2, par3, par4);
        }
    }

    /**
     * Called when a player damages a block and updates damage counters
     */
    public void onPlayerDamageBlock(int par1, int par2, int par3, int par4)
    {
        if (!this.field_48518_c)
        {
            super.onPlayerDamageBlock(par1, par2, par3, par4);
        }
    }

    /**
     * Called when a player completes the destruction of a block
     */
    public boolean onPlayerDestroyBlock(int par1, int par2, int par3, int par4)
    {
        return this.field_48518_c ? false : super.onPlayerDestroyBlock(par1, par2, par3, par4);
    }

    /**
     * Notifies the server of things like consuming food, etc...
     */
    public boolean sendUseItem(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack)
    {
        if (this.field_48518_c)
        {
            this.func_48516_j();
            return false;
        }
        else
        {
            return super.sendUseItem(par1EntityPlayer, par2World, par3ItemStack);
        }
    }

    /**
     * Handles a players right click
     */
    public boolean onPlayerRightClick(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack, int par4, int par5, int par6, int par7)
    {
        if (this.field_48518_c)
        {
            this.func_48516_j();
            return false;
        }
        else
        {
            return super.onPlayerRightClick(par1EntityPlayer, par2World, par3ItemStack, par4, par5, par6, par7);
        }
    }

    /**
     * Attacks an entity
     */
    public void attackEntity(EntityPlayer par1EntityPlayer, Entity par2Entity)
    {
        if (this.field_48518_c)
        {
            this.func_48516_j();
        }
        else
        {
            super.attackEntity(par1EntityPlayer, par2Entity);
        }
    }
}
