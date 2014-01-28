package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

@SideOnly(Side.CLIENT)
public class GuiOptions extends GuiScreen
{
    private static final GameSettings.Options[] field_146440_f = new GameSettings.Options[] {GameSettings.Options.FOV, GameSettings.Options.DIFFICULTY};
    private final GuiScreen field_146441_g;
    private final GameSettings field_146443_h;
    protected String field_146442_a = "Options";
    private static final String __OBFID = "CL_00000700";

    public GuiOptions(GuiScreen par1GuiScreen, GameSettings par2GameSettings)
    {
        this.field_146441_g = par1GuiScreen;
        this.field_146443_h = par2GameSettings;
    }

    // JAVADOC METHOD $$ func_73866_w_
    public void initGui()
    {
        int i = 0;
        this.field_146442_a = I18n.getStringParams("options.title", new Object[0]);
        GameSettings.Options[] aoptions = field_146440_f;
        int j = aoptions.length;

        for (int k = 0; k < j; ++k)
        {
            GameSettings.Options options = aoptions[k];

            if (options.getEnumFloat())
            {
                this.field_146292_n.add(new GuiOptionSlider(options.returnEnumOrdinal(), this.field_146294_l / 2 - 155 + i % 2 * 160, this.field_146295_m / 6 - 12 + 24 * (i >> 1), options));
            }
            else
            {
                GuiOptionButton guioptionbutton = new GuiOptionButton(options.returnEnumOrdinal(), this.field_146294_l / 2 - 155 + i % 2 * 160, this.field_146295_m / 6 - 12 + 24 * (i >> 1), options, this.field_146443_h.getKeyBinding(options));

                if (options == GameSettings.Options.DIFFICULTY && this.field_146297_k.theWorld != null && this.field_146297_k.theWorld.getWorldInfo().isHardcoreModeEnabled())
                {
                    guioptionbutton.field_146124_l = false;
                    guioptionbutton.field_146126_j = I18n.getStringParams("options.difficulty", new Object[0]) + ": " + I18n.getStringParams("options.difficulty.hardcore", new Object[0]);
                }

                this.field_146292_n.add(guioptionbutton);
            }

            ++i;
        }

        this.field_146292_n.add(new GuiButton(106, this.field_146294_l / 2 - 152, this.field_146295_m / 6 + 72 - 6, 150, 20, I18n.getStringParams("options.sounds", new Object[0])));
        this.field_146292_n.add(new GuiButton(8675309, this.field_146294_l / 2 + 2, this.field_146295_m / 6 + 72 - 6, 150, 20, "Super Secret Settings...")
        {
            private static final String __OBFID = "CL_00000701";
            public void func_146113_a(SoundHandler p_146113_1_)
            {
                SoundEventAccessorComposite soundeventaccessorcomposite = p_146113_1_.func_147686_a(new SoundCategory[] {SoundCategory.ANIMALS, SoundCategory.BLOCKS, SoundCategory.MOBS, SoundCategory.PLAYERS, SoundCategory.WEATHER});

                if (soundeventaccessorcomposite != null)
                {
                    p_146113_1_.func_147682_a(PositionedSoundRecord.func_147674_a(soundeventaccessorcomposite.func_148729_c(), 0.5F));
                }
            }
        });
        this.field_146292_n.add(new GuiButton(101, this.field_146294_l / 2 - 152, this.field_146295_m / 6 + 96 - 6, 150, 20, I18n.getStringParams("options.video", new Object[0])));
        this.field_146292_n.add(new GuiButton(100, this.field_146294_l / 2 + 2, this.field_146295_m / 6 + 96 - 6, 150, 20, I18n.getStringParams("options.controls", new Object[0])));
        this.field_146292_n.add(new GuiButton(102, this.field_146294_l / 2 - 152, this.field_146295_m / 6 + 120 - 6, 150, 20, I18n.getStringParams("options.language", new Object[0])));
        this.field_146292_n.add(new GuiButton(103, this.field_146294_l / 2 + 2, this.field_146295_m / 6 + 120 - 6, 150, 20, I18n.getStringParams("options.multiplayer.title", new Object[0])));
        this.field_146292_n.add(new GuiButton(105, this.field_146294_l / 2 - 152, this.field_146295_m / 6 + 144 - 6, 150, 20, I18n.getStringParams("options.resourcepack", new Object[0])));
        this.field_146292_n.add(new GuiButton(104, this.field_146294_l / 2 + 2, this.field_146295_m / 6 + 144 - 6, 150, 20, I18n.getStringParams("options.snooper.view", new Object[0])));
        this.field_146292_n.add(new GuiButton(200, this.field_146294_l / 2 - 100, this.field_146295_m / 6 + 168, I18n.getStringParams("gui.done", new Object[0])));
    }

    protected void func_146284_a(GuiButton p_146284_1_)
    {
        if (p_146284_1_.field_146124_l)
        {
            if (p_146284_1_.field_146127_k < 100 && p_146284_1_ instanceof GuiOptionButton)
            {
                this.field_146443_h.setOptionValue(((GuiOptionButton)p_146284_1_).func_146136_c(), 1);
                p_146284_1_.field_146126_j = this.field_146443_h.getKeyBinding(GameSettings.Options.getEnumOptions(p_146284_1_.field_146127_k));
            }

            if (p_146284_1_.field_146127_k == 8675309)
            {
                this.field_146297_k.entityRenderer.func_147705_c();
            }

            if (p_146284_1_.field_146127_k == 101)
            {
                this.field_146297_k.gameSettings.saveOptions();
                this.field_146297_k.func_147108_a(new GuiVideoSettings(this, this.field_146443_h));
            }

            if (p_146284_1_.field_146127_k == 100)
            {
                this.field_146297_k.gameSettings.saveOptions();
                this.field_146297_k.func_147108_a(new GuiControls(this, this.field_146443_h));
            }

            if (p_146284_1_.field_146127_k == 102)
            {
                this.field_146297_k.gameSettings.saveOptions();
                this.field_146297_k.func_147108_a(new GuiLanguage(this, this.field_146443_h, this.field_146297_k.getLanguageManager()));
            }

            if (p_146284_1_.field_146127_k == 103)
            {
                this.field_146297_k.gameSettings.saveOptions();
                this.field_146297_k.func_147108_a(new ScreenChatOptions(this, this.field_146443_h));
            }

            if (p_146284_1_.field_146127_k == 104)
            {
                this.field_146297_k.gameSettings.saveOptions();
                this.field_146297_k.func_147108_a(new GuiSnooper(this, this.field_146443_h));
            }

            if (p_146284_1_.field_146127_k == 200)
            {
                this.field_146297_k.gameSettings.saveOptions();
                this.field_146297_k.func_147108_a(this.field_146441_g);
            }

            if (p_146284_1_.field_146127_k == 105)
            {
                this.field_146297_k.gameSettings.saveOptions();
                this.field_146297_k.func_147108_a(new GuiScreenResourcePacks(this));
            }

            if (p_146284_1_.field_146127_k == 106)
            {
                this.field_146297_k.gameSettings.saveOptions();
                this.field_146297_k.func_147108_a(new GuiScreenOptionsSounds(this, this.field_146443_h));
            }
        }
    }

    // JAVADOC METHOD $$ func_73863_a
    public void drawScreen(int par1, int par2, float par3)
    {
        this.func_146276_q_();
        this.drawCenteredString(this.field_146289_q, this.field_146442_a, this.field_146294_l / 2, 15, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}