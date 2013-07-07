package za.co.mcportcentral.inventory;

import net.minecraft.inventory.Container;

import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

public class CraftCustomInventoryView extends CraftInventoryView {

    public CraftCustomInventoryView(HumanEntity player, Inventory viewing,
            Container container) {
        super(player, viewing, container);
        // TODO Auto-generated constructor stub
    }

}
