package com.example.group55.androidchess55.models;

import android.util.Log;

import com.example.group55.androidchess55.activities.ChessBoard.ChessBoardActivity;

import java.io.Serializable;
import java.util.*;

/**
 * An abstract class used to define the outline of a chess piece. 
 * It gives the ability to set/get a piece's color, name, position, number of moves, and all possible moves it can make.
 * It gives the ability to check if a given destination is safe from enemy pieces.
 * It also gives pieces the ability to check if a given move is valid and actually move to the requested position on the board.
 * 
 * @author Jake Karasik (jak451)
 * @author Benjamin Ker (bk375)
 */
public abstract class ChessPiece implements Serializable {
	/**
	 * Used for tracking serialized version
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Piece name.
	 */
	private char name;
	/**
	 * Piece color.
	 */
	private char color;
	/**
	 * Position of piece within board.
	 */
	private int[] pos = new int[2];
	/**
	 * Number of times piece has moved.
	 */
	private int has_moved = 0;
    /**
     * List of possible/valid moves piece can make.
     */
    public LinkedList<int[]> possible_moves;
	
	/**
	 * Create new chess piece given a name and color.
	 * 
	 * @param name Name of piece.
	 * @param color Color of piece.
	 */
	public ChessPiece(char name, char color) {
		this.name = name;
		this.color = color;
	}

	/**
	 * Create new chess piece given chess piece.
	 * 
	 * @param piece Piece to copy.
	 */
    public ChessPiece(ChessPiece piece) {
        name = piece.name;
        color = piece.color;
        setPos(piece.getPos().clone());
        has_moved = piece.has_moved;
        //listUpdate();
    }
	
	/**
	 * Gets number of times piece was moved.
	 * 
	 * @return Times moved.
	 */
	public int hasMoved() { return has_moved; }

	/**
	 * Set number of moves a piece has made.
	 * 
	 * @param turn Times moved.
	 */
	public void setMoved(int turn){ has_moved = turn; }

	/**
	 * Gets name of piece as char.
	 * 
	 * @return Piece name as char.
	 */
	public char getName() { return name; }      // Returns name of piece

	/**
	 * Gets color of piece.
	 * 
	 * @return Piece color.
	 */
	public char getColor() { return color; }    // Returns color of piece

	/**
	 * Identify piece by it's color char followed by it's name char.
	 * 
	 * @return Piece string returned in format colorName.
	 */
	public String toString() { return color + "" + name; }  // Returns the color and name of piece

	/**
	 * Gets current position of piece within board.
	 * 
	 * @return Coordinates of piece within board.
	 */
	public int[] getPos() { return pos; }       // Returns the position of the piece

	/**
	 * Sets the current position of piece within board.
	 * 
	 * @param pos Coordinates of new position within board.
	 */
	public void setPos(int[] pos) {          // Updates the position of the piece
		this.pos[0] = pos[0];
		this.pos[1] = pos[1];
	}
	
	/**
	 * Identifies if passed piece is opposite color of this piece.
	 * 
	 * @param piece Piece to compare this's color with.
	 * @return <code>true</code> if passed piece is opposite color of this piece.
	 */
	public boolean isEnemyOf(ChessPiece piece) {
		return piece.getColor() != this.getColor();
	}

