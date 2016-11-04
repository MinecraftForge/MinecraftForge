package net.minecraftforge.event;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;
import java.util.Map;

/**
 * EntitySelectorEvent is fired whenever Minecraft collects entity selectors.
 * This happens (one or multiple times) when you use something like @a[gamemode=1] in a command.<br>
 * This event is fired via {@link ForgeEventFactory#gatherEntitySelectors(Map, String, ICommandSender, Vec3d)},
 * which is executed in {@link net.minecraft.command.EntitySelector#matchEntities(ICommandSender, String, Class)}<br>
 * <br>
 * This event is not cancelable and does not have a result.<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}
 */
public class EntitySelectorEvent extends Event {

    private final Map<String, String> map;
    private final String mainSelector;
    private final ICommandSender sender;
    private final Vec3d position;
    private final List<Predicate<Entity>> selectors;

    public EntitySelectorEvent(Map<String, String> map, String mainSelector, ICommandSender sender, Vec3d position) {
        this.map = map;
        this.mainSelector = mainSelector;
        this.sender = sender;
        this.position = position;
        selectors = Lists.newArrayList();
    }

    /**
     * Add your custom selector.
     * It is recommend to use "modid:name", if checking for own arguments, to avoid inter-mod interference (e.g.  "@a[Forge:timeplayed=100]").
     *
     * @param selector Your custom predicate
     */
    public void addPredicate(Predicate<Entity> selector) {
        if (selector == null) {
            throw new NullPointerException("Attempted to add null predicate as entity selector");
        }
        selectors.add(selector);
    }

    /**
     * @return The main selector used (e.g. 'a' for all players or 'e' for all entities)
     */
    public String getMainSelector() {
        return mainSelector;
    }

    /**
     * Example: "@a[test=true]" would result in a map with "test"=>"true"
     *
     * @return The argument map. Maps all given argument names with its value.
     */
    public Map<String, String> getMap() {
        return map;
    }

    /**
     * See {@link EntitySelector#getPosFromArguments(Map, Vec3d)}
     *
     * @return A position either specified in the selector arguments or by the players position.
     */
    public Vec3d getPosition() {
        return position;
    }

    /**
     * @return The sender of the command.
     */
    public ICommandSender getSender() {
        return sender;
    }

    /**
     * @return The list of added custom selectors
     */
    List<Predicate<Entity>> getSelectors() {
        return selectors;
    }


}
