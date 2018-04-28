package net.minecraftforge.common;

import net.minecraft.command.CommandException;

public class WorldNotFoundException extends CommandException {
    public WorldNotFoundException(String worldname) {
        super("Could not load world: " + worldname);
    }
}
