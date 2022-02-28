/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.ticket;

public interface ITicketManager<T>
{
    void add(SimpleTicket<T> ticket);

    void remove(SimpleTicket<T> ticket);
}
