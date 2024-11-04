package com.ladi;

import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import static java.awt.Color.RED;

//to do:

//bugfixes, progressbar not working, AI repeats moves, AI plays the same side always
//optimizations, get rid of unnecessary and unused code
//improvements to the AI
//better gui/ui
//menu and other features


//known bugs:
//
//

//(from 9/23/2024)//changes(date, line, what changed):

//23/10/2024: integrated the stage of the game and remaining placeable pieces to the boardState StringBuilder
//23/10/2024: line 591-594: made the opposite function faster and shorter
//24/10/2024: removed most unused code segments, to make the code easier to read
//
//
//
//

public class MalmoPanel extends JPanel {
    //the depth the algorithm will use when calculating:
    public static int algDepth=9;

    //store remaining placeable and overall pieces for both players:
    public int remainingPlaceablePiecesForPlayer1 =9;
    public int remainingPlaceablePiecesForPlayer2 =9;
    public int remainingPiecesForPlayer1 =9;
    public int remainingPiecesForPlayer2 =9;

    //store every possible neighbours and possible mills:
    public static final int [][] mills = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 9, 21}, {3, 10, 18}, {6, 11, 16}, {2, 14, 23}, {5, 13, 20}, {8, 12, 17}, {15, 16, 17}, {18, 19,20}, {21, 22, 23}, {9, 10, 11}, {1, 4, 7}, {12, 13, 14}, {16, 19, 22}};
    public static final int [][] neighbours = {{1, 9}, {0, 2, 4}, {1, 14}, {4, 10}, {1, 3, 5, 7}, {4, 13}, {7, 11}, {4, 6, 8}, {7, 12}, {0, 10, 21}, {3, 9, 11, 18}, {6, 10, 15}, {8, 13, 17}, {5, 12, 14, 20}, {2, 13, 23}, {11, 16}, {15,17, 19}, {12, 16}, {10, 19}, {16, 18, 20, 22}, {13, 19}, {9, 22}, {19, 21, 23}, {14, 22}};

    //to prevent any actions while calculating:
    public boolean calculating=false;

    //is it player 1's move or not:
    private boolean player1onMove = true;

    //set to false when for example the button notices that it is in a mill:
    private boolean move = true;

    public int originalPosition =-1;
    JLabel player1Label;
    JLabel player1CounterLabel;
    JLabel player2Label;
    JLabel player2CounterLabel;
    JProgressBar pBar;
    JButton aiMoveButton;

    public ArrayList<JButton> buttonList;

    //the way to store a board's State
    public StringBuilder currentBoardState = new StringBuilder("000000000000000000000000099000");
    //0-23:boardState, with each of them contributing to one coordinate on the board;
    //24:stage of the game(0 or 1; with 1 meaning that it is no longer the placing stage of the game);
    //25-26:remaining placeable pieces for player 1 and 2 respectively;
    //27(positive or negative 0/1)-28-30):corresponding score(score of player1-player2);

    public MalmoPanel(){

        setLayout(null);

        //make the labels:
        player1Label = new JLabel("Player 1 Remaining pieces:");
        player1Label.setLocation(10,10);
        player1Label.setSize(300,30);
        player1Label.setVisible(true);
        add(player1Label);
        player1CounterLabel = new JLabel("0");
        player1CounterLabel.setLocation(200,10);
        player1CounterLabel.setSize(300,30);
        player1CounterLabel.setVisible(true);
        add(player1CounterLabel);
        player2Label = new JLabel("Player 2 Remaining pieces:");
        player2Label.setLocation(300,10);
        player2Label.setSize(300,30);
        player2Label.setVisible(true);
        add(player2Label);
        player2CounterLabel = new JLabel("0");
        player2CounterLabel.setLocation(500,10);
        player2CounterLabel.setSize(300,30);
        player2CounterLabel.setVisible(true);
        add(player2CounterLabel);

        //make the progressbar:
        pBar = new JProgressBar(0,100);
        pBar.setSize(300, 30);
        pBar.setLocation(125, 500);
        pBar.setValue(50);
        pBar.setString("50");
        add(pBar);
        pBar.setVisible(true);
        pBar.setStringPainted(true);
        pBar.setForeground(RED);

        //make the button for the AI:
        aiMoveButton = new JButton("AI");
        aiMoveButton.setSize(60,60);
        aiMoveButton.setLocation(125, 500);
        aiMoveButton.setVisible(true);
        aiMoveButton.setBackground(RED);
        add(aiMoveButton);

        buttonList = new ArrayList<>();

        //make the buttons and place them according to buttonCoordinates:
        int[][] buttonCoordinates = {{1, 1}, {1, 4}, {1, 7}, {2, 2}, {2, 4}, {2, 6}, {3, 3}, {3, 4}, {3, 5}, {4, 1}, {4, 2}, {4, 3},{4, 5},{4, 6},{4, 7},{5, 3},{5, 4},{5, 5},{6, 2},{6, 4},{6, 6},{7, 1},{7, 4},{7, 7},};
        for (int i = 0; i < buttonCoordinates.length; i++){
            JButton button = new JButton(String.valueOf(i + 1));
            button.setLocation(buttonCoordinates[i][1] * 60, buttonCoordinates[i][0] * 60);
            button.setSize(60,60);
            add(button);
            buttonList.add(button);
        }
        setVisible(true);

        refresh();

        //actionsListener when any button is pressed:
        ActionListener buttonPressedListener = event -> {

            //prevent any actions while the program is running so no contradicting input can be given while calculations are happening:
            if (!calculating){
                calculating=true;
                JButton pressedButton = (JButton) event.getSource();

                //currentPosition the determine which button has been pressed:
                int currentPosition = buttonList.indexOf(pressedButton);
                System.out.println("ActionListener: currentPosition: " + currentPosition);

                //aiMove button pressed:
                if (move && pressedButton == aiMoveButton){
                    System.out.println("ActionListener: calling aiMove");
                    currentBoardState = aiMove(currentBoardState, playerOnTurn(),algDepth);
                    refresh();
                    nextPlayerOnTurn();
                    calculating=false;
                }

                //player 1 to place a piece:
                else if(move && player1onMove && remainingPlaceablePiecesForPlayer1 > 0 && isPlacementEmpty(currentPosition,currentBoardState)){
                    currentBoardState.setCharAt(currentPosition, '1');
                    remainingPlaceablePiecesForPlayer1--;
                    currentBoardState.setCharAt(25, String.valueOf(currentBoardState.charAt(26)-1).charAt(0));
                    refresh();
                    if (amIinMill(currentPosition,1,currentBoardState)){
                        // player one to remove a piece:
                        move = false;
                    }
                    else {
                        nextPlayerOnTurn();
                    }
                }

                //player 2 to place a piece:
                else if(move && !player1onMove && remainingPlaceablePiecesForPlayer2 > 0 && isPlacementEmpty(currentPosition,currentBoardState)){
                    currentBoardState.setCharAt(currentPosition, '2');
                    remainingPlaceablePiecesForPlayer2--;
                    currentBoardState.setCharAt(26, String.valueOf(currentBoardState.charAt(26)-1).charAt(0));
                    refresh();
                    if (amIinMill(currentPosition,2,currentBoardState)) {
                        // player two to remove a piece:
                        move = false;
                    }
                    else {
                        nextPlayerOnTurn();
                    }
                }

                //taking the piece (after mill):
                else if(!move && Character.getNumericValue(currentBoardState.charAt(currentPosition))!=playerOnTurn() && Character.getNumericValue(currentBoardState.charAt(currentPosition))!=0 && !amIinMill(currentPosition, opposite(playerOnTurn()),currentBoardState)){
                    if(currentBoardState.charAt(currentPosition) == 1) {
                        remainingPiecesForPlayer1--;
                    }
                    else{
                        remainingPiecesForPlayer2--;
                    }
                    currentBoardState.setCharAt(currentPosition, '0');
                    move = true;
                    refresh();
                    nextPlayerOnTurn();
                }

                //move part 1 - picking up the piece:
                else if(move && Character.getNumericValue(currentBoardState.charAt(currentPosition)) == playerOnTurn()) {
                    originalPosition = currentPosition;
                    System.out.println("ActionListener: originalPosition: " + currentPosition);
                }

                //move part 2 - placing piece to new place:
                else if(originalPosition >-1 && move && isPlacementEmpty(currentPosition,currentBoardState) && canIMoveHere(currentPosition, originalPosition, currentBoardState)){
                    currentBoardState.setCharAt(currentPosition,String.valueOf(Character.getNumericValue(currentBoardState.charAt(originalPosition))).charAt(0));
                    currentBoardState.setCharAt(originalPosition, '0');
                    originalPosition =-1;
                    refresh();
                    if(amIinMill(currentPosition, playerOnTurn(),currentBoardState))
                        move = false;
                    else
                        nextPlayerOnTurn();
                    System.out.println("ActionListener: newCurrentPosition: " + currentPosition);
                }

                //jumping when the player 1 has only 3 pieces:
                else if(originalPosition >-1 && move && isPlacementEmpty(currentPosition,currentBoardState) &&remainingPiecesForPlayer1==3&&player1onMove){
                    currentBoardState.setCharAt(currentPosition,String.valueOf(Character.getNumericValue(currentBoardState.charAt(originalPosition))).charAt(0));
                    currentBoardState.setCharAt(originalPosition, '0');
                    originalPosition =-1;
                    refresh();
                    if(amIinMill(currentPosition, playerOnTurn(),currentBoardState))
                        move = false;
                    else
                        nextPlayerOnTurn();
                    System.out.println("ActionListener: newCurrentPosition: " + currentPosition);
                }

                //jumping when the player 2 has only 3 pieces:
                else if(originalPosition >-1 && move && isPlacementEmpty(currentPosition,currentBoardState) &&remainingPiecesForPlayer2==3&&!player1onMove){
                    currentBoardState.setCharAt(currentPosition,String.valueOf(Character.getNumericValue(currentBoardState.charAt(originalPosition))).charAt(0));
                    currentBoardState.setCharAt(originalPosition, '0');
                    originalPosition =-1;
                    refresh();
                    if(amIinMill(currentPosition, playerOnTurn(),currentBoardState))
                        move = false;
                    else
                        nextPlayerOnTurn();
                    System.out.println("ActionListener: newCurrentPosition: " + currentPosition);
                }

                // end of game:
                calculating = !(remainingPiecesForPlayer1 > 2 && remainingPiecesForPlayer2 > 2);
                if(remainingPiecesForPlayer1 < 3){
                    System.out.println("Player 2 won!");
                    calculating=false;
                } else if (remainingPiecesForPlayer2 < 3){
                    System.out.println("Player 2 won!");
                    calculating=false;
                }
            }
        };

        //add action listeners to the buttons:
        aiMoveButton.addActionListener(buttonPressedListener);
        for (JButton button : buttonList)
            button.addActionListener(buttonPressedListener);
    }

    //UNFINISHED//simple refresh function that gets called after every action to makes sure the app is in accordance with what happened:
    public void refresh(){
        refreshButtonColors();
        player1CounterLabel.setText(String.valueOf(remainingPlaceablePiecesForPlayer1));
        player2CounterLabel.setText(String.valueOf(remainingPlaceablePiecesForPlayer2));
        if (getScore(currentBoardState)<-50){
            pBar.setValue(0);
        }
        else if (getScore(currentBoardState)>50){
            pBar.setValue(100);
        }
        else{
            pBar.setValue(50+ getScore(currentBoardState));
            pBar.setString(String.valueOf(getScore(currentBoardState)));
        }
    }

    //the function that gets called when we want the AI to make the move instead of the user:
    public StringBuilder aiMove(StringBuilder boardState,int side, int depth){
        boolean maximisingPlayer= side == 2;
        if (boardState.charAt(25)=='9'&side==1){
            boardState.setCharAt(13,'1');
            return boardState;
        }
        return baseMinimax(boardState, depth, -999, +999, maximisingPlayer);
    }

    //UNFINISHED//evaluates the board based on given parameters(the parameters are unfinished):
    public StringBuilder evaluateBoard(StringBuilder boardState){
        //score -999 -- +999
        int score=0;
        //evaluationBasis(awarded points are subject to change):
        //points for having centerPieces: 5,11,14,20
        int centerPoint=90;
        int[] centerPieces = {4, 10, 13, 19};
        //points for having middle pieces: 2,8,10,12,13,15,7,23
        int middlePoint=60;
        int[] middlePieces = {1,7,9,11,12,14,6,22};
        //points for having corner pieces in the middle circle: 4,6,19,21
        int midcorPoint=50;
        int[] midcorPieces = {3, 5, 18, 20};
        //points for having corner pieces: 1,3,7,9,16,18,22,24
        int cornerPoint=30;
        int[] cornerPieces = {0, 2, 6, 8, 15, 17, 21, 23};
        //points/mills (checked places for mills:1,6,8,12,13,17,19,24
        int[] millPlaces = {1,6,8,12,13,17,19,24};
        int millPoint=20;
        //points based on placement
        for (int i=0; i<23;i++){
            for (int j = 0; j < 8; j++) {
                if (1 == boardState.charAt(i) && i == middlePieces[j]) {
                    score = score + middlePoint;
                }
                else if (2 == boardState.charAt(i) && i == middlePieces[j]) {
                    score = score - middlePoint;
                }
            }
            for (int j = 0; j < 4; j++) {
                if (1 == boardState.charAt(i) && i == centerPieces[j]) {
                    score = score + centerPoint;
                }
                else if (2 == boardState.charAt(i) && i == centerPieces[j]) {
                    score = score - centerPoint;
                }
            }
            for (int j = 0; j < 4; j++) {
                if (1 == boardState.charAt(i) && i == midcorPieces[j]) {
                    score = score + midcorPoint;
                }
                else if (2 == boardState.charAt(i) && i == midcorPieces[j]) {
                    score = score - midcorPoint;
                }
            }
            for (int j = 0; j < 8; j++) {
                if (1 == boardState.charAt(i) && i == cornerPieces[j]) {
                    score = score + cornerPoint;
                }
                else if (2 == boardState.charAt(i) && i == cornerPieces[j]) {
                    score = score - cornerPoint;
                }
            }

        }
        //points based on mills
        for (int i=0;i<8;i++){
            score=score+numberOfMills(millPlaces[i],1,boardState)*millPoint;
            score=score-numberOfMills(millPlaces[i],2,boardState)*millPoint;
        }
        return writeScore(boardState,score);
    }

    //finds the possible moves from the starting position when stuff is being only placed
    public ArrayList<StringBuilder> startingPossibleMoves(StringBuilder boardState, int side){
        int oppositeSide=opposite(side);
        ArrayList<StringBuilder>possibleBoardStatesList=new ArrayList<>();
        for (int i=0;i<24;i++){
            if (boardState.charAt(i)==String.valueOf(side).charAt(0)){
                StringBuilder newBoardState = new StringBuilder(boardState);
                newBoardState.setCharAt(i,'0');
                newBoardState.setCharAt(i,String.valueOf(side).charAt(0));
                int val = (side==1)?25:26;
                newBoardState.setCharAt(val,String.valueOf(newBoardState.charAt(val)-1).charAt(0));
                int newMillCount = 0;
                if (amIinMill(i, side,boardState)){
                    for (int k=0;k<24;k++) {
                        if (newBoardState.charAt(k)==String.valueOf(oppositeSide).charAt(0) && !amIinMill(newBoardState.charAt(k),String.valueOf(oppositeSide).charAt(0),boardState)){
                            StringBuilder newMillBoardState = new StringBuilder(boardState);
                            newMillBoardState.setCharAt(k,'0');
                            possibleBoardStatesList.add(evaluateBoard(newMillBoardState));
                            System.out.println("possibleMoves(): new newMillBoardState: " + newMillBoardState);
                            newMillCount++;
                        }
                    }
                }
                if (newMillCount == 0){
                    possibleBoardStatesList.add(evaluateBoard(newBoardState));
                    System.out.println("possibleMoves(): new newBoardState: " + newBoardState);
                }
            }
        }
        return possibleBoardStatesList;
    }

    //gives the possible moves in a list from the boardState in regard to the relevant side:
    public ArrayList<StringBuilder> possibleMoves(StringBuilder boardState, int side){

        //System.out.println("possibleMoves(): originalBoardState: " + originalBoardState);
        ArrayList<StringBuilder> possibleBoardStatesList = new ArrayList<>();

        int oppositeSide;
        if(side==1){oppositeSide=2;}else{oppositeSide=1;}

        for (int i = 0; i < 24; i++){
            if(boardState.charAt(i) == String.valueOf(side).charAt(0)){
                for (int j = 0; j < neighborsCount(i); j++) {
                    int neighbour = neighbours[i][j];
                    if (canIMoveHere(neighbour, i,boardState)) {
                        StringBuilder newBoardState = new StringBuilder(boardState);
                        newBoardState.setCharAt(i,'0');
                        newBoardState.setCharAt(neighbour,String.valueOf(side).charAt(0));
                        int newMillCount = 0;
                        if (amIinMill(neighbour, side,boardState)){
                            for (int k=0;k<24;k++) {
                                if (newBoardState.charAt(k)==String.valueOf(oppositeSide).charAt(0) && !amIinMill(newBoardState.charAt(k),String.valueOf(oppositeSide).charAt(0),boardState)){
                                    StringBuilder newMillBoardState = new StringBuilder(boardState);
                                    newMillBoardState.setCharAt(k,'0');
                                    possibleBoardStatesList.add(evaluateBoard(newMillBoardState));
                                    System.out.println("possibleMoves(): new newMillBoardState: " + newMillBoardState);
                                    newMillCount++;
                                 }
                            }
                        }
                        if (newMillCount == 0){
                            possibleBoardStatesList.add(evaluateBoard(newBoardState));
                            System.out.println("possibleMoves(): new newBoardState: " + newBoardState);
                        }
                    }
                }
            }
        }
        return possibleBoardStatesList;
    }

    //simple read and write function to read/write Score from the StringBuilder boardState:
    public StringBuilder writeScore(@NotNull StringBuilder boardState, int score){
        //score -999 -- +999
        int num=score%10;
        boardState.setCharAt(29,String.valueOf(num).charAt(0));
        num=(score-num)/10%10;
        boardState.setCharAt(28,String.valueOf(num).charAt(0));
        num=((score-num)/10-num)/10%10;
        boardState.setCharAt(27,String.valueOf(num).charAt(0));
        //27(positive or negative 0/1)
        if (score<0){
            boardState.setCharAt(26,'1');
        }
        else if(score>0){
            boardState.setCharAt(26,'0');
        }
        return boardState;
    }
    public int getScore(@NotNull StringBuilder boardState){
        int score=0;
        if(boardState.charAt(27)!='0')score=boardState.charAt(27)*100;
        if(boardState.charAt(28)!='0')score=score+boardState.charAt(28)*10;
        if(boardState.charAt(29)!='0')score=score+boardState.charAt(29);
        if(boardState.charAt(26)==1){
            score=-score;
        }
        return score;
    }

    //gives the number of Mills on the input boardState:
    public int numberOfMills(int num,int side,StringBuilder boardState){
        //num is boardPlace-1!!!
        int numberOfMills=0;
        for (int i=0; i<16;i++){
            if (Character.getNumericValue(boardState.charAt(mills[i][0]))==side &&
                Character.getNumericValue(boardState.charAt(mills[i][1]))==side &&
                Character.getNumericValue(boardState.charAt(mills[i][2]))==side){
                for (int j=0;j<3;j++){if (mills[i][j]==num) numberOfMills++;}
            }
        }
        return numberOfMills;
    }

    //checks if the input place is in a mill:
    public boolean amIinMill(int num,int side,StringBuilder boardState) {
        return (numberOfMills(num, side, boardState) > 0);
    }

    //checks if it's possible to move from oldPlace to newPlace with normal movement:
    public boolean canIMoveHere(int newPlace, int oldPlace, @NotNull StringBuilder boardState){
        //cp=current place, + public int origin
        if (Character.getNumericValue(boardState.charAt(newPlace))==0) {
            for (int i = 0; i < neighborsCount(oldPlace); i++) {
                if (isNeighbor(oldPlace, i)==newPlace)
                    return true;
            }
        }
        return false;}

    //returns the number of neighbors:
    public int neighborsCount(int num){
        return neighbours[num].length;
    }

    //checks if the two inputs are neighbors:
    public int isNeighbor(int num, int i){
        return neighbours[num][i];
    }

    //refresh the button's colors to reflect the current boardState:
    public void refreshButtonColors(){
        for (int i = 0; i < buttonList.size(); i++) {
            JButton button = buttonList.get(i);
            int colorValue = Character.getNumericValue(currentBoardState.charAt(i));
            switch (colorValue) {
                case 0:button.setBackground(Color.BLACK);break;
                case 1:button.setBackground(Color.WHITE);break;
                case 2:button.setBackground(RED);break;
            }
        }
    }

    //simple opposite function, changes 1 to 2 or 2 to 1:
    public int opposite(int num){
        return (num==1)?2:1;
    }

    //advance the turn, if player one moved then player two's turn:
    private int playerOnTurn() {return(player1onMove)?1:2;}

    //advance the turn, similar to playerOnTurn() if player one moved then player two's turn:
    private void nextPlayerOnTurn() {player1onMove = !player1onMove;}

    //make the move:(currently no real function)
    public StringBuilder makeMove(ArrayList<StringBuilder> boardStateList, StringBuilder boardState){
        return boardState;
    }

    //is the position empty:
    private boolean isPlacementEmpty (int position, @NotNull StringBuilder boardState) {
        return (Character.getNumericValue(boardState.charAt(position))==0);
    }

    //checks if the game is over:
    public boolean gameIsOver(StringBuilder boardState){
        int p1=0;
        int p2=0;
        for (int i=0;i<24;i++){
            if (boardState.charAt(i)=='2'){
                p2++;
            }else if (boardState.charAt(i)=='1'){
                p1++;
            }
        }
        return p1 < 3 || p2 < 3;
    }

    //minimax function with Alpha-Beta pruning to determine the best move in the game:
    public StringBuilder baseMinimax(StringBuilder boardState, int depth, int alpha, int beta, boolean isMaximizingPlayer) {

        int side=(isMaximizingPlayer)?2:1;

        // Base case: if the game is over (win/loss/draw) or max depth reached:
        if (gameIsOver(boardState) || depth == 0) {
            // Evaluate the current game state and return the score:
            return evaluateBoard(boardState);
        }
        StringBuilder bestState = null;

        // Maximizing player's turn (your turn):
        if (isMaximizingPlayer) {
            // Initialize maxEval to a very low value:
            int maxEval = Integer.MIN_VALUE;

            // Loop through all possible moves for the maximizing player:
            ArrayList<StringBuilder> listOfPossibleMoves = possibleMoves(boardState, side);
            if((int) boardState.charAt(26) >0) {
                listOfPossibleMoves = startingPossibleMoves(boardState, side);
            }
            // Simulate the move and get the resulting game state:
            for (int i=0; i< listOfPossibleMoves.size();i++) {
                StringBuilder newState = makeMove(listOfPossibleMoves, listOfPossibleMoves.get(i));

                // Recursively call minimax for the opponent's turn (minimizing player):
                StringBuilder potentialState = baseMinimax(newState, depth - 1, alpha, beta, false);

                int score = getScore(evaluateBoard(potentialState));

                // If this move is better, update maxEval and bestState:
                if (score > maxEval){
                    maxEval = score;
                    bestState = newState;
                }

                // Alpha-Beta pruning: update alpha and prune if necessary:
                alpha = Math.max(alpha, maxEval);
                if (beta <= alpha) {
                    // Prune the remaining branches:
                    break;
                }
            }

            return bestState;
            // Minimizing player's turn (opponent's turn):
        }
        else{
            // Initialize to the highest possible value:
            int minEval = Integer.MAX_VALUE;
            // Loop through all possible moves for the minimizing player:
            ArrayList<StringBuilder>listOfPossibleMoves=possibleMoves(boardState,side);
            if((int) boardState.charAt(25) >0) {
                listOfPossibleMoves = startingPossibleMoves(boardState, side);
            }

            for (int i=0; i< listOfPossibleMoves.size();i++) {
                // Generate the new board state by making this move:
                StringBuilder newState = makeMove(listOfPossibleMoves, listOfPossibleMoves.get(i));

                // Call minimax recursively for the maximizing player's turn:
                StringBuilder potentialState = baseMinimax(newState, depth - 1, alpha, beta, true);

                // Evaluate the resulting board state:
                int score = getScore(evaluateBoard(potentialState));

                // If this move is better, update minEval and bestState:
                if (score < minEval) {
                    minEval = score;
                    bestState = newState;
                }

                // Alpha-Beta pruning: update beta and prune if necessary:
                beta = Math.min(beta, minEval);
                if (beta <= alpha) {
                    // Prune the remaining branches:
                    break;
                }
            }
            // Return the best resulting board state:
            return bestState;
        }
    }
}