/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.recipebook.AbstractRecipeBookGui;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeRecipeBookCategory;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.common.extensions.IForgeRecipeBookCategory;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

@Mod("base_crafting_book_test")
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class CraftingBookTest {
    @ObjectHolder("rare")
    public static final ForgeRecipeBookCategory<?> rare = null;
    @ObjectHolder("test")
    public static final ForgeRecipeBookCategory<?> test = null;
    @ObjectHolder("test_uncommon")
    public static final ForgeRecipeBookCategory<?> test_uncommon = null;

    @ObjectHolder("test_block")
    public static final Block test_block = null;
    @ObjectHolder("test_tile")
    public static final TileEntityType<TestTile> test_tile = null;
    @ObjectHolder("test_container")
    public static final ContainerType<TestContainer> test_container = null;
    @ObjectHolder("test_recipe")
    public static final IRecipeSerializer<?> test_recipe = null;

    @SubscribeEvent
    public static void registerRecipe(@Nonnull RegistryEvent.Register<IRecipeSerializer<?>> event) {
        IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();
        registry.register(new TestRecipeSerializer<>(TestRecipe::new, 200).setRegistryName("base_crafting_book_test", "test_recipe"));
    }

    @SubscribeEvent
    public static void registerForgeRecipeBookCategory(RegistryEvent.Register<IForgeRecipeBookCategory<?>> event) {
        // search category for custom machine
        event.getRegistry().register(new ForgeRecipeBookCategory<IRecipeType<?>>(true, TestRecipe.test, new ItemStack(Items.COMPASS)).setRegistryName("base_crafting_book_test", "test"));
        // custom machine category showing only uncommon recipes
        event.getRegistry().register(new ForgeRecipeBookCategory<IRecipeType<?>>(false,
                TestRecipe.test, recipe -> recipe.getRecipeOutput().getRarity() == Rarity.UNCOMMON, new ItemStack(Items.GOLDEN_AXE), new ItemStack(Items.STONE_PICKAXE))
                .setRegistryName("base_crafting_book_test", "test_uncommon"));
        // added new category to crafting table & player displaying only rare output items recipes
        event.getRegistry().register(new ForgeRecipeBookCategory<IRecipeType<?>>(false, IRecipeType.CRAFTING, recipe -> recipe.getRecipeOutput().getRarity() == Rarity.RARE, new ItemStack(Items.BEACON))
                .setRegistryName("base_crafting_book_test", "rare"));
    }

    @SubscribeEvent
    public static void registerBlocks(@Nonnull RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new TestBlock().setRegistryName("base_crafting_book_test", "test_block"));
    }

    @SubscribeEvent
    public static void registerItems(@Nonnull RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new BlockItem(test_block, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("base_crafting_book_test", "test_block"));
    }

    @SubscribeEvent
    public static void registerTileEntity(@Nonnull RegistryEvent.Register<TileEntityType<?>> event) {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
        registry.register(TileEntityType.Builder.create(TestTile::new, test_block)
                .build(null).setRegistryName("base_crafting_book_test", "test_tile"));
    }

    @SubscribeEvent
    public static void registerContainer(@Nonnull RegistryEvent.Register<ContainerType<?>> event) {
        ContainerType<TestContainer> test_container = IForgeContainerType.create(TestContainer::new);
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
        registry.register(test_container.setRegistryName("base_crafting_book_test", "test_container"));
        ScreenManager.registerFactory(test_container, TestGui::new);
    }

    private static class TestRecipeBookGui extends AbstractRecipeBookGui {

        @Override
        protected boolean func_212962_b() {
            return this.recipeBook.isFilteringCraftable();
        }

        @Override
        protected void func_212959_a(boolean p_212959_1_) {
            this.recipeBook.setFilteringCraftable(p_212959_1_);
        }

        @Override
        protected boolean func_212963_d() {
            return this.recipeBook.isGuiOpen();
        }

        @Override
        protected void func_212957_c(boolean p_212957_1_) {
            this.recipeBook.setGuiOpen(p_212957_1_);
        }

        @Override
        protected String func_212960_g() {
            return "gui.recipebook.toggleRecipes.craftable";
        }

        @Override
        protected Set<Item> func_212958_h() {
            return AbstractFurnaceTileEntity.getBurnTimes().keySet();
        }
    }

    private static class TestGui extends ContainerScreen<TestContainer> implements IRecipeShownListener {
        private static final ResourceLocation RECIPE_BUTTON = new ResourceLocation("textures/gui/recipe_button.png");
        private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/container/furnace.png");
        private final AbstractRecipeBookGui recipeBookGui;
        private boolean narrowWidth;

        private TestGui(TestContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
            super(screenContainer, inv, titleIn);
            this.recipeBookGui = new TestRecipeBookGui();
        }

        @Override
        public void init() {
            super.init();
            this.narrowWidth = this.width < 379;
            this.recipeBookGui.func_201520_a(this.width, this.height, this.minecraft, this.narrowWidth, this.container);
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.narrowWidth, this.width, this.xSize);
            this.addButton((new ImageButton(this.guiLeft + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON, (p_214087_1_) -> {
                this.recipeBookGui.func_201518_a(this.narrowWidth);
                this.recipeBookGui.toggleVisibility();
                this.guiLeft = this.recipeBookGui.updateScreenPosition(this.narrowWidth, this.width, this.xSize);
                ((ImageButton)p_214087_1_).setPosition(this.guiLeft + 20, this.height / 2 - 49);
            })));
        }

        @Override
        public void tick() {
            super.tick();
            this.recipeBookGui.tick();
        }

        @Override
        public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
            this.renderBackground();
            if (this.recipeBookGui.isVisible() && this.narrowWidth) {
                this.drawGuiContainerBackgroundLayer(p_render_3_, p_render_1_, p_render_2_);
                this.recipeBookGui.render(p_render_1_, p_render_2_, p_render_3_);
            } else {
                this.recipeBookGui.render(p_render_1_, p_render_2_, p_render_3_);
                super.render(p_render_1_, p_render_2_, p_render_3_);
                this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, true, p_render_3_);
            }

            this.renderHoveredToolTip(p_render_1_, p_render_2_);
            this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, p_render_1_, p_render_2_);
        }

        @Override
        protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
            String s = this.title.getFormattedText();
            this.font.drawString(s, (float)(this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, 4210752);
            this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
        }

        @Override
        protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bindTexture(BACKGROUND);
            int i = this.guiLeft;
            int j = this.guiTop;
            this.blit(i, j, 0, 0, this.xSize, this.ySize);
        }

        @Override
        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            if (this.recipeBookGui.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
                return true;
            } else {
                return this.narrowWidth && this.recipeBookGui.isVisible() ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
            }
        }

        @Override
        protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
            super.handleMouseClick(slotIn, slotId, mouseButton, type);
            this.recipeBookGui.slotClicked(slotIn);
        }

        @Override
        public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
            return this.recipeBookGui.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? false : super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }

        @Override
        protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
            boolean flag = p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.xSize) || p_195361_3_ >= (double)(p_195361_6_ + this.ySize);
            return this.recipeBookGui.func_195604_a(p_195361_1_, p_195361_3_, this.guiLeft, this.guiTop, this.xSize, this.ySize, p_195361_7_) && flag;
        }

        @Override
        public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
            return this.recipeBookGui.charTyped(p_charTyped_1_, p_charTyped_2_) ? true : super.charTyped(p_charTyped_1_, p_charTyped_2_);
        }

        @Override
        public void recipesUpdated() {
            this.recipeBookGui.recipesUpdated();
        }

        @Override
        public RecipeBookGui func_194310_f() {
            return this.recipeBookGui;
        }

        @Override
        public void removed() {
            this.recipeBookGui.removed();
            super.removed();
        }
    }

    private static class TestBlock extends Block {
        private TestBlock() {
            super(Properties.create(Material.ROCK));
        }

        @Override
        public boolean hasTileEntity(BlockState state) {
            return true;
        }

        @Nullable
        @Override
        public TileEntity createTileEntity(BlockState state, IBlockReader world) {
            return new TestTile();
        }
        @SuppressWarnings("deprecation")
        @Override
        public boolean onBlockActivated(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos, @Nonnull PlayerEntity player,
                                        @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
            TestTile tile = (TestTile) worldIn.getTileEntity(pos);

            if (tile != null) {
                if (!worldIn.isRemote) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, tile, tile.getPos());
                }
                return true;
            } else {
                throw new IllegalStateException("Named container provider is missing");
            }
        }
    }

    private static class TestTile extends TileEntity implements INamedContainerProvider {

        private TestTile() {
            super(test_tile);
        }

        @Override
        public ITextComponent getDisplayName() {
            assert getType().getRegistryName() != null;
            return new StringTextComponent(getType().getRegistryName().getPath());
        }

        @Nullable
        @Override
        public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
            return new TestContainer(p_createMenu_1_, pos, world, p_createMenu_2_, p_createMenu_3_);
        }

        @Nullable
        @Override
        public SUpdateTileEntityPacket getUpdatePacket() {
            return new SUpdateTileEntityPacket(getPos(), getType().hashCode(), getUpdateTag());
        }

        @Nonnull
        @Override
        public CompoundNBT getUpdateTag() {
            return write(new CompoundNBT());
        }

        @Override
        public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
            super.onDataPacket(net, pkt);
            read(pkt.getNbtCompound());
        }
    }

    private static class TestContainer extends RecipeBookContainer<IInventory> {
        protected final World world;
        private final IInventory furnaceInventory;

        private TestContainer(int windowId, @Nonnull PlayerInventory inv, @Nonnull PacketBuffer extraData) {
            this(windowId, extraData.readBlockPos(), Minecraft.getInstance().world, inv, Minecraft.getInstance().player);
        }

        private TestContainer(int id, @Nonnull BlockPos pos, @Nonnull World world, @Nonnull PlayerInventory inventory, @Nonnull PlayerEntity player) {
            super(test_container, id);
            IInventory furnaceInventoryIn = new Inventory(3);
            assertInventorySize(furnaceInventoryIn, 3);
            this.furnaceInventory = furnaceInventoryIn;
            this.world = inventory.player.world;
            this.addSlot(new Slot(furnaceInventoryIn, 0, 56, 17));
            this.addSlot(new Slot(furnaceInventoryIn, 1, 56, 53));
            this.addSlot(new FurnaceResultSlot(inventory.player, furnaceInventoryIn, 2, 116, 35));

            for(int i = 0; i < 3; ++i) {
                for(int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
                }
            }

            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
            }
        }

        @Override
        public void func_201771_a(RecipeItemHelper p_201771_1_) {
            if (this.furnaceInventory instanceof IRecipeHelperPopulator) {
                ((IRecipeHelperPopulator)this.furnaceInventory).fillStackedContents(p_201771_1_);
            }
        }

        @Override
        public void clear() {
            this.furnaceInventory.clear();
        }

        @Override
        public boolean matches(IRecipe<? super IInventory> recipeIn) {
            return recipeIn.matches(this.furnaceInventory, this.world);
        }

        @Override
        public int getOutputSlot() {
            return 2;
        }

        @Override
        public int getWidth() {
            return 1;
        }

        @Override
        public int getHeight() {
            return 1;
        }

        @Override
        public int getSize() {
            return 3;
        }

        @Override
        public IRecipeType<?> getRecipeType() {
            return TestRecipe.test;
        }

        @Override
        public boolean canInteractWith(PlayerEntity playerIn) {
            return this.furnaceInventory.isUsableByPlayer(playerIn);
        }

        @Override
        public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
            ItemStack itemstack = ItemStack.EMPTY;
            Slot slot = this.inventorySlots.get(index);
            if (slot != null && slot.getHasStack()) {
                ItemStack itemstack1 = slot.getStack();
                itemstack = itemstack1.copy();
                if (index == 2) {
                    if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                        return ItemStack.EMPTY;
                    }

                    slot.onSlotChange(itemstack1, itemstack);
                } else if (index != 1 && index != 0) {
                    if (this.func_217057_a(itemstack1)) {
                        if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (AbstractFurnaceTileEntity.isFuel(itemstack1)) {
                        if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 3 && index < 30) {
                        if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }

                if (itemstack1.isEmpty()) {
                    slot.putStack(ItemStack.EMPTY);
                } else {
                    slot.onSlotChanged();
                }

                if (itemstack1.getCount() == itemstack.getCount()) {
                    return ItemStack.EMPTY;
                }

                slot.onTake(playerIn, itemstack1);
            }

            return itemstack;
        }

        protected boolean func_217057_a(ItemStack p_217057_1_) {
            return this.world.getRecipeManager().getRecipe(TestRecipe.test, new Inventory(p_217057_1_), this.world).isPresent();
        }
    }

    private static class TestRecipe extends AbstractCookingRecipe {
        private static final IRecipeType<TestRecipe> test = IRecipeType.register("test");

        private TestRecipe(ResourceLocation idIn, String groupIn, Ingredient ingredientIn, ItemStack resultIn, float experienceIn, int cookTimeIn) {
            super(test, idIn, groupIn, ingredientIn, resultIn, experienceIn, cookTimeIn);
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return test_recipe;
        }
    }

    private static class TestRecipeSerializer<T extends AbstractCookingRecipe> extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
        private final int field_222178_t;
        private final IFactory<T> field_222179_u;

        private TestRecipeSerializer(IFactory<T> p_i50025_1_, int p_i50025_2_) {
            this.field_222178_t = p_i50025_2_;
            this.field_222179_u = p_i50025_1_;
        }

        @Override
        public T read(ResourceLocation recipeId, JsonObject json) {
            String s = JSONUtils.getString(json, "group", "");
            JsonElement jsonelement = (JsonElement)(JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient"));
            Ingredient ingredient = Ingredient.deserialize(jsonelement);
            //Forge: Check if primitive string to keep vanilla or a object which can contain a count field.
            if (!json.has("result")) throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
            ItemStack itemstack;
            if (json.get("result").isJsonObject()) itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            else {
                String s1 = JSONUtils.getString(json, "result");
                ResourceLocation resourcelocation = new ResourceLocation(s1);
                itemstack = new ItemStack(Registry.ITEM.getValue(resourcelocation).orElseThrow(() -> {
                    return new IllegalStateException("Item: " + s1 + " does not exist");
                }));
            }
            float f = JSONUtils.getFloat(json, "experience", 0.0F);
            int i = JSONUtils.getInt(json, "cookingtime", this.field_222178_t);
            return this.field_222179_u.create(recipeId, s, ingredient, itemstack, f, i);
        }

        @Override
        public T read(ResourceLocation recipeId, PacketBuffer buffer) {
            String s = buffer.readString(32767);
            Ingredient ingredient = Ingredient.read(buffer);
            ItemStack itemstack = buffer.readItemStack();
            float f = buffer.readFloat();
            int i = buffer.readVarInt();
            return this.field_222179_u.create(recipeId, s, ingredient, itemstack, f, i);
        }

        @Override
        public void write(PacketBuffer buffer, T recipe) {
            buffer.writeString(recipe.getGroup());
            recipe.getIngredients().get(0).write(buffer);
            buffer.writeItemStack(recipe.getRecipeOutput());
            buffer.writeFloat(recipe.getExperience());
            buffer.writeVarInt(recipe.getCookTime());
        }

        interface IFactory<T extends AbstractCookingRecipe> {
            T create(ResourceLocation p_create_1_, String p_create_2_, Ingredient p_create_3_, ItemStack p_create_4_, float p_create_5_, int p_create_6_);
        }
    }
}
