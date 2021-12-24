package net.minecraftforge.debug.entity.living;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("living_get_projectile_event_test")
public class LivingGetProjectileEventTest
{
    public static final boolean ENABLE = true;
    public static final Logger LOGGER = LogManager.getLogger();

    public LivingGetProjectileEventTest()
    {
        if (ENABLE)
        {
            MinecraftForge.EVENT_BUS.addListener(this::onPlayerGetProjectile);
            MinecraftForge.EVENT_BUS.addListener(this::onLivingGetProjectile);
        }
    }

    public void onPlayerGetProjectile(LivingGetProjectileEvent event)
    {
        if (event.getEntityLiving() instanceof Player && event.getProjectileItemStack().getItem().equals(Items.ARROW))
        {
            event.setProjectileItemStack(new ItemStack(Items.SPECTRAL_ARROW));
        }
    }

    public void onLivingGetProjectile(LivingGetProjectileEvent event)
    {
        LOGGER.info("{} about to fire {} with a {}", event.getEntity(), event.getProjectileItemStack(), event.getProjectileWeaponItemStack());
    }
}
