package com.kimerasoft_ec.alpuzzle;

import android.graphics.Bitmap;

public class PuzzleItem {
    private int x, y, id;
    private Bitmap image;
    private boolean interchanged;

    public boolean isInterchanged() {
        return interchanged;
    }

    public void setInterchanged(boolean interchanged) {
        this.interchanged = interchanged;
    }

    public PuzzleItem(int x, int y, Bitmap image, int id) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.id = id;
        interchanged = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
