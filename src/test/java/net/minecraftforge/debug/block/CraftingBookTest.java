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
import net.minecraft.client.gui.recipebook.FurnaceRecipeGui;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IngredientNBT;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod("base_crafting_book_test")
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class CraftingBookTest {
    @ObjectHolder("invalid")
    public static final RecipeBookCategories invalid = null;
    @ObjectHolder("test")
    public static final RecipeBookCategories test = null;

    @ObjectHolder("millstone_block")
    public static final Block millstone_block = null;
    @ObjectHolder("millstone_tile")
    public static final TileEntityType<TestTile> millstone_tile = null;
    @ObjectHolder("millstone_container")
    public static final ContainerType<TestContainer> millstone_container = null;
    @ObjectHolder("millstone_recipe")
    public static final IRecipeSerializer<?> millstone_recipe = null;

    @SubscribeEvent
    public static void registerRecipe(@Nonnull RegistryEvent.Register<IRecipeSerializer<?>> event) {
        IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();
        registry.register(new TestSerializer(TestRecipe::new).setRegistryName("base_crafting_book_test", "millstone_recipe"));
    }

    @SubscribeEvent
    public static void registerRecipeBookCategories(RegistryEvent.Register<RecipeBookCategories> event) {
        event.getRegistry().register(new RecipeBookCategories(IRecipeType.CRAFTING).setIcon(new ItemStack(Items.BEACON)).setRegistryName("base_crafting_book_test", "invalid"));
        event.getRegistry().register(new RecipeBookCategories(TestRecipe.millstone).setSearch().setIcon(new ItemStack(Items.BELL)).setRegistryName("base_crafting_book_test", "test"));
    }

    @SubscribeEvent
    public static void registerBlocks(@Nonnull RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new TestBlock().setRegistryName("base_crafting_book_test", "millstone_block"));
    }

    @SubscribeEvent
    public static void registerItems(@Nonnull RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new BlockItem(millstone_block, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS))
                .setRegistryName("base_crafting_book_test", "millstone_block"));
    }

    @SubscribeEvent
    public static void registerTileEntity(@Nonnull RegistryEvent.Register<TileEntityType<?>> event) {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
        registry.register(TileEntityType.Builder.create(TestTile::new, millstone_block)
                .build(null).setRegistryName("base_crafting_book_test", "millstone_tile"));
    }

    @SubscribeEvent
    public static void registerContainer(@Nonnull RegistryEvent.Register<ContainerType<?>> event) {
        ContainerType<TestContainer> millstone_container = IForgeContainerType.create(TestContainer::new);
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
        registry.register(millstone_container.setRegistryName("base_crafting_book_test", "millstone_container"));
        ScreenManager.registerFactory(millstone_container, TestGui::new);
    }


    private static class TestGui extends ContainerScreen<TestContainer> implements IRecipeShownListener {
        private static final ResourceLocation field_214089_l = new ResourceLocation("textures/gui/recipe_button.png");
        public final AbstractRecipeBookGui field_214088_k;
        private boolean field_214090_m;
        private final ResourceLocation field_214091_n;

        public TestGui(TestContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
            super(screenContainer, inv, titleIn);
            this.field_214088_k = new FurnaceRecipeGui();
            this.field_214091_n = new ResourceLocation("textures/gui/container/furnace.png");
        }

        public void init() {
            super.init();
            this.field_214090_m = this.width < 379;
            this.field_214088_k.func_201520_a(this.width, this.height, this.minecraft, this.field_214090_m, this.container);
            this.guiLeft = this.field_214088_k.updateScreenPosition(this.field_214090_m, this.width, this.xSize);
            this.addButton((new ImageButton(this.guiLeft + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, field_214089_l, (p_214087_1_) -> {
                this.field_214088_k.func_201518_a(this.field_214090_m);
                this.field_214088_k.toggleVisibility();
                this.guiLeft = this.field_214088_k.updateScreenPosition(this.field_214090_m, this.width, this.xSize);
                ((ImageButton)p_214087_1_).setPosition(this.guiLeft + 20, this.height / 2 - 49);
            })));
        }

        public void tick() {
            super.tick();
            this.field_214088_k.tick();
        }

        public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
            this.renderBackground();
            if (this.field_214088_k.isVisible() && this.field_214090_m) {
                this.drawGuiContainerBackgroundLayer(p_render_3_, p_render_1_, p_render_2_);
                this.field_214088_k.render(p_render_1_, p_render_2_, p_render_3_);
            } else {
                this.field_214088_k.render(p_render_1_, p_render_2_, p_render_3_);
                super.render(p_render_1_, p_render_2_, p_render_3_);
                this.field_214088_k.renderGhostRecipe(this.guiLeft, this.guiTop, true, p_render_3_);
            }

            this.renderHoveredToolTip(p_render_1_, p_render_2_);
            this.field_214088_k.renderTooltip(this.guiLeft, this.guiTop, p_render_1_, p_render_2_);
        }

        protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
            String s = this.title.getFormattedText();
            this.font.drawString(s, (float)(this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, 4210752);
            this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
        }

        protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bindTexture(this.field_214091_n);
            int i = this.guiLeft;
            int j = this.guiTop;
            this.blit(i, j, 0, 0, this.xSize, this.ySize);
            /*if (((TestContainer)this.container).func_217061_l()) {
                int k = ((TestContainer)this.container).getBurnLeftScaled();
                this.blit(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
            }

            int l = ((TestContainer)this.container).getCookProgressionScaled();
            this.blit(i + 79, j + 34, 176, 14, l + 1, 16);*/
        }

        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            if (this.field_214088_k.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
                return true;
            } else {
                return this.field_214090_m && this.field_214088_k.isVisible() ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
            }
        }

        protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
            super.handleMouseClick(slotIn, slotId, mouseButton, type);
            this.field_214088_k.slotClicked(slotIn);
        }

        public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
            return this.field_214088_k.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? false : super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }

        protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
            boolean flag = p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.xSize) || p_195361_3_ >= (double)(p_195361_6_ + this.ySize);
            return this.field_214088_k.func_195604_a(p_195361_1_, p_195361_3_, this.guiLeft, this.guiTop, this.xSize, this.ySize, p_195361_7_) && flag;
        }

        public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
            return this.field_214088_k.charTyped(p_charTyped_1_, p_charTyped_2_) ? true : super.charTyped(p_charTyped_1_, p_charTyped_2_);
        }

        public void recipesUpdated() {
            this.field_214088_k.recipesUpdated();
        }

        public RecipeBookGui func_194310_f() {
            return this.field_214088_k;
        }

        public void removed() {
            this.field_214088_k.removed();
            super.removed();
        }
    }

    private static class TestBlock extends Block {
        public TestBlock() {
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

        public TestTile() {
            super(millstone_tile);
        }

        @Override
        public ITextComponent getDisplayName() {
            assert getType().getRegistryName() != null;
            return new StringTextComponent(getType().getRegistryName().getPath());
        }

        @Nullable
        @Override
        public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
            return new TestContainer(p_createMenu_1_, pos, world, p_createMenu_2_, p_createMenu_3_, new IntArray(0));
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

    private static class TestContainer extends RecipeBookContainer<TestContainer> implements IInventory {
        protected final World world;

        public TestContainer(int windowId, @Nonnull PlayerInventory inv, @Nonnull PacketBuffer extraData) {
            this(windowId, extraData.readBlockPos(), Minecraft.getInstance().world, inv, Minecraft.getInstance().player, new IntArray(0));
        }

        TestContainer(int id, @Nonnull BlockPos pos, @Nonnull World world, @Nonnull PlayerInventory inventory, @Nonnull PlayerEntity player, @Nonnull IIntArray data) {
            super(millstone_container, id);
            this.world = inventory.player.world;
        }

        @Override
        public void func_201771_a(RecipeItemHelper p_201771_1_) {

        }

        @Override
        public void clear() {

        }

        @Override
        public boolean matches(IRecipe<? super TestContainer> recipeIn) {
            return false;
        }

        @Override
        public int getOutputSlot() {
            return 0;
        }

        @Override
        public int getWidth() {
            return 0;
        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public int getSize() {
            return 0;
        }

        @Override
        public IRecipeType<?> getRecipeType() {
            return TestRecipe.millstone;
        }

        @Override
        public boolean canInteractWith(PlayerEntity playerIn) {
            return true;
        }

        @Override
        public int getSizeInventory() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public ItemStack getStackInSlot(int index) {
            return null;
        }

        @Override
        public ItemStack decrStackSize(int index, int count) {
            return null;
        }

        @Override
        public ItemStack removeStackFromSlot(int index) {
            return null;
        }

        @Override
        public void setInventorySlotContents(int index, ItemStack stack) {

        }

        @Override
        public void markDirty() {

        }

        @Override
        public boolean isUsableByPlayer(PlayerEntity player) {
            return false;
        }
    }

    public static class TestSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<TestRecipe> {
        private final TestSerializer.IFactory<TestRecipe> factory;

        public TestSerializer(@Nonnull TestSerializer.IFactory<TestRecipe> factory) {
            this.factory = factory;
        }

        @Override
        @Nonnull
        public TestRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            String group = JSONUtils.getString(json, "group", "");
            JsonElement jsonelement = JSONUtils.isJsonArray(json, "ingredient")
                    ? JSONUtils.getJsonArray(json, "ingredient")
                    : JSONUtils.getJsonObject(json, "ingredient");
            Ingredient ingredient = IngredientNBT.deserialize(jsonelement);
            ItemStack result;
            ItemStack secondResult = ItemStack.EMPTY;
            int activateCount = JSONUtils.getInt(json, "activateCount", 1);
            double secondChance = JSONUtils.getFloat(json, "secondChance", 1.0f);

            if (!json.has("result")) {
                throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
            }

            if (json.get("result").isJsonObject()) {
                result = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "result"), true);
            } else {
                String s1 = JSONUtils.getString(json, "result");
                ResourceLocation resourcelocation = new ResourceLocation(s1);
                //noinspection deprecation
                result = new ItemStack(Registry.ITEM.getValue(resourcelocation).orElseThrow(() -> new IllegalStateException("Item: " + s1 + " does not exist")));
            }

            if (json.has("secondResult")) {
                if (json.get("secondResult").isJsonObject()) {
                    secondResult = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "secondResult"), true);
                } else {
                    String string = JSONUtils.getString(json, "secondResult");
                    ResourceLocation resourceLocation = new ResourceLocation(string);
                    //noinspection deprecation
                    secondResult = new ItemStack(Registry.ITEM.getValue(resourceLocation).orElseThrow(() -> new IllegalStateException("Item: " + string + " does not exist")));
                }
            } else {
                secondChance = 0;
            }

            return this.factory.create(recipeId, group, ingredient, result, secondResult, secondChance, activateCount);
        }

        @Nullable
        @Override
        public TestRecipe read(@Nonnull ResourceLocation recipeId, PacketBuffer buffer) {
            String group = buffer.readString(32767);
            Ingredient ingredient = Ingredient.read(buffer);
            ItemStack result = buffer.readItemStack();
            ItemStack secondResult = buffer.readItemStack();
            double secondChance = buffer.readDouble();
            int activateCount = buffer.readInt();

            return this.factory.create(recipeId, group, ingredient, result, secondResult, secondChance, activateCount);
        }

        @Override
        public void write(PacketBuffer buffer, TestRecipe recipe) {
            buffer.writeString(recipe.group);
            recipe.ingredient.write(buffer);
            buffer.writeItemStack(recipe.result);
            buffer.writeItemStack(recipe.secondResult);
            buffer.writeDouble(recipe.secondChance);
            buffer.writeInt(recipe.activateCount);
        }

        public interface IFactory<T extends TestRecipe> {
            T create(@Nonnull ResourceLocation resourceLocation, @Nonnull String group, @Nonnull Ingredient ingredient, @Nonnull ItemStack result,
                     @Nonnull ItemStack secondResult, double secondChance, int activateCount);
        }
    }


    public static class TestRecipe implements IRecipe<IInventory> {
        public static final IRecipeType<TestRecipe> millstone = IRecipeType.register("millstone");

        private final IRecipeType<?> type;
        private final ResourceLocation id;
        final String group;
        final Ingredient ingredient;
        final ItemStack result;
        final ItemStack secondResult;
        final double secondChance;
        final int activateCount;

        public TestRecipe(@Nonnull ResourceLocation resourceLocation, @Nonnull String group, @Nonnull Ingredient ingredient,
                          @Nonnull ItemStack result, @Nonnull ItemStack secondResult, double secondChance, int activateCount) {
            type = millstone;
            id = resourceLocation;
            this.group = group;
            this.ingredient = ingredient;
            this.result = result;
            this.secondResult = secondResult;
            this.secondChance = secondChance;
            this.activateCount = activateCount;
        }

        @Override
        public boolean matches(IInventory inv, @Nonnull World worldIn) {
            return this.ingredient.test(inv.getStackInSlot(0));
        }

        @Override
        @Nonnull
        public ItemStack getCraftingResult(@Nullable IInventory inv) {
            return result.copy();
        }

        @Nonnull
        public ItemStack getCraftingSecondResult() {
            return secondResult.copy();
        }

        @Override
        public boolean canFit(int width, int height) {
            return true;
        }

        @Override
        @Nonnull
        public ItemStack getRecipeOutput() {
            return result;
        }

        @Nonnull
        public ItemStack getRecipeSecondOutput() {
            return secondResult;
        }

        @Override
        @Nonnull
        public ResourceLocation getId() {
            return id;
        }

        @Override
        @Nonnull
        public IRecipeSerializer<?> getSerializer() {
            //noinspection ConstantConditions
            return millstone_recipe;
        }

        @Override
        @Nonnull
        public IRecipeType<?> getType() {
            return type;
        }

        @Override
        @Nonnull
        public NonNullList<Ingredient> getIngredients() {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();
            nonnulllist.add(this.ingredient);
            return nonnulllist;
        }

        @Override
        @Nonnull
        public ItemStack getIcon() {
            return new ItemStack(millstone_block);
        }

        public double getSecondChance() {
            return secondChance;
        }

        public int getActivateCount() {
            return activateCount;
        }
    }
}
