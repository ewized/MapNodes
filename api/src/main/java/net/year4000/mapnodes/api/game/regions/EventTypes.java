/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.game.regions;

public enum EventTypes {
    CUSTOM("custom"),
    ENTER("enter"),
    ENTITY_DAMAGE("entity_damage"),
    EXIT("exit"),
    BUILD("build"),
    DESTROY("destroy"),
    CHEST("chest"),
    CREATURE_SPAWN("creature_spawns"),
    KILL_PLAYER("kill_player"),
    TNT("tnt"),
    BOW("bow"),
    ITEM_DROP("item_drops"),
    FALLING_BLOCKS("falling_blocks"),
    ;

    private String name;

    EventTypes(String name) {
        this.name = name;
    }

    public static EventTypes getFromName(String name) {
        for (EventTypes type : values()) {
            if (type.isType(name)) {
                return type;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public boolean isType(String name) {
        return this.getName().equals(name.toLowerCase());
    }
}