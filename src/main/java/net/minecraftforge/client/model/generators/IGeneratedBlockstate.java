package net.minecraftforge.client.model.generators;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;

@VisibleForTesting
public interface IGeneratedBlockstate {

    JsonObject toJson();
}
