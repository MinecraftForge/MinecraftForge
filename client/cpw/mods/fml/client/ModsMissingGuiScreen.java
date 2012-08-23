package cpw.mods.fml.client;

import cpw.mods.fml.common.MissingModsException;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import net.minecraft.src.GuiErrorScreen;

public class ModsMissingGuiScreen extends GuiErrorScreen
{

    private MissingModsException modsMissing;

    public ModsMissingGuiScreen(MissingModsException modsMissing)
    {
        this.modsMissing = modsMissing;
    }

    @Override
    public void func_73863_a(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        this.func_73873_v_();
        int offset = 85 - modsMissing.missingMods.size() * 10;
        this.func_73732_a(this.field_73886_k, "Forge Mod Loader has found a problem with your minecraft installation", this.field_73880_f / 2, offset, 0xFFFFFF);
        offset+=10;
        this.func_73732_a(this.field_73886_k, "The mods and versions listed below could not be found", this.field_73880_f / 2, offset, 0xFFFFFF);
        offset+=5;
        for (ArtifactVersion v : modsMissing.missingMods)
        {
            offset+=10;
            this.func_73732_a(this.field_73886_k, String.format("%s : %s", v.getLabel(), v.getVersionString()), this.field_73880_f / 2, offset, 0xEEEEEE);
        }
        offset+=20;
        this.func_73732_a(this.field_73886_k, "The file 'ForgeModLoader-client-0.log' contains more information", this.field_73880_f / 2, offset, 0xFFFFFF);
    }
}
