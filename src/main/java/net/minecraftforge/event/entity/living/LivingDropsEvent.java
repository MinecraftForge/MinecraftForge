package net.minecraftforge.event.entity.living;

import java.util.List;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;

/**
 * LivingDropsEvent is fired when an Entity's death causes dropped items to appear.<br>
 * This event is fired whenever an Entity dies and drops items in
 * EntityLivingBase#onDeath(DamageSource).<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingDrops(EntityLivingBase, DamageSource, ArrayList<EntityItem>, int, boolean, int)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused the drop to occur.<br>
 * {@link #drops} contains the ArrayList of EntityItems that will be dropped.<br>
 * {@link #lootingLevel} contains the amount of loot that will be dropped.<br>
 * {@link #recentlyHit} determines whether the Entity doing the drop has recently been damaged.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity does not drop anything.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class LivingDropsEvent extends LivingEvent
{
    private final DamageSource source;
    private final List<EntityItem> drops;
    private final int lootingLevel;
    private final boolean recentlyHit;

    public LivingDropsEvent(EntityLivingBase entity, DamageSource source, List<EntityItem> drops, int lootingLevel, boolean recentlyHit)
    {
        super(entity);
        this.source = source;
        this.drops = drops;
        this.lootingLevel = lootingLevel;
        this.recentlyHit = recentlyHit;
    }

    public DamageSource getSource()
    {
        return source;
    }

    public List<EntityItem> getDrops()
    {
        return drops;
    }

    public int getLootingLevel()
    {
        return lootingLevel;
    }

    public boolean isRecentlyHit()
    {
        return recentlyHit;
    }
}
