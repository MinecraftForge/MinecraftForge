/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay;

public enum DataType {
    FLOAT(4),
    UNORM_BYTE(1);

    public final int componentSize;

    DataType(int componentSize) {
        this.componentSize = componentSize;
    }
}
