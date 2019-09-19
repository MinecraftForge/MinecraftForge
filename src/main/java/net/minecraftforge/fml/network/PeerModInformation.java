package net.minecraftforge.fml.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;

public class PeerModInformation {

    private final List<String> mods;
    private final Map<ResourceLocation, String> channelVersions;
    private final boolean isVanilla;

    public PeerModInformation(List<String> mods, Map<ResourceLocation, String> channelVersions) {
        this.mods = ImmutableList.copyOf(mods);
        this.channelVersions = ImmutableMap.copyOf(channelVersions);
        this.isVanilla = false;
    }

    public PeerModInformation() {
        this.isVanilla = true;
        this.mods = ImmutableList.of();
        this.channelVersions = ImmutableMap.of();
    }

    public List<String> getMods() {
        return mods;
    }

    public Map<ResourceLocation, String> getChannelVersions() {
        return channelVersions;
    }

    public boolean isVanilla() {
        return isVanilla;
    }
}
