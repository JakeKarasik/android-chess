package com.example.group55.androidchess55.models;

import com.example.group55.androidchess55.activities.ChessBoard.ChessBoardActivity;

import java.util.LinkedList;

/**
 * This class extends <code>ChessPiece</code> to create a Bishop.
 * Bishop inherits all of the methods from <code>ChessPiece</code> while also defining the possible moves for a Bishop.
 * 
 * @author Jake Karasik (jak451)
 * @author Benjamin Ker (bk375)
 */
public class Bishop extends ChessPiece {

	/**
	 * Create new Bishop given a team color.
	 * 
	 * @param color Color of piece.
	 */
	public Bishop(char color) {
		super('B', color);
	}

	/**
	 * Create a copy of a Bishop given a Bishop.
	 * 
	 * @param piece Bishop to copy.
	 */
    public Bishop(ChessPiece piece) {
        super(piece);
    }

    /* (non-Javadoc)
     * @see com.example.group55.androidchess55.ChessPiece#listUpdate()
     */
    @Override
    public void listUpdate(){
        int origin[] = getPos();
        possible_moves = new LinkedList<>();

        int delta[][] = {{1,1},{-1,1},{-1,-1},{1,-1}};

        for (int[] bop : delta) {
            int move[] = { origin[0] + bop [0] , origin[1] + bop [1] };

            while(ChessBoardActivity.inBounds(move)){
                ChessPiece dest_piece = ChessBoardActivity.board[move[0]][move[1]];
                // If space is empty, can move to it

                if(dest_piece == null ){
                    possible_moves.add(move.clone());
                }
                // If found enemy, max distance reached, break out
                if(dest_piece != null && (isEnemyOf(dest_piece) || ChessBoardActivity.zone_check_mode)){
                    possible_moves.add(move.clone());
					if(dest_piece.getName() == 'K' && isEnemyOf(dest_piece)){
						move[0] = move[0] + bop[0];
						move[1] = move[1] + bop[1];
						possible_moves.add(move.clone());
					}
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
