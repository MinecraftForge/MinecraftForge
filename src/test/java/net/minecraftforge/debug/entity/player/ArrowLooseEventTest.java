package net.minecraftforge.debug.entity.player;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.event.entity.player.ProjectileLooseEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.AmmoHolderHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("arrow_loose_event_test")
@Mod.EventBusSubscriber
public class ArrowLooseEventTest
{
    private static final boolean ENABLE = false;
    private static final Logger LOGGER = LogManager.getLogger(ArrowLooseEventTest.class);

    @SubscribeEvent
    public static void onArrowLoosed(ProjectileLooseEvent event)
    {
        if (!ENABLE && !(event.getShootable().getItem() instanceof CrossbowItem)) return;
        LOGGER.info("The ArrowLooseEvent has been called!");
        event.setCanceled(true);
        PlayerEntity player = event.getPlayer();
        ItemStack shootable = event.getShootable();
        boolean hasMultiShot = EnchantmentHelper.getEnchantmentLevel(Enchantments.MULTISHOT, shootable) > 0;

        if (hasMultiShot)
        {
            for (int i = 0; i < 7; i++)
            {
                if (i == 0)
                {
                    fireProjectile(player, event.getAmmo(), 1.0F, 0.0F);
                } else if (i == 1)
                {
                    fireProjectile(player, event.getAmmo(), 1.0F, -10.0F);
                } else if (i == 2)
                {
                    fireProjectile(player, event.getAmmo(), 1.0F, 10.0F);
                } else if (i == 3)
                {
                    fireProjectile(player, event.getAmmo(), 1.75F, 0.0F);
                } else if (i == 4)
                {
                    fireProjectile(player, event.getAmmo(), 1.75F, -10.0F);
                } else
                {
                    fireProjectile(player, event.getAmmo(), 1.75F, 10.0F);
                }
            }
        }
        AmmoHolderHelper.consumeAmmo(shootable);
    }

    @SubscribeEvent
    public static void onArrowLoosePost(ProjectileLooseEvent.Post event)
    {
        if (!ENABLE && !(event.getShootable().getItem() instanceof BowItem)) return;
        LOGGER.info("The ArrowLooseEvent.Post has been called!");
        PlayerEntity player = event.getPlayer();
        ItemStack shootable = event.getShootable();
        boolean hasMultiShot = EnchantmentHelper.getEnchantmentLevel(Enchantments.MULTISHOT, shootable) > 0;

        if (hasMultiShot)
        {
            for (int i = 0; i < 2; i++)
            {
                if (i == 0)
                {
                    fireProjectile(player, event.getAmmo(), 1.0F, -10.0F);
                } else
                {
                    fireProjectile(player, event.getAmmo(), 1.0F,10.0F);
                }
            }
        }
    }

    private static void fireProjectile(PlayerEntity player, ItemStack ammo, float upVector, float projectileAngle)
    {
        ArrowItem arrowitem = (ArrowItem)(ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
        ProjectileEntity projectileentity = arrowitem.createArrow(player.getEntityWorld(), ammo, player);
        Vector3d vector3d1 = player.getUpVector(upVector);
        Quaternion quaternion = new Quaternion(new Vector3f(vector3d1), projectileAngle, true);
        Vector3d vector3d = player.getLook(1.0F);
        Vector3f vector3f = new Vector3f(vector3d);
        vector3f.transform(quaternion);
        projectileentity.shoot(vector3f.getX(), vector3f.getY(), vector3f.getZ(), 3.15F, 1.0F);
    }

}
