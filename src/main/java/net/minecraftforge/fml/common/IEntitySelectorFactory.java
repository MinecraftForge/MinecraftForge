package net.minecraftforge.fml.common;

import com.google.common.base.Predicate;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Can be registered in {@link net.minecraftforge.fml.common.registry.GameRegistry} to generate entity predicates for custom entity selectors in commands.
 * See CustomEntitySelectorTest
 */
public interface IEntitySelectorFactory
{
    /**
     * Called every time a command that contains entity selectors is executed
     *
     * @param arguments    A map with all arguments and their values
     * @param mainSelector The main selector string (e.g. 'a' for all players or 'e' for all entities)
     * @param sender       The sender of the command
     * @param position     A position either specified in the selector arguments or by the players position. See {@link EntitySelector#getPosFromArguments(Map, Vec3d)}
     * @return A list of new predicates, can be empty ({@link Collections#emptyList()} but not null.
     */
    @Nonnull List<Predicate<Entity>> createPredicates(Map<String, String> arguments, String mainSelector, ICommandSender sender, Vec3d position);
}
