package net.minecraftforge.debug.world;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.RaidEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

@Mod("raid_event_test")
public class RaidEventTest
{
    public RaidEventTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::getCustomRaid);
        MinecraftForge.EVENT_BUS.addListener(this::loadCustomRaid);
        MinecraftForge.EVENT_BUS.addListener(this::onRaidSpawnGroup);
        MinecraftForge.EVENT_BUS.addListener(this::onRaidStop);
    }

    private static final Random RAND = new Random();
    public void getCustomRaid(RaidEvent.GetOrCreate event)
    {
        if (RAND.nextBoolean())
            event.setCustomRaid((id) -> new TestModRaid(id, event.getLevel(), event.getPos()));
    }

    public void loadCustomRaid(RaidEvent.Load event)
    {
        event.setRaidConstructor((level, tag) -> tag.getBoolean("isModRaid") ? new TestModRaid(level, tag) : null);
    }

    public void onRaidSpawnGroup(RaidEvent.SpawnGroup event)
    {
        if (event.getRaid() instanceof TestModRaid)
            return;
        event.setShouldSpawnBonusGroup(true);
    }

    public void onRaidStop(RaidEvent.Stop event)
    {
        event.getRaid().getRaidEvent().getPlayers().forEach((player) -> player.sendMessage(new TextComponent("Raid has ended"), Util.NIL_UUID));
    }

    static class TestModRaid extends Raid
    {
        public TestModRaid(int id, ServerLevel level, BlockPos pos)
        {
            super(id, level, pos);
        }

        public TestModRaid(ServerLevel level, CompoundTag compound)
        {
            super(level, compound);
        }

        @Override
        public void tick()
        {
            super.tick();
            if (!isStopped())
            {
                if (getLevel().getGameTime() % 60 == 0)
                {
                    new ArrayList<>(getRaidEvent().getPlayers()).forEach((player) ->
                    {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60));
                    });
                    Set<Raider> raiders = getGroupRaiderMap().get(getGroupsSpawned());
                    if (raiders != null && raiders.size() < 3)
                        raiders.forEach((raider) -> {
                            raider.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60));
                            raider.addEffect(new MobEffectInstance(MobEffects.POISON, 60));
                        });
                }
            }
        }

        @Override
        public CompoundTag save(CompoundTag tag)
        {
            tag.putBoolean("isModRaid", true);
            return super.save(tag);
        }
    }
}
