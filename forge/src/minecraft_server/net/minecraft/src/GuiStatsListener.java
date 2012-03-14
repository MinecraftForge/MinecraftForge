package net.minecraft.src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GuiStatsListener implements ActionListener
{
    final GuiStatsComponent statsComponent;

    GuiStatsListener(GuiStatsComponent par1GuiStatsComponent)
    {
        this.statsComponent = par1GuiStatsComponent;
    }

    public void actionPerformed(ActionEvent par1ActionEvent)
    {
        GuiStatsComponent.update(this.statsComponent);
    }
}
