package org.bukkit.craftbukkit.block;

import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.craftbukkit.CraftWorld;

public class CraftCommandBlock extends CraftBlockState implements CommandBlock {
    private final net.minecraft.tileentity.TileEntityCommandBlock commandBlock;
    private String command;
    private String name;

    public CraftCommandBlock(Block block) {
        super(block);

        CraftWorld world = (CraftWorld) block.getWorld();
        commandBlock = (net.minecraft.tileentity.TileEntityCommandBlock) world.getTileEntityAt(getX(), getY(), getZ());
        command = commandBlock.command;
        name = commandBlock.getCommandSenderName();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command != null ? command : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name : "@";
    }

    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);

        if (result) {
            commandBlock.setCommand(command);
            commandBlock.setCommandSenderName(name);
        }

        return result;
    }
}
