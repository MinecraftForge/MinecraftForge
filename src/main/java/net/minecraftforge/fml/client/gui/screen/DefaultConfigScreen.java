package net.minecraftforge.fml.client.gui.screen;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.client.gui.IPressHandler;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Default screen for configs from the mod menu. Can handle boolean, integer, long, double and string properties.
 */
@OnlyIn(Dist.CLIENT)
public class DefaultConfigScreen extends Screen {
    protected Screen previousScreen;
    protected ForgeConfigSpec configSpec;
    protected static final Logger LOGGER = LogManager.getLogger();

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
    @SuppressWarnings("unchecked")
    protected int buildElements(int spacing, int x, UnmodifiableConfig actualValues, String k, Object v) {
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

                Object valueObj = configValue.get();
                if (valueObj instanceof Double) {
                    double d = (double) valueObj;
                    ForgeConfigSpec.ConfigValue<Double> doubleConfigValue = (ForgeConfigSpec.ConfigValue<Double>) configValue;

                    TextFieldWidget textField = new TextFieldWidget(font, x + font.getStringWidth(k), spacing, width - font.getStringWidth(k) - 20, 18, k);
                    textField.setValidator(s -> s.isEmpty() || StringUtils.isNumeric(s) || s.matches("[0-9]*\\.?[0-9]*"));
                    textField.setText(String.valueOf(d));
                    textField.setResponder(s -> {
                        try {
                            if (!s.isEmpty()) {

                                double dd = Double.parseDouble(s);
                                if (valueRange != null) {
                                    String[] range = valueRange.toString().split(" ~ ");
                                    double min = Double.parseDouble(range[0]);
                                    double max = Double.parseDouble(range[1]);
                                    if (dd < min)
                                        dd = min;
                                    if (dd > max)
                                        dd = max;
                                }
                                doubleConfigValue.set(dd);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    addButton(textField);
                    spacing += 22;

                } else if (valueObj instanceof Long) {
                    long l = (long) valueObj;
                    ForgeConfigSpec.ConfigValue<Long> longConfigValue = (ForgeConfigSpec.ConfigValue<Long>) configValue;

                    TextFieldWidget textField = new TextFieldWidget(font, x + font.getStringWidth(k), spacing, width - font.getStringWidth(k) - 20, 18, k);
                    textField.setValidator(s -> s.isEmpty() || StringUtils.isNumeric(s));
                    textField.setText(String.valueOf(l));
                    textField.setResponder(s -> {
                        if (!s.isEmpty()) {
                            long ll = Long.parseLong(s);
                            if (valueRange != null) {
                                String[] range = valueRange.toString().split(" ~ ");
                                long min = Long.parseLong(range[0]);
                                long max = Long.parseLong(range[1]);
                                if (ll < min)
                                    ll = min;
                                if (ll > max)
                                    ll = max;
                            }
                            longConfigValue.set(ll);
                        }
                    });
                    addButton(textField);
                    spacing += 22;
                } else if (valueObj instanceof Integer) {
                    int i = (int) valueObj;
                    ForgeConfigSpec.ConfigValue<Integer> integerConfigValue = (ForgeConfigSpec.ConfigValue<Integer>) configValue;
                    TextFieldWidget textField = new TextFieldWidget(font, x + font.getStringWidth(k), spacing, width - font.getStringWidth(k) - 20, 18, k);
                    textField.setValidator(s -> s.isEmpty() || StringUtils.isNumeric(s));
                    textField.setText(String.valueOf(i));
                    textField.setResponder(s -> {
                        if (!s.isEmpty()) {
                            try {
                                int ii = Integer.parseInt(s);
                                if (valueRange != null) {
                                    String[] range = valueRange.toString().split(" ~ ");
                                    int min = Integer.parseInt(range[0]);
                                    int max = Integer.parseInt(range[1]);
                                    if (ii < min)
                                        ii = min;
                                    if (ii > max)
                                        ii = max;
                                }
                                integerConfigValue.set(ii);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    addButton(textField);
                    spacing += 22;
                } else if (valueObj instanceof String) {
                    String str = (String) valueObj;
                    ForgeConfigSpec.ConfigValue<String> stringConfigValue = (ForgeConfigSpec.ConfigValue<String>) configValue;
                    TextFieldWidget textField = new TextFieldWidget(font, x + font.getStringWidth(k), spacing, width - font.getStringWidth(k) - 20, 18, k);
                    textField.setText(str);
                    textField.setResponder(s -> {
                        if (!s.isEmpty()) {
                            try {
                                stringConfigValue.set(s);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    addButton(textField);
                    spacing += 22;
                } else {
                    LOGGER.warn("Couldn't handle property '{}'", valueObj);
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
