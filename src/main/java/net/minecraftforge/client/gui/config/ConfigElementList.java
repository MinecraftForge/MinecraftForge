package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ConfigElementList extends AbstractOptionList<ConfigElementList.ConfigElement> {
    private final ConfigScreen configScreen;
    private int maxListLabelWidth;

    public ConfigElementList(ConfigScreen configScreen, Minecraft mcIn, ConfigCategoryInfo info) {
        super(mcIn, configScreen.field_230708_k_ + 45, configScreen.field_230709_l_, 43, configScreen.field_230709_l_ - 32, 20);
        this.configScreen = configScreen;
        for (String key : info.elements())
            this.func_230513_b_(createConfigElement(key, info));
//        ConfigTracker.INSTANCE.getConfigsForMod("forge").get(ModConfig.Type.CLIENT).getSpec().getValues().valueMap().forEach((s, o) -> {
//            if (o instanceof UnmodifiableConfig) {
//                System.out.println(s + ":");
//                // Create screen button
//                // printEverything((UnmodifiableConfig) o, indent + 1);
//            } else {
//                final ForgeConfigSpec.ConfigValue<?> configValue = (ForgeConfigSpec.ConfigValue<?>) o;
//                System.out.println(s + " = " + configValue.get());
//                this.func_230513_b_(new ConfigElement(configValue.get().));
//            }
//        });
    }

    private ConfigElement createConfigElement(String key, ConfigCategoryInfo categoryInfo) {
        Object raw = categoryInfo.getValue(key);
        if (raw instanceof UnmodifiableConfig) {
            UnmodifiableConfig value = (UnmodifiableConfig) raw;
            UnmodifiableConfig valueInfo = (UnmodifiableConfig) categoryInfo.getSpec(key);
//            for (String key : value.valueMap().keySet())
//                printValue(path + '.' + key, values, infos);
            return new ConfigElementImpl(key, "") {
                final Button open;
                {
                    ConfigCategoryInfo valueCategoryInfo = new ConfigCategoryInfo(key, value, valueInfo) {
                        @Override
                        public Object getValue(String key) {
                            return value.get(key);
                        }

                        @Override
                        public Object getSpec(String key) {
                            return valueInfo.get(key);
                        }
                    };
                    open = new Button(0, 0, 50, 20, new StringTextComponent(key), (p_214387_2_) -> {
                        ConfigScreen screen = new ConfigScreen(ConfigElementList.this.configScreen, new StringTextComponent(key), valueCategoryInfo);
                        ConfigElementList.this.field_230668_b_.displayGuiScreen(screen);
                    });
                }

                @Override
                // render
                public void func_230432_a_(MatrixStack matrixStack, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int mouseX, int mouseY, boolean p_230432_9_, float partialTicks) {
                    super.func_230432_a_(matrixStack, p_230432_2_, p_230432_3_, p_230432_4_, p_230432_5_, p_230432_6_, mouseX, mouseY, p_230432_9_, partialTicks);
                    open.field_230690_l_ = p_230432_4_ + 190 + 20 - 120;
                    open.field_230691_m_ = p_230432_3_;
                    open.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
                }

                @Override
                public List<? extends IGuiEventListener> func_231039_at__() {
                    return ImmutableList.of(btnReset, open);
                }
            };
        } else {
            ConfigValue<?> value = (ConfigValue<?>) raw;
            ValueSpec valueInfo = (ValueSpec) categoryInfo.getSpec(key);
            String name = new TranslationTextComponent(valueInfo.getTranslationKey()).getString();
            return new ConfigElementImpl(name + ": " + value.get(), valueInfo.getComment());
        }
    }

    @Override
    protected int func_230952_d_() {
        return super.func_230952_d_() + 15 + 20;
    }

    @Override
    public int func_230949_c_() {
        return super.func_230949_c_() + 32;
    }

    @OnlyIn(Dist.CLIENT)
    public abstract static class ConfigElement extends AbstractOptionList.Entry<ConfigElement> {
        String translatedName;
        String description;

        public ConfigElement(String translatedName, String description) {
            this.translatedName = translatedName;
            this.description = description;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class ConfigElementImpl extends ConfigElement {
        private final ITextComponent nameComponent;
        //        private final Button btnChangeKeyBinding;
        protected final Button btnReset;

        private ConfigElementImpl(String translatedName, String description) {
            super(translatedName, description);
            if (maxListLabelWidth < translatedName.length())
                maxListLabelWidth = translatedName.length();
            this.nameComponent = new StringTextComponent(translatedName);
//         this.btnChangeKeyBinding = new Button(0, 0, 75 + 20 /*Forge: add space*/, 20, p_i232281_3_, (p_214386_2_) -> {
////            ConfigEntryList.this.configScreen.buttonId = p_i232281_2_;
//         }) {
//            protected IFormattableTextComponent func_230442_c_() {
//               return p_i232281_2_.isInvalid() ? new TranslationTextComponent("narrator.controls.unbound", p_i232281_3_) : new TranslationTextComponent("narrator.controls.bound", p_i232281_3_, super.func_230442_c_());
//            }
//         };
            this.btnReset = new Button(0, 0, 50, 20, new TranslationTextComponent("controls.reset"), (p_214387_2_) -> {
                System.out.println("Reset " + translatedName);
//            keybinding.setToDefault();
//            ConfigEntryList.this.field_230668_b_.gameSettings.setKeyBindingCode(p_i232281_2_, p_i232281_2_.getDefault());
//            KeyBinding.resetKeyBindingArrayAndHash();
            });
//            {
//                @Override
//                protected IFormattableTextComponent func_230442_c_() {
//                    return new TranslationTextComponent("narrator.controls.reset", translatedName);
//                }
//            };
        }

        @Override
        // render
        public void func_230432_a_(MatrixStack matrixStack, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int mouseX, int mouseY, boolean p_230432_9_, float partialTicks) {
//            boolean flag = ConfigEntryList.this.configScreen.buttonId == this.keybinding;
            ConfigElementList.this.field_230668_b_.fontRenderer.func_243248_b(matrixStack, this.nameComponent, (float) (p_230432_4_ + /*90*/ - ConfigElementList.this.maxListLabelWidth), (float) (p_230432_3_ + p_230432_6_ / 2 - 9 / 2), 0xffffff);
            this.btnReset.field_230690_l_ = p_230432_4_ + 190 + 20;
            this.btnReset.field_230691_m_ = p_230432_3_;
//            this.btnReset.field_230693_o_ = !this.keybinding.isDefault();
            this.btnReset.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
//            this.btnChangeKeyBinding.field_230690_l_ = p_230432_4_ + 105;
//            this.btnChangeKeyBinding.field_230691_m_ = p_230432_3_;
//            this.btnChangeKeyBinding.func_238482_a_(this.keybinding.func_238171_j_());
//            boolean flag1 = false;
//            boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G
//            if (!this.keybinding.isInvalid()) {
//                for (KeyBinding keybinding : ConfigEntryList.this.field_230668_b_.gameSettings.keyBindings) {
//                    if (keybinding != this.keybinding && this.keybinding.conflicts(keybinding)) {
//                        flag1 = true;
//                        keyCodeModifierConflict &= keybinding.hasKeyCodeModifierConflict(keybinding);
//                    }
//                }
//            }
//
//            if (flag) {
//                this.btnChangeKeyBinding.func_238482_a_((new StringTextComponent("> ")).func_230529_a_(this.btnChangeKeyBinding.func_230458_i_().func_230532_e_().func_240699_a_(TextFormatting.YELLOW)).func_240702_b_(" <").func_240699_a_(TextFormatting.YELLOW));
//            } else if (flag1) {
//                this.btnChangeKeyBinding.func_238482_a_(this.btnChangeKeyBinding.func_230458_i_().func_230532_e_().func_240699_a_(keyCodeModifierConflict ? TextFormatting.GOLD : TextFormatting.RED));
//            }
//
//            this.btnChangeKeyBinding.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
        }

        @Override
        public List<? extends IGuiEventListener> func_231039_at__() {
            return ImmutableList.of(this.btnReset);
        }

//      public boolean func_231044_a_(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
//         if (this.btnChangeKeyBinding.func_231044_a_(p_231044_1_, p_231044_3_, p_231044_5_)) {
//            return true;
//         } else {
//            return this.btnReset.func_231044_a_(p_231044_1_, p_231044_3_, p_231044_5_);
//         }
//      }
//
//      public boolean func_231048_c_(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
//         return this.btnChangeKeyBinding.func_231048_c_(p_231048_1_, p_231048_3_, p_231048_5_) || this.btnReset.func_231048_c_(p_231048_1_, p_231048_3_, p_231048_5_);
//      }
    }
}
