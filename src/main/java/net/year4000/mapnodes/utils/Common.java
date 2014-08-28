package net.year4000.mapnodes.utils;

import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.MessageUtil;

public final class Common {
    private Common() {
        // Utility Class
    }

    /** Color numbers based on its percent */
    private static boolean toggle = false;

    public static String colorNumber(int current, int total) {
        double percent = ((double)current / (double)total) * 100;
        ChatColor color;

        //LogUtil.log(percent+"");

        // danger dark red
        if (percent < 10) {
            toggle = !toggle;
            color = toggle ? ChatColor.DARK_RED : ChatColor.RED;
        }
        // danger red
        else if (percent < 20) {
            color = ChatColor.RED;
        }
        // warning yellow
        else if (percent < 50) {
            color = ChatColor.YELLOW;
        }
        // good green
        else {
            color = ChatColor.GREEN;
        }

        return MessageUtil.replaceColors(color.toString() + current);
    }

    /** Color numbers based on its capacity */
    public static String colorCapacity(int current, int total) {
        ChatColor color;

        // warning yellow
        if (current >= total - 3) {
            color = ChatColor.YELLOW;
        }
        // warning orange/gold
        else if (current >= total - 2) {
            color = ChatColor.GOLD;
        }
        // danger red
        else if (current >= total - 1) {
            color = ChatColor.RED;
        }
        // danger dark red
        else if (current >= total) {
            color = ChatColor.DARK_RED;
        }
        // good green
        else {
            color = ChatColor.GREEN;
        }

        return MessageUtil.replaceColors(color.toString() + current);
    }

    public static String formatSeparators(String format, ChatColor prefix, ChatColor suffix) {
        format = format.replace("", "") // no reason for this just to keep real one bellow
            .replaceAll("\\.", suffix + "." + prefix)
            .replaceAll("\\-", suffix + "-" + prefix)
            .replaceAll("_", suffix + "_" + prefix);

        //format = format.replaceAll("(\\.|\\-|_)", suffix + "%1" + prefix);

        //LogUtil.debug(format);
        return MessageUtil.replaceColors(prefix + format + suffix);
    }

    /** Make any string to a simple truncated message */
    public static String shortMessage(int size, String message) {
        int length = message.length();
        int last = size;
        String shortMsg = message.substring(0, (length > size ? size : length));

        while (shortMsg.endsWith(" ")) {
            shortMsg = shortMsg.substring(0, --last);
        }

        return shortMsg + (length > size ? "..." : "");
    }
}