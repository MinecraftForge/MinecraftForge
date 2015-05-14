package cpw.mods.fml.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.resources.I18n;
import cpw.mods.fml.common.StartupQuery;

public class GuiConfirmation extends GuiNotification
{
    public GuiConfirmation(StartupQuery query)
    {
        super(query);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 155, this.height - 38, I18n.format("gui.yes")));
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height - 38, I18n.format("gui.no")));
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled && (button.id == 0 || button.id == 1))
        {
            FMLClientHandler.instance().showGuiScreen(null);
            query.setResult(button.id == 0);
            query.finish();
        }
    }
}
