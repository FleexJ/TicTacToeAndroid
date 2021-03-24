package com.example.tictactoeandroid.component;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import com.example.tictactoeandroid.MainActivity;

public class Cell {
    private int color = Color.DKGRAY;

    private Button button;

    private int row;
    private int col;

    public Cell(Button button, final int row, final int col, final MainActivity activity) {
        this.button = button;
        this.row = row;
        this.col = col;
        ColorStateList colorStateList = button.getTextColors();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.action(row, col);
            }
        });
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getText() {
        return String.valueOf(button.getText());
    }

    public void setText(String text) {
        button.setText(text);
    }

    public void setTextColor(int color) {
        button.setTextColor(color);
    }

    public void clear() {
        button.setText("");
        button.setTextColor(Color.DKGRAY);
    }

    public boolean isEmpty() {
        return button.getText().equals("");
    }

    public boolean equals(Cell cell) {
        return this.button.getText().equals(
                cell.button.getText()
        );
    }

    public boolean equals(String string) {
        return this.getText().equals(string);
    }
}
