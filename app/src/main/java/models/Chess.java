package models;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * The class that runs the Chess program. 
 * It manages the chess board and loops waiting on user input until the game is over.
 * 
 * @author Jake Karasik (jak451)
 * @author Benjamin Ker (bk375)
 */
public class Chess {

	/**
	 * Create 8 x 8 Chessboard.
	 */
	static ChessPiece[][] board = new ChessPiece[8][8];
    /**
     * Initialize turn to 1.
     */
    static int turn = 1;
    /**
     * Set default promotion to Queen.
     */
    static char promotion = '\0';

    /**
     * Black King's current position.
     */
    static int[] black_king = new int[]{0,4};
    /**
     * White King's current position.
     */
    static int[] white_king = new int[]{7,4};
    /**
     * <code>true</code> if either Black or White King is in check.
     */
    static boolean isInCheck = false;
    /**
     * <code>true</code> when testing for safe zones for check.
     */
    static boolean zone_check_mode = false;
    /**
     * Contains valid coordinates for escaping check.
     */
    static LinkedList<int[]> escape_check;

	/**
	 * Prints current board with correct format.
	 */
	public static void printBoard() {
		for (int i = 0;i < 8;i++) {
			for (int j = 0;j < 8;j++) {
				// If no piece, print white space or black space
				if (board[i][j] == null) {
					System.out.print((i + j) % 2 == 0 ? "  " : "##"); //Print white space if odd or black space if even
				} else {
					System.out.print(board[i][j]);
				}
				System.out.print(" ");
			}
			// Print numbers on right hand side
			System.out.print(8 - i);
			System.out.println();
		}
		// Print letters at bottom
		for (int k=0;k<8;k++) {
			System.out.print(" "+ (char)('a' + k) + " ");
		}
	}

	
	/**
	 * Converts a given string coordinate to its position on the board.
	 * 
	 * @param pos Coordinate in string form. Ex: a1
	 * @return <code>[row, col]</code> of given pos.
	 */
	public static int[] strPositionToXY(String pos) {
		//Create array for final pos
		int[] final_pos = new int[2];

		//Convert letter/number to separate ints
		final_pos[0] = 8 - Character.getNumericValue(pos.charAt(1));
		final_pos[1] = pos.charAt(0) - 'a';

		return final_pos;
	}

	/**
	 * Places given piece on board at given position and sets the pieces pos field.
	 * 
	 * @param pos Position to place piece.
	 * @param piece ChessPiece to be placed.
	 */
	public static void placePiece(int[] pos, ChessPiece piece) {
		board[pos[0]][pos[1]] = piece;
		piece.setPos(pos);
	}
	
	/**
	 * Checks that the coordinates given are inside the bounds
	 * 
	 * @param input Coordinates to check.
	 * @return <code>true</code> if input is within the bounds of the board.
	 */
	public static boolean inBounds(int[] input){
	    return input[0] >= 0 && input[1] >= 0 && input[0] <= 7 && input[1] <= 7;
    }
	
	/**
	 * Initializes board with all the pieces for both Black and White in their correct starting positions.
	 */
	public static void initBoard() {

		// Place black pieces
        placePiece(strPositionToXY("a7"), new Pawn('b'));
		placePiece(strPositionToXY("b7"), new Pawn('b'));
		placePiece(strPositionToXY("c7"), new Pawn('b'));
		placePiece(strPositionToXY("d7"), new Pawn('b'));
		placePiece(strPositionToXY("e7"), new Pawn('b'));
		placePiece(strPositionToXY("f7"), new Pawn('b'));
		placePiece(strPositionToXY("g7"), new Pawn('b'));
		placePiece(strPositionToXY("h7"), new Pawn('b'));
		placePiece(strPositionToXY("e8"), new King('b'));
		placePiece(strPositionToXY("d8"), new Queen('b'));
		placePiece(strPositionToXY("c8"), new Bishop('b'));
		placePiece(strPositionToXY("f8"), new Bishop('b'));
		placePiece(strPositionToXY("b8"), new Knight('b'));
		placePiece(strPositionToXY("g8"), new Knight('b'));
		placePiece(strPositionToXY("a8"), new Rook('b'));
		placePiece(strPositionToXY("h8"), new Rook('b'));
		
		// Place white pieces
		placePiece(strPositionToXY("a2"), new Pawn('w'));
		placePiece(strPositionToXY("b2"), new Pawn('w'));
		placePiece(strPositionToXY("c2"), new Pawn('w'));
		placePiece(strPositionToXY("d2"), new Pawn('w'));
		placePiece(strPositionToXY("e2"), new Pawn('w'));
		placePiece(strPositionToXY("f2"), new Pawn('w'));
		placePiece(strPositionToXY("g2"), new Pawn('w'));
		placePiece(strPositionToXY("h2"), new Pawn('w'));
		placePiece(strPositionToXY("e1"), new King('w'));
		placePiece(strPositionToXY("d1"), new Queen('w'));
		placePiece(strPositionToXY("c1"), new Bishop('w'));
		placePiece(strPositionToXY("f1"), new Bishop('w'));
		placePiece(strPositionToXY("b1"), new Knight('w'));
		placePiece(strPositionToXY("g1"), new Knight('w'));
		placePiece(strPositionToXY("a1"), new Rook('w'));
		placePiece(strPositionToXY("h1"), new Rook('w'));

	}
	
