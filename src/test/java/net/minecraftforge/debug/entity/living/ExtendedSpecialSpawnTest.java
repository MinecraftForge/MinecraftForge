package net.minecraftforge.debug.entity.living;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

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

        if (event.getEntity() != null)
            if (cancelEventRec(event.getEntity()))
                event.setCanceled(true);
    }

    /** Handles the entity and all it's passengers recursively
     *
     * @param entity
     * @return true, if the event should be cancelled, false otherwise
     */
    public static boolean cancelEventRec(Entity entity)
    {
        // Rename all skeletons
        if (entity instanceof EntitySkeleton)
        {
            entity.setCustomNameTag("Dinnerbone");
        }

        // Prevents Creepers from spawning
        if (entity instanceof EntityCreeper)
        {
            return true;
        }

        // Give Zombies a diamond sword
        if (entity instanceof EntityZombie)
        {
            EntityZombie zombie = (EntityZombie) entity;

            zombie.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Item.getByNameOrId("minecraft:diamond_sword")));
        }

        for(Entity passenger : entity.getPassengers())
        {
            if (cancelEventRec(passenger))
                return true;
        }
        return false;
    }
}