/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.event;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.Event;

public class GatherDataEvent extends Event
{
    private final DataGenerator generator;
    private final boolean server;
    private final boolean client;
    private final boolean dev;
    private final boolean reports;
    private final boolean validate;

    public GatherDataEvent(DataGenerator gen, boolean server, boolean client, boolean dev, boolean reports, boolean validate)
    {
        this.generator = gen;
        this.server = server;
        this.client = client;
        this.dev = dev;
        this.reports = reports;
        this.validate = validate;
    }

    public DataGenerator getGenerator() { return this.generator; }
    public boolean includeServer() { return this.server; }
    public boolean includeClient() { return this.client; }
    public boolean includeDev() { return this.dev; }
    public boolean includeReports() { return this.reports; }
    public boolean validate() { return this.validate; }
}
