package com.example.group55.androidchess55.models;

import java.util.LinkedList;

/**
 * This class extends <code>ChessPiece</code> to create a King.
 * King inherits all of the methods from <code>ChessPiece</code> while also defining the possible moves for a King including castling.
 * 
 * @author Jake Karasik (jak451)
 * @author Benjamin Ker (bk375)
 */
public class King extends ChessPiece {
	
	/**
	 * Create new King given a team color.
	 * 
	 * @param color Color of piece.
	 */
	public King(char color) {
		super('K', color);
	}
	
	/**
	 * Create a copy of a King given a King.
	 * 
	 * @param piece King to copy.
	 */
    public King(ChessPiece piece) {
        super(piece);
    }

	/**
	 * Determines if (this) King is in check by using it's current position.
	 * 
	 * @return <code>true</code> if (this) King is in check.
	 */
	public boolean isInCheck() {
		return !isSafe(getPos(), getColor());
	}

	/**
	 * A modified version of <code>listUpdate()</code> that doesn't include castling and is used only for safe zone checking.
	 */
	public void listUpdateNoSpec() {
		int origin[] = getPos(); //[ROW, COL]
		possible_moves = new LinkedList<>();

		//Standard moves
		int[] up 			= {origin[0] - 1, origin[1]};
		int[] up_right 		= {origin[0] - 1, origin[1] + 1};
		int[] right 		= {origin[0], origin[1] + 1};
		int[] down_right 	= {origin[0] + 1, origin[1] + 1};
		int[] down 			= {origin[0] + 1, origin[1]};
		int[] down_left 	= {origin[0] + 1, origin[1] - 1};
		int[] left 			= {origin[0], origin[1] - 1};
		int[] up_left 		= {origin[0] - 1, origin[1] - 1};

		int[][] standard_moves = {up, up_right, right, down_right, down, down_left, left, up_left};

		for (int[] move : standard_moves) {

			if (Chess.inBounds(move)) {
				ChessPiece dest_piece = Chess.board[move[0]][move[1]];
				//If moves are within bounds
                if (Chess.zone_check_mode) {
                    possible_moves.add(move.clone());
                }else if ((dest_piece == null || isEnemyOf(dest_piece))) {
					possible_moves.add(move.clone());
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see chess.ChessPiece#listUpdate()
	 */
	@Override
	public void listUpdate() {
		int origin[] = getPos(); //[ROW, COL]
		possible_moves = new LinkedList<>();
		
		//Standard moves
		int[] up 			= {origin[0] - 1, origin[1]};
		int[] up_right 		= {origin[0] - 1, origin[1] + 1};
		int[] right 		= {origin[0], origin[1] + 1}; 
		int[] down_right 	= {origin[0] + 1, origin[1] + 1}; 
		int[] down 			= {origin[0] + 1, origin[1]};
		int[] down_left 	= {origin[0] + 1, origin[1] - 1};
		int[] left 			= {origin[0], origin[1] - 1};
		int[] up_left 		= {origin[0] - 1, origin[1] - 1};
		
		int[][] standard_moves = {up, up_right, right, down_right, down, down_left, left, up_left};
		
		for (int[] move : standard_moves) {
			
			if (Chess.inBounds(move)) {
                ChessPiece dest_piece = Chess.board[move[0]][move[1]];

                //If moves are valid, add to list, if it would put itself in check, cannot make that move
                if (Chess.zone_check_mode) {
                    possible_moves.add(move.clone());
                // Either safe or enemy, AND the move won't land king in danger
                } else if ((dest_piece == null || isEnemyOf(dest_piece)) && ChessPiece.isSafe(move, getColor())) {
					possible_moves.add(move.clone());
				}
			}
		}
		
		//If can castle, add possible moves to list
		if(hasMoved() == 0  && !isInCheck()){
			LinkedList<int[]> castling_moves = getCastlingMoves();

			if (castling_moves != null && castling_moves.size() > 0) {
				for (int[] move : castling_moves) {
					possible_moves.add(move.clone());
				}
			}
		}
	}

	/**
	 * Gets LinkedList of moves available for castling.
	 * 
	 * @return <code>null</code> if no castling moves are available.
	 */
	public LinkedList<int[]> getCastlingMoves() {
		ChessPiece left_rook_pos, right_rook_pos;
		int row = this.getColor() == 'b' ? 0 : 7;

		
		LinkedList<int[]> results = new LinkedList<int[]>();

		left_rook_pos = Chess.board[row][0];
		right_rook_pos = Chess.board[row][7];		
		
		// Check if piece is rook and has never moved
		if (left_rook_pos != null && 
			left_rook_pos.getName() == 'R' && 
			left_rook_pos.hasMoved() == 0) {
			
			boolean left_safe = true;
			for (int i = 0; i < 3; i++) {
				int[] test_spot = {row, i + 1};
				if (Chess.board[row][i + 1] != null || !isSafe(test_spot, getColor())) {
					left_safe = false;
					break;
				}
			}
			
			if (left_safe) {
				results.add(new int[] {row, 2});
			}
		}
		
		if (right_rook_pos != null && 
			right_rook_pos.getName() == 'R' && 
			right_rook_pos.hasMoved() == 0) {
			
			boolean right_safe = true;
			for (int i = 0; i < 2; i++) {
				int[] test_spot = {row, i + 5};
				if (Chess.board[row][i + 5] != null || !isSafe(test_spot, getColor())) {
					right_safe = false;
					break;
				}
			}
			
			if (right_safe) {
				results.add(new int[] {row, 6});
			}
		}

		return results;

	}
	
	/**
	 * Checks if current is castling.
	 * 
	 * @param dest Move to check.
	 * @return <code>true</code> if current move is castling.
	 */
	public boolean isCastling(int[] dest) {
		int row = this.getColor() == 'b' ? 0 : 7;
		return this.hasMoved() == 0 && !this.isInCheck() && (dest[1] == 6 || dest[1] == 2) && dest[0] == row;
	}
	
	/**
	 * Gets final position of where the rook will be located after castling.
	 * 
	 * @param dest Position where king will be moving to.
	 * @return Coordinates of where castled rook will be placed.
	 */
	public int[] castleFinalPos(int[] dest) {
		int row = this.getColor() == 'b' ? 0 : 7;
		
		if (dest[1] == 2) {
			return new int[] {row, 3}; //if left side (c1 or c7)
		} else {
			return new int[] {row, 5}; //if right side (g1 or g7)
		}
		
	}
	
	/* (non-Javadoc)
	 * @see chess.ChessPiece#move(int[])
	 */
	@Override
	public boolean move(int[] dest) {
		int row = this.getColor() == 'b' ? 0 : 7;
		int[] old_pos = getPos();
		
		if (isValidMove(dest)) {
			
			//Set new pos to this piece
			Chess.board[dest[0]][dest[1]] = this;
			
			//If is castling, get final location of castle

			if (isCastling(dest)) {
				int[] cfp = castleFinalPos(dest);
				
				if (cfp[1] == 3) { //left
					ChessPiece left_rook = Chess.board[row][0];
					
					//Set new pos to this piece
					Chess.board[cfp[0]][cfp[1]] = left_rook;
					
					//Clear old spot
					Chess.board[row][0] = null;
					
					left_rook.setPos(cfp);
					left_rook.listUpdate();
					left_rook.setMoved(left_rook.hasMoved()+1);
					
				} else { //right
					ChessPiece right_rook = Chess.board[row][7];
					
					//Set new pos to this piece
					Chess.board[cfp[0]][cfp[1]] = right_rook;
					
					//Clear old spot
					Chess.board[row][7] = null;
					
					right_rook.setPos(cfp);
					right_rook.listUpdate();
					right_rook.setMoved(right_rook.hasMoved()+1);
				}
			}

			
			//Clear old location
			Chess.board[old_pos[0]][old_pos[1]] = null;
			
			//Save new pos
			setPos(dest);
			
			setMoved(hasMoved()+1);
			return true;
		}
		return false;
	}
}
