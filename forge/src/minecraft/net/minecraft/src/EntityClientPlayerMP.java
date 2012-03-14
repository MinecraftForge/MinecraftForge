package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class EntityClientPlayerMP extends EntityPlayerSP
{
    public NetClientHandler sendQueue;

    /**
     * Tick counter that resets every 20 ticks, used for sending inventory updates
     */
    private int inventoryUpdateTickCounter = 0;

    /** has the client player's health been set? */
    private boolean hasSetHealth = false;
    private double oldPosX;

    /** Old Minimum Y of the bounding box */
    private double oldMinY;
    private double oldPosY;
    private double oldPosZ;
    private float oldRotationYaw;
    private float oldRotationPitch;

    /** Check if was on ground last update */
    private boolean wasOnGround = false;

    /** should the player stop sneaking? */
    private boolean shouldStopSneaking = false;
    private boolean wasSneaking = false;

    /** The time since the client player moved */
    private int timeSinceMoved = 0;

    public EntityClientPlayerMP(Minecraft par1Minecraft, World par2World, Session par3Session, NetClientHandler par4NetClientHandler)
    {
        super(par1Minecraft, par2World, par3Session, 0);
        this.sendQueue = par4NetClientHandler;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        return false;
    }

    /**
     * Heal living entity (param: amount of half-hearts)
     */
    public void heal(int par1) {}

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (this.worldObj.blockExists(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ)))
        {
            super.onUpdate();
            this.onUpdate2();
        }
    }

    public void onUpdate2()
    {
        if (this.inventoryUpdateTickCounter++ == 20)
        {
            this.sendInventoryChanged();
            this.inventoryUpdateTickCounter = 0;
        }

        boolean var1 = this.isSprinting();

        if (var1 != this.wasSneaking)
        {
            if (var1)
            {
                this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 4));
            }
            else
            {
                this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 5));
            }

            this.wasSneaking = var1;
        }

        boolean var2 = this.isSneaking();

        if (var2 != this.shouldStopSneaking)
        {
            if (var2)
            {
                this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 1));
            }
            else
            {
                this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 2));
            }

            this.shouldStopSneaking = var2;
        }

        double var3 = this.posX - this.oldPosX;
        double var5 = this.boundingBox.minY - this.oldMinY;
        double var7 = this.posY - this.oldPosY;
        double var9 = this.posZ - this.oldPosZ;
        double var11 = (double)(this.rotationYaw - this.oldRotationYaw);
        double var13 = (double)(this.rotationPitch - this.oldRotationPitch);
        boolean var15 = var5 != 0.0D || var7 != 0.0D || var3 != 0.0D || var9 != 0.0D;
        boolean var16 = var11 != 0.0D || var13 != 0.0D;

        if (this.ridingEntity != null)
        {
            if (var16)
            {
                this.sendQueue.addToSendQueue(new Packet11PlayerPosition(this.motionX, -999.0D, -999.0D, this.motionZ, this.onGround));
            }
            else
            {
                this.sendQueue.addToSendQueue(new Packet13PlayerLookMove(this.motionX, -999.0D, -999.0D, this.motionZ, this.rotationYaw, this.rotationPitch, this.onGround));
            }

            var15 = false;
        }
        else if (var15 && var16)
        {
            this.sendQueue.addToSendQueue(new Packet13PlayerLookMove(this.posX, this.boundingBox.minY, this.posY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround));
            this.timeSinceMoved = 0;
        }
        else if (var15)
        {
            this.sendQueue.addToSendQueue(new Packet11PlayerPosition(this.posX, this.boundingBox.minY, this.posY, this.posZ, this.onGround));
            this.timeSinceMoved = 0;
        }
        else if (var16)
        {
            this.sendQueue.addToSendQueue(new Packet12PlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
            this.timeSinceMoved = 0;
        }
        else
        {
            this.sendQueue.addToSendQueue(new Packet10Flying(this.onGround));

            if (this.wasOnGround == this.onGround && this.timeSinceMoved <= 200)
            {
                ++this.timeSinceMoved;
            }
            else
            {
                this.timeSinceMoved = 0;
            }
        }

        this.wasOnGround = this.onGround;

        if (var15)
        {
            this.oldPosX = this.posX;
            this.oldMinY = this.boundingBox.minY;
            this.oldPosY = this.posY;
            this.oldPosZ = this.posZ;
        }

        if (var16)
        {
            this.oldRotationYaw = this.rotationYaw;
            this.oldRotationPitch = this.rotationPitch;
        }
    }

    public EntityItem func_48152_as()
    {
        this.sendQueue.addToSendQueue(new Packet14BlockDig(4, 0, 0, 0, 0));
        return null;
    }

    /**
     * Send a inventory changed message to the server if the current inventory has been modified.
     */
    public void sendInventoryChanged() {}

    /**
     * Joins the passed in entity item with the world. Args: entityItem
     */
    protected void joinEntityItemWithWorld(EntityItem par1EntityItem) {}

    /**
     * Sends a chat message from the player. Args: chatMessage
     */
    public void sendChatMessage(String par1Str)
    {
        this.sendQueue.addToSendQueue(new Packet3Chat(par1Str));
    }

    /**
     * Swings the item the player is holding.
     */
    public void swingItem()
    {
        super.swingItem();
        this.sendQueue.addToSendQueue(new Packet18Animation(this, 1));
    }

    public void respawnPlayer()
    {
        this.sendInventoryChanged();
        this.sendQueue.addToSendQueue(new Packet9Respawn(this.dimension, (byte)this.worldObj.difficultySetting, this.worldObj.getWorldInfo().getTerrainType(), this.worldObj.getWorldHeight(), 0));
    }

    /**
     * Deals damage to the entity. If its a EntityPlayer then will take damage from the armor first and then health
     * second with the reduced value. Args: damageAmount
     */
    protected void damageEntity(DamageSource par1DamageSource, int par2)
    {
        this.setEntityHealth(this.getEntityHealth() - par2);
    }

    /**
     * sets current screen to null (used on escape buttons of GUIs)
     */
    public void closeScreen()
    {
        this.sendQueue.addToSendQueue(new Packet101CloseWindow(this.craftingInventory.windowId));
        this.inventory.setItemStack((ItemStack)null);
        super.closeScreen();
    }

    /**
     * Updates health locally.
     */
    public void setHealth(int par1)
    {
        if (this.hasSetHealth)
        {
            super.setHealth(par1);
        }
        else
        {
            this.setEntityHealth(par1);
            this.hasSetHealth = true;
        }
    }

    /**
     * Adds a value to a statistic field.
     */
    public void addStat(StatBase par1StatBase, int par2)
    {
        if (par1StatBase != null)
        {
            if (par1StatBase.isIndependent)
            {
                super.addStat(par1StatBase, par2);
            }
        }
    }

    /**
     * Used by NetClientHandler.handleStatistic
     */
    public void incrementStat(StatBase par1StatBase, int par2)
    {
        if (par1StatBase != null)
        {
            if (!par1StatBase.isIndependent)
            {
                super.addStat(par1StatBase, par2);
            }
        }
    }
}
