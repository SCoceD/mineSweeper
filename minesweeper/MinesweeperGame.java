package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellValueEx(x, y, Color.ORANGE, "");
//                if(gameField[y][x].isMine){setCellValue(x, y, MINE);}
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors(){
        for (int i = 0; i <gameField.length; i++) {
            for (int j = 0; j < gameField[i].length; j++) {
                for (GameObject gameObject: getNeighbors(gameField[j][i])) {
                    if(gameObject.isMine && !gameField[j][i].isMine){
                        gameField[j][i].countMineNeighbors++;
                    }
                }
            }
        }
    }

    private void openTile(int x, int y){
        if(!gameField[y][x].isOpen && !isGameStopped && !gameField[y][x].isFlag){
            if(gameField[y][x].isMine){
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            }
            gameField[y][x].isOpen = true;
            if(!gameField[y][x].isMine){score +=5;}
            setScore(score);
            countClosedTiles--;

            if(!gameField[y][x].isMine){
                if (gameField[y][x].countMineNeighbors > 0){
                    setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                }
                else{setCellValue(x, y, "");}
                    setCellColor(x, y, Color.GREEN);
                if(gameField[y][x].countMineNeighbors == 0){
                    for (GameObject o : getNeighbors(gameField[y][x])) {
                        if(!o.isOpen){
                            openTile(o.x, o.y );}
                    }
                }
            }
            else{setCellValue(x, y, MINE);}
            if(countClosedTiles==countMinesOnField && !gameField[y][x].isMine){win();}
        }

    }

    private void markTile(int x, int y){
        if(!isGameStopped){
            if(!gameField[y][x].isOpen && countFlags >0 && !gameField[y][x].isFlag) {
                gameField[y][x].isFlag = true;
                setCellValue(x, y, FLAG);
                setCellColor(x, y, Color.YELLOW);
                countFlags--;
            }
            else if(gameField[y][x].isFlag){
                gameField[y][x].isFlag = false;
                setCellValue(x, y, "");
                setCellColor(x, y, Color.ORANGE);
                countFlags++;
            }
        }
    }

    private void gameOver(){
        showMessageDialog(Color.RED, "Pizdec", Color.BLACK, 30);
        isGameStopped = true;
    }

    private void win(){
        showMessageDialog(Color.AQUAMARINE, "Zaebca", Color.WHITE, 35);
        isGameStopped = true;
    }

    private void restart(){
        countClosedTiles = SIDE * SIDE;
        score = 0;
        isGameStopped = false;
        setScore(score);
        countMinesOnField = 0;
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if(!isGameStopped) {
            openTile(x, y);
        }else {
        restart();}
    }

    @Override
    public void onMouseRightClick(int x, int y){
        markTile(x, y);
    }
}