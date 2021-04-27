package com.example.tictactoeandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tictactoeandroid.component.Cell;
import com.example.tictactoeandroid.component.GotWinnerException;

import java.util.LinkedList;
import java.util.Random;

public class MainActivity extends Activity {

    private int move = 1;
    private String winner = "";
    private Cell[][] cells = new Cell[3][3];
    public static String cross = "X";
    public static String zero = "0";
    private LinkedList<Cell> cellLinkedList = new LinkedList<>();

    private TextView textStatus;
    private Switch switchMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchMode = findViewById(R.id.switchMode);
        switchMode.setChecked(true);

        textStatus = findViewById(R.id.textStatus);
        int color = getResources().getColor(R.color.colorCross);
        String status = getString(R.string.textStatus_step) + " " + cross;
        updateStatus(status, color);

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

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void updateStatus(String text, int color) {
        textStatus.setTextColor(color);
        textStatus.setText(text);
    }

    public void action(int row, int col) {
        //Если игра окончена
        if (!winner.isEmpty())
            return;
        //Ход игрока или бота
        if (cells[row][col].isEmpty()) {
            if (move % 2 != 0) {
                cells[row][col].setText(cross);
                int color = getResources().getColor(R.color.colorCross);
                cells[row][col].setTextColor(color);
                String status = getString(R.string.textStatus_step) + " " + zero;
                color = getResources().getColor(R.color.colorZero);
                updateStatus(status, color);
                cellLinkedList.add(cells[row][col]);
                Log.v("Step", "\n" + cross + "\trow: " + row + "\tcol: " + col);
            }
            else {
                cells[row][col].setText(zero);
                int color = getResources().getColor(R.color.colorZero);
                cells[row][col].setTextColor(color);
                String status = getString(R.string.textStatus_step) + " " + cross;
                color = getResources().getColor(R.color.colorCross);
                updateStatus(status, color);
                cellLinkedList.add(cells[row][col]);
                Log.v("Step", "\n" + zero + "\trow: " + row + "\tcol: " + col);
            }
            move++;
        }
        //Проверка на победу после хода
        if (checkWin()) {
            String status = getString(R.string.textStatus_win) + " " + winner;
            int color;
            if (winner.equals(cross))
                color = getResources().getColor(R.color.colorCross);
            else
                color = getResources().getColor(R.color.colorZero);
            updateStatus(status, color);
            return;
        }
        //Если кончились ходы
        if (move == 10) {
            int color = getResources().getColor(R.color.colorStatusDraw);
            String status = getResources().getString(R.string.textStatus_draw);
            updateStatus(status, color);
            return;
        }
        //Если включена игра с ботом
        if (switchMode.isChecked()
                && move % 2 == 0
        )
        {
            botAction(row, col);
        }
    }

    public void restart(View view) {
        for (Cell[] arr: cells)
            for (Cell cell: arr)
                cell.clear();
        move = 1;
        String status = getString(R.string.textStatus_step) + " " + cross;
        int color = getResources().getColor(R.color.colorCross);
        updateStatus(status, color);
        winner = "";
        cellLinkedList = new LinkedList<>();
    }

    public void stepBack(View view) {
        if (cellLinkedList.size() != 0) {
            Cell cell = cellLinkedList.getLast();
            cellLinkedList.remove(cell);
            cell.clear();
            if (move % 2 == 0) {
                String status = getString(R.string.textStatus_step) + " " + cross;
                int color = getResources().getColor(R.color.colorCross);
                updateStatus(status, color);
            }
            else {
                String status = getString(R.string.textStatus_step) + " " + zero;
                int color = getResources().getColor(R.color.colorZero);
                updateStatus(status, color);
            }
            move--;
            winner = "";
        }
    }

    public boolean checkWin() {
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
        }
        return !winner.isEmpty();
    }

    //Проверка линии на нахождение победной комбинации
    public void checkLineOnWin(int xStart, int yStart, int xStep, int yStep) throws GotWinnerException {
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

    //Ход бота
    public void botAction(int row, int col) {
        //Проверяем чей ход, чтобы определить свою сторону
        if (move % 2 == 0) {
            Cell cell = scanBotOnStep(zero, cross, row, col);
            action(cell.getRow(), cell.getCol());
        }
        else {
            Cell cell = scanBotOnStep(cross, zero, row, col);
            action(cell.getRow(), cell.getCol());
        }
    }

    /*
    * Функция возвращает ячейку, на которую стоит сходить
    * me - сторона за которую надо делать ход (cross|zero)
    * enemy - враг, которого надо попытаться заблокировать (cross|zero)
    */
    public Cell scanBotOnStep(String me, String enemy, int row, int col) {
        //Если свободен центр на втором ходе
        if (move == 2 ) {
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
        Cell cell = null;
        //Попытка выиграть игру
        //scan rows
        for (int i = 0; i < 3; i++) {
            cell = scanLineOnStep(0, i, 1, 0, 2, me, 1);
            if (cell != null) {
                return cell;
            }
        }
        //scan columns
        for (int i = 0; i < 3; i++) {
            cell = scanLineOnStep(i, 0, 0, 1, 2, me, 1);
            if (cell != null) {
                return cell;
            }
        }
        //scan left diagonal
        cell = scanLineOnStep(0, 0, 1, 1, 2, me, 1);
        if (cell != null) {
            return cell;
        }
        //scan right diagonal
        cell = scanLineOnStep(2, 0, -1, 1, 2, me, 1);
        if (cell != null) {
            return cell;
        }

        //Попытка заблокировать победу врага
        //scan left diagonal
        cell = scanLineOnStep(0, 0, 1, 1, 2, enemy, 1);
        if (cell != null) {
            return cell;
        }
        //scan right diagonal
        cell = scanLineOnStep(2, 0, -1, 1, 2, enemy, 1);
        if (cell != null) {
            return cell;
        }
        //scan row
        cell = scanLineOnStep(0, row, 1, 0, 2, enemy, 1);
        if (cell != null) {
            return cell;
        }
        //scan column
        cell = scanLineOnStep(col, 0, 0, 1, 2, enemy, 1);
        if (cell != null) {
            return cell;
        }
        //Простой ход, если не удалось выиграть или заблокировать
        for (int j = 2; j >= 1; j--) {
            //scan left diagonal
            cell = scanLineOnStep(0, 0, 1, 1, 1, me, j);
            if (cell != null) {
                return cell;
            }
            //scan right diagonal
            cell = scanLineOnStep(2, 0, -1, 1, 1, me, j);
            if (cell != null) {
                return cell;
            }
            //scan columns
            for (int i = 0; i < 3; i++) {
                cell = scanLineOnStep(i, 0, 0, 1, 1, me, j);
                if (cell != null) {
                    return cell;
                }
            }
            //scan rows
            for (int i = 0; i < 3; i++) {
                cell = scanLineOnStep(0, i, 1, 0, 1, me, j);
                if (cell != null) {
                    return cell;
                }
            }
        }
        return null;
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
    public Cell scanLineOnStep(int xStart, int yStart, int xStep, int yStep, int count, String player, int countEmpty) {
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
            }
            else
                //Если идет последовательность одинаковых непустых клеток, то считаем ее длину
                if (cell.equals(player)) {
                    k++;
                }
                //Если встретилась другая клетка, то обнуляем счет
                else {
                    k = 0;
                    kEmpty = 0;
                }
            //Если найдена оптимальная ячейка для хода, то возвращаем ее
            if (k == count && result != null && kEmpty == countEmpty) {
                return result;
            }
            i += yStep;
            j += xStep;
        }
        return null;
    }
}