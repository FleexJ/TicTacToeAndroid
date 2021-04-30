package com.example.tictactoeandroid.component;

public class GotCellToStepException extends Exception {

    private Cell cell;

    public GotCellToStepException(Cell cell) {
        this.cell = cell;
    }

    public Cell getCell() {
        return cell;
    }
}
