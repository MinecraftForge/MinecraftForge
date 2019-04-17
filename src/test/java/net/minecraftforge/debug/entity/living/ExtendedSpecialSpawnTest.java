package net.minecraftforge.debug.entity.living;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(
        modid = ExtendedSpecialSpawnTest.MOD_ID,
        name = "Extended SpecialSpawn Test",
        version = "1.0",
        acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber(modid = ExtendedSpecialSpawnTest.MOD_ID)
public class ExtendedSpecialSpawnTest
{
    static final String MOD_ID = "extended_special_spawn_test";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn event)
    {
        if (!ENABLED)
        {
            return;
        }

        // Rename all skeletons
        if (event.getEntity() instanceof EntitySkeleton)
        {
            event.getEntity().setCustomNameTag("Dinnerbone");
        }

        // Prevents Creepers from spawning
        if (event.getEntity() instanceof EntityCreeper)
        {
            event.setCanceled(true);
        }

        // Give Zombies a diamond sword
        if (event.getEntity() instanceof EntityZombie)
        {
            EntityZombie zombie = (EntityZombie) event.getEntity();

            zombie.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Item.getByNameOrId("minecraft:diamond_sword")));
        }
    }
}