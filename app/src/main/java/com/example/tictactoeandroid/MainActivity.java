package com.example.tictactoeandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tictactoeandroid.component.Cell;
import com.example.tictactoeandroid.component.GotWinnerException;

public class MainActivity extends Activity {

    private int move = 1;
    private String winner = "";
    private Cell[][] cells = new Cell[3][3];
    public static String cross = "X";
    public static String zero = "0";

    private TextView textStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textStatus = findViewById(R.id.textStatus);
        String status = getString(R.string.textStatus_step) + " " + cross;
        textStatus.setText(status);

        cells[0][0] = new Cell( (Button)findViewById(R.id.buttonCell0_0), 0, 0, this);
        cells[0][1] = new Cell( (Button)findViewById(R.id.buttonCell0_1), 0, 1, this);
        cells[0][2] = new Cell( (Button)findViewById(R.id.buttonCell0_2), 0, 2, this);

        cells[1][0] = new Cell( (Button)findViewById(R.id.buttonCell1_0), 1, 0, this);
        cells[1][1] = new Cell( (Button)findViewById(R.id.buttonCell1_1), 1, 1, this);
        cells[1][2] = new Cell( (Button)findViewById(R.id.buttonCell1_2), 1, 2, this);

        cells[2][0] = new Cell( (Button)findViewById(R.id.buttonCell2_0), 2, 0, this);
        cells[2][1] = new Cell( (Button)findViewById(R.id.buttonCell2_1), 2, 1, this);
        cells[2][2] = new Cell( (Button)findViewById(R.id.buttonCell2_2), 2, 2, this);
    }

    public void action(int row, int col) {
        if (!winner.isEmpty())
            return;
        if (cells[row][col].isEmpty()) {
            if (move % 2 != 0) {
                cells[row][col].setText(cross);
                String status = getString(R.string.textStatus_step) + " " + zero;
                textStatus.setText(status);
            }
            else {
                cells[row][col].setText(zero);
                String status = getString(R.string.textStatus_step) + " " + cross;
                textStatus.setText(status);
            }
            move++;
        }
        if (checkWin()) {
            String status = getString(R.string.textStatus_win) + " " + winner;
            textStatus.setText(status);
            return;
        }
        if (move == 10)
            textStatus.setText(getString(R.string.textStatus_draw));
    }

    public void restart(View view) {
        for (Cell[] arr: cells)
            for (Cell cell: arr)
                cell.clear();
        move = 1;
        String status = getString(R.string.textStatus_step) + " " + cross;
        textStatus.setText(status);
        winner = "";
    }

    public boolean checkWin() {
        winner = "";
        try {
            //rows
            for (int i = 0; i < 3; i ++)
                checkLine(0, i, 1, 0);
            //cols
            for (int i = 0; i < 3; i++)
                checkLine(i, 0, 0, 1);
            //left diagonal
            checkLine(0, 0, 1, 1);
            //right diagonal
            checkLine(2, 0, -1, 1);
        } catch (GotWinnerException e) {
            winner = e.getWinner();
        }
        return !winner.isEmpty();
    }

    public void checkLine(int xStart, int yStart, int xStep, int yStep) throws GotWinnerException {
        int count = 1;
        Cell saved = cells[yStart][xStart];
        int i = yStart + yStep;
        int j = xStart + xStep;
        for (int k = 1; k < 3; k++) {
            Cell cell = cells[i][j];
            if (saved.equals(cell) && !saved.isEmpty())
                count++;
            else {
                saved = cell;
                count = 1;
            }
            if (count == 3)
                throw new GotWinnerException(saved.getText());
            i += yStep;
            j += xStep;
        }
    }
}