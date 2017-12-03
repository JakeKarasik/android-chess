package models;

import java.util.LinkedList;

/**
 * This class extends <code>ChessPiece</code> to create a Queen.
 * Queen inherits all of the methods from <code>ChessPiece</code> while also defining the possible moves for a Queen.
 * 
 * @author Jake Karasik (jak451)
 * @author Benjamin Ker (bk375)
 */
public class Queen extends ChessPiece {
	
	/**
	 * Create new Queen given a team color.
	 * 
	 * @param color Color of piece.
	 */
	public Queen(char color) {
		super('Q', color);
	}
	
	/**
	 * Create a copy of a Queen given a Queen.
	 * 
	 * @param piece Queen to copy.
	 */
    public Queen(ChessPiece piece) {
        super(piece);
    }

	/* (non-Javadoc)
	 * @see chess.ChessPiece#listUpdate()
	 */
	@Override
	public void listUpdate(){
		int origin[] = getPos();
		possible_moves = new LinkedList<>();

		int delta[][] = {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};

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
