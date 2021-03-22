package com.example.tictactoeandroid.component;

import android.content.Context;
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.action(row, col);
            }
        });
    }

    public String getText() {
        return String.valueOf(button.getText());
    }

    public void setText(String text) {
        button.setText(text);
    }

    public void setColor(int color) {
        button.setBackgroundColor(color);
    }

    public void clear() {
        button.setText("");
//        button.setBackgroundColor();
    }

    public boolean isEmpty() {
        return button.getText().equals("");
    }

    public boolean equals(Cell cell) {
        return this.button.getText().equals(
                cell.button.getText()
        );
    }
}
