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
                spacing = handleBoolean(spacing, x, k, booleanValue, b);
            } else if (specValue instanceof ForgeConfigSpec.ConfigValue) {
                ForgeConfigSpec.ConfigValue<?> configValue = (ForgeConfigSpec.ConfigValue<?>) specValue;
                Object valueObj = configValue.get();
                if (valueObj instanceof Double) {
                    double d = (double) valueObj;
                    ForgeConfigSpec.ConfigValue<Double> doubleConfigValue = (ForgeConfigSpec.ConfigValue<Double>) configValue;
                    spacing = handleDouble(spacing, x, k, valueRange, d, doubleConfigValue);
                } else if (valueObj instanceof Long) {
                    long l = (long) valueObj;
                    ForgeConfigSpec.ConfigValue<Long> longConfigValue = (ForgeConfigSpec.ConfigValue<Long>) configValue;
                    spacing = handleLong(spacing, x, k, valueRange, l, longConfigValue);
                } else if (valueObj instanceof Integer) {
                    int i = (int) valueObj;
                    ForgeConfigSpec.ConfigValue<Integer> integerConfigValue = (ForgeConfigSpec.ConfigValue<Integer>) configValue;
                    spacing = handleInteger(spacing, x, k, valueRange, i, integerConfigValue);
                } else if (valueObj instanceof String) {
                    String str = (String) valueObj;
                    ForgeConfigSpec.ConfigValue<String> stringConfigValue = (ForgeConfigSpec.ConfigValue<String>) configValue;
                    spacing = handleString(spacing, x, k, str, stringConfigValue);
                } else {
                    LOGGER.warn("Couldn't handle property '{}'", valueObj);
                }
            }
        }
        return spacing;
    }

    protected int handleBoolean(int spacing, int x, String key, ForgeConfigSpec.BooleanValue booleanValue, Boolean value) {
        ExtendedButton switchButton = new ExtendedButton(x, spacing, font.getStringWidth(key + value.toString()) + 30, 18, key + ": " + value.toString(), (IPressHandler<ExtendedButton>) button -> {
            booleanValue.set(!booleanValue.get());
            button.setMessage(key + ": " + booleanValue.get());
        });
        addButton(switchButton);
        spacing += 20;
        return spacing;
    }

    protected int handleString(int spacing, int x, String key, String value, ForgeConfigSpec.ConfigValue<String> stringConfigValue) {
        TextFieldWidget textField = new TextFieldWidget(font, x + font.getStringWidth(key), spacing, width - font.getStringWidth(key) - 20, 18, key);
        textField.setText(value);
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
        return spacing;
    }

    protected int handleInteger(int spacing, int x, String key, Object valueRange, int value, ForgeConfigSpec.ConfigValue<Integer> integerConfigValue) {
        TextFieldWidget textField = new TextFieldWidget(font, x + font.getStringWidth(key), spacing, width - font.getStringWidth(key) - 20, 18, key);
        textField.setValidator(s -> s.isEmpty() || StringUtils.isNumeric(s));
        textField.setText(String.valueOf(value));
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
        return spacing;
    }

    protected int handleLong(int spacing, int x, String key, Object valueRange, long value, ForgeConfigSpec.ConfigValue<Long> longConfigValue) {
        TextFieldWidget textField = new TextFieldWidget(font, x + font.getStringWidth(key), spacing, width - font.getStringWidth(key) - 20, 18, key);
        textField.setValidator(s -> s.isEmpty() || StringUtils.isNumeric(s));
        textField.setText(String.valueOf(value));
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
        return spacing;
    }

    protected int handleDouble(int spacing, int x, String key, Object valueRange, double value, ForgeConfigSpec.ConfigValue<Double> doubleConfigValue) {
        TextFieldWidget textField = new TextFieldWidget(font, x + font.getStringWidth(key), spacing, width - font.getStringWidth(key) - 20, 18, key);
        textField.setValidator(s -> s.isEmpty() || StringUtils.isNumeric(s) || s.matches("[0-9]*\\.?[0-9]*"));
        textField.setText(String.valueOf(value));
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
        return spacing;
    }

    @Override
    public void render(int mouseX, int mouseY, float p_render_3_) {
        renderBackground();
        drawCenteredString(font, title.getFormattedText(), width / 2, 6, 0xffffff);
        super.render(mouseX, mouseY, p_render_3_);
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
