package net.minecraftforge.common.util;

import java.util.regex.Pattern;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public final class ChatComponents {

    /**
     * Creates a new {@link IChatCompononent} with parsed links.
     */
    public static IChatComponent newChatWithLinks(String string)
    {
        final Pattern pattern = Pattern.compile("((?:(?:https?)://)?(?:[-\\w_\\.]{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[\u00A7 \\n]|$))))",
                Pattern.CASE_INSENSITIVE);
        IChatComponent ichat = new ChatComponentText("");
        // Go through the words, looking for a url
        String[] array = string.split(" ");
        for (int i = 0; i < array.length; i++)
        {
            IChatComponent link = new ChatComponentText(array[i]);
            if (pattern.matcher(array[i]).matches())
            {
                String url = array[i].toLowerCase();
                // Add schema so client doesn't crash.
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                {
                    url = "http://" + url;
                }
                ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
                link.getChatStyle().setChatClickEvent(click);
            }
            // Append this word.
            ichat.appendSibling(link);
        }
        return ichat;
    }
}
