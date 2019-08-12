package net.minecraftforge.common.util;

import net.minecraft.item.BlockItemUseContext;

public class SameBlockPlaceContext extends BlockItemUseContext {

    public SameBlockPlaceContext(BlockItemUseContext context) {
        super(context);
        this.replaceClicked = true;
    }

}
