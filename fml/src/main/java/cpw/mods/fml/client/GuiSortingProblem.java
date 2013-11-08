package cpw.mods.fml.client;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.toposort.ModSortingException;
import cpw.mods.fml.common.toposort.ModSortingException.SortingExceptionData;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import net.minecraft.client.gui.GuiScreen;

public class GuiSortingProblem extends GuiScreen {
    private ModSortingException modSorting;
    private SortingExceptionData<ModContainer> failedList;

    public GuiSortingProblem(ModSortingException modSorting)
    {
        this.modSorting = modSorting;
        this.failedList = modSorting.getExceptionData();
    }

    @Override
    public void func_73866_w_()
    {
        super.func_73866_w_();
    }

    @Override
    public void func_73863_a(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        this.func_73873_v_();
        int offset = Math.max(85 - (failedList.getVisitedNodes().size() + 3) * 10, 10);
        this.func_73732_a(this.field_73886_k, "Forge Mod Loader has found a problem with your minecraft installation", this.field_73880_f / 2, offset, 0xFFFFFF);
        offset+=10;
        this.func_73732_a(this.field_73886_k, "A mod sorting cycle was detected and loading cannot continue", this.field_73880_f / 2, offset, 0xFFFFFF);
        offset+=10;
        this.func_73732_a(this.field_73886_k, String.format("The first mod in the cycle is %s", failedList.getFirstBadNode()), this.field_73880_f / 2, offset, 0xFFFFFF);
        offset+=10;
        this.func_73732_a(this.field_73886_k, "The remainder of the cycle involves these mods", this.field_73880_f / 2, offset, 0xFFFFFF);
        offset+=5;
        for (ModContainer mc : failedList.getVisitedNodes())
        {
            offset+=10;
            this.func_73732_a(this.field_73886_k, String.format("%s : before: %s, after: %s", mc.toString(), mc.getDependants(), mc.getDependencies()), this.field_73880_f / 2, offset, 0xEEEEEE);
        }
        offset+=20;
        this.func_73732_a(this.field_73886_k, "The file 'ForgeModLoader-client-0.log' contains more information", this.field_73880_f / 2, offset, 0xFFFFFF);
    }

}
