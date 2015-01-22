package net.yazeed44.groceryshopping.utils;

import java.io.Serializable;

/**
 * Created by yazeed44 on 12/31/14.
 */
public final class Item implements Serializable {


    public static final String DEFAULT_NOTE = "لايوجد";
    private String note = DEFAULT_NOTE;
    private static int count = 0;
    public final String name;
    public final String combination;
    public final int key;
    private float amount = 1;

    public Item(String name, String combination) {
        this.name = name;
        this.combination = combination;
        key = count++;
    }

    @Override
    public String toString() {
        return name;
    }


    public boolean isNoteValid() {
        return !(getNote().equals(DEFAULT_NOTE));
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {

        if (note.isEmpty()) {
            this.note = DEFAULT_NOTE;
            return;
        }


        this.note = note;
    }
}
