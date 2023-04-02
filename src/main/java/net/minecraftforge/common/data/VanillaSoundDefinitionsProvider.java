/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.ForgeMod;

public class VanillaSoundDefinitionsProvider extends SoundDefinitionsProvider {
    public VanillaSoundDefinitionsProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, "minecraft", helper);
    }

    @Override
    public void registerSounds() {
        this.add(ForgeMod.BUCKET_EMPTY_MILK.getId(), definition().subtitle("subtitles.item.bucket.empty")
                .with(sound("item/bucket/empty1"), sound("item/bucket/empty1").pitch(0.9),
                        sound("item/bucket/empty2"), sound("item/bucket/empty3")));
        this.add(ForgeMod.BUCKET_FILL_MILK.getId(), definition().subtitle("subtitles.item.bucket.fill")
                .with(sound("item/bucket/fill1"), sound("item/bucket/fill2"), sound("item/bucket/fill3")));
    }
}
