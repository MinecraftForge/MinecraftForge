package cpw.mods.fml.client;

import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.toposort.ModSortingException;
import cpw.mods.fml.common.toposort.ModSortingException.SortingExceptionData;

public class GuiSortingProblem extends GuiScreen {
    private SortingExceptionData<ModContainer> failedList;

    public GuiSortingProblem(ModSortingException modSorting)
    {
        this.failedList = modSorting.getExceptionData();
    }

    // JAVADOC METHOD $$ func_73866_w_
    @Override
    public void initGui()
    {
        super.initGui();
    }

    // JAVADOC METHOD $$ func_73863_a
    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        int offset = Math.max(85 - (failedList.getVisitedNodes().size() + 3) * 10, 10);
        this.drawCenteredString(this.field_146289_q, "Forge Mod Loader has found a problem with your minecraft installation", this.field_146294_l / 2, offset, 0xFFFFFF);
        offset+=10;
        this.drawCenteredString(this.field_146289_q, "A mod sorting cycle was detected and loading cannot continue", this.field_146294_l / 2, offset, 0xFFFFFF);
        offset+=10;
        this.drawCenteredString(this.field_146289_q, String.format("The first mod in the cycle is %s", failedList.getFirstBadNode()), this.field_146294_l / 2, offset, 0xFFFFFF);
        offset+=10;
        this.drawCenteredString(this.field_146289_q, "The remainder of the cycle involves these mods", this.field_146294_l / 2, offset, 0xFFFFFF);
        offset+=5;
        for (ModContainer mc : failedList.getVisitedNodes())
        {
            offset+=10;
            this.drawCenteredString(this.field_146289_q, String.format("%s : before: %s, after: %s", mc.toString(), mc.getDependants(), mc.getDependencies()), this.field_146294_l / 2, offset, 0xEEEEEE);
        }
        offset+=20;
        this.drawCenteredString(this.field_146289_q, "The file 'ForgeModLoader-client-0.log' contains more information", this.field_146294_l / 2, offset, 0xFFFFFF);
    }

}