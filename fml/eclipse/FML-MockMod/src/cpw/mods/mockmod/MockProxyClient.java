package cpw.mods.mockmod;

import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiErrorScreen;
import cpw.mods.fml.client.CustomModLoadingErrorDisplayException;

public class MockProxyClient extends MockProxy
{
    @Override
    public void throwError()
    {
        throw new CustomModLoadingErrorDisplayException()
        {

            @Override
            public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int par1, int par2, float par3)
            {
                errorScreen.func_73732_a(fontRenderer, "MockMod doesn't like your installation", errorScreen.field_73880_f / 2, 75, 0xFFFFFF);

            }

            @Override
            public void initGui(GuiErrorScreen errorScreen, FontRenderer renderer)
            {
                // noop
            }
        };
    }
}
