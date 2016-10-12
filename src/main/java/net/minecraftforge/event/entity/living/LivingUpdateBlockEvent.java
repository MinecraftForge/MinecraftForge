package net.minecraftforge.event.entity.living;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * LivingUpdateBlockEvent is fired whenever a living Entity (excluding player) changes or replaces block state. <br>
 * <br>
 * Entities which can trigger this event: Sheep, EnderDragon, Enderman, Rabbit, Silverfish, Wither, Zombie, Snowman and Villager (Farmer)
 * <br>
 * This event is fired in these methods: <br>
 * <ul>
 *     <li>{@link net.minecraft.entity.monster.EntityEnderman.AIPlaceBlock#updateTask()}</li>
 *     <li>{@link net.minecraft.entity.monster.EntityEnderman.AITakeBlock#updateTask()}</li>
 *     <li>{@link net.minecraft.entity.ai.EntityAIEatGrass#updateTask()}</li>
 *     <li>{@link net.minecraft.entity.boss.EntityDragon#destroyBlocksInAABB()}</li>
 *     <li>{@link net.minecraft.entity.passive.EntityRabbit.AIRaidFarm#updateTask()}</li>
 *     <li>{@link net.minecraft.entity.monster.EntitySilverfish.AISummonSilverfish#updateTask()}</li>
 *     <li>{@link net.minecraft.entity.monster.EntitySilverfish.AIHideInStone#startExecuting()}</li>
 *     <li>{@link net.minecraft.entity.boss.EntityWither#updateAITasks()}</li>
 *     <li>{@link net.minecraft.entity.ai.EntityAIBreakDoor#updateTask()}</li>
 *     <li>{@link net.minecraft.entity.ai.EntityAIHarvestFarmland#updateTask()}</li>
 *     <li>{@link net.minecraft.entity.monster.EntitySnowman#onLivingUpdate()}>
 * </ul>
 * <br>
 * {@link #pos} contains position of the block which being updated/replaced<br>
 * {@link #block} contains new state of the block.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, block is not updated.<br>
 * Be aware that some entities will try to update block even if it was canceled on previous tick <br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class LivingUpdateBlockEvent extends LivingEvent
{

    private final BlockPos pos;
    private final IBlockState block;

    public LivingUpdateBlockEvent(EntityLivingBase entity, BlockPos pos, IBlockState block)
    {
        super(entity);
        this.pos = pos;
        this.block = block;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public IBlockState getBlock()
    {
        return block;
    }
    
}
