package com.sujayt123.service;

/**
 * Created by sujay on 8/21/17.
 */
public class TestData {
    private char[][] invalidBoard;
    private char[][] invalidBoard2;
    private char[][] validBoard;
    private String gameQueue1;
    private String gameQueue2;
    private String gameQueue3;

    public TestData(char[][] invalidBoard, char[][] invalidBoard2, char[][] validBoard, String gameQueue1, String gameQueue2, String gameQueue3) {
        this.invalidBoard = invalidBoard;
        this.invalidBoard2 = invalidBoard2;
        this.validBoard = validBoard;
        this.gameQueue1 = gameQueue1;
        this.gameQueue2 = gameQueue2;
        this.gameQueue3 = gameQueue3;
    }

    public char[][] getInvalidBoard() {
        return invalidBoard;
    }

    public void setInvalidBoard(char[][] invalidBoard) {
        this.invalidBoard = invalidBoard;
    }

    public char[][] getInvalidBoard2() {
        return invalidBoard2;
    }

    public void setInvalidBoard2(char[][] invalidBoard2) {
        this.invalidBoard2 = invalidBoard2;
    }

    public char[][] getValidBoard() {
        return validBoard;
    }

    public void setValidBoard(char[][] validBoard) {
        this.validBoard = validBoard;
    }

    public String getGameQueue1() {
        return gameQueue1;
    }

    public void setGameQueue1(String gameQueue1) {
        this.gameQueue1 = gameQueue1;
    }

    public String getGameQueue2() {
        return gameQueue2;
    }

    public void setGameQueue2(String gameQueue2) {
        this.gameQueue2 = gameQueue2;
    }

    public String getGameQueue3() {
        return gameQueue3;
    }

    public void setGameQueue3(String gameQueue3) {
        this.gameQueue3 = gameQueue3;
    }
}
