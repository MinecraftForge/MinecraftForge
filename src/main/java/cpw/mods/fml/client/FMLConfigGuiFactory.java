package cpw.mods.fml.client;

import java.util.List;
import java.util.Set;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class FMLConfigGuiFactory implements IModGuiFactory {
    public static class FMLConfigGuiScreen extends GuiScreen {
        private GuiScreen parent;

        public FMLConfigGuiScreen(GuiScreen parent)
        {
            this.parent = parent;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void func_73866_w_()
        {
            this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 75, this.field_146295_m - 38, I18n.func_135052_a("gui.done")));
        }

        @Override
        protected void func_146284_a(GuiButton p_73875_1_)
        {
            if (p_73875_1_.field_146124_l && p_73875_1_.field_146127_k == 1)
            {
                FMLClientHandler.instance().showGuiScreen(parent);
            }
        }

        @Override
        public void func_73863_a(int p_73863_1_, int p_73863_2_, float p_73863_3_)
        {
            this.func_146276_q_();
            this.func_73732_a(this.field_146289_q, "Forge Mod Loader test config screen", this.field_146294_l / 2, 40, 0xFFFFFF);
            super.func_73863_a(p_73863_1_, p_73863_2_, p_73863_3_);
        }

    }

    @SuppressWarnings("unused")
    private Minecraft minecraft;
    @Override
    public void initialize(Minecraft minecraftInstance)
    {
        this.minecraft = minecraftInstance;
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass()
    {
        return FMLConfigGuiScreen.class;
    }

    private static final Set<RuntimeOptionCategoryElement> fmlCategories = ImmutableSet.of(new RuntimeOptionCategoryElement("HELP", "FML"));

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return fmlCategories;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
    {
        return new RuntimeOptionGuiHandler() {
            @Override
            public void paint(int x, int y, int w, int h)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void close()
            {
            }

            @Override
            public void addWidgets(List<Gui> widgets, int x, int y, int w, int h)
            {
                widgets.add(new GuiButton(100, x+10, y+10, "HELLO"));
            }

            @Override
            public void actionCallback(int actionId)
            {
                // TODO Auto-generated method stub

            }
        };
    }

}
