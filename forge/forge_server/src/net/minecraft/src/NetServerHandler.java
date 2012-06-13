package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.server.FMLServerHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.forge.ForgeHooks;
import net.minecraft.src.forge.ForgeHooksServer;
import net.minecraft.src.forge.MessageManager;
import java.io.UnsupportedEncodingException;

public class NetServerHandler extends NetHandler implements ICommandListener
{
    /** The logging system. */
    public static Logger logger = Logger.getLogger("Minecraft");

    /** The underlying network manager for this server handler. */
    public NetworkManager netManager;

    /** This is set to true whenever a player disconnects from the server */
    public boolean connectionClosed = false;

    /** Reference to the MinecraftServer object. */
    public MinecraftServer mcServer;

    /** Reference to the EntityPlayerMP object. */
    private EntityPlayerMP playerEntity;
    private int field_35009_f;

    /** holds the amount of tick the player is floating */
    private int playerInAirTime;
    private boolean field_22003_h;
    private int field_35013_i;
    private long field_35011_j;

    /** The Java Random object. */
    private static Random rndmObj = new Random();
    private long field_35010_l;
    private int field_45001_m = 0;
    private int field_48073_n = 0;

    /** last known x position for this connection */
    private double lastPosX;

    /** last known y position for this connection */
    private double lastPosY;

    /** last known z position for this connection */
    private double lastPosZ;

    /** is true when the player has moved since his last movement packet */
    private boolean hasMoved = true;
    private IntHashMap field_10_k = new IntHashMap();

    public NetServerHandler(MinecraftServer par1MinecraftServer, NetworkManager par2NetworkManager, EntityPlayerMP par3EntityPlayerMP)
    {
        this.mcServer = par1MinecraftServer;
        this.netManager = par2NetworkManager;
        par2NetworkManager.setNetHandler(this);
        this.playerEntity = par3EntityPlayerMP;
        par3EntityPlayerMP.playerNetServerHandler = this;
    }

    /**
     * handle all the packets for the connection
     */
    public void handlePackets()
    {
        this.field_22003_h = false;
        ++this.field_35009_f;
        this.netManager.processReadPackets();

        if ((long)this.field_35009_f - this.field_35010_l > 20L)
        {
            this.field_35010_l = (long)this.field_35009_f;
            this.field_35011_j = System.nanoTime() / 1000000L;
            this.field_35013_i = rndmObj.nextInt();
            this.sendPacket(new Packet0KeepAlive(this.field_35013_i));
        }

        if (this.field_45001_m > 0)
        {
            --this.field_45001_m;
        }

        if (this.field_48073_n > 0)
        {
            --this.field_48073_n;
        }
    }

