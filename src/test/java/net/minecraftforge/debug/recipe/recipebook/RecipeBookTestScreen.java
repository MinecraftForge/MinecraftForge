package net.minecraftforge.debug.recipe.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

/**
 * Mostly copied from {@link CraftingScreen}
 */
public class RecipeBookTestScreen extends AbstractContainerScreen<RecipeBookTestMenu> implements RecipeUpdateListener
{
    private static final ResourceLocation TEXTURE = RecipeBookExtensionTest.getId("textures/gui/container/recipe_book_test.png");
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
    private boolean widthTooNarrow;

    public RecipeBookTestScreen(RecipeBookTestMenu menu, Inventory inv, Component title)
    {
        super(menu, inv, title);
    }

    @Override
    protected void init()
    {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.titleLabelX = this.imageWidth - this.font.width(Language.getInstance().getVisualOrder(this.title)) - 5;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (button) ->
        {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            ((ImageButton)button).setPosition(this.leftPos + 5, this.height / 2 - 49);
        }));
        this.addWidget(this.recipeBookComponent);
        this.setInitialFocus(this.recipeBookComponent);
    }

    @Override
    protected void containerTick()
    {
        super.containerTick();
        this.recipeBookComponent.tick();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(stack);
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow)
        {
            this.renderBg(stack, partialTicks, mouseX, mouseY);
            this.recipeBookComponent.render(stack, mouseX, mouseY, partialTicks);
        }
        else
        {
            this.recipeBookComponent.render(stack, mouseX, mouseY, partialTicks);
            super.render(stack, mouseX, mouseY, partialTicks);
            this.recipeBookComponent.renderGhostRecipe(stack, this.leftPos, this.topPos, true, partialTicks);
        }
        this.renderTooltip(stack, mouseX, mouseY);
        this.recipeBookComponent.renderTooltip(stack, this.leftPos, this.topPos, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = this.leftPos;
        int y = this.topPos;
        this.blit(stack, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    /**
     * Color gotten from super
     */
    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY)
    {
        this.font.draw(stack, this.title, this.titleLabelX, this.titleLabelY, 4210752);
    }

    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY)
    {
        return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int buttonId)
    {
        if (this.recipeBookComponent.mouseClicked(mouseX, mouseY, buttonId))
        {
            this.setFocused(this.recipeBookComponent);
            return true;
        }
        return this.widthTooNarrow && this.recipeBookComponent.isVisible() || super.mouseClicked(mouseX, mouseY, buttonId);
    }

    protected boolean hasClickedOutside(double mouseX, double mouseY, int x, int y, int buttonIdx)
    {
        boolean flag = mouseX < (double)x || mouseY < (double)y || mouseX >= (double)(x + this.imageWidth) || mouseY >= (double)(y + this.imageHeight);
        return flag && this.recipeBookComponent.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, buttonIdx);
    }

    protected void slotClicked(Slot slot, int mouseX, int mouseY, ClickType clickType)
    {
        super.slotClicked(slot, mouseX, mouseY, clickType);
        this.recipeBookComponent.slotClicked(slot);
    }

    @Override
    public void recipesUpdated()
    {
        this.recipeBookComponent.recipesUpdated();
    }

    @Override
    public void removed()
    {
        this.recipeBookComponent.removed();
        super.removed();
    }

    @Override
    public RecipeBookComponent getRecipeBookComponent()
    {
        return this.recipeBookComponent;
    }
}
