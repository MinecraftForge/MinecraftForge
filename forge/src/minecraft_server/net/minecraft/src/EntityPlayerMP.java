package net.minecraft.src;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.forge.IGuiHandler;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.NetworkMod;
import net.minecraft.src.forge.packets.PacketOpenGUI;

public class EntityPlayerMP extends EntityPlayer implements ICrafting
{
    /** The NetServerHandler for this particular player. */
    public NetServerHandler playerNetServerHandler;

    /** Reference to the MinecraftServer object. */
    public MinecraftServer mcServer;

    /** The ItemInWorldManager belonging to this player */
    public ItemInWorldManager itemInWorldManager;

    /** player X position as seen by PlayerManager */
    public double managedPosX;

    /** player Z position as seen by PlayerManager */
    public double managedPosZ;

    /** LinkedList that holds loaded chunks */
    public List loadedChunks = new LinkedList();

    /**
     * set of ChunkCoordIntPair, represents blocks that the player will receive block updates from
     */
    public Set listeningChunks = new HashSet();

    /** amount of health the client was last set to */
    private int lastHealth = -99999999;
    private int field_35221_cc = -99999999;
    private boolean field_35222_cd = true;

    /** Amount of experience the client was last set to */
    private int lastExperience = -99999999;

    /** how many ticks of invulnerability(spawn protection) this player has */
    private int ticksOfInvuln = 60;

    /** The inventory of the player */
    private ItemStack[] playerInventory = new ItemStack[] {null, null, null, null, null};

    /** the currently in use window id */
    private int currentWindowId = 0;

    /**
     * set to true when player is moving quantity of items from one inventory to another(crafting) but item in either
     * slot is not changed
     */
    public boolean isChangingQuantityOnly;
    public int ping;

    /**
     * Set when a player beats the ender dragon, used to determine whether a Packet9Respawn is valid.
     */
    public boolean gameOver = false;

    public EntityPlayerMP(MinecraftServer par1MinecraftServer, World par2World, String par3Str, ItemInWorldManager par4ItemInWorldManager)
    {
        super(par2World);
        par4ItemInWorldManager.thisPlayer = this;
        this.itemInWorldManager = par4ItemInWorldManager;
        ChunkCoordinates var5 = par2World.getSpawnPoint();
        int var6 = var5.posX;
        int var7 = var5.posZ;
        int var8 = var5.posY;

        if (!par2World.worldProvider.hasNoSky)
        {
            var6 += this.rand.nextInt(20) - 10;
            var8 = par2World.getTopSolidOrLiquidBlock(var6, var7);
            var7 += this.rand.nextInt(20) - 10;
        }

        this.setLocationAndAngles((double)var6 + 0.5D, (double)var8, (double)var7 + 0.5D, 0.0F, 0.0F);
        this.mcServer = par1MinecraftServer;
        this.stepHeight = 0.0F;
        this.username = par3Str;
        this.yOffset = 0.0F;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("playerGameType"))
        {
            this.itemInWorldManager.toggleGameType(par1NBTTagCompound.getInteger("playerGameType"));
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("playerGameType", this.itemInWorldManager.getGameType());
    }

    /**
     * Sets the reference to the World object.
     */
    public void setWorld(World par1World)
    {
        super.setWorld(par1World);
    }

    /**
     * Removes i number of levels
     */
    public void removeExperience(int par1)
    {
        super.removeExperience(par1);
        this.lastExperience = -1;
    }

    public void func_20057_k()
    {
        this.craftingInventory.onCraftGuiOpened(this);
    }

    /**
     * returns the inventory of this entity (only used in EntityPlayerMP it seems)
     */
    public ItemStack[] getInventory()
    {
        return this.playerInventory;
    }

    /**
     * sets the players height back to normal after doing things like sleeping and dieing
     */
    protected void resetHeight()
    {
        this.yOffset = 0.0F;
    }

    public float getEyeHeight()
    {
        return 1.62F;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.itemInWorldManager.updateBlockRemoving();
        --this.ticksOfInvuln;
        this.craftingInventory.updateCraftingResults();

        for (int var1 = 0; var1 < 5; ++var1)
        {
            ItemStack var2 = this.getEquipmentInSlot(var1);

            if (var2 != this.playerInventory[var1])
            {
                this.mcServer.getEntityTracker(this.dimension).sendPacketToTrackedPlayers(this, new Packet5PlayerInventory(this.entityId, var1, var2));
                this.playerInventory[var1] = var2;
            }
        }
    }