    /**
     * Kick the offending player and give a reason why
     */
    public void kickPlayer(String par1Str)
    {
        if (!this.connectionClosed)
        {
            this.playerEntity.func_30002_A();
            this.sendPacket(new Packet255KickDisconnect(par1Str));
            this.netManager.serverShutdown();
            this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat("\u00a7e" + this.playerEntity.username + " left the game."));
            this.mcServer.configManager.playerLoggedOut(this.playerEntity);
            this.connectionClosed = true;
        }
    }

    public void handleFlying(Packet10Flying par1Packet10Flying)
    {
        WorldServer var2 = this.mcServer.getWorldManager(this.playerEntity.dimension);
        this.field_22003_h = true;

        if (!this.playerEntity.gameOver)
        {
            double var3;

            if (!this.hasMoved)
            {
                var3 = par1Packet10Flying.yPosition - this.lastPosY;

                if (par1Packet10Flying.xPosition == this.lastPosX && var3 * var3 < 0.01D && par1Packet10Flying.zPosition == this.lastPosZ)
                {
                    this.hasMoved = true;
                }
            }

            if (this.hasMoved)
            {
                double var5;
                double var7;
                double var9;
                double var13;

                if (this.playerEntity.ridingEntity != null)
                {
                    float var28 = this.playerEntity.rotationYaw;
                    float var4 = this.playerEntity.rotationPitch;
                    this.playerEntity.ridingEntity.updateRiderPosition();
                    var5 = this.playerEntity.posX;
                    var7 = this.playerEntity.posY;
                    var9 = this.playerEntity.posZ;
                    double var29 = 0.0D;
                    var13 = 0.0D;

                    if (par1Packet10Flying.rotating)
                    {
                        var28 = par1Packet10Flying.yaw;
                        var4 = par1Packet10Flying.pitch;
                    }

                    if (par1Packet10Flying.moving && par1Packet10Flying.yPosition == -999.0D && par1Packet10Flying.stance == -999.0D)
                    {
                        if (par1Packet10Flying.xPosition > 1.0D || par1Packet10Flying.zPosition > 1.0D)
                        {
                            System.err.println(this.playerEntity.username + " was caught trying to crash the server with an invalid position.");
                            this.kickPlayer("Nope!");
                            return;
                        }

                        var29 = par1Packet10Flying.xPosition;
                        var13 = par1Packet10Flying.zPosition;
                    }

                    this.playerEntity.onGround = par1Packet10Flying.onGround;
                    this.playerEntity.onUpdateEntity(true);
                    this.playerEntity.moveEntity(var29, 0.0D, var13);
                    this.playerEntity.setPositionAndRotation(var5, var7, var9, var28, var4);
                    this.playerEntity.motionX = var29;
                    this.playerEntity.motionZ = var13;

                    if (this.playerEntity.ridingEntity != null)
                    {
                        var2.func_12017_b(this.playerEntity.ridingEntity, true);
                    }

                    if (this.playerEntity.ridingEntity != null)
                    {
                        this.playerEntity.ridingEntity.updateRiderPosition();
                    }

                    this.mcServer.configManager.serverUpdateMountedMovingPlayer(this.playerEntity);
                    this.lastPosX = this.playerEntity.posX;
                    this.lastPosY = this.playerEntity.posY;
                    this.lastPosZ = this.playerEntity.posZ;
                    var2.updateEntity(this.playerEntity);
                    return;
                }

                if (this.playerEntity.isPlayerSleeping())
                {
                    this.playerEntity.onUpdateEntity(true);
                    this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                    var2.updateEntity(this.playerEntity);
                    return;
                }

                var3 = this.playerEntity.posY;
                this.lastPosX = this.playerEntity.posX;
                this.lastPosY = this.playerEntity.posY;
                this.lastPosZ = this.playerEntity.posZ;
                var5 = this.playerEntity.posX;
                var7 = this.playerEntity.posY;
                var9 = this.playerEntity.posZ;
                float var11 = this.playerEntity.rotationYaw;
                float var12 = this.playerEntity.rotationPitch;

                if (par1Packet10Flying.moving && par1Packet10Flying.yPosition == -999.0D && par1Packet10Flying.stance == -999.0D)
                {
                    par1Packet10Flying.moving = false;
                }

                if (par1Packet10Flying.moving)
                {
                    var5 = par1Packet10Flying.xPosition;
                    var7 = par1Packet10Flying.yPosition;
                    var9 = par1Packet10Flying.zPosition;
                    var13 = par1Packet10Flying.stance - par1Packet10Flying.yPosition;

                    if (!this.playerEntity.isPlayerSleeping() && (var13 > 1.65D || var13 < 0.1D))
                    {
                        this.kickPlayer("Illegal stance");
                        logger.warning(this.playerEntity.username + " had an illegal stance: " + var13);
                        return;
                    }

                    if (Math.abs(par1Packet10Flying.xPosition) > 3.2E7D || Math.abs(par1Packet10Flying.zPosition) > 3.2E7D)
                    {
                        this.kickPlayer("Illegal position");
                        return;
                    }
                }

                if (par1Packet10Flying.rotating)
                {
                    var11 = par1Packet10Flying.yaw;
                    var12 = par1Packet10Flying.pitch;
                }

                this.playerEntity.onUpdateEntity(true);
                this.playerEntity.ySize = 0.0F;
                this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, var11, var12);

                if (!this.hasMoved)
                {
                    return;
                }

                var13 = var5 - this.playerEntity.posX;
                double var15 = var7 - this.playerEntity.posY;
                double var17 = var9 - this.playerEntity.posZ;
                double var19 = var13 * var13 + var15 * var15 + var17 * var17;

                if (var19 > 100.0D)
                {
                    logger.warning(this.playerEntity.username + " moved too quickly!");
                    this.kickPlayer("You moved too quickly :( (Hacking?)");
                    return;
                }

                float var21 = 0.0625F;
                boolean var22 = var2.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().contract((double)var21, (double)var21, (double)var21)).size() == 0;

                if (this.playerEntity.onGround && !par1Packet10Flying.onGround && var15 > 0.0D)
                {
                    this.playerEntity.addExhaustion(0.2F);
                }

                this.playerEntity.moveEntity(var13, var15, var17);
                this.playerEntity.onGround = par1Packet10Flying.onGround;
                this.playerEntity.addMovementStat(var13, var15, var17);
                double var23 = var15;
                var13 = var5 - this.playerEntity.posX;
                var15 = var7 - this.playerEntity.posY;

                if (var15 > -0.5D || var15 < 0.5D)
                {
                    var15 = 0.0D;
                }

                var17 = var9 - this.playerEntity.posZ;
                var19 = var13 * var13 + var15 * var15 + var17 * var17;
                boolean var25 = false;

                if (var19 > 0.0625D && !this.playerEntity.isPlayerSleeping() && !this.playerEntity.itemInWorldManager.isCreative())
                {
                    var25 = true;
                    logger.warning(this.playerEntity.username + " moved wrongly!");
                    System.out.println("Got position " + var5 + ", " + var7 + ", " + var9);
                    System.out.println("Expected " + this.playerEntity.posX + ", " + this.playerEntity.posY + ", " + this.playerEntity.posZ);
                }

                this.playerEntity.setPositionAndRotation(var5, var7, var9, var11, var12);
                boolean var26 = var2.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().contract((double)var21, (double)var21, (double)var21)).size() == 0;

                if (var22 && (var25 || !var26) && !this.playerEntity.isPlayerSleeping())
                {
                    this.teleportTo(this.lastPosX, this.lastPosY, this.lastPosZ, var11, var12);
                    return;
                }

                AxisAlignedBB var27 = this.playerEntity.boundingBox.copy().expand((double)var21, (double)var21, (double)var21).addCoord(0.0D, -0.55D, 0.0D);

                if (!this.mcServer.allowFlight && !this.playerEntity.itemInWorldManager.isCreative() && !var2.isAABBEmpty(var27))
                {
                    if (var23 >= -0.03125D)
                    {
                        ++this.playerInAirTime;

                        if (this.playerInAirTime > 80)
                        {
                            logger.warning(this.playerEntity.username + " was kicked for floating too long!");
                            this.kickPlayer("Flying is not enabled on this server");
                            return;
                        }
                    }
                }
                else
                {
                    this.playerInAirTime = 0;
                }

                this.playerEntity.onGround = par1Packet10Flying.onGround;
                this.mcServer.configManager.serverUpdateMountedMovingPlayer(this.playerEntity);
                this.playerEntity.handleFalling(this.playerEntity.posY - var3, par1Packet10Flying.onGround);
            }
        }
    }

    /**
     * Teleports the player to the specified destination and rotation
     */
    public void teleportTo(double par1, double par3, double par5, float par7, float par8)
    {
        this.hasMoved = false;
        this.lastPosX = par1;
        this.lastPosY = par3;
        this.lastPosZ = par5;
        this.playerEntity.setPositionAndRotation(par1, par3, par5, par7, par8);
        this.playerEntity.playerNetServerHandler.sendPacket(new Packet13PlayerLookMove(par1, par3 + 1.6200000047683716D, par3, par5, par7, par8, false));
    }

    public void handleBlockDig(Packet14BlockDig par1Packet14BlockDig)
    {
        WorldServer var2 = this.mcServer.getWorldManager(this.playerEntity.dimension);

        if (par1Packet14BlockDig.status == 4)
        {
            this.playerEntity.dropOneItem();
        }
        else if (par1Packet14BlockDig.status == 5)
        {
            this.playerEntity.stopUsingItem();
        }
        else
        {
            boolean var3 = var2.disableSpawnProtection = var2.worldProvider.worldType != 0 || this.mcServer.configManager.isOp(this.playerEntity.username);
            boolean var4 = false;
            
            if (par1Packet14BlockDig.status == 0)
            {
                var4 = true;
            }

            if (par1Packet14BlockDig.status == 2)
            {
                var4 = true;
            }

            int var5 = par1Packet14BlockDig.xPosition;
            int var6 = par1Packet14BlockDig.yPosition;
            int var7 = par1Packet14BlockDig.zPosition;
            
            if (var4)
            {
                double var8 = this.playerEntity.posX - ((double)var5 + 0.5D);
                double var10 = this.playerEntity.posY - ((double)var6 + 0.5D) + 1.5D;
                double var12 = this.playerEntity.posZ - ((double)var7 + 0.5D);
                double var14 = var8 * var8 + var10 * var10 + var12 * var12;
                
                double dist = playerEntity.itemInWorldManager.getBlockReachDistance() + 1;
                dist *= dist;

                if (var14 > dist)
                {
                    return;
                }

                if (var6 >= this.mcServer.buildLimit)
                {
                    return;
                }
            }

            ChunkCoordinates var19 = var2.getSpawnPoint();
            int var9 = MathHelper.abs(var5 - var19.posX);
            int var20 = MathHelper.abs(var7 - var19.posZ);

            if (var9 > var20)
            {
                var20 = var9;
            }
            
            if (par1Packet14BlockDig.status == 0)
            {
            	if (!ForgeHooksServer.canMineBlock(this.playerEntity, var5, var6, var7))
            	{
            		this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(var5, var6, var7, var2));
            	}
            	else if (var20 <= mcServer.spawnProtectionSize && !var3)
                {
                    this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(var5, var6, var7, var2));
                }
                else
                {
                    this.playerEntity.itemInWorldManager.blockClicked(var5, var6, var7, par1Packet14BlockDig.face);
                }
            }
            else if (par1Packet14BlockDig.status == 2)
            {
                this.playerEntity.itemInWorldManager.blockRemoving(var5, var6, var7);

                if (var2.getBlockId(var5, var6, var7) != 0)
                {
                    this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(var5, var6, var7, var2));
                }
            }
            else if (par1Packet14BlockDig.status == 3)
            {
                double var11 = this.playerEntity.posX - ((double)var5 + 0.5D);
                double var13 = this.playerEntity.posY - ((double)var6 + 0.5D);
                double var15 = this.playerEntity.posZ - ((double)var7 + 0.5D);
                double var17 = var11 * var11 + var13 * var13 + var15 * var15;

                if (var17 < 256.0D)
                {
                    this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(var5, var6, var7, var2));
                }
            }

            var2.disableSpawnProtection = false;
        }
    }

    public void handlePlace(Packet15Place par1Packet15Place)
    {
        WorldServer var2 = this.mcServer.getWorldManager(this.playerEntity.dimension);
        ItemStack var3 = this.playerEntity.inventory.getCurrentItem();
        boolean var4 = false;
        int var5 = par1Packet15Place.xPosition;
        int var6 = par1Packet15Place.yPosition;
        int var7 = par1Packet15Place.zPosition;
        int var8 = par1Packet15Place.direction;
        boolean var9 = var2.disableSpawnProtection = var2.worldProvider.worldType != 0 || this.mcServer.configManager.isOp(this.playerEntity.username);
        if (!ForgeHooksServer.canPlaceBlock(this.playerEntity, var5, var6, var7)){var4 = true;} 
        if (par1Packet15Place.direction == 255)
        {
            if (var3 == null)
            {
                return;
            }

            this.playerEntity.itemInWorldManager.itemUsed(this.playerEntity, var2, var3);
        }
        else if (par1Packet15Place.yPosition >= this.mcServer.buildLimit - 1 && (par1Packet15Place.direction == 1 || par1Packet15Place.yPosition >= this.mcServer.buildLimit))
        {
            this.playerEntity.playerNetServerHandler.sendPacket(new Packet3Chat("\u00a77Height limit for building is " + this.mcServer.buildLimit));
            var4 = true;
        }
        else
        {
            ChunkCoordinates var10 = var2.getSpawnPoint();
            int var11 = MathHelper.abs(var5 - var10.posX);
            int var12 = MathHelper.abs(var7 - var10.posZ);

            if (var11 > var12)
            {
                var12 = var11;
            }
            double dist = playerEntity.itemInWorldManager.getBlockReachDistance() + 1;
            dist *= dist;
            if (this.hasMoved && this.playerEntity.getDistanceSq((double)var5 + 0.5D, (double)var6 + 0.5D, (double)var7 + 0.5D) < dist && (var12 > mcServer.spawnProtectionSize || var9))
            {
                this.playerEntity.itemInWorldManager.activeBlockOrUseItem(this.playerEntity, var2, var3, var5, var6, var7, var8);
            }

            var4 = true;
        }

        if (var4)
        {
            this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(var5, var6, var7, var2));

            if (var8 == 0)
            {
                --var6;
            }

            if (var8 == 1)
            {
                ++var6;
            }

            if (var8 == 2)
            {
                --var7;
            }

            if (var8 == 3)
            {
                ++var7;
            }

            if (var8 == 4)
            {
                --var5;
            }

            if (var8 == 5)
            {
                ++var5;
            }

            this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(var5, var6, var7, var2));
        }

        var3 = this.playerEntity.inventory.getCurrentItem();

        if (var3 != null && var3.stackSize == 0)
        {
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
            var3 = null;
        }

        if (var3 == null || var3.getMaxItemUseDuration() == 0)
        {
            this.playerEntity.isChangingQuantityOnly = true;
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack.copyItemStack(this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]);
            Slot var13 = this.playerEntity.craftingInventory.func_20127_a(this.playerEntity.inventory, this.playerEntity.inventory.currentItem);
            this.playerEntity.craftingInventory.updateCraftingResults();
            this.playerEntity.isChangingQuantityOnly = false;

            if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), par1Packet15Place.itemStack))
            {
                this.sendPacket(new Packet103SetSlot(this.playerEntity.craftingInventory.windowId, var13.slotNumber, this.playerEntity.inventory.getCurrentItem()));
            }
        }

        var2.disableSpawnProtection = false;
    }

    public void handleErrorMessage(String par1Str, Object[] par2ArrayOfObj)
    {
        logger.info(this.playerEntity.username + " lost connection: " + par1Str);
        this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat("\u00a7e" + this.playerEntity.username + " left the game."));
        this.mcServer.configManager.playerLoggedOut(this.playerEntity);
        this.connectionClosed = true;
    }

    public void registerPacket(Packet par1Packet)
    {
        logger.warning(this.getClass() + " wasn\'t prepared to deal with a " + par1Packet.getClass());
        this.kickPlayer("Protocol error, unexpected packet");
    }

    /**
     * Adds the packet to the underlying network manager's send queue.
     */
    public void sendPacket(Packet par1Packet)
    {
        this.netManager.addToSendQueue(par1Packet);
    }

    public void handleBlockItemSwitch(Packet16BlockItemSwitch par1Packet16BlockItemSwitch)
    {
        if (par1Packet16BlockItemSwitch.id >= 0 && par1Packet16BlockItemSwitch.id < InventoryPlayer.func_25054_e())
        {
            this.playerEntity.inventory.currentItem = par1Packet16BlockItemSwitch.id;
        }
        else
        {
            logger.warning(this.playerEntity.username + " tried to set an invalid carried item");
        }
    }

    public void handleChat(Packet3Chat par1Packet3Chat)
    {
        String var2 = par1Packet3Chat.message;

        if (var2.length() > 100)
        {
            this.kickPlayer("Chat message too long");
        }
        else
        {
            var2 = var2.trim();

            for (int var3 = 0; var3 < var2.length(); ++var3)
            {
                if (!ChatAllowedCharacters.isAllowedCharacter(var2.charAt(var3)))
                {
                    this.kickPlayer("Illegal characters in chat");
                    return;
                }
            }

            if (FMLServerHandler.instance().handleChatPacket(par1Packet3Chat,playerEntity)) {
              // We handled it
            }
            else if (var2.startsWith("/"))
            {
                this.handleSlashCommand(var2);
            }
            else
            {
                var2 = ForgeHooks.onServerChat(playerEntity, var2);
                if (var2 != null)
                {
                    var2 = "<" + this.playerEntity.username + "> " + var2;
                    logger.info(var2);
                    this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat(var2));
                }
            }

            this.field_45001_m += 20;

            if (this.field_45001_m > 200)
            {
                this.kickPlayer("disconnect.spam");
            }
        }
    }

    /**
     * Processes a / command
     */
    private void handleSlashCommand(String par1Str)
    {
        if (par1Str.toLowerCase().startsWith("/me "))
        {
            par1Str = "* " + this.playerEntity.username + " " + par1Str.substring(par1Str.indexOf(" ")).trim();
            logger.info(par1Str);
            this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat(par1Str));
        }
        else if (par1Str.toLowerCase().startsWith("/kill"))
        {
            this.playerEntity.attackEntityFrom(DamageSource.generic, 1000);
        }
        else if (par1Str.toLowerCase().startsWith("/tell "))
        {
            String[] var2 = par1Str.split(" ");

            if (var2.length >= 3)
            {
                par1Str = par1Str.substring(par1Str.indexOf(" ")).trim();
                par1Str = par1Str.substring(par1Str.indexOf(" ")).trim();
                par1Str = "\u00a77" + this.playerEntity.username + " whispers " + par1Str;
                logger.info(par1Str + " to " + var2[1]);

                if (!this.mcServer.configManager.sendPacketToPlayer(var2[1], new Packet3Chat(par1Str)))
                {
                    this.sendPacket(new Packet3Chat("\u00a7cThere\'s no player by that name online."));
                }
            }
        }
        else if (ForgeHooks.onChatCommand(this.playerEntity, this.mcServer.configManager.isOp(playerEntity.username), par1Str.substring(1)))
        {
            logger.info("Forge: " + playerEntity.username + " issues command: " + par1Str.substring(1));
        }
        else
        {
            String var3;

            if (this.mcServer.configManager.isOp(this.playerEntity.username))
            {
                var3 = par1Str.substring(1);
                logger.info(this.playerEntity.username + " issued server command: " + var3);
                this.mcServer.addCommand(var3, this);
            }
            else
            {
                var3 = par1Str.substring(1);
                logger.info(this.playerEntity.username + " tried command: " + var3);
            }
        }
    }

    public void handleAnimation(Packet18Animation par1Packet18Animation)
    {
        if (par1Packet18Animation.animate == 1)
        {
            this.playerEntity.swingItem();
        }
    }

    /**
     * runs registerPacket on the given Packet19EntityAction
     */
    public void handleEntityAction(Packet19EntityAction par1Packet19EntityAction)
    {
        if (par1Packet19EntityAction.state == 1)
        {
            this.playerEntity.setSneaking(true);
        }
        else if (par1Packet19EntityAction.state == 2)
        {
            this.playerEntity.setSneaking(false);
        }
        else if (par1Packet19EntityAction.state == 4)
        {
            this.playerEntity.setSprinting(true);
        }
        else if (par1Packet19EntityAction.state == 5)
        {
            this.playerEntity.setSprinting(false);
        }
        else if (par1Packet19EntityAction.state == 3)
        {
            this.playerEntity.wakeUpPlayer(false, true, true);
            this.hasMoved = false;
        }
    }

    public void handleKickDisconnect(Packet255KickDisconnect par1Packet255KickDisconnect)
    {
        this.netManager.networkShutdown("disconnect.quitting", new Object[0]);
    }

    /**
     * return the number of chuckDataPackets from the netManager
     */
    public int getNumChunkDataPackets()
    {
        return this.netManager.getNumChunkDataPackets();
    }

    /**
     * Logs the message with a level of INFO.
     */
    public void log(String par1Str)
    {
        this.sendPacket(new Packet3Chat("\u00a77" + par1Str));
    }

    /**
     * Gets the players username.
     */
    public String getUsername()
    {
        return this.playerEntity.username;
    }

    public void handleUseEntity(Packet7UseEntity par1Packet7UseEntity)
    {
        WorldServer var2 = this.mcServer.getWorldManager(this.playerEntity.dimension);
        Entity var3 = var2.getEntityByID(par1Packet7UseEntity.targetEntity);

        if (var3 != null)
        {
            boolean var4 = this.playerEntity.canEntityBeSeen(var3);
            double var5 = 36.0D;

            if (!var4)
            {
                var5 = 9.0D;
            }

            if (this.playerEntity.getDistanceSqToEntity(var3) < var5)
            {
                if (par1Packet7UseEntity.isLeftClick == 0)
                {
                    this.playerEntity.useCurrentItemOnEntity(var3);
                }
                else if (par1Packet7UseEntity.isLeftClick == 1)
                {
                    this.playerEntity.attackTargetEntityWithCurrentItem(var3);
                }
            }
        }
    }

    /**
     * respawns the player
     */
    public void handleRespawn(Packet9Respawn par1Packet9Respawn)
    {
        int dim = (this.mcServer.getWorldManager(this.playerEntity.dimension).worldProvider.canRespawnHere() ? this.playerEntity.dimension : 0);        
        if (this.playerEntity.gameOver)
        {
            this.playerEntity = this.mcServer.configManager.recreatePlayerEntity(this.playerEntity, dim, true);
        }
        else
        {
            if (this.playerEntity.getHealth() > 0)
            {
                return;
            }

            this.playerEntity = this.mcServer.configManager.recreatePlayerEntity(this.playerEntity, dim, false);
        }
    }

    public void handleCloseWindow(Packet101CloseWindow par1Packet101CloseWindow)
    {
        this.playerEntity.closeCraftingGui();
    }

    public void handleWindowClick(Packet102WindowClick par1Packet102WindowClick)
    {
        if (this.playerEntity.craftingInventory.windowId == par1Packet102WindowClick.window_Id && this.playerEntity.craftingInventory.getCanCraft(this.playerEntity))
        {
            ItemStack var2 = this.playerEntity.craftingInventory.slotClick(par1Packet102WindowClick.inventorySlot, par1Packet102WindowClick.mouseClick, par1Packet102WindowClick.holdingShift, this.playerEntity);

            if (ItemStack.areItemStacksEqual(par1Packet102WindowClick.itemStack, var2))
            {
                this.playerEntity.playerNetServerHandler.sendPacket(new Packet106Transaction(par1Packet102WindowClick.window_Id, par1Packet102WindowClick.action, true));
                this.playerEntity.isChangingQuantityOnly = true;
                this.playerEntity.craftingInventory.updateCraftingResults();
                this.playerEntity.updateHeldItem();
                this.playerEntity.isChangingQuantityOnly = false;
            }
            else
            {
                this.field_10_k.addKey(this.playerEntity.craftingInventory.windowId, Short.valueOf(par1Packet102WindowClick.action));
                this.playerEntity.playerNetServerHandler.sendPacket(new Packet106Transaction(par1Packet102WindowClick.window_Id, par1Packet102WindowClick.action, false));
                this.playerEntity.craftingInventory.setCanCraft(this.playerEntity, false);
                ArrayList var3 = new ArrayList();

                for (int var4 = 0; var4 < this.playerEntity.craftingInventory.inventorySlots.size(); ++var4)
                {
                    var3.add(((Slot)this.playerEntity.craftingInventory.inventorySlots.get(var4)).getStack());
                }

                this.playerEntity.updateCraftingInventory(this.playerEntity.craftingInventory, var3);
            }
        }
    }

    public void handleEnchantItem(Packet108EnchantItem par1Packet108EnchantItem)
    {
        if (this.playerEntity.craftingInventory.windowId == par1Packet108EnchantItem.windowId && this.playerEntity.craftingInventory.getCanCraft(this.playerEntity))
        {
            this.playerEntity.craftingInventory.enchantItem(this.playerEntity, par1Packet108EnchantItem.enchantment);
            this.playerEntity.craftingInventory.updateCraftingResults();
        }
    }

    /**
     * Handle a creative slot packet.
     */
    public void handleCreativeSetSlot(Packet107CreativeSetSlot par1Packet107CreativeSetSlot)
    {
        if (this.playerEntity.itemInWorldManager.isCreative())
        {
            boolean var2 = par1Packet107CreativeSetSlot.slot < 0;
            ItemStack var3 = par1Packet107CreativeSetSlot.itemStack;
            boolean var4 = par1Packet107CreativeSetSlot.slot >= 36 && par1Packet107CreativeSetSlot.slot < 36 + InventoryPlayer.func_25054_e();
            boolean var5 = var3 == null || var3.itemID < Item.itemsList.length && var3.itemID >= 0 && Item.itemsList[var3.itemID] != null;
            boolean var6 = var3 == null || var3.getItemDamage() >= 0 && var3.getItemDamage() >= 0 && var3.stackSize <= 64 && var3.stackSize > 0;

            if (var4 && var5 && var6)
            {
                if (var3 == null)
                {
                    this.playerEntity.inventorySlots.putStackInSlot(par1Packet107CreativeSetSlot.slot, (ItemStack)null);
                }
                else
                {
                    this.playerEntity.inventorySlots.putStackInSlot(par1Packet107CreativeSetSlot.slot, var3);
                }

                this.playerEntity.inventorySlots.setCanCraft(this.playerEntity, true);
            }
            else if (var2 && var5 && var6 && this.field_48073_n < 200)
            {
                this.field_48073_n += 20;
                EntityItem var7 = this.playerEntity.dropPlayerItem(var3);

                if (var7 != null)
                {
                    var7.func_48316_k();
                }
            }
        }
    }

    public void handleTransaction(Packet106Transaction par1Packet106Transaction)
    {
        Short var2 = (Short)this.field_10_k.lookup(this.playerEntity.craftingInventory.windowId);

        if (var2 != null && par1Packet106Transaction.shortWindowId == var2.shortValue() && this.playerEntity.craftingInventory.windowId == par1Packet106Transaction.windowId && !this.playerEntity.craftingInventory.getCanCraft(this.playerEntity))
        {
            this.playerEntity.craftingInventory.setCanCraft(this.playerEntity, true);
        }
    }

    /**
     * Updates Client side signs
     */
    public void handleUpdateSign(Packet130UpdateSign par1Packet130UpdateSign)
    {
        WorldServer var2 = this.mcServer.getWorldManager(this.playerEntity.dimension);

        if (var2.blockExists(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition))
        {
            TileEntity var3 = var2.getBlockTileEntity(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition);

            if (var3 instanceof TileEntitySign)
            {
                TileEntitySign var4 = (TileEntitySign)var3;

                if (!var4.isEditable())
                {
                    this.mcServer.logWarning("Player " + this.playerEntity.username + " just tried to change non-editable sign");
                    return;
                }
            }

            int var6;
            int var9;

            for (var9 = 0; var9 < 4; ++var9)
            {
                boolean var5 = true;

                if (par1Packet130UpdateSign.signLines[var9].length() > 15)
                {
                    var5 = false;
                }
                else
                {
                    for (var6 = 0; var6 < par1Packet130UpdateSign.signLines[var9].length(); ++var6)
                    {
                        if (ChatAllowedCharacters.allowedCharacters.indexOf(par1Packet130UpdateSign.signLines[var9].charAt(var6)) < 0)
                        {
                            var5 = false;
                        }
                    }
                }

                if (!var5)
                {
                    par1Packet130UpdateSign.signLines[var9] = "!?";
                }
            }

            if (var3 instanceof TileEntitySign)
            {
                var9 = par1Packet130UpdateSign.xPosition;
                int var10 = par1Packet130UpdateSign.yPosition;
                var6 = par1Packet130UpdateSign.zPosition;
                TileEntitySign var7 = (TileEntitySign)var3;

                for (int var8 = 0; var8 < 4; ++var8)
                {
                    var7.signText[var8] = par1Packet130UpdateSign.signLines[var8];
                }

                var7.onInventoryChanged();
                var2.markBlockNeedsUpdate(var9, var10, var6);
            }
        }
    }

    /**
     * Handle a keep alive packet.
     */
    public void handleKeepAlive(Packet0KeepAlive par1Packet0KeepAlive)
    {
        if (par1Packet0KeepAlive.randomId == this.field_35013_i)
        {
            int var2 = (int)(System.nanoTime() / 1000000L - this.field_35011_j);
            this.playerEntity.ping = (this.playerEntity.ping * 3 + var2) / 4;
        }
    }

    /**
     * determine if it is a server handler
     */
    public boolean isServerHandler()
    {
        return true;
    }

    /**
     * Handle a player abilities packet.
     */
    public void handlePlayerAbilities(Packet202PlayerAbilities par1Packet202PlayerAbilities)
    {
        this.playerEntity.capabilities.isFlying = par1Packet202PlayerAbilities.isFlying && this.playerEntity.capabilities.allowFlying;
    }
    
    public EntityPlayerMP getPlayerEntity()
    {
        return playerEntity;
    }
    
    @Override
    public void handleCustomPayload(Packet250CustomPayload pkt)
    {
        FMLServerHandler.instance().handlePacket250(pkt, playerEntity);
        MessageManager inst = MessageManager.getInstance();
        if (pkt.channel.equals("REGISTER")) 
        {
            try 
            {
                String channels = new String(pkt.data, "UTF8");
                for (String channel : channels.split("\0")) 
                {
                    inst.addActiveChannel(netManager, channel);
                }
            } 
            catch (UnsupportedEncodingException ex) 
            {
                ModLoader.throwException("NetServerHandler.handleCustomPayload", ex);
            }
        } 
        else if (pkt.channel.equals("UNREGISTER")) 
        {
            try 
            {
                String channels = new String(pkt.data, "UTF8");
                for (String channel : channels.split("\0")) 
                {
                    inst.removeActiveChannel(netManager, channel);
                }
            }
            catch (UnsupportedEncodingException ex) 
            {
                ModLoader.throwException("NetServerHandler.handleCustomPayload", ex);
            }
        } 
        else 
        {
            inst.dispatchIncomingMessage(netManager, pkt.channel, pkt.data);
        }
    }
    
    @Override
    public void handleMapData(Packet131MapData par1Packet131MapData)
    {
        ForgeHooks.onItemDataPacket(netManager, par1Packet131MapData);
    }

    @Override
    public void handleTileEntityData(Packet132TileEntityData pkt)
    {
        World world = this.getPlayerEntity().worldObj;
        if (world.blockExists(pkt.xPosition, pkt.yPosition, pkt.zPosition))
        {
            TileEntity te = world.getBlockTileEntity(pkt.xPosition, pkt.yPosition, pkt.zPosition);
            if (te != null)
            {
                te.onDataPacket(netManager,  pkt);
            }
            else 
            {
                ModLoader.getLogger().log(Level.WARNING, String.format(
                        "Received a TileEntityData packet for a location that did not have a TileEntity: (%d, %d, %d) %d: %d, %d, %d", 
                        pkt.xPosition, pkt.yPosition, pkt.zPosition,
                        pkt.actionType, 
                        pkt.customParam1, pkt.customParam2, pkt.customParam3));
            }
        }
    }
}
