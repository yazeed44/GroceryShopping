package net.yazeed44.groceryshopping.utils;

import android.text.Html;
import android.text.Spanned;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yazeed44 on 2/1/15.
 */
public final class ItemsMsgOrganizer {

    private List<Item> mChosenItems;
    private List<ItemsOrganized> mItemsOrganized;

    private ItemsMsgOrganizer(final List<Item> chosenItems) {
        mChosenItems = chosenItems;
    }


    public static ItemsMsgOrganizer from(final List<Item> chosenItems) {
        return new ItemsMsgOrganizer(chosenItems);
    }


    public List<ItemsOrganized> organize() {

        mItemsOrganized = new ArrayList<>();
        final List<String> categories = new ArrayList<>();

        for (final Item chosenItem : mChosenItems) {
            if (categories.contains(chosenItem.category)) {
                createOrReturnItemsOrganized(chosenItem.category, mItemsOrganized).mItems.add(chosenItem);


            } else {
                categories.add(chosenItem.category);
                final ItemsOrganized newItemsOrganized = createEmptyItemsOrganized(chosenItem.category);
                newItemsOrganized.mItems.add(chosenItem);
                mItemsOrganized.add(newItemsOrganized);

            }
        }

        return mItemsOrganized;
    }

    private ItemsOrganized createOrReturnItemsOrganized(final String categoryName, final List<ItemsOrganized> itemsOrganizedList) {
        ItemsOrganized requestedItems = null;
        for (final ItemsOrganized itemsOrganized : itemsOrganizedList) {

            if (itemsOrganized.categoryName.equals(categoryName)) {
                requestedItems = itemsOrganized;
                break;
            }
        }

        if (requestedItems == null) {
            requestedItems = createEmptyItemsOrganized(categoryName);
        }


        return requestedItems;
    }

    private ItemsOrganized createEmptyItemsOrganized(final String categoryName) {
        return new ItemsOrganized(categoryName);
    }


    public String getText() {

        String text = "";
        for (final ItemsOrganized category : mItemsOrganized) {
            text += category.categoryName + " : " + "\n" + category.generateTxt() + "\n";
        }

        return text;
    }


    public static class ItemsOrganized {


        public final String categoryName;
        private final List<Item> mItems = new ArrayList<>();

        public ItemsOrganized(final String categoryName) {
            this.categoryName = categoryName;
        }

        private String generateHtml() {
            String itemsHtml = "";
            for (final Item item : mItems) {
                String itemHtml = "";
                final String nameHtml = item.name;
                final String amountHtml = " : " + item.getAmount();
                final String combinationHtml = " " + item.getChosenUnit();
                itemHtml += nameHtml + amountHtml + combinationHtml + "<br/>";
                itemsHtml += itemHtml;
            }
            return itemsHtml;


        }


        public Spanned generateTxt() {
            return Html.fromHtml(generateHtml());
        }
    }
}
