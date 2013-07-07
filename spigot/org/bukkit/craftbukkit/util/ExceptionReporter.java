package org.bukkit.craftbukkit.util;

import org.bukkit.Bukkit;

public final class ExceptionReporter {

    public static void handle(Throwable t, String... messages) {
        for (String message : messages) {
            Bukkit.getLogger().severe(message);
        }
        Bukkit.getLogger().severe("Spigot recommends you report this to http://www.mcportcentral.co.za/");
        Bukkit.getLogger().severe("");
        Bukkit.getLogger().severe("Spigot version: " + Bukkit.getBukkitVersion());
        Bukkit.getLogger().severe("Exception Trace Begins:");
        StackTraceElement[] stack = t.getStackTrace();
        for (int line = 0; line < stack.length; line++) {
            Bukkit.getLogger().severe("    " + stack[line].toString());
        }
        Bukkit.getLogger().severe("Exception Trace Ends.");
        Bukkit.getLogger().severe("");
    }

    public static void handle(Throwable t) {
        handle(t, "Spigot has encountered an unexpected exception!");
    }
}
