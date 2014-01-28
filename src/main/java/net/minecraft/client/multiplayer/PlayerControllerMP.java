package net.minecraft.client.multiplayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

@SideOnly(Side.CLIENT)
public class PlayerControllerMP
{
    // JAVADOC FIELD $$ field_78776_a
    private final Minecraft mc;
    private final NetHandlerPlayClient netClientHandler;
    // JAVADOC FIELD $$ field_78775_c
    private int currentBlockX = -1;
    // JAVADOC FIELD $$ field_78772_d
    private int currentBlockY = -1;
    // JAVADOC FIELD $$ field_78773_e
    private int currentblockZ = -1;
    private ItemStack field_85183_f;
    // JAVADOC FIELD $$ field_78770_f
    private float curBlockDamageMP;
    // JAVADOC FIELD $$ field_78780_h
    private float stepSoundTickCounter;
    // JAVADOC FIELD $$ field_78781_i
    private int blockHitDelay;
    // JAVADOC FIELD $$ field_78778_j
    private boolean isHittingBlock;
    // JAVADOC FIELD $$ field_78779_k
    private WorldSettings.GameType currentGameType;
    // JAVADOC FIELD $$ field_78777_l
    private int currentPlayerItem;
    private static final String __OBFID = "CL_00000881";

    public PlayerControllerMP(Minecraft p_i45062_1_, NetHandlerPlayClient p_i45062_2_)
    {
        this.currentGameType = WorldSettings.GameType.SURVIVAL;
        this.mc = p_i45062_1_;
        this.netClientHandler = p_i45062_2_;
    }

    // JAVADOC METHOD $$ func_78744_a
    public static void clickBlockCreative(Minecraft par0Minecraft, PlayerControllerMP par1PlayerControllerMP, int par2, int par3, int par4, int par5)
    {
        if (!par0Minecraft.theWorld.extinguishFire(par0Minecraft.thePlayer, par2, par3, par4, par5))
        {
            par1PlayerControllerMP.onPlayerDestroyBlock(par2, par3, par4, par5);
        }
    }

    // JAVADOC METHOD $$ func_78748_a
    public void setPlayerCapabilities(EntityPlayer par1EntityPlayer)
    {
        this.currentGameType.configurePlayerCapabilities(par1EntityPlayer.capabilities);
    }

    // JAVADOC METHOD $$ func_78747_a
    public boolean enableEverythingIsScrewedUpMode()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_78746_a
    public void setGameType(WorldSettings.GameType par1EnumGameType)
    {
        this.currentGameType = par1EnumGameType;
        this.currentGameType.configurePlayerCapabilities(this.mc.thePlayer.capabilities);
    }

    // JAVADOC METHOD $$ func_78745_b
    public void flipPlayer(EntityPlayer par1EntityPlayer)
    {
        par1EntityPlayer.rotationYaw = -180.0F;
    }

