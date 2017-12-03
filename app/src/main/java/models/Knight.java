package models;

import java.util.LinkedList;

/**
 * This class extends <code>ChessPiece</code> to create a Knight.
 * Knight inherits all of the methods from <code>ChessPiece</code> while also defining the possible moves for a Knight.
 * 
 * @author Jake Karasik (jak451)
 * @author Benjamin Ker (bk375)
 */
public class Knight extends ChessPiece {
	
	/**
	 * Create new Knight given a team color.
	 * 
	 * @param color Color of piece.
	 */
	public Knight(char color) {
		super('N', color);
	}

	/**
	 * Create a copy of a Knight given a Knight.
	 * 
	 * @param piece Knight to copy.
	 */
    public Knight(ChessPiece piece) {
        super(piece);
    }

	/* (non-Javadoc)
	 * @see chess.ChessPiece#listUpdate()
	 */
	@Override
	public void listUpdate(){
        int origin[] = getPos();

        // Clear and start from empty

        possible_moves =  new LinkedList<>();
        int[] left2_up1 	= {origin[0] - 1, origin[1] - 2};
        int[] left1_up2		= {origin[0] - 2, origin[1] - 1};
        int[] right2_up1 	= {origin[0] - 1, origin[1] + 2};
        int[] right1_up2 	= {origin[0] - 2, origin[1] + 1};
        int[] right2_down1	= {origin[0] + 1, origin[1] + 2};
        int[] right1_down2	= {origin[0] + 2, origin[1] + 1};
        int[] left2_down1 	= {origin[0] + 1, origin[1] - 2};
        int[] left1_down2	= {origin[0] + 2, origin[1] - 1};

        int[][] standard_moves = {left2_up1, left1_up2, right2_up1, right1_up2,
                right2_down1, right1_down2,left2_down1, left1_down2};

        for(int[] move : standard_moves){

            if (Chess.inBounds(move)) {

                ChessPiece dest_piece = Chess.board[move[0]][move[1]];

                //If moves are valid, add to list
                if (dest_piece == null || isEnemyOf(dest_piece) || Chess.zone_check_mode) {
                    possible_moves.add(move.clone());
                }
            }
        }
	}
}