	/**
	 * Given a piece returns a clone of it.
	 * 
	 * @param item Piece to clone.
	 * @return Cloned piece.
	 */
	public static ChessPiece cloner(ChessPiece item){
        switch (item.getName()) {
            case 'P':
                return new Pawn(item);
            case 'Q':
                return new Queen(item);
            case 'N':
                return new Knight(item);
            case 'B':
                return new Bishop(item);
            case 'R':
                return new Rook(item);
            case 'K':
                return new King(item);
            default:
                return new Pawn(item);
        }
    }

	/**
	 * Runs chess game until resign, draw, or checkmate.
	 * 
	 * @param args n/a
	 */
	public static void main(String[] args) {

		// Initialize default board
		initBoard();
        printBoard();

        Scanner scanner = new Scanner(System.in);
		boolean draw_flag = false;

		while (true){
            System.out.println("\n");
            if (turn % 2 == 1) { System.out.print("White's move: "); } else {
                System.out.print("Black's move: ");
            }

		    // Wait for user input
		    String line = scanner.nextLine();
            System.out.println();
            String input[] = line.split("\\b");
            int len = input.length;
            /*
             *  len == 1 --> resign
             *  len == 3 --> regular input
             *  len == 5 --> promote
             *  len == 6 --> draw
             */

            // Set promotion
            if (len == 5) { promotion = input[4].charAt(0); }

            // Resignation
            if (line.equals("resign")) {
                if (turn % 2 == 1) { System.out.println("Black wins"); System.exit(0); }
                System.out.println("White wins"); System.exit(0);
            }

            // Accept draw or decline
            if (line.equals("draw")) {
                if (draw_flag) {
                    System.out.println("draw");
                    System.exit(0);
                } else {
                    System.out.print("Illegal move, try again");
                    continue;
                }
            } else { draw_flag = false; }

            // Offer draw
            if (len == 6) { draw_flag = true; }//System.out.println("Draw offered\n"); }

            // Set origin and destination - (0,0) top left
            int origin_x = strPositionToXY(input[0])[1]; // column
            int origin_y = strPositionToXY(input[0])[0]; // row
            int dest_y = strPositionToXY(input[2])[0]; // row
            ChessPiece origin = board[origin_y][origin_x];
            int dest[] = strPositionToXY(input[2]);

            char turn_color = (turn % 2 == 0) ? 'b' : 'w';

            boolean pass = true;
            if (isInCheck) {
                pass = false;
                for (int[] item : escape_check) {
                    if (Arrays.equals(item,dest)) {
                        pass = true;
                    }
                }
            }

            if (origin != null && origin.getColor() == turn_color && origin.move(dest) && pass) {
            	
                // Check for promotion
                if (origin.getName() == 'P' && (dest_y == 7 || dest_y == 0)) {
                    switch(promotion){
                        case 'N':
                            placePiece(dest, new Knight(turn_color));
                            break;
                        case 'B':
                            placePiece(dest, new Bishop(turn_color));
                            break;
                        case 'R':
                            placePiece(dest, new Rook(turn_color));
                            break;
                        default:
                            placePiece(dest, new Queen(turn_color));
                            break;
                    }
                    promotion = '\0';
                }
                printBoard();
                turn++;

                // If a king is moved, update it's global position
                if (origin.getName() == 'K') {
                	if (origin.getColor() == 'w') {
                		white_king = dest.clone();
                	} else {
                		black_king = dest.clone();
                	}
                }

                // Check if a king has been placed in check
                if (!ChessPiece.isSafe(white_king, 'w')) {
                    isInCheck = true;
                    if (board[white_king[0]][white_king[1]].mateChecker()) {
                        System.out.println("\n");
                        System.out.println("Checkmate");
                        System.out.println();
                        System.out.println("Black wins");
                        System.exit(0);
                    }
                	System.out.println("\n");
                	System.out.print("Check");
                } else if (!ChessPiece.isSafe(black_king, 'b')) {
                    isInCheck = true;
                    if (board[black_king[0]][black_king[1]].mateChecker()) {
                        System.out.println("\n");
                        System.out.println("Checkmate");
                        System.out.println();
                        System.out.print("White wins");
                        System.exit(0);
                    }
                    System.out.println("\n");
                    System.out.print("Check");
                } else {
                    isInCheck = false;
                }
            } else {
                System.out.print("Illegal move, try again");
            }
        }
	}
}
