package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.fml.ModContainer;

public class FMLConstructModEvent extends ParallelDispatchEvent {
    public FMLConstructModEvent(final ModContainer container) {
        super(container);
    }
}
