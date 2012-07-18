package net.minecraft.src.forge.idrequest;

import net.minecraft.src.Block;
import net.minecraft.src.Item;

public abstract class IDCallbackAdapter implements IIDCallback {
    @Override
    public void unregister(String name, int id) {
        if(id < Block.blocksList.length)
            Block.blocksList[id] = null;
        Item.itemsList[id] = null;
    }
}
