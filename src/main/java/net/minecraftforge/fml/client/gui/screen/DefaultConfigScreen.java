package net.minecraftforge.fml.client.gui.screen;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.client.gui.IPressHandler;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class DefaultConfigScreen extends Screen {
    protected Screen previousScreen;
    protected ForgeConfigSpec configSpec;

    public DefaultConfigScreen(ITextComponent titleIn, Screen previousScreen, ForgeConfigSpec forgeConfigSpec) {
        super(titleIn);
        this.previousScreen = previousScreen;
        this.configSpec = forgeConfigSpec;
    }

    @Override
    protected void init() {
        super.init();
        Map<String, Object> specMap = configSpec.getSpec().valueMap();
        Map<String, Object> valueMap = configSpec.getValues().valueMap();
        int spacing = 30;
        int x = 6;
        for (String key : specMap.keySet()) {
            Object value = specMap.get(key);
            //nested configs
            if (value instanceof UnmodifiableConfig) {
                UnmodifiableConfig unmodifiableConfig = (UnmodifiableConfig) value;
                Map<String, Object> entries = unmodifiableConfig.valueMap();
                UnmodifiableConfig actualValues = (UnmodifiableConfig) valueMap.get(key);
                for (String k : entries.keySet()) {
                    Object v = entries.get(k);
                    spacing = buildElements(spacing, x, actualValues, k, v);
                }
            } else {
                //immediate config
                spacing = buildElements(spacing, x, configSpec.getValues(), key, value);
            }
        }
    }

    /**
     * Builds screen elements from the config
     *
     * @return vertical offset for the next screen element
     */
    private int buildElements(int spacing, int x, UnmodifiableConfig actualValues, String k, Object v) {
        if (v instanceof ForgeConfigSpec.ValueSpec) {
            ForgeConfigSpec.ValueSpec valueSpec = (ForgeConfigSpec.ValueSpec) v;
            Object specValue = actualValues.get(k);
            Object valueRange = valueSpec.getRange();
            if (specValue instanceof ForgeConfigSpec.BooleanValue) {
                ForgeConfigSpec.BooleanValue booleanValue = (ForgeConfigSpec.BooleanValue) specValue;
                Boolean b = booleanValue.get();
                ExtendedButton switchButton = new ExtendedButton(x, spacing, font.getStringWidth(k + b.toString()) + 30, 18, k + ": " + b.toString(), (IPressHandler<ExtendedButton>) button -> {
                    booleanValue.set(!booleanValue.get());
                    button.setMessage(k + ": " + booleanValue.get());
                });
                addButton(switchButton);
                spacing += 20;
            } else if (specValue instanceof ForgeConfigSpec.ConfigValue) {
                ForgeConfigSpec.ConfigValue<?> configValue = (ForgeConfigSpec.ConfigValue<?>) specValue;
//                System.out.println(configValue);
                Object valueObj = configValue.get();
                if (valueObj instanceof Double) {
                    double d = (double) valueObj;
                    ForgeConfigSpec.ConfigValue<Double> doubleConfigValue = (ForgeConfigSpec.ConfigValue<Double>) configValue;
                    if (valueRange == null) {
                        TextFieldWidget textField = new TextFieldWidget(font, x + font.getStringWidth(k), spacing, width - font.getStringWidth(d + k), 18, k);
                        textField.setValidator(s -> s.isEmpty() || StringUtils.isNumeric(s) || s.matches("[0-9]*\\.?[0-9]*"));
                        textField.setText(String.valueOf(d));
                        textField.setResponder(s -> {
                            try {
                                if (!s.isEmpty()) {
                                    doubleConfigValue.set(Double.parseDouble(s));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        addButton(textField);
                        spacing += 20;
                        ;
                    } else {
//                        System.out.println(valueRange);
                    }
                }
            }
        }
        return spacing;
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        renderBackground();
        drawCenteredString(font, title.getFormattedText(), width / 2, 6, 0xffffff);
        super.render(p_render_1_, p_render_2_, p_render_3_);
        for (Widget button : buttons) {
            if (button instanceof TextFieldWidget) {
                if (!button.getMessage().isEmpty()) {
                    drawString(font, button.getMessage(), button.x - font.getStringWidth(button.getMessage()) - 3, button.y + 6, 0xffffff);
                }
            }
        }
    }

    @Override
    public void onClose() {
        minecraft.displayGuiScreen(previousScreen);
    }
}