	/**
	 * Builds escape_check list for getting out of check.
	 * 
     * @return <code>true</code> if escape_check list is empty.
     */
    public boolean mateChecker(){

	    // Keep track of possible escape moves
        ChessBoardActivity.escape_check = new LinkedList<>();
        ChessBoardActivity.deny_check = new LinkedList<>();

        // Update and check if there are any possible moves
	    listUpdate();

	    // Add all valid moves by the King
        if (possible_moves.size() != 0) { ChessBoardActivity.escape_check.addAll(possible_moves); }


        // Iterate through the board looking your friends

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece friend = ChessBoardActivity.board[i][j];

                if ((friend != null) &&               // Not null
                    (!isEnemyOf(friend)) &&           // Same color
                    (friend.getName() != 'K')){       // Not same piece (king)

					int saveHasMoved = friend.hasMoved();
                    // Update list of moves for friendly piece
                    friend.listUpdate();

                    // If moves exist...
                    if (friend.possible_moves.size() != 0) {

                        // Check each move to see if it can get you out of check

                        for (int[] item : friend.possible_moves) {

                            // Clone the pieces, pawn used for special case
                            ChessPiece cloned_origin = ChessBoardActivity.cloner(friend); // A copy of our friendly piece
                            ChessPiece cloned_dest; // A copy of our destination
                            ChessPiece cloned_pawn = null; // A copy of our pawn for enpassant

                            // SPECIAL CASE: ENPASSANT
                            if	(friend.getName() == 'P' &&                  // Is a pawn
                                 ChessBoardActivity.board[item[0]][item[1]] == null &&    // Moving to empty space
                                 friend.getPos()[1] != item[1]) {               // diagonally
                                    
                                int delta = (ChessBoardActivity.turn % 2 == 0) ? 1 : -1;
                                cloned_pawn = new Pawn(ChessBoardActivity.board[item[0] - delta][item[1]]);
                                cloned_dest = null;
                                friend.move(item);

                            } else if (ChessBoardActivity.board[item[0]][item[1]] != null) {
                                // Clone our friendly piece and the piece it can take, then move
                                ChessPiece toClone = ChessBoardActivity.board[item[0]][item[1]];
                                cloned_dest = ChessBoardActivity.cloner(toClone);
                                friend.move(item);

                            } else {
                                // Blocking
                                cloned_dest = null;
                                friend.move(item);
                            }

                            // Check if this breaks check
                            if (isSafe(pos, color)) {
                                ChessBoardActivity.deny_check.add(item.clone());
                            }
                            // Revert move
                            ChessBoardActivity.board[cloned_origin.getPos()[0]][cloned_origin.getPos()[1]] = friend;
                            friend.setPos(cloned_origin.getPos());
                            friend.setMoved(saveHasMoved);


                            ChessBoardActivity.board[item[0]][item[1]] = cloned_dest;
                            if(cloned_dest != null) {
                                ChessBoardActivity.board[item[0]][item[1]].setPos(cloned_dest.getPos());
                            }

                            if (cloned_pawn != null) {
                                ChessBoardActivity.board[cloned_pawn.getPos()[0]][cloned_pawn.getPos()[1]] = cloned_pawn;
                                ChessBoardActivity.board[cloned_pawn.getPos()[0]][cloned_pawn.getPos()[1]].setPos(cloned_pawn.getPos());
                            }
                        }
                    }
                }


            }
        }
        return ( ChessBoardActivity.escape_check.size() == 0 && ChessBoardActivity.deny_check.size() == 0);
    }
    /**
     * Checks if a given space is safe from the enemy color.
     * 
     * @param target Space is check.
     * @param color Color of this.
     * @return <code>false</code> if space is threatened by enemy color.
     */
    public static boolean isSafe(int[] target, char color) {

        ChessBoardActivity.zone_check_mode = true;

	    for (int i = 0; i < 8; i++) {
	        for (int j = 0; j < 8; j++) {
	            ChessPiece piece = ChessBoardActivity.board[i][j];
	            if (piece != null && piece.getColor() != color && // Not null or same color
                   (i != target[0] || j != target[1])) { // Not same piece
					piece.listUpdate();
	                if (piece.possible_moves.size() != 0) {
	                    for (int[] item : piece.possible_moves) {
	                        if (Arrays.equals(item,target)) {
	                            ChessBoardActivity.zone_check_mode = false;
	                            return false;
                            }
                        }
                    }
                }
            }
        }

        ChessBoardActivity.zone_check_mode = false;
        return true;
    }

	/**
	 * Checks possible_moves to see if given destination is valid.
	 * 
	 * @param dest Position to check is valid destination given current location.
	 * @return <code>true</code> if requested dest is in <code>possible_moves</code>.
	 */
	public boolean isValidMove(int[] dest) {
		listUpdate();
		for(int[] item : possible_moves) {
		   if (Arrays.equals(item, dest)) {
		        return true;
		   }
		}
		return false;
	}
	
	/**
	 * Attempts to move piece to given destination. 
	 * Calls <code>isValidMove()</code>, and if it returns <code>true</code>, than the move is made and the board is updated accordingly.
	 * 
	 * @param dest Position to move piece to.
	 * @return <code>true</code> if dest is valid and piece is moved.
	 */
	public boolean move(int[] dest) {
		if (isValidMove(dest)) {
			int[] old_pos = getPos();
			ChessBoardActivity.board[dest[0]][dest[1]] = this;
			ChessBoardActivity.board[old_pos[0]][old_pos[1]] = null;
			setPos(dest);
			setMoved(hasMoved() + 1);
			return true;
		}
		return false;
	}

	/**
	 * Updates <code>possibles_moves</code> containing all possible moves that the piece can make.
	 */
	public abstract void listUpdate();

}