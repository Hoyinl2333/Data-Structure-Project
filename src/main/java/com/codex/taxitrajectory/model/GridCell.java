package com.codex.taxitrajectory.model;

import lombok.Data;

@Data
public class GridCell {
    private int row;
    private int col;
    private int count;  // 该网格内的出租车数量
}