    /**
     * 0: Tool in Hand; 1-4: Armor
     */
    public ItemStack getEquipmentInSlot(int par1)
    {
        return par1 == 0 ? this.inventory.getCurrentItem() : this.inventory.armorInventory[par1 - 1];
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource par1DamageSource)
    {
        this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat(par1DamageSource.getDeathMessage(this)));
        this.inventory.dropAllItems();
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        if (this.ticksOfInvuln > 0)
        {
            return false;
        }
        else
        {
            if (!this.mcServer.pvpOn && par1DamageSource instanceof EntityDamageSource)
            {
                Entity var3 = par1DamageSource.getEntity();

                if (var3 instanceof EntityPlayer)
                {
                    return false;
                }

                if (var3 instanceof EntityArrow)
                {
                    EntityArrow var4 = (EntityArrow)var3;

                    if (var4.shootingEntity instanceof EntityPlayer)
                    {
                        return false;
                    }
                }
            }

            return super.attackEntityFrom(par1DamageSource, par2);
        }
    }

    /**
     * returns if pvp is enabled or not
     */
    protected boolean isPVPEnabled()
    {
        return this.mcServer.pvpOn;
    }

    /**
     * Heal living entity (param: amount of half-hearts)
     */
    public void heal(int par1)
    {
        super.heal(par1);
    }

    /**
     * updates the player, also has a flag to tell the server if it should update chunks or not(wanted onUpdate(because
     * this calls super.onUpdate) but already one in this class)
     */
    public void onUpdateEntity(boolean par1)
    {
        super.onUpdate();

        for (int var2 = 0; var2 < this.inventory.getSizeInventory(); ++var2)
        {
            ItemStack var3 = this.inventory.getStackInSlot(var2);

            if (var3 != null && Item.itemsList[var3.itemID].func_28019_b() && this.playerNetServerHandler.getNumChunkDataPackets() <= 2)
            {
                Packet var4 = ((ItemMapBase)Item.itemsList[var3.itemID]).getUpdatePacket(var3, this.worldObj, this);

                if (var4 != null)
                {
                    this.playerNetServerHandler.sendPacket(var4);
                }
            }
        }

        if (par1 && !this.loadedChunks.isEmpty())
        {
            ChunkCoordIntPair var10 = (ChunkCoordIntPair)this.loadedChunks.get(0);
            double var12 = var10.func_48477_a(this);

            for (int var5 = 0; var5 < this.loadedChunks.size(); ++var5)
            {
                ChunkCoordIntPair var6 = (ChunkCoordIntPair)this.loadedChunks.get(var5);
                double var7 = var6.func_48477_a(this);

                if (var7 < var12)
                {
                    var10 = var6;
                    var12 = var7;
                }
            }

            if (var10 != null)
            {
                boolean var14 = false;

                if (this.playerNetServerHandler.getNumChunkDataPackets() < 4)
                {
                    var14 = true;
                }

                if (var14)
                {
                    WorldServer var15 = this.mcServer.getWorldManager(this.dimension);

                    if (var15.blockExists(var10.chunkXPos << 4, 0, var10.chunkZPos << 4))
                    {
                        Chunk var16 = var15.getChunkFromChunkCoords(var10.chunkXPos, var10.chunkZPos);

                        if (var16.isTerrainPopulated)
                        {
                            this.loadedChunks.remove(var10);
                            this.playerNetServerHandler.sendPacket(new Packet51MapChunk(var15.getChunkFromChunkCoords(var10.chunkXPos, var10.chunkZPos), true, 0));
                            List var8 = var15.getTileEntityList(var10.chunkXPos * 16, 0, var10.chunkZPos * 16, var10.chunkXPos * 16 + 16, 256, var10.chunkZPos * 16 + 16);

                            for (int var9 = 0; var9 < var8.size(); ++var9)
                            {
                                this.getTileEntityInfo((TileEntity)var8.get(var9));
                            }
                        }
                    }
                }
            }
        }

        if (this.inPortal)
        {
            if (this.mcServer.propertyManagerObj.getBooleanProperty("allow-nether", true))
            {
                if (this.craftingInventory != this.inventorySlots)
                {
                    this.closeScreen();
                }

                if (this.ridingEntity != null)
                {
                    this.mountEntity(this.ridingEntity);
                }
                else
                {
                    this.timeInPortal += 0.0125F;

                    if (this.timeInPortal >= 1.0F)
                    {
                        this.timeInPortal = 1.0F;
                        this.timeUntilPortal = 10;
                        boolean var11 = false;
                        byte var13;

                        if (this.dimension == -1)
                        {
                            var13 = 0;
                        }
                        else
                        {
                            var13 = -1;
                        }

                        this.mcServer.configManager.sendPlayerToOtherDimension(this, var13);
                        this.lastExperience = -1;
                        this.lastHealth = -1;
                        this.field_35221_cc = -1;
                        this.triggerAchievement(AchievementList.portal);
                    }
                }

                this.inPortal = false;
            }
        }
        else
        {
            if (this.timeInPortal > 0.0F)
            {
                this.timeInPortal -= 0.05F;
            }

            if (this.timeInPortal < 0.0F)
            {
                this.timeInPortal = 0.0F;
            }
        }

        if (this.timeUntilPortal > 0)
        {
            --this.timeUntilPortal;
        }

        if (this.getEntityHealth() != this.lastHealth || this.field_35221_cc != this.foodStats.getFoodLevel() || this.foodStats.getSaturationLevel() == 0.0F != this.field_35222_cd)
        {
            this.playerNetServerHandler.sendPacket(new Packet8UpdateHealth(this.getEntityHealth(), this.foodStats.getFoodLevel(), this.foodStats.getSaturationLevel()));
            this.lastHealth = this.getEntityHealth();
            this.field_35221_cc = this.foodStats.getFoodLevel();
            this.field_35222_cd = this.foodStats.getSaturationLevel() == 0.0F;
        }

        if (this.experienceTotal != this.lastExperience)
        {
            this.lastExperience = this.experienceTotal;
            this.playerNetServerHandler.sendPacket(new Packet43Experience(this.experience, this.experienceTotal, this.experienceLevel));
        }
    }

    public void travelToTheEnd(int par1)
    {
        if (this.dimension == 1 && par1 == 1)
        {
            this.triggerAchievement(AchievementList.theEnd2);
            this.worldObj.setEntityDead(this);
            this.gameOver = true;
            this.playerNetServerHandler.sendPacket(new Packet70Bed(4, 0));
        }
        else
        {
            this.triggerAchievement(AchievementList.theEnd);
            ChunkCoordinates var2 = this.mcServer.getWorldManager(par1).getEntrancePortalLocation();

            if (var2 != null)
            {
                this.playerNetServerHandler.teleportTo((double)var2.posX, (double)var2.posY, (double)var2.posZ, 0.0F, 0.0F);
            }

            this.mcServer.configManager.sendPlayerToOtherDimension(this, 1);
            this.lastExperience = -1;
            this.lastHealth = -1;
            this.field_35221_cc = -1;
        }
    }

    /**
     * gets description packets from all TileEntity's that override func_20070
     */
    private void getTileEntityInfo(TileEntity par1TileEntity)
    {
        if (par1TileEntity != null)
        {
            Packet var2 = par1TileEntity.getDescriptionPacket();

            if (var2 != null)
            {
                this.playerNetServerHandler.sendPacket(var2);
            }
        }
    }

    /**
     * Called whenever an item is picked up from walking over it. Args: pickedUpEntity, stackSize
     */
    public void onItemPickup(Entity par1Entity, int par2)
    {
        if (!par1Entity.isDead)
        {
            EntityTracker var3 = this.mcServer.getEntityTracker(this.dimension);

            if (par1Entity instanceof EntityItem)
            {
                var3.sendPacketToTrackedPlayers(par1Entity, new Packet22Collect(par1Entity.entityId, this.entityId));
            }

            if (par1Entity instanceof EntityArrow)
            {
                var3.sendPacketToTrackedPlayers(par1Entity, new Packet22Collect(par1Entity.entityId, this.entityId));
            }

            if (par1Entity instanceof EntityXPOrb)
            {
                var3.sendPacketToTrackedPlayers(par1Entity, new Packet22Collect(par1Entity.entityId, this.entityId));
            }
        }

        super.onItemPickup(par1Entity, par2);
        this.craftingInventory.updateCraftingResults();
    }

    /**
     * Swings the item the player is holding.
     */
    public void swingItem()
    {
        if (!this.isSwinging)
        {
            this.swingProgressInt = -1;
            this.isSwinging = true;
            EntityTracker var1 = this.mcServer.getEntityTracker(this.dimension);
            var1.sendPacketToTrackedPlayers(this, new Packet18Animation(this, 1));
        }
    }

    public void func_22068_s() {}

    /**
     * puts player to sleep on specified bed if possible
     */
    public EnumStatus sleepInBedAt(int par1, int par2, int par3)
    {
        EnumStatus var4 = super.sleepInBedAt(par1, par2, par3);

        if (var4 == EnumStatus.OK)
        {
            EntityTracker var5 = this.mcServer.getEntityTracker(this.dimension);
            Packet17Sleep var6 = new Packet17Sleep(this, 0, par1, par2, par3);
            var5.sendPacketToTrackedPlayers(this, var6);
            this.playerNetServerHandler.teleportTo(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.playerNetServerHandler.sendPacket(var6);
        }

        return var4;
    }

    public void wakeUpPlayer(boolean par1, boolean par2, boolean par3)
    {
        if (this.isPlayerSleeping())
        {
            EntityTracker var4 = this.mcServer.getEntityTracker(this.dimension);
            var4.sendPacketToTrackedPlayersAndTrackedEntity(this, new Packet18Animation(this, 3));
        }

        super.wakeUpPlayer(par1, par2, par3);

        if (this.playerNetServerHandler != null)
        {
            this.playerNetServerHandler.teleportTo(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        }
    }

    /**
     * set entity to null to unmount
     */
    public void mountEntity(Entity par1Entity)
    {
        super.mountEntity(par1Entity);
        this.playerNetServerHandler.sendPacket(new Packet39AttachEntity(this, this.ridingEntity));
        this.playerNetServerHandler.teleportTo(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on the ground to update the fall distance
     * and deal fall damage if landing on the ground.  Args: distanceFallenThisTick, onGround
     */
    protected void updateFallState(double par1, boolean par3) {}

    /**
     * process player falling based on movement packet
     */
    public void handleFalling(double par1, boolean par3)
    {
        super.updateFallState(par1, par3);
    }

    /**
     * get the next window id to use
     */
    private void getNextWidowId()
    {
        this.currentWindowId = this.currentWindowId % 100 + 1;
    }

    /**
     * Displays the crafting GUI for a workbench.
     */
    public void displayWorkbenchGUI(int par1, int par2, int par3)
    {
        this.getNextWidowId();
        closeCraftingGui(); //TODO: This is a bug fix for chests staying open in SMP
        this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 1, "Crafting", 9));
        this.craftingInventory = new ContainerWorkbench(this.inventory, this.worldObj, par1, par2, par3);
        this.craftingInventory.windowId = this.currentWindowId;
        this.craftingInventory.onCraftGuiOpened(this);
    }

    public void displayGUIEnchantment(int par1, int par2, int par3)
    {
        this.getNextWidowId();
        closeCraftingGui(); //TODO: This is a bug fix for chests staying open in SMP
        this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 4, "Enchanting", 9));
        this.craftingInventory = new ContainerEnchantment(this.inventory, this.worldObj, par1, par2, par3);
        this.craftingInventory.windowId = this.currentWindowId;
        this.craftingInventory.onCraftGuiOpened(this);
    }

    /**
     * Displays the GUI for interacting with a chest inventory. Args: chestInventory
     */
    public void displayGUIChest(IInventory par1IInventory)
    {
        this.getNextWidowId();
        closeCraftingGui(); //TODO: This is a bug fix for chests staying open in SMP
        this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 0, par1IInventory.getInvName(), par1IInventory.getSizeInventory()));
        this.craftingInventory = new ContainerChest(this.inventory, par1IInventory);
        this.craftingInventory.windowId = this.currentWindowId;
        this.craftingInventory.onCraftGuiOpened(this);
    }

    /**
     * Displays the furnace GUI for the passed in furnace entity. Args: tileEntityFurnace
     */
    public void displayGUIFurnace(TileEntityFurnace par1TileEntityFurnace)
    {
        this.getNextWidowId();
        closeCraftingGui(); //TODO: This is a bug fix for chests staying open in SMP
        this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 2, par1TileEntityFurnace.getInvName(), par1TileEntityFurnace.getSizeInventory()));
        this.craftingInventory = new ContainerFurnace(this.inventory, par1TileEntityFurnace);
        this.craftingInventory.windowId = this.currentWindowId;
        this.craftingInventory.onCraftGuiOpened(this);
    }

    /**
     * Displays the dipsenser GUI for the passed in dispenser entity. Args: TileEntityDispenser
     */
    public void displayGUIDispenser(TileEntityDispenser par1TileEntityDispenser)
    {
        this.getNextWidowId();
        closeCraftingGui(); //TODO: This is a bug fix for chests staying open in SMP
        this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 3, par1TileEntityDispenser.getInvName(), par1TileEntityDispenser.getSizeInventory()));
        this.craftingInventory = new ContainerDispenser(this.inventory, par1TileEntityDispenser);
        this.craftingInventory.windowId = this.currentWindowId;
        this.craftingInventory.onCraftGuiOpened(this);
    }

    public void displayGUIBrewingStand(TileEntityBrewingStand par1TileEntityBrewingStand)
    {
        this.getNextWidowId();
        closeCraftingGui(); //TODO: This is a bug fix for chests staying open in SMP
        this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 5, par1TileEntityBrewingStand.getInvName(), par1TileEntityBrewingStand.getSizeInventory()));
        this.craftingInventory = new ContainerBrewingStand(this.inventory, par1TileEntityBrewingStand);
        this.craftingInventory.windowId = this.currentWindowId;
        this.craftingInventory.onCraftGuiOpened(this);
    }

    /**
     * inform the player of a change in a single slot
     */
    public void updateCraftingInventorySlot(Container par1Container, int par2, ItemStack par3ItemStack)
    {
        if (!(par1Container.getSlot(par2) instanceof SlotCrafting))
        {
            if (!this.isChangingQuantityOnly)
            {
                this.playerNetServerHandler.sendPacket(new Packet103SetSlot(par1Container.windowId, par2, par3ItemStack));
            }
        }
    }

    public void func_28017_a(Container par1Container)
    {
        this.updateCraftingInventory(par1Container, par1Container.func_28127_b());
    }

    /**
     * update the crafting window inventory with the items in the list
     */
    public void updateCraftingInventory(Container par1Container, List par2List)
    {
        this.playerNetServerHandler.sendPacket(new Packet104WindowItems(par1Container.windowId, par2List));
        this.playerNetServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getItemStack()));
    }

    /**
     * send information about the crafting inventory to the client(currently only for furnace times)
     */
    public void updateCraftingInventoryInfo(Container par1Container, int par2, int par3)
    {
        this.playerNetServerHandler.sendPacket(new Packet105UpdateProgressbar(par1Container.windowId, par2, par3));
    }

    public void onItemStackChanged(ItemStack par1ItemStack) {}

    /**
     * set current crafting inventory back to the 2x2 square
     */
    public void closeScreen()
    {
        this.playerNetServerHandler.sendPacket(new Packet101CloseWindow(this.craftingInventory.windowId));
        this.closeCraftingGui();
    }

    /**
     * updates item held by mouse, This method always returns before doing anything...
     */
    public void updateHeldItem()
    {
        if (!this.isChangingQuantityOnly)
        {
            this.playerNetServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getItemStack()));
        }
    }

    /**
     * close the current crafting gui
     */
    public void closeCraftingGui()
    {
        this.craftingInventory.onCraftGuiClosed(this);
        this.craftingInventory = this.inventorySlots;
    }

    /**
     * Adds a value to a statistic field.
     */
    public void addStat(StatBase par1StatBase, int par2)
    {
        if (par1StatBase != null)
        {
            if (!par1StatBase.isIndependent)
            {
                while (par2 > 100)
                {
                    this.playerNetServerHandler.sendPacket(new Packet200Statistic(par1StatBase.statId, 100));
                    par2 -= 100;
                }

                this.playerNetServerHandler.sendPacket(new Packet200Statistic(par1StatBase.statId, par2));
            }
        }
    }

    public void func_30002_A()
    {
        if (this.ridingEntity != null)
        {
            this.mountEntity(this.ridingEntity);
        }

        if (this.riddenByEntity != null)
        {
            this.riddenByEntity.mountEntity(this);
        }

        if (this.sleeping)
        {
            this.wakeUpPlayer(true, false, false);
        }
    }

    public void func_30001_B()
    {
        this.lastHealth = -99999999;
    }

    public void addChatMessage(String par1Str)
    {
        StringTranslate var2 = StringTranslate.getInstance();
        String var3 = var2.translateKey(par1Str);
        this.playerNetServerHandler.sendPacket(new Packet3Chat(var3));
    }

    protected void func_35199_C()
    {
        this.playerNetServerHandler.sendPacket(new Packet38EntityStatus(this.entityId, (byte)9));
        super.func_35199_C();
    }

    /**
     * sets the itemInUse when the use item button is clicked. Args: itemstack, int maxItemUseDuration
     */
    public void setItemInUse(ItemStack par1ItemStack, int par2)
    {
        super.setItemInUse(par1ItemStack, par2);

        if (par1ItemStack != null && par1ItemStack.getItem() != null && par1ItemStack.getItem().getItemUseAction(par1ItemStack) == EnumAction.eat)
        {
            EntityTracker var3 = this.mcServer.getEntityTracker(this.dimension);
            var3.sendPacketToTrackedPlayersAndTrackedEntity(this, new Packet18Animation(this, 5));
        }
    }

    protected void onNewPotionEffect(PotionEffect par1PotionEffect)
    {
        super.onNewPotionEffect(par1PotionEffect);
        this.playerNetServerHandler.sendPacket(new Packet41EntityEffect(this.entityId, par1PotionEffect));
    }

    protected void onChangedPotionEffect(PotionEffect par1PotionEffect)
    {
        super.onChangedPotionEffect(par1PotionEffect);
        this.playerNetServerHandler.sendPacket(new Packet41EntityEffect(this.entityId, par1PotionEffect));
    }

    protected void onFinishedPotionEffect(PotionEffect par1PotionEffect)
    {
        super.onFinishedPotionEffect(par1PotionEffect);
        this.playerNetServerHandler.sendPacket(new Packet42RemoveEntityEffect(this.entityId, par1PotionEffect));
    }

    /**
     * Sets the position of the entity and updates the 'last' variables
     */
    public void setPositionAndUpdate(double par1, double par3, double par5)
    {
        this.playerNetServerHandler.teleportTo(par1, par3, par5, this.rotationYaw, this.rotationPitch);
    }

    /**
     * Called when the player performs a critical hit on the Entity. Args: entity that was hit critically
     */
    public void onCriticalHit(Entity par1Entity)
    {
        EntityTracker var2 = this.mcServer.getEntityTracker(this.dimension);
        var2.sendPacketToTrackedPlayersAndTrackedEntity(this, new Packet18Animation(par1Entity, 6));
    }

    public void onEnchantmentCritical(Entity par1Entity)
    {
        EntityTracker var2 = this.mcServer.getEntityTracker(this.dimension);
        var2.sendPacketToTrackedPlayersAndTrackedEntity(this, new Packet18Animation(par1Entity, 7));
    }
    
    /**
     * Opens a Gui for the player. 
     * 
     * @param mod The mod associated with the gui
     * @param ID The ID number for the Gui
     * @param world The World
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     */
    @Override
    public void openGui(BaseMod mod, int ID, World world, int x, int y, int z)
    {
        if (!(mod instanceof NetworkMod))
        {
            return;
        }
        IGuiHandler handler = MinecraftForge.getGuiHandler(mod);
        if (handler != null)
        {
            Container container = handler.getGuiContainer(ID, this, world, x, y, z);
            if (container != null)
            {
                getNextWidowId();
                closeCraftingGui();
                PacketOpenGUI pkt = new PacketOpenGUI(currentWindowId, MinecraftForge.getModID((NetworkMod)mod), ID, x, y, z);
                playerNetServerHandler.sendPacket(pkt.getPacket());
                craftingInventory = container; 
                craftingInventory.windowId = currentWindowId;
                craftingInventory.onCraftGuiOpened(this);
            }
        }
    }
}
