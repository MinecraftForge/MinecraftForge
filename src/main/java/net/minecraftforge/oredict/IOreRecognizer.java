package net.minecraftforge.oredict;

import net.minecraft.item.ItemStack;

import java.util.List;


public interface IOreRecognizer
{
    List<String> getOreNames(ItemStack itemStack);
}
