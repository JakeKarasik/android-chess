package com.example.group55.androidchess55.models;

import java.util.LinkedList;

/**
 * This class extends <code>ChessPiece</code> to create a Rook.
 * Rook inherits all of the methods from <code>ChessPiece</code> while also defining the possible moves for a Rook.
 * 
 * @author Jake Karasik (jak451)
 * @author Benjamin Ker (bk375)
 */
public class Rook extends ChessPiece {
	
	/**
	 * Create new Rook given a team color.
	 * 
	 * @param color Color of piece.
	 */
	public Rook(char color) {
		super('R', color);
	}

	/**
	 * Create a copy of a Rook given a Rook.
	 * 
	 * @param piece Rook to copy.
	 */
    public Rook(ChessPiece piece) {
        super(piece);
    }

    /* (non-Javadoc)
     * @see chess.ChessPiece#listUpdate()
     */
    @Override
    public void listUpdate(){
        int origin[] = getPos();
        possible_moves = new LinkedList<>();

        int delta[][] = {{1,0},{0,1},{-1,0},{0,-1}};

        for (int[] bop : delta) {
            int move[] = { origin[0] + bop [0] , origin[1] + bop [1] };

            while(Chess.inBounds(move)){
                ChessPiece dest_piece = Chess.board[move[0]][move[1]];
                // If space is empty, can move to it
                if(dest_piece == null ){
                    possible_moves.add(move.clone());
                }
                // If found enemy, max distance reached, break out
                if(dest_piece != null && (isEnemyOf(dest_piece) || Chess.zone_check_mode)){
                    possible_moves.add(move.clone());
                    break;
                }
                // If found ally, can't move there, break out
                if(dest_piece != null && !isEnemyOf(dest_piece)){
                    break;
                }
                move[0] = move[0] + bop[0];
                move[1] = move[1] + bop[1];
            }
        }
    }
}
