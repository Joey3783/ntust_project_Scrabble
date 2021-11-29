package GameInfomation;

import java.util.ArrayList;


public class Board {

    private ArrayList<ArrayList<Tile>> board;
    private final int size = 9;

    public Board(){

        board = new ArrayList<>(size);

        for(int i=0;i<size;i++){
            board.add(new ArrayList<>(size));
        }

        for(int row=0;row<size;row++){
            for(int col=0;col<size;col++) {
                board.get(row).add(col, new Tile());
            }
        }
    }

    public ArrayList<ArrayList<Tile>> getBoard() {
        return board;
    }

    public ArrayList<Character> getBoardChar(){
        ArrayList<Character> boardchar = new ArrayList<>();
        for(int row=0;row<board.size();row++){
            for(int col=0;col<board.size();col++){
                if(board.get(row).get(col).isAlreadyOnBoard()){
                    boardchar.add(board.get(row).get(col).getLetter());
                }else {
                    boardchar.add('*');
                }
            }
        }
        return boardchar;
    }

    public ArrayList<Character> getBoardScore(){
        ArrayList<Character> boardscore = new ArrayList<>();
        for(int row=0;row<board.size();row++){
            for(int col=0;col<board.size();col++){

                boardscore.add(String.valueOf(board.get(row).get(col).getScore()).toCharArray()[0]);

            }
        }
        return boardscore;
    }

    public ArrayList<ArrayList<Tile>> getUnlockBoard(){
        ArrayList<ArrayList<Tile>> unlockBoard = new ArrayList<>();
        for(int i=0;i<size;i++){
            unlockBoard.add(new ArrayList<>(size));
        }

        for(int row=0;row<size;row++){
            for(int col=0;col<size;col++) {
                unlockBoard.get(row).add(col, new Tile());
            }
        }

        for(int row=0;row<board.size();row++){
            for(int col=0;col<board.size();col++){

                if(board.get(row).get(col).isAlreadyOnBoard()){
                    unlockBoard.get(row).set(col,board.get(row).get(col));
                }

            }
        }
        return unlockBoard;
    }

    public ArrayList<ArrayList<Character>> getUnlockBoardlist(){
        ArrayList<ArrayList<Character>> unlockBoard = new ArrayList<>();

        for(int i=0;i<size;i++){
            unlockBoard.add(new ArrayList<>(size));
        }

        for(int row=0;row<size;row++){
            for(int col=0;col<size;col++) {
                unlockBoard.get(row).add(col, ' ');
            }
        }

        for(int row=0;row<board.size();row++){
            for(int col=0;col<board.size();col++){

                if(board.get(row).get(col).isAlreadyOnBoard()){
                    unlockBoard.get(row).set(col,board.get(row).get(col).getLetter());
                }

            }
        }
        return unlockBoard;
    }
}
