package com.codex.taxitrajectory.model;

import lombok.Data;
import java.util.Objects;

@Data
public class GridCell {
    private int row;
    private int col;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GridCell gridCell = (GridCell) o;
        return row == gridCell.row && col == gridCell.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}