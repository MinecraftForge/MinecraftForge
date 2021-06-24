package awesomesyd.fishingexample;

import java.lang.reflect.Field;
import java.util.List;

import awesomesyd.fishingexample.FishingExample.Register;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@Mod.EventBusSubscriber
public class EventHandling
{
    private static final Field hooked = ObfuscationReflectionHelper.findField(FishingBobberEntity.class, "field_146043_c");

    public EventHandling()
    {
    }

    public static class RodFinder
    {
        ItemStack rod;
        int level;

        public RodFinder(PlayerEntity player, Enchantment enchant)
        {
            rod = player.getMainHandItem();
            level = EnchantmentHelper.getItemEnchantmentLevel(enchant, rod);
            if (level == 0)
            {
                rod = player.getOffhandItem();
                level = EnchantmentHelper.getItemEnchantmentLevel(enchant, rod);
            }
        }
    }

    /// stick an arrow to block, and bobber to that
    public static void stickBobberToBlock(FishingBobberEntity bob, Vector3d impact)
    {
        Vector3d motion = new Vector3d(
                impact.x - bob.position().x,
                impact.y - bob.position().y,
                impact.z - bob.position().z);
        ArrowEntity arrow = new ArrowEntity(bob.level,
                impact.x + motion.x, impact.y + motion.y, impact.z + motion.z);
        bob.level.addFreshEntity(arrow);
        arrow.setDeltaMovement(motion);
        try
        {
            hooked.set(bob, arrow);
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void handleImpact(ProjectileImpactEvent.FishingBobber event)
    {
        // RayTraceResult stuff = event.getRayTraceResult();//Not needed with new FighingBobber Event
        // if (event.getEntity() instanceof FishingBobberEntity) {
        // FishingBobberEntity bob = (FishingBobberEntity) event.getEntity();
        FishingBobberEntity bob = event.getFishingBobber();
        PlayerEntity player = bob.getPlayerOwner();
        RayTraceResult trace = event.getRayTraceResult();

        if (EnchantmentHelper.getEnchantmentLevel(Register.BLOCK_REEL.get(), player) > 0)
        {
            if (trace.getType() == RayTraceResult.Type.BLOCK)
            {
                bob.getPersistentData().putBoolean("reel", true);
                stickBobberToBlock(bob, trace.getLocation());
            }
        }

        if (EnchantmentHelper.getEnchantmentLevel(Register.GRAPPLE.get(), player) > 0)
        {
            if (trace.getType() != RayTraceResult.Type.MISS)
            {
                bob.getPersistentData().putBoolean("grapple", true);
                if (trace.getType() == RayTraceResult.Type.BLOCK)
                {// it does entities automatically
                    stickBobberToBlock(bob, trace.getLocation());
                }
            }
        }

        int punchLevel = EnchantmentHelper.getEnchantmentLevel(Register.FISH_PUNCH.get(), player);
        if (punchLevel > 0 && trace.getType() == RayTraceResult.Type.ENTITY)
        {
            Vector3d vector3d = bob.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale(0.6 * punchLevel);
            if (vector3d.lengthSqr() > 0.0D)
            {// copy of bow punch
                Entity entity = ((EntityRayTraceResult) trace).getEntity();
                if (entity instanceof LivingEntity)
                {
                    ((LivingEntity) entity).push(vector3d.x, 0.3D, vector3d.z);
                }
            }
        }
        // }
    }

    public static BlockPos getBlock(FishingBobberEntity bob)
    {
        double tx = bob.getX();
        double ty = bob.getY();
        double tz = bob.getZ();
        BlockPos pos = new BlockPos(tx, ty, tz);// if bobber is in a block, we pull that block
        if (bob.level.getBlockState(pos).isAir())
        {// if bobber is in air (most of the time) we need to find a block
            double dx = tx - Math.floor(tx);
            double dy = ty - Math.floor(ty);
            double dz = tz - Math.floor(tz);
            if (Math.abs(dx - .5) > Math.abs(dy - .5))
            {// x is closer than y
                if (Math.abs(dx - .5) > Math.abs(dz - .5))
                {// x is closest
                    if (dx > .5)
                        pos = new BlockPos(tx + 1.5 - dx, ty, tz);// middle of next block
                    else
                        pos = new BlockPos(tx - .5 - dx, ty, tz);
                }
                else
                { // x closer than y, x farther than z
                    if (dz > .5)
                        pos = new BlockPos(tx, ty, tz + 1.5 - dz);
                    else
                        pos = new BlockPos(tx, ty, tz - .5 - dz);
                }
            }
            else
            {// y closer than x
                if (Math.abs(dy - .5) > Math.abs(dz - .5))
                {// y is closest
                    if (dy > .5)
                        pos = new BlockPos(tx, ty + 1.5 - dy, tz);// middle of next block
                    else
                        pos = new BlockPos(tx, ty - .5 - dy, tz);
                }
                else
                {// z is closest
                    if (dz > .5)
                        pos = new BlockPos(tx, ty, tz + 1.5 - dz);
                    else
                        pos = new BlockPos(tx, ty, tz - .5 - dz);
                }
            }
        }
        return pos;
    }

    @SubscribeEvent
    public static void reelIn(EntityLeaveWorldEvent event)
    {
        if (!(event.getEntity() instanceof FishingBobberEntity))
        {
            return;
        }
        // the bobber is in the middle of being removed at this point, but everything seems to work ok
        FishingBobberEntity bob = (FishingBobberEntity) event.getEntity();
        PlayerEntity player = bob.getPlayerOwner();
        Entity hookedIn = bob.getHookedIn();
        RodFinder arod = new RodFinder(player, Register.YANK.get());
        if (arod.level > 0 && hookedIn != null)
        {
            Vector3d vector3d = (new Vector3d(
                    player.getX() - bob.getX(),
                    player.getY() - bob.getY(),
                    player.getZ() - bob.getZ()));
            hookedIn.setDeltaMovement(hookedIn.getDeltaMovement().add(vector3d.scale(arod.level / 5f)));
        }
        if (hookedIn instanceof ArrowEntity)
        {
            hookedIn.remove();
        }
        if (bob.getPersistentData().contains("reel") && (hookedIn instanceof ArrowEntity) && !bob.level.isClientSide)
        {
            // get whichever hand item happens to be a correctly enchanted fishing rod, we will need it for the "retrieve" call
            arod = new RodFinder(player, Register.BLOCK_REEL.get());
            if (arod.level > 0)
            {
                BlockPos pos = getBlock(bob);
                if (!BlockReelEnchantment.canReel(bob.level, pos, arod.level))
                    return;
                bob.level.destroyBlock(pos, true, player);
                List<ItemEntity> drops = bob.level.getEntitiesOfClass(ItemEntity.class, bob.getBoundingBox().inflate(1));
                if (drops.size() == 0)
                    return;
                try
                {
                    hooked.set(bob, drops.get(0));
                }
                catch (IllegalArgumentException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                bob.retrieve(arod.rod);// pull the itemstack in
            }
        }
        if (bob.getPersistentData().contains("grapple") &&
                EnchantmentHelper.getEnchantmentLevel(Register.GRAPPLE.get(), player) > 0)
        {
            Vector3d toMove = bob.position().subtract(player.position());
            player.setDeltaMovement(player.getDeltaMovement().add(toMove.scale(.15)));
        }
        if (hookedIn instanceof LivingEntity)
        {
            LivingEntity target = (LivingEntity) hookedIn;
            arod = new RodFinder(player, Register.YOINK.get());
            if (arod.level > 0)
            {
                for (EquipmentSlotType slot : EquipmentSlotType.values())
                {
                    ItemStack stack = target.getItemBySlot(slot);
                    if (player.getRandom().nextInt(100) < arod.level && !stack.isEmpty())
                    {
                        ItemEntity yoinked = target.spawnAtLocation(stack);
                        target.setItemSlot(slot, ItemStack.EMPTY);
                        try
                        {
                            hooked.set(bob, yoinked);
                        }
                        catch (IllegalArgumentException | IllegalAccessException e)
                        {
                            e.printStackTrace();
                        }
                        bob.retrieve(arod.rod);
                    }
                }
            }
            arod = new RodFinder(player, Register.BARBED.get());
            if (arod.level > 0)
            {
                target.hurt((new IndirectEntityDamageSource("arrow", bob, player)).setProjectile(),
                        (float) (2 * arod.level));
            }
        }
    }
}