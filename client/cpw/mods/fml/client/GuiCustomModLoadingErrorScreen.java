package cpw.mods.fml.client;

import net.minecraft.client.gui.GuiErrorScreen;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.WrongMinecraftVersionException;

public class GuiCustomModLoadingErrorScreen extends GuiErrorScreen
{
    private CustomModLoadingErrorDisplayException customException;
    public GuiCustomModLoadingErrorScreen(CustomModLoadingErrorDisplayException customException)
    {
        this.customException = customException;
    }
    @Override
    public void func_73866_w_()
    {
        super.func_73866_w_();
        this.customException.initGui(this, field_73886_k);
    }
    @Override
    public void func_73863_a(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        this.func_73873_v_();
        this.customException.drawScreen(this, field_73886_k, p_73863_1_, p_73863_2_, p_73863_3_);
    }
}
