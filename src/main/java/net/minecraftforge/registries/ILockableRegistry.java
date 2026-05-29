/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.registries;

public interface ILockableRegistry {

    //Lock a registry to disable any direct Register calls.
    // All future calls must be done via singularity registry methods not vanilla
    void lock();
}
