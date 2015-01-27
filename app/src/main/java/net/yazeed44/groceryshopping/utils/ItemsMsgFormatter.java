package net.yazeed44.groceryshopping.utils;

import android.text.Html;
import android.text.Spanned;

import java.util.ArrayList;

/**
 * Created by yazeed44 on 1/21/15.
 */
public final class ItemsMsgFormatter {

    private ArrayList<Item> mItems;

    private ItemsMsgFormatter(final ArrayList<Item> items) {
        mItems = items;
    }

    public static ItemsMsgFormatter from(final ArrayList<Item> items) {
        return new ItemsMsgFormatter(items);
    }


    private String generateHtml() {
        String itemsHtml = "";

        for (final Item item : mItems) {
            String itemHtml = "";
            final String nameHtml = item.name;
            final String amountHtml = " : " + item.getAmount();
            final String combinationHtml = " " + item.getChosenUnit();
            itemHtml += nameHtml + amountHtml + combinationHtml + "<br/>";
            itemHtml += generateDividerHtml(itemHtml);

            itemsHtml += itemHtml;
        }

        return itemsHtml;
    }

    private String generateDividerHtml(final String itemHtml) {

        String dividerHtml = "";
        return dividerHtml;

        /*
        for (int i = 0 ; i < itemHtml.length() ; i++){
            dividerHtml += "-";
        }
        dividerHtml += "<br/>";
        return dividerHtml;*/
    }


    public Spanned format() {
        return Html.fromHtml(generateHtml());

    }
}
