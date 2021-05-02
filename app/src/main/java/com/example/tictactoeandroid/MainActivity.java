package com.example.tictactoeandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tictactoeandroid.component.Cell;
import com.example.tictactoeandroid.component.GotCellToStepException;
import com.example.tictactoeandroid.component.GotWinnerException;

import java.util.LinkedList;
import java.util.Random;

public class MainActivity extends Activity {

    private int move = 1;
    private String winner = "";
    private Cell[][] cells = new Cell[3][3];
    public static String cross = "X";
    public static String zero = "0";
    private LinkedList<Cell> moveBuffer = new LinkedList<>();

    private TextView textStatus;
    private Switch switchMode;
    private ImageButton buttonPrev;
    private Button buttonRestart;
    private Button buttonStepBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchMode = findViewById(R.id.switchMode);
        switchMode.setChecked(true);
        switchMode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showToastShort(
                        getString(R.string.switchBotOnOffToast)
                );
                return true;
            }
        });

        buttonPrev = findViewById(R.id.buttonPrev);
        buttonPrev.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showToastShort(
                        getString(R.string.stepBackButtonToast)
                );
                return true;
            }
        });

        buttonRestart = findViewById(R.id.buttonRestart);
        buttonRestart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showToastShort(
                        getString(R.string.restartButtonToast)
                );
                return true;
            }
        });

        buttonStepBot = findViewById(R.id.buttonStepBot);
        buttonStepBot.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showToastShort(
                        getString(R.string.stepBotButtonToast)
                );
                return true;
            }
        });

        textStatus = findViewById(R.id.textStatus);
        int color = getResources().getColor(R.color.colorCross);
        String status = getString(R.string.textStatus_step) + " " + cross;
        updateStatus(status, color);

        cells[0][0] = new Cell((Button) findViewById(R.id.buttonCell0_0), 0, 0, this);
        cells[0][1] = new Cell((Button) findViewById(R.id.buttonCell0_1), 0, 1, this);
        cells[0][2] = new Cell((Button) findViewById(R.id.buttonCell0_2), 0, 2, this);

        cells[1][0] = new Cell((Button) findViewById(R.id.buttonCell1_0), 1, 0, this);
        cells[1][1] = new Cell((Button) findViewById(R.id.buttonCell1_1), 1, 1, this);
        cells[1][2] = new Cell((Button) findViewById(R.id.buttonCell1_2), 1, 2, this);

        cells[2][0] = new Cell((Button) findViewById(R.id.buttonCell2_0), 2, 0, this);
        cells[2][1] = new Cell((Button) findViewById(R.id.buttonCell2_1), 2, 1, this);
        cells[2][2] = new Cell((Button) findViewById(R.id.buttonCell2_2), 2, 2, this);
    }

    public void showToastShort(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void updateStatus(String text, int color) {
        textStatus.setTextColor(color);
        textStatus.setText(text);
    }

    public void restart(View view) {
        for (Cell[] arr: cells)
            for (Cell cell: arr)
                cell.clear();

        move = 1;
        updateStatus(
                getString(R.string.textStatus_step) + " " + cross,
                getResources().getColor(R.color.colorCross)
        );
        winner = "";
        moveBuffer.clear();
    }

    public void stepBack(View view) {
        if (moveBuffer.size() != 0) {
            moveBuffer.getLast().clear();
            moveBuffer.removeLast();

            if (move % 2 == 0)
                updateStatus(
                        getString(R.string.textStatus_step) + " " + cross,
                        getResources().getColor(R.color.colorCross)
                );
            else
                updateStatus(
                        getString(R.string.textStatus_step) + " " + zero,
                        getResources().getColor(R.color.colorZero)
                );
            move--;
            winner = "";
        }
    }

    public void takeCell(Cell cell) {
        if (!cell.isEmpty())
            return;

        if (move % 2 != 0) {
            cell.setText(cross);
            cell.setTextColor(
                    getResources().getColor(R.color.colorCross)
            );

            updateStatus(
                    getString(R.string.textStatus_step) + " " + zero,
                    getResources().getColor(R.color.colorZero)
            );

            Log.v("Step", cross + "\trow: " + cell.getRow() + "\tcol: " + cell.getCol());
        }
        else {
            cell.setText(zero);
            cell.setTextColor(
                    getResources().getColor(R.color.colorZero)
            );

            updateStatus(
                    getString(R.string.textStatus_step) + " " + cross,
                    getResources().getColor(R.color.colorCross)
            );

            Log.v("Step", zero + "\trow: " + cell.getRow() + "\tcol: " + cell.getCol());
        }
        moveBuffer.add(cell);
        move++;

        isEnd();
    }

    public void action(int row, int col) {
        //Если игра окончена
        if (isEnd())
            return;

        //Ход игрока
        if (cells[row][col].isEmpty()) {
            takeCell(cells[row][col]);
        } else
            return;

        //Если включена игра с ботом
        if (switchMode.isChecked())
            botAction(null);
    }

    //Ход бота
    public void botAction(View view) {
        if (isEnd())
            return;

        Cell cell;
        if (move == 1 || move == 2 && cells[1][1].isEmpty())
            cell = cells[1][1];
        else //Проверяем чей ход, чтобы определить свою сторону
            if (move % 2 != 0)
                cell = scanBotOnStep(cross, zero,
                        moveBuffer.getLast().getRow(),
                        moveBuffer.getLast().getCol());
            else
                cell = scanBotOnStep(zero, cross,
                        moveBuffer.getLast().getRow(),
                        moveBuffer.getLast().getCol());

        takeCell(cell);
    }

    //Проверка линии на нахождение победной комбинации
    public void checkLineOnWin(int xStart,
                               int yStart,
                               int xStep,
                               int yStep) throws GotWinnerException {
        int count = 1;
        Cell saved = cells[yStart][xStart];
        int i = yStart + yStep;
        int j = xStart + xStep;

        while (i < 3 && j < 3 && i >= 0 && j >= 0) {
            Cell cell = cells[i][j];
            //Если встретилась последовательность одинаковых непустых ячеек
            if (saved.equals(cell) && !saved.isEmpty())
                count++;
            //Если они отличаются
            else {
                saved = cell;
                count = 1;
            }
            //Если победная комбинация, то вызываем exception с данными победителя
            if (count == 3)
                throw new GotWinnerException(saved.getText());

            i += yStep;
            j += xStep;
        }
    }

    public boolean isEnd() {
        if (!winner.isEmpty())
            return true;

        if (move >= 10) {
            int color = getResources().getColor(R.color.colorStatusDraw);
            String status = getResources().getString(R.string.textStatus_draw);
            updateStatus(status, color);
            return true;
        }

        winner = "";
        try {
            //rows
            for (int i = 0; i < 3; i ++)
                checkLineOnWin(0, i, 1, 0);
            //cols
            for (int i = 0; i < 3; i++)
                checkLineOnWin(i, 0, 0, 1);
            //left diagonal
            checkLineOnWin(0, 0, 1, 1);
            //right diagonal
            checkLineOnWin(2, 0, -1, 1);
        } catch (GotWinnerException e) {
            winner = e.getWinner();
            String status = getString(R.string.textStatus_win) + " " + winner;
            if (winner.equals(cross)) {
                int color = getResources().getColor(R.color.colorCross);
                updateStatus(status, color);
            }
            else if (winner.equals(zero)) {
                int color = getResources().getColor(R.color.colorZero);
                updateStatus(status, color);
            }
            return true;
        }
        return false;
    }

    /*
     * Функция проверяет линию на возможность хода
     * xStart - координата начала линии по X
     * yStart - координата начала линии по Y
     * xStep - шаг по оси X
     * yStep - шаг по оси Y
     * player - тип подсчитываемых последовательных ячеек(cross|zero)
     * countEmpty - количество пустых клеток
     */
    public void scanLineOnStep(int xStart,
                               int yStart,
                               int xStep,
                               int yStep,
                               int count,
                               String player,
                               int countEmpty) throws GotCellToStepException{
        int k = 0;
        int kEmpty = 0;
        Cell result = null;
        int i = yStart;
        int j = xStart;

        while (i < 3 && j < 3 && i >= 0 && j >= 0) {
            Cell cell = cells[i][j];
            //Если встретилась пустая ячейка, запоминаем ее
            if (cell.isEmpty()) {
                result = cell;
                kEmpty++;
            } //Если идет последовательность одинаковых непустых клеток, то считаем ее длину
            else if (cell.equals(player)) {
                k++;
            } else { //Если встретилась другая клетка, то обнуляем счет
                k = 0;
                kEmpty = 0;
            }
            //Если найдена оптимальная ячейка для хода, то возвращаем ее
            if (k == count && result != null && kEmpty == countEmpty) {
                throw new GotCellToStepException(result);
            }
            i += yStep;
            j += xStep;
        }
    }

    /*
    * Функция возвращает ячейку, на которую стоит сходить
    * me - сторона за которую надо делать ход (cross|zero)
    * enemy - враг, которого надо попытаться заблокировать (cross|zero)
    */
    public Cell scanBotOnStep(String me, String enemy, int row, int col) {
        if (move < 3 ) {
            //Если свободен центр на первых 2-х ходах
            if (cells[1][1].isEmpty())
                return cells[1][1];
            else {
                Random random = new Random();
                while(true) {
                    int i = random.nextInt(3);
                    int j = random.nextInt(3);
                    Cell cell = cells[i][j];
                    if (cell.isEmpty())
                        return cell;
                }
            }
        }
        try {
            //Попытка выиграть игру
            //scan rows
            for (int i = 0; i < 3; i++) {
                scanLineOnStep(0, i, 1, 0, 2, me, 1);
            }
            //scan columns
            for (int i = 0; i < 3; i++) {
                scanLineOnStep(i, 0, 0, 1, 2, me, 1);
            }
            //scan left diagonal
            scanLineOnStep(0, 0, 1, 1, 2, me, 1);
            //scan right diagonal
            scanLineOnStep(2, 0, -1, 1, 2, me, 1);

            //Попытка заблокировать победу врага
            //scan left diagonal
            scanLineOnStep(0, 0, 1, 1, 2, enemy, 1);
            //scan right diagonal
            scanLineOnStep(2, 0, -1, 1, 2, enemy, 1);
            //scan row
            scanLineOnStep(0, row, 1, 0, 2, enemy, 1);
            //scan column
            scanLineOnStep(col, 0, 0, 1, 2, enemy, 1);

            //Простой ход, если не удалось выиграть или заблокировать
            for (int j = 2; j >= 1; j--) {
                //scan left diagonal
                scanLineOnStep(0, 0, 1, 1, 1, me, j);
                //scan right diagonal
                scanLineOnStep(2, 0, -1, 1, 1, me, j);
                //scan columns
                for (int i = 0; i < 3; i++) {
                    scanLineOnStep(i, 0, 0, 1, 1, me, j);
                }
                //scan rows
                for (int i = 0; i < 3; i++) {
                    scanLineOnStep(0, i, 1, 0, 1, me, j);
                }
            }
        } catch (GotCellToStepException e) {
            return e.getCell();
        }
        return null;
    }
}