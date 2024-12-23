package com.example.a2.placeholder;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 */
public class PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<PlaceholderItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, PlaceholderItem> ITEM_MAP = new HashMap<>();

    /**
     * A counter for the amount of rows used.
     */
    private static final int COUNT = 5;

    /**
     * A list of names for the items.
     */
    private static final String[] ITEM_NAMES = {
            "Something",
            "Something_else",
            "Hello",
            "Hello_world",
            "Hi"
    };

    /**
     * A list of information about the items.
     */
    private static final String[] ITEM_INFO = {
            "Cool information",
            "Some information",
            "Other kind of information",
            "Something info",
            "The last information"
    };

    /**
     * A list of more information about the items.
     */
    private static final String[] MORE_ITEM_INFO = {
            "The last information",
            "Something info",
            "Some information",
            "Other kind of information",
            "Cool information"
    };

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createPlaceholderItem(i));
        }
    }

    private static void addItem(PlaceholderItem item) {
        // Add items and give them an id.
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static PlaceholderItem createPlaceholderItem(int position) {
        // Creates a placeholder item for all items.
        return new PlaceholderItem(String.valueOf(position), ITEM_NAMES[position - 1], ITEM_INFO[position - 1], MORE_ITEM_INFO[position - 1]);
    }

    /**
     * A placeholder item representing a piece of content.
     */
    public static class PlaceholderItem {
        public final String id; // The id for the content/item.
        public final String content; // The name for the item.
        public final String info; // The first information about the item
        public final String more_info; // The second information about the item.

        public PlaceholderItem(String id, String content, String info, String more_info) {
            this.id = id;
            this.content = content;
            this.info = info;
            this.more_info = more_info;
        }

        @NonNull
        @Override
        public String toString() {
            return content;
        }
    }
}