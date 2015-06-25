/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.UserCache;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GameMap;
import net.year4000.mapnodes.messages.Message;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.sdk.HttpFetcher;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@NoArgsConstructor
/** Details about the current map. */
public final class NodeMap implements GameMap {
    private static final String BACKUP_BASE = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final LoadingCache<UUID, String> UUID_NAMES = CacheBuilder.<UUID, String>newBuilder()
    .build(new CacheLoader<UUID, String>() {
        @Override
        public String load(UUID uuid) throws Exception {
            // Replace Authors UUID with their name
            UserCache cache = MapNodesPlugin.getInst().getUsercache();

            try {
                if (cache.hasUUID(uuid)) {
                    return cache.getPlayer(uuid);
                }
                else {
                    String name = MapNodesPlugin.getInst().getApi().getAccount(uuid.toString()).getUsername();
                    cache.addPlayer(uuid, name);
                    return name;
                }
            }
            catch (Exception e) {
                try {
                    JsonObject data = HttpFetcher.get(BACKUP_BASE + uuid.toString().replaceAll("-", ""), JsonObject.class);
                    String name = data.get("name").getAsString();
                    cache.addPlayer(uuid, name);
                    return name;
                }
                catch (Exception ex) {
                    return "";
                }
            }
        }
    });

    /** The name of the current map. */
    @Since(1.0)
    private String name = null;

    /** The version of the current map. */
    @Since(1.0)
    private String version = null;

    /** The object/goal of the current map. */
    @Since(1.0)
    private String description = null;

    /** Any one that has helped with current the map. */
    @Since(1.0)
    private List<String> authors = new ArrayList<>();
    private transient GameManager game;

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(name != null, Msg.util("settings.map.name"));

        checkArgument(version != null, Msg.util("settings.map.version"));

        checkArgument(description != null, Msg.util("settings.map.description"));

        checkArgument(authors.size() > 0, Msg.util("settings.map.authors"));
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    /** Convert authors's uuid */
    public void convertAuthors() {
        List<String> names = new ArrayList<>();

        for (String name : authors) {
            if (name.contains("-")) {
                String author = UUID_NAMES.getUnchecked(UUID.fromString(name));
                names.add(author.equals("") ? "unknown" : author);
            }
            else {
                names.add(name);
            }
        }

        authors = names;
    }

    /** Assign the game to this region */
    public void assignNodeGame(GameManager game) {
        this.game = game;
    }

    /** Get main author */
    public String getMainAuthor() {
        return authors.get(0);
    }

    /** Map title includes map name and map version */
    public String title() {
        return ChatColor.GREEN + name + " " + Common.formatSeparators(version, ChatColor.GRAY, ChatColor.DARK_GRAY);
    }

    /** Get other authors */
    public List<String> getOtherAuthors() {
        List<String> others = new ArrayList<>(authors);
        others.remove(0);
        return others;
    }

    /** Get the description in your own locale */
    public String getDescription(String locale) {
        return game.locale(locale, description);
    }

    /** Get multi line description */
    public List<String> getMultiLineDescription(String locale) {
        return getMultiLineDescription(locale, 6);
    }

    public List<String> getMultiLineDescription(String locale, int size) {
        List<String> lines = new ArrayList<>();
        String[] spited = game.locale(locale, description).split(" ");

        String line = "";
        int counter = 0;

        for (String word : spited) {
            counter++;
            boolean last = spited.length == counter;

            line += word + " ";

            if (counter > size || last) {
                lines.add(line);
                line = "";
                counter = 0;
            }
        }

        lines.add(line);

        return lines;
    }

    /** Does this map have more than one author */
    public boolean hasOtherAuthors() {
        return getAuthors().size() != 1;
    }

    /** Get short description messages */
    public String getShortDescription(int size) {
        return Common.shortMessage(size, description);
    }

    /** Get the book page for this map */
    public List<String> getBookPage(Player player) {
        List<String> lines = new ArrayList<>();

        lines.add(MessageUtil.message("\n\n&6%s", name));
        lines.add(MessageUtil.message("&0%s&7: &5%s", Msg.locale(player, "map.by"), getMainAuthor()));
        lines.add(MessageUtil.message("\n\n&0%s", getShortDescription(45)));

        return lines;
    }

    /** Fancy authors display */
    public String author(String locale) {
        if (hasOtherAuthors()) {
            int size = getOtherAuthors().size();

            if (size == 1) {
                return Msg.locale(locale, "map.author_duo", getMainAuthor(), getOtherAuthors().get(0));
            }
            else {
                return Msg.locale(locale, "map.authors", getMainAuthor(), String.valueOf(size));
            }
        }

        return Msg.locale(locale, "map.author", getMainAuthor());
    }

    /** Fancy authors display */
    public String author(CommandSender sender) {
        /*if (sender instanceof Player) {
            return author(((Player) sender).spigot().getLocale());
        }
        else {
            return author(Message.DEFAULT_LOCALE);
        }*/
        return author(Message.DEFAULT_LOCALE);
    }
}