    public boolean shouldDrawHUD()
    {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    // JAVADOC METHOD $$ func_78751_a
    public boolean onPlayerDestroyBlock(int par1, int par2, int par3, int par4)
    {
        ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
        if (stack != null && stack.getItem() != null && stack.getItem().onBlockStartBreak(stack, par1, par2, par3, mc.thePlayer))
        {
            return false;
        }

        if (this.currentGameType.isAdventure() && !this.mc.thePlayer.isCurrentToolAdventureModeExempt(par1, par2, par3))
        {
            return false;
        }
        else if (this.currentGameType.isCreative() && this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)
        {
            return false;
        }
        else
        {
            WorldClient worldclient = this.mc.theWorld;
            Block block = worldclient.func_147439_a(par1, par2, par3);

            if (block.func_149688_o() == Material.field_151579_a)
            {
                return false;
            }
            else
            {
                worldclient.playAuxSFX(2001, par1, par2, par3, Block.func_149682_b(block) + (worldclient.getBlockMetadata(par1, par2, par3) << 12));
                int i1 = worldclient.getBlockMetadata(par1, par2, par3);
                boolean flag = block.removedByPlayer(worldclient, mc.thePlayer, par1, par2, par3);

                if (flag)
                {
                    block.func_149664_b(worldclient, par1, par2, par3, i1);
                }

                this.currentBlockY = -1;

                if (!this.currentGameType.isCreative())
                {
                    ItemStack itemstack = this.mc.thePlayer.getCurrentEquippedItem();

                    if (itemstack != null)
                    {
                        itemstack.func_150999_a(worldclient, block, par1, par2, par3, this.mc.thePlayer);

                        if (itemstack.stackSize == 0)
                        {
                            this.mc.thePlayer.destroyCurrentEquippedItem();
                        }
                    }
                }

                return flag;
            }
        }
    }

    // JAVADOC METHOD $$ func_78743_b
    public void clickBlock(int par1, int par2, int par3, int par4)
    {
        if (!this.currentGameType.isAdventure() || this.mc.thePlayer.isCurrentToolAdventureModeExempt(par1, par2, par3))
        {
            if (this.currentGameType.isCreative())
            {
                this.netClientHandler.func_147297_a(new C07PacketPlayerDigging(0, par1, par2, par3, par4));
                clickBlockCreative(this.mc, this, par1, par2, par3, par4);
                this.blockHitDelay = 5;
            }
            else if (!this.isHittingBlock || !this.sameToolAndBlock(par1, par2, par3))
            {
                if (this.isHittingBlock)
                {
                    this.netClientHandler.func_147297_a(new C07PacketPlayerDigging(1, this.currentBlockX, this.currentBlockY, this.currentblockZ, par4));
                }

                this.netClientHandler.func_147297_a(new C07PacketPlayerDigging(0, par1, par2, par3, par4));
                Block block = this.mc.theWorld.func_147439_a(par1, par2, par3);
                boolean flag = block.func_149688_o() != Material.field_151579_a;

                if (flag && this.curBlockDamageMP == 0.0F)
                {
                    block.func_149699_a(this.mc.theWorld, par1, par2, par3, this.mc.thePlayer);
                }

                if (flag && block.func_149737_a(this.mc.thePlayer, this.mc.thePlayer.worldObj, par1, par2, par3) >= 1.0F)
                {
                    this.onPlayerDestroyBlock(par1, par2, par3, par4);
                }
                else
                {
                    this.isHittingBlock = true;
                    this.currentBlockX = par1;
                    this.currentBlockY = par2;
                    this.currentblockZ = par3;
                    this.field_85183_f = this.mc.thePlayer.getHeldItem();
                    this.curBlockDamageMP = 0.0F;
                    this.stepSoundTickCounter = 0.0F;
                    this.mc.theWorld.func_147443_d(this.mc.thePlayer.func_145782_y(), this.currentBlockX, this.currentBlockY, this.currentblockZ, (int)(this.curBlockDamageMP * 10.0F) - 1);
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_78767_c
    public void resetBlockRemoving()
    {
        if (this.isHittingBlock)
        {
            this.netClientHandler.func_147297_a(new C07PacketPlayerDigging(1, this.currentBlockX, this.currentBlockY, this.currentblockZ, -1));
        }

        this.isHittingBlock = false;
        this.curBlockDamageMP = 0.0F;
        this.mc.theWorld.func_147443_d(this.mc.thePlayer.func_145782_y(), this.currentBlockX, this.currentBlockY, this.currentblockZ, -1);
    }

    // JAVADOC METHOD $$ func_78759_c
    public void onPlayerDamageBlock(int par1, int par2, int par3, int par4)
    {
        this.syncCurrentPlayItem();

        if (this.blockHitDelay > 0)
        {
            --this.blockHitDelay;
        }
        else if (this.currentGameType.isCreative())
        {
            this.blockHitDelay = 5;
            this.netClientHandler.func_147297_a(new C07PacketPlayerDigging(0, par1, par2, par3, par4));
            clickBlockCreative(this.mc, this, par1, par2, par3, par4);
        }
        else
        {
            if (this.sameToolAndBlock(par1, par2, par3))
            {
                Block block = this.mc.theWorld.func_147439_a(par1, par2, par3);

                if (block.func_149688_o() == Material.field_151579_a)
                {
                    this.isHittingBlock = false;
                    return;
                }

                this.curBlockDamageMP += block.func_149737_a(this.mc.thePlayer, this.mc.thePlayer.worldObj, par1, par2, par3);

                if (this.stepSoundTickCounter % 4.0F == 0.0F)
                {
                    this.mc.func_147118_V().func_147682_a(new PositionedSoundRecord(new ResourceLocation(block.field_149762_H.func_150498_e()), (block.field_149762_H.func_150497_c() + 1.0F) / 8.0F, block.field_149762_H.func_150494_d() * 0.5F, (float)par1 + 0.5F, (float)par2 + 0.5F, (float)par3 + 0.5F));
                }

                ++this.stepSoundTickCounter;

                if (this.curBlockDamageMP >= 1.0F)
                {
                    this.isHittingBlock = false;
                    this.netClientHandler.func_147297_a(new C07PacketPlayerDigging(2, par1, par2, par3, par4));
                    this.onPlayerDestroyBlock(par1, par2, par3, par4);
                    this.curBlockDamageMP = 0.0F;
                    this.stepSoundTickCounter = 0.0F;
                    this.blockHitDelay = 5;
                }

                this.mc.theWorld.func_147443_d(this.mc.thePlayer.func_145782_y(), this.currentBlockX, this.currentBlockY, this.currentblockZ, (int)(this.curBlockDamageMP * 10.0F) - 1);
            }
            else
            {
                this.clickBlock(par1, par2, par3, par4);
            }
        }
    }

    // JAVADOC METHOD $$ func_78757_d
    public float getBlockReachDistance()
    {
        return this.currentGameType.isCreative() ? 5.0F : 4.5F;
    }

    public void updateController()
    {
        this.syncCurrentPlayItem();

        if (this.netClientHandler.func_147298_b().func_150724_d())
        {
            this.netClientHandler.func_147298_b().processReadPackets();
        }
        else if (this.netClientHandler.func_147298_b().func_150730_f() != null)
        {
            this.netClientHandler.func_147298_b().func_150729_e().func_147231_a(this.netClientHandler.func_147298_b().func_150730_f());
        }
        else
        {
            this.netClientHandler.func_147298_b().func_150729_e().func_147231_a(new ChatComponentText("Disconnected from server"));
        }
    }

    private boolean sameToolAndBlock(int par1, int par2, int par3)
    {
        ItemStack itemstack = this.mc.thePlayer.getHeldItem();
        boolean flag = this.field_85183_f == null && itemstack == null;

        if (this.field_85183_f != null && itemstack != null)
        {
            flag = itemstack.getItem() == this.field_85183_f.getItem() && ItemStack.areItemStackTagsEqual(itemstack, this.field_85183_f) && (itemstack.isItemStackDamageable() || itemstack.getItemDamage() == this.field_85183_f.getItemDamage());
        }

        return par1 == this.currentBlockX && par2 == this.currentBlockY && par3 == this.currentblockZ && flag;
    }

    // JAVADOC METHOD $$ func_78750_j
    private void syncCurrentPlayItem()
    {
        int i = this.mc.thePlayer.inventory.currentItem;

        if (i != this.currentPlayerItem)
        {
            this.currentPlayerItem = i;
            this.netClientHandler.func_147297_a(new C09PacketHeldItemChange(this.currentPlayerItem));
        }
    }

    // JAVADOC METHOD $$ func_78760_a
    public boolean onPlayerRightClick(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack, int par4, int par5, int par6, int par7, Vec3 par8Vec3)
    {
        this.syncCurrentPlayItem();
        float f = (float)par8Vec3.xCoord - (float)par4;
        float f1 = (float)par8Vec3.yCoord - (float)par5;
        float f2 = (float)par8Vec3.zCoord - (float)par6;
        boolean flag = false;

        if (par3ItemStack != null &&
            par3ItemStack.getItem() != null &&
            par3ItemStack.getItem().onItemUseFirst(par3ItemStack, par1EntityPlayer, par2World, par4, par5, par6, par7, f, f1, f2))
        {
                return true;
        }

        if (!par1EntityPlayer.isSneaking() || par1EntityPlayer.getHeldItem() == null || par1EntityPlayer.getHeldItem().getItem().doesSneakBypassUse(par2World, par4, par5, par6, par1EntityPlayer))
        {
            flag = par2World.func_147439_a(par4, par5, par6).func_149727_a(par2World, par4, par5, par6, par1EntityPlayer, par7, f, f1, f2);
        }

        if (!flag && par3ItemStack != null && par3ItemStack.getItem() instanceof ItemBlock)
        {
            ItemBlock itemblock = (ItemBlock)par3ItemStack.getItem();

            if (!itemblock.func_150936_a(par2World, par4, par5, par6, par7, par1EntityPlayer, par3ItemStack))
            {
                return false;
            }
        }

        this.netClientHandler.func_147297_a(new C08PacketPlayerBlockPlacement(par4, par5, par6, par7, par1EntityPlayer.inventory.getCurrentItem(), f, f1, f2));

        if (flag)
        {
            return true;
        }
        else if (par3ItemStack == null)
        {
            return false;
        }
        else if (this.currentGameType.isCreative())
        {
            int j1 = par3ItemStack.getItemDamage();
            int i1 = par3ItemStack.stackSize;
            boolean flag1 = par3ItemStack.tryPlaceItemIntoWorld(par1EntityPlayer, par2World, par4, par5, par6, par7, f, f1, f2);
            par3ItemStack.setItemDamage(j1);
            par3ItemStack.stackSize = i1;
            return flag1;
        }
        else
        {
            if (!par3ItemStack.tryPlaceItemIntoWorld(par1EntityPlayer, par2World, par4, par5, par6, par7, f, f1, f2))
            {
                return false;
            }
            if (par3ItemStack.stackSize <= 0)
            {
                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(par1EntityPlayer, par3ItemStack));
            }
            return true;
        }
    }

    // JAVADOC METHOD $$ func_78769_a
    public boolean sendUseItem(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.func_147297_a(new C08PacketPlayerBlockPlacement(-1, -1, -1, 255, par1EntityPlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
        int i = par3ItemStack.stackSize;
        ItemStack itemstack1 = par3ItemStack.useItemRightClick(par2World, par1EntityPlayer);

        if (itemstack1 == par3ItemStack && (itemstack1 == null || itemstack1.stackSize == i))
        {
            return false;
        }
        else
        {
            par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = itemstack1;

            if (itemstack1.stackSize <= 0)
            {
                par1EntityPlayer.inventory.mainInventory[par1EntityPlayer.inventory.currentItem] = null;
                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(par1EntityPlayer, itemstack1));
            }

            return true;
        }
    }

    public EntityClientPlayerMP func_147493_a(World p_147493_1_, StatFileWriter p_147493_2_)
    {
        return new EntityClientPlayerMP(this.mc, p_147493_1_, this.mc.getSession(), this.netClientHandler, p_147493_2_);
    }

    // JAVADOC METHOD $$ func_78764_a
    public void attackEntity(EntityPlayer par1EntityPlayer, Entity par2Entity)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.func_147297_a(new C02PacketUseEntity(par2Entity, C02PacketUseEntity.Action.ATTACK));
        par1EntityPlayer.attackTargetEntityWithCurrentItem(par2Entity);
    }

    public boolean func_78768_b(EntityPlayer par1EntityPlayer, Entity par2Entity)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.func_147297_a(new C02PacketUseEntity(par2Entity, C02PacketUseEntity.Action.INTERACT));
        return par1EntityPlayer.interactWith(par2Entity);
    }

    public ItemStack windowClick(int par1, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
        short short1 = par5EntityPlayer.openContainer.getNextTransactionID(par5EntityPlayer.inventory);
        ItemStack itemstack = par5EntityPlayer.openContainer.slotClick(par2, par3, par4, par5EntityPlayer);
        this.netClientHandler.func_147297_a(new C0EPacketClickWindow(par1, par2, par3, par4, itemstack, short1));
        return itemstack;
    }

    // JAVADOC METHOD $$ func_78756_a
    public void sendEnchantPacket(int par1, int par2)
    {
        this.netClientHandler.func_147297_a(new C11PacketEnchantItem(par1, par2));
    }

    // JAVADOC METHOD $$ func_78761_a
    public void sendSlotPacket(ItemStack par1ItemStack, int par2)
    {
        if (this.currentGameType.isCreative())
        {
            this.netClientHandler.func_147297_a(new C10PacketCreativeInventoryAction(par2, par1ItemStack));
        }
    }

    public void func_78752_a(ItemStack par1ItemStack)
    {
        if (this.currentGameType.isCreative() && par1ItemStack != null)
        {
            this.netClientHandler.func_147297_a(new C10PacketCreativeInventoryAction(-1, par1ItemStack));
        }
    }

    public void onStoppedUsingItem(EntityPlayer par1EntityPlayer)
    {
        this.syncCurrentPlayItem();
        this.netClientHandler.func_147297_a(new C07PacketPlayerDigging(5, 0, 0, 0, 255));
        par1EntityPlayer.stopUsingItem();
    }

    public boolean func_78763_f()
    {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    // JAVADOC METHOD $$ func_78762_g
    public boolean isNotCreative()
    {
        return !this.currentGameType.isCreative();
    }

    // JAVADOC METHOD $$ func_78758_h
    public boolean isInCreativeMode()
    {
        return this.currentGameType.isCreative();
    }

    // JAVADOC METHOD $$ func_78749_i
    public boolean extendedReach()
    {
        return this.currentGameType.isCreative();
    }

    public boolean func_110738_j()
    {
        return this.mc.thePlayer.isRiding() && this.mc.thePlayer.ridingEntity instanceof EntityHorse;
    }
}