package cpw.mods.fml.client;

import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.util.StringTranslate;

import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;

import cpw.mods.fml.common.registry.ItemData;
import cpw.mods.fml.common.versioning.ArtifactVersion;

public class GuiIdMismatchScreen extends GuiYesNo {
    private List<String> missingIds = Lists.newArrayList();
    private List<String> mismatchedIds = Lists.newArrayList();
    private boolean allowContinue;

    public GuiIdMismatchScreen(MapDifference<Integer, ItemData> idDifferences, boolean allowContinue)
    {
        super(null,"ID mismatch", "Should I continue?", 1);
        field_73942_a = this;
        for (Entry<Integer, ItemData> entry : idDifferences.entriesOnlyOnLeft().entrySet())
        {
            missingIds.add(String.format("ID %d (ModID: %s, type %s) is missing", entry.getValue().getItemId(), entry.getValue().getModId(), entry.getValue().getItemType()));
        }
        for (Entry<Integer, ValueDifference<ItemData>> entry : idDifferences.entriesDiffering().entrySet())
        {
            ItemData world = entry.getValue().leftValue();
            ItemData game = entry.getValue().rightValue();
            mismatchedIds.add(String.format("ID %d is mismatched. World: (ModID: %s, type %s, ordinal %d) Game (ModID: %s, type %s, ordinal %d)", world.getItemId(), world.getModId(), world.getItemType(), world.getOrdinal(), game.getModId(), game.getItemType(), game.getOrdinal()));
        }
        this.allowContinue = allowContinue;
    }

    @Override
    public void func_73878_a(boolean choice, int p_73878_2_)
    {
        FMLClientHandler.instance().callbackIdDifferenceResponse(choice);
    }

    @Override
    public void func_73863_a(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        this.func_73873_v_();
        if (!allowContinue && field_73887_h.size() == 2)
        {
            field_73887_h.remove(0);
        }
        int offset = Math.max(85 - missingIds.size() * 10 + mismatchedIds.size() * 30, 10);
        this.func_73732_a(this.field_73886_k, "Forge Mod Loader has found world ID mismatches", this.field_73880_f / 2, offset, 0xFFFFFF);
        offset += 10;
        for (String s: missingIds) {
            this.func_73732_a(this.field_73886_k, s, this.field_73880_f / 2, offset, 0xEEEEEE);
            offset += 10;
        }
        for (String s: mismatchedIds) {
            this.func_73732_a(this.field_73886_k, s, this.field_73880_f / 2, offset, 0xEEEEEE);
            offset += 10;
        }
        offset += 10;
        if (allowContinue)
        {
            this.func_73732_a(this.field_73886_k, "Do you wish to continue loading?", this.field_73880_f / 2, offset, 0xFFFFFF);
            offset += 10;
        }
        else
        {
            this.func_73732_a(this.field_73886_k, "You cannot connect to this server", this.field_73880_f / 2, offset, 0xFFFFFF);
            offset += 10;
        }
        // super.super. Grrr
        for (int var4 = 0; var4 < this.field_73887_h.size(); ++var4)
        {
            GuiButton var5 = (GuiButton)this.field_73887_h.get(var4);
            var5.field_73743_d = Math.min(offset + 10, this.field_73881_g - 20);
            if (!allowContinue)
            {
                var5.field_73746_c = this.field_73880_f / 2 - 75;
                var5.field_73744_e = StringTranslate.func_74808_a().func_74805_b("gui.done");
            }
            var5.func_73737_a(this.field_73882_e, p_73863_1_, p_73863_2_);
        }
    }
}
