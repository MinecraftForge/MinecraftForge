package net.minecraftforge.energy;

import cpw.mods.fml.common.eventhandler.Event;

public class EnergyConsumerEvent extends Event {
    public final IEnergyConsumer tile;
    
    private EnergyConsumerEvent(IEnergyConsumer tile) {
        this.tile = tile;
    }
    
    public static class Load extends EnergyConsumerEvent {
        public Load(IEnergyConsumer tile) {
            super(tile);
        }
    }
    
    public static class Unload extends EnergyConsumerEvent {
        public Unload(IEnergyConsumer tile) {
            super(tile);
        }
    }
}
