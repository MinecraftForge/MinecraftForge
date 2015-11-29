package net.minecraftforge.common.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.Container;


    public class GuiContainerPair {

        public final Container container;
        public final Gui gui;

        public GuiContainerPair(Container container, Gui gui){
            this.container=container;
            this.gui=gui;
        }

    }

