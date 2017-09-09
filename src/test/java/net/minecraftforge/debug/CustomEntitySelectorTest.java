package net.minecraftforge.debug;

import com.google.common.base.Predicate;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.IEntitySelectorFactory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * You can verify that this test is working fine by following these steps.
 * Spawn a few cows and a few sheep and then use "/kill @e[forge:min_health=!10]"
 * All entities with less than 10 health points (current, not max) should die.
 * This should include the sheep, but not the (healthy) cows.
 */
@Mod(modid = "customentityselectortest", name = "Custom Entity Selector Test", version = "1.0", acceptableRemoteVersions = "*")
public class CustomEntitySelectorTest
{

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        GameRegistry.registerEntitySelector(new EntitySelectorFactory(), "forge:min_health");
    }

    private class EntitySelectorFactory implements IEntitySelectorFactory
    {

        @Nonnull
        @Override
        public List<Predicate<Entity>> createPredicates(Map<String, String> arguments, String mainSelector, ICommandSender sender, Vec3d position)
        {
            String health = arguments.get("forge:min_health");

            //If our selector is used in this command create a Predicate otherwise return an empty list.
            if (health != null)
            {
                final boolean invert = health.startsWith("!");

                if (invert)
                {
                    health = health.substring(1);
                }
                try
                {
                    final int value = Integer.parseInt(health);

                    //Return a list of predicates. All these predicates have to apply for any entity to be selected.
                    return Collections.<Predicate<Entity>>singletonList(new Predicate<Entity>()
                    {
                        @Override
                        public boolean apply(@Nullable Entity input)
                        {
                            if (!(input instanceof EntityLivingBase))
                            {
                                return false;
                            }
                            return (((EntityLivingBase) input).getHealth() >= value) != invert;
                        }
                    });
                }
                catch (NumberFormatException e)
                {
                    sender.sendMessage(new TextComponentString("Entity selector 'forge:min_health' has to be an integer"));
                }
            }
            return Collections.emptyList();
        }
    }
}
