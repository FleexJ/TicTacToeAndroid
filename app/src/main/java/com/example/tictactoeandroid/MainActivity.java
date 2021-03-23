package com.example.tictactoeandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

public class MainActivity extends Activity {

    private int move = 1;
    private String winner = "";
    private Cell[][] cells = new Cell[3][3];
    public static String cross = "X";
    public static String zero = "0";

    private TextView textStatus;
    private Switch switchMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchMode = findViewById(R.id.switchMode);
        switchMode.setChecked(true);

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

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void action(int row, int col) {
        //Если игра окончена
        if (!winner.isEmpty())
            return;
        //Ход игрока или бота
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
        //Проверка на победу после хода
        if (checkWin()) {
            String status = getString(R.string.textStatus_win) + " " + winner;
            textStatus.setText(status);
            return;
        }
        //Если кончились ходы
        if (move == 10) {
            textStatus.setText(getString(R.string.textStatus_draw));
            return;
        }
        //Если включена игра с ботом
        if (switchMode.isChecked() && move % 2 == 0) {
            botAction();
        }
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

    protected boolean checkWin() {
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
    protected void checkLineOnWin(int xStart, int yStart, int xStep, int yStep) throws GotWinnerException {
        int count = 1;
        Cell saved = cells[yStart][xStart];
        int i = yStart + yStep;
        int j = xStart + xStep;
        for (int k = 1; k < 3; k++) {
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
    public void botAction() {
        //Проверяем чей ход, чтобы определить свою сторону
        if (move % 2 == 0) {
            Cell cell = scanBotOnStep(zero, cross);
            action(cell.getRow(), cell.getCol());
        }
        else {
            Cell cell = scanBotOnStep(cross, zero);
            action(cell.getRow(), cell.getCol());
        }
    }

    /*
    * Функция возвращает ячейку, на которую стоит сходить
    * me - сторона за которую надо делать ход (cross|zero)
    * enemy - враг, которого надо попытаться заблокировать (cross|zero)
    */
    protected Cell scanBotOnStep(String me, String enemy) {
        //Если свободен центр на втором ходе
        if (move == 2) {
            if (cells[1][1].isEmpty()) {
                return cells[1][1];
            }
        }
        Cell cell = null;
        //Попытка выиграть игру
        //scan left diagonal
        cell = scanLineOnStep(0, 0, 1, 1, 2, me, false);
        if (cell != null) {
            return cell;
        }
        //scan right diagonal
        cell = scanLineOnStep(2, 0, -1, 1, 2, me, false);
        if (cell != null) {
            return cell;
        }
        //scan rows
        for (int i = 0; i < 3; i++) {
            cell = scanLineOnStep(0, i, 1, 0, 2, me, false);
            if (cell != null) {
                return cell;
            }
        }
        //scan columns
        for (int i = 0; i < 3; i++) {
            cell = scanLineOnStep(i, 0, 0, 1, 2, me, false);
            if (cell != null) {
                return cell;
            }
        }
        //Попытка заблокировать победу врага
        //scan left diagonal
        cell = scanLineOnStep(0, 0, 1, 1, 2, enemy, false);
        if (cell != null) {
            return cell;
        }
        //scan right diagonal
        cell = scanLineOnStep(2, 0, -1, 1, 2, enemy, false);
        if (cell != null) {
            return cell;
        }
        //scan rows
        for (int i = 0; i < 3; i++) {
            cell = scanLineOnStep(0, i, 1, 0, 2, enemy, false);
            if (cell != null) {
                return cell;
            }
        }
        //scan columns
        for (int i = 0; i < 3; i++) {
            cell = scanLineOnStep(i, 0, 0, 1, 2, enemy, false);
            if (cell != null) {
                return cell;
            }
        }
        //Простой ход, если не удалось выиграть или заблокировать
        for (int j = 3; j >= 0; j--) {
            //scan left diagonal
            cell = scanLineOnStep(0, 0, 1, 1, j, me, true);
            if (cell != null) {
                return cell;
            }
            //scan right diagonal
            cell = scanLineOnStep(2, 0, -1, 1, j, me, true);
            if (cell != null) {
                return cell;
            }
            //scan rows
            for (int i = 0; i < 3; i++) {
                cell = scanLineOnStep(0, i, 1, 0, j, me, true);
                if (cell != null) {
                    return cell;
                }
            }
            //scan columns
            for (int i = 0; i < 3; i++) {
                cell = scanLineOnStep(i, 0, 0, 1, j, me, true);
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
    * countEmpty - логическая переменная, отвечает за подсчет пустых ячеек(считать их или нет), нужна для выгодного хода, когдв не удалось заблокировать или выиграть
    */
    protected Cell scanLineOnStep(int xStart, int yStart, int xStep, int yStep, int count, String player, boolean countEmpty) {
        int k = 0;
        Cell result = null;
        int i = yStart;
        int j = xStart;

        for (int c = 0; c < 3; c++) {
            Cell cell = cells[i][j];
            //Если встретилась пустая ячейка, запоминаем ее
            if (cell.isEmpty()) {
                result = cell;
                //Если нужно их считать
                if (countEmpty)
                    k++;
            }
            else
                //Если идет последовательность одинаковых непустых клеток, то считаем ее длину
                if (cell.equals(player)) {
                    k++;
                }
                //Если встретилась другая клетка, то обнуляем счет
                else {
                    k = 0;
                }
            //Если найдена оптимальная ячейка для хода, то возвращаем ее
            if (k == count && result != null) {
                return result;
            }
            i += yStep;
            j += xStep;
        }
        return null;
    }
}