package net.minecraftforge.client.event;

import net.minecraft.world.BossInfoLerping;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired on MinecraftForge.EVENT_BUS before the boss bars render in GuiBossOverlay.renderBossHealth.
 * This is useful if you want to draw your boss' bar using custom textures or other rendering.
 * This event is cancelable. If canceled, the vanilla boss bar rendering logic will not run.
 */
@Cancelable
public class RenderBossInfoEvent extends Event {

    private final BossInfoLerping bossInfo;

    public RenderBossInfoEvent(BossInfoLerping bossInfo)
    {
        this.bossInfo = bossInfo;
    }

    public BossInfoLerping getBossInfo()
    {
        return bossInfo;
    }

}
