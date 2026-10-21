package com.school.schedule.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Matrix {
    private final String title;
    private final String cornerLabel;
    private final List<String> rowLabels;
    private final List<String> days;
    private final int periods;           // sub-columns per day
    private final boolean periodHeader;  // show a Tiết sub-header row
    private final String[][] cells;
}