package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class needs to handle holding state for the displayed config.
 * It should be the "master" class that everything passes through.
 * Duties:
 * - To load the stuff for the config file
 * - To display a message to the user when the config file on the system is changed
 * and the displayed version needs to be updated (display a list of the user's changes
 * and wait for them to click reload, maybe in the future allow them to merge their changes)
 * - To manage the scrollable screen displaying the widgets
 */
public class ConfigScreen extends Screen {

    private final Screen parentScreen;
    private final ConfigCategoryInfo categoryInfo;
    private Button resetButton;
    private ConfigElementList configElementList;

    public ConfigScreen(Screen parentScreen, ITextComponent titleIn, ConfigCategoryInfo categoryInfo) {
        super(titleIn);
        this.parentScreen = parentScreen;
        this.categoryInfo = categoryInfo;
    }

    public ConfigScreen(Screen screen, StringTextComponent testing, ModInfo selectedMod) {
        this(screen, testing, makeCategoryInfo(selectedMod));
    }

    private static ConfigCategoryInfo makeCategoryInfo(ModInfo selectedMod) {
        Collection<ModConfig> configs = ConfigTracker.INSTANCE.getConfigsForMod(selectedMod.getModId()).values();
        Map<String, ModConfig> mapped = new HashMap<>();
        for (ModConfig config : configs)
            mapped.put(config.getType().name().toLowerCase(), config);
        return ConfigCategoryInfo.of(
                mapped::keySet,
                key -> mapped.get(key).getSpec().getValues().get(key),
                key -> mapped.get(key).getSpec().getSpec().get(key)
        );
    }

    @Override
    // init
    protected void func_231160_c_() {
        super.func_231160_c_();
        configElementList = new ConfigElementList(this, field_230706_i_, categoryInfo);
        // children.add
        field_230705_e_.add(configElementList);
        resetButton = func_230480_a_(new Button(field_230708_k_ / 2 - 155, field_230709_l_ - 29, 150, 20, new TranslationTextComponent("reset.config"), (p_213125_1_) -> {
//            for(KeyBinding keybinding : this.gameSettings.keyBindings) {
//                keybinding.setToDefault();
//            }
//            KeyBinding.resetKeyBindingArrayAndHash();
        }));
        // Add the "done" button
        func_230480_a_(new Button(field_230708_k_ / 2 - 155 + 160, field_230709_l_ - 29, 150, 20, DialogTexts.field_240632_c_, b -> field_230706_i_.displayGuiScreen(parentScreen)));
    }

    @Override
    // render
    public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // renderBackground(matrixStack)
        func_230446_a_(matrixStack);
        // configEntryList.render()
        configElementList.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
        // drawCenteredString(matrixStack, fontRenderer, title, width / 2, 8, 0xFFFFFF)
        func_238472_a_(matrixStack, field_230712_o_, field_230704_d_, this.field_230708_k_ / 2, 8, 0xFFFFFF);

//        modConfig.getSpec().

//        resetButton.field_230693_o_ = anyConfigElementNotDefault;

        // Renders our widgets for us
        // super.render(matrixStack, mouseX, mouseY, partialTicks);
        super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
    }

    // config.getSpec().getValues().get("client.showLoadWarnings").get() -> Boolean

    ForgeConfigSpec.ConfigValue<?> getSpecConfigValue(ModConfig config, String path) {
        return config.getSpec().getValues().get(path);
    }

    protected void resetConfig() {
        // Delegate resetting to the parent screen if we are a sub-screen (list/sub config)
        if (parentScreen instanceof ConfigScreen) {
            ((ConfigScreen) parentScreen).resetConfig();
        } else {
            // TODO: Reset
        }
    }

    @Override
    // closeScreen
    public void func_231175_as__() {
        field_230706_i_.displayGuiScreen(parentScreen);
    }
}
