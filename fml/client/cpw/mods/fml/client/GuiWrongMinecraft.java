package cpw.mods.fml.client;

import net.minecraft.client.gui.GuiErrorScreen;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.WrongMinecraftVersionException;
import cpw.mods.fml.common.versioning.ArtifactVersion;

public class GuiWrongMinecraft extends GuiErrorScreen
{
    private WrongMinecraftVersionException wrongMC;
    public GuiWrongMinecraft(WrongMinecraftVersionException wrongMC)
    {
        this.wrongMC = wrongMC;
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
        int offset = 75;
        this.func_73732_a(this.field_73886_k, "Forge Mod Loader has found a problem with your minecraft installation", this.field_73880_f / 2, offset, 0xFFFFFF);
        offset+=10;
        this.func_73732_a(this.field_73886_k, String.format("The mod listed below does not want to run in Minecraft version %s", Loader.instance().getMinecraftModContainer().getVersion()), this.field_73880_f / 2, offset, 0xFFFFFF);
        offset+=5;
        offset+=10;
        this.func_73732_a(this.field_73886_k, String.format("%s (%s) wants Minecraft %s", wrongMC.mod.getName(), wrongMC.mod.getModId(), wrongMC.mod.acceptableMinecraftVersionRange()), this.field_73880_f / 2, offset, 0xEEEEEE);
        offset+=20;
        this.func_73732_a(this.field_73886_k, "The file 'ForgeModLoader-client-0.log' contains more information", this.field_73880_f / 2, offset, 0xFFFFFF);
    }
}
