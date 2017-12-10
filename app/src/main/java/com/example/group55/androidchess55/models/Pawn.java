package com.example.group55.androidchess55.models;

import com.example.group55.androidchess55.activities.ChessBoard.ChessBoardActivity;

import java.util.*;

/**
 * This class extends <code>ChessPiece</code> to create a Pawn.
 * Pawn inherits all of the methods from <code>ChessPiece</code> while also defining the possible moves for a Pawn including Enpassant.
 * 
 * @author Jake Karasik (jak451)
 * @author Benjamin Ker (bk375)
 */
public class Pawn extends ChessPiece {

	/**
	 * Create new Pawn given a team color.
	 * 
	 * @param color Color of piece.
	 */
	public Pawn(char color) {
        super('P', color);
    }
	
	/**
	 * Create a copy of a Pawn given a Pawn.
	 * 
	 * @param piece Pawn to copy.
	 */
    public Pawn(ChessPiece piece) {
        super(piece);
    }

    /**
     * Helper for listUpdate to deal with pawn's moving diagonally.
     * 
     * @param move A diagonal move coordinate to be made by this pawn.
     * @param delta Determines direction based on color of piece.
     */
    private void pawnDiag(int move[], int delta){
        if(ChessBoardActivity.inBounds(move)) {

            if( ChessBoardActivity.zone_check_mode ){
                possible_moves.add(move.clone());
                // If there's a piece, it can be captured
            }else if(ChessBoardActivity.board[move[0]][move[1]] != null && ChessBoardActivity.board[move[0]][move[1]].isEnemyOf(this)) {
                possible_moves.add(move.clone());
                // Check for enpassant
            }else if(   ChessBoardActivity.board[move[0] - delta][move[1]] != null &&
                        ChessBoardActivity.board[move[0] - delta][move[1]].getName() == getName() &&
                        ChessBoardActivity.board[move[0] - delta][move[1]].getColor() != getColor() &&
                        ChessBoardActivity.turn - ChessBoardActivity.board[move[0] - delta][move[1]].hasMoved() == 1){

                possible_moves.add(move.clone());
            }
        }
    }

    /* (non-Javadoc)
     * @see chess.ChessPiece#listUpdate()
     */
    @Override
    public void listUpdate(){
	    int origin[] = getPos();
	    int delta;
	    if(getColor() == 'w'){  delta = -1; }else{  delta = 1;  }

        // Clear and start from empty
        possible_moves =  new LinkedList<>();
        int move[] = new int[2];

        // Can move forward one space
        move[0] = origin[0] + delta;
        move[1] = origin[1];
        // And within bounds
        if(ChessBoardActivity.inBounds(move) && ChessBoardActivity.board[move[0]][move[1]] == null){
            // Clone object and add to LinkedList
            possible_moves.add(move.clone());
            // Can move forward two spaces
            move[0] = origin[0] + 2*delta;
            move[1] = origin[1];
            // And within bounds and not blocked
            if(ChessBoardActivity.inBounds(move) && ChessBoardActivity.board[move[0]][move[1]] == null && hasMoved() == 0){
                possible_moves.add(move.clone());
            }
        }
        // Can move diagonally
        move[0] = origin[0] + delta;
        move[1] = origin[1] + delta;
        pawnDiag(move, delta);
        move[0] = origin[0] + delta;
        move[1] = origin[1] - delta;
        pawnDiag(move, delta);
    }

    /* (non-Javadoc)
     * @see chess.ChessPiece#move(int[])
     */
    @Override
    public boolean move(int[] dest){
        if (isValidMove(dest)) {
            int[] old_pos = getPos();

            // If moving by two, open it up to enpassant
            if (Math.abs(dest[0] - old_pos[0]) == 2) {
                setMoved(ChessBoardActivity.turn);
            } else {
                setMoved(-2);
            }
            // Check enpassant
            int delta;
            if (getColor() == 'w') {  delta = -1; } else {  delta = 1;  }

            // Right diagonal
            if(     (ChessBoardActivity.board[dest[0]][dest[1]] == null) && // Null destination
                    (dest[0] == old_pos[0] + delta) && // Right diagonal
                    (dest[1] == old_pos[1] + delta)){
                ChessBoardActivity.board[old_pos[0]][old_pos[1] + delta] = null;
            }
            // Left diagonal
            if(     (ChessBoardActivity.board[dest[0]][dest[1]] == null) && // Null destination
                    (dest[0] == old_pos[0] + delta) && // Right diagonal
                    (dest[1] == old_pos[1] - delta)){
                ChessBoardActivity.board[old_pos[0]][old_pos[1] - delta] = null;
            }
            ChessBoardActivity.board[dest[0]][dest[1]] = this;
            ChessBoardActivity.board[old_pos[0]][old_pos[1]] = null;
            setPos(dest);

            return true;
        }else{ return false; }
    }
}
