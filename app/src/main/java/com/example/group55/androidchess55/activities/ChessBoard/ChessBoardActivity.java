package com.example.group55.androidchess55.activities.ChessBoard;

import android.util.Log;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;

import com.example.group55.androidchess55.R;
import com.example.group55.androidchess55.activities.ChessBoard.adapters.ChessBoardAdapter;
import com.example.group55.androidchess55.activities.HomeScreen.HomeScreenActivity;
import com.example.group55.androidchess55.models.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class ChessBoardActivity extends AppCompatActivity {

	// Public globals; accessible by other classes

	/**
	 * Main chessboard.
	 */
	public static ChessPiece[][] board;

	/**
	 * Keeps track of turns. Initialized to 1.
	 */
	public static int turn = 1;

	/**
	 * <code>true</code> when testing for safe zones for check.
	 */
	public static boolean zone_check_mode = false;

	/**
	 * Contains valid coordinates for escaping check by moving king.
	 */
	public static LinkedList<int[]> escape_check;

	/**
	 * Contains valid coordinates for escaping check by moving non-king.
	 */
	public static LinkedList<int[]> deny_check;


	// Local globals; inaccessible by other classes.

	/**
	 * Adapter for GridView.
	 */
	static BaseAdapter adapter;

	/**
	 * GridView that displays our chessboard.
	 */
	GridView board_grid;

	/**
	 * Stores previous chessboard for undo.
	 */
	static ChessPiece[][] prev_board;

	/**
	 * Conversion of 2D chessboard to 1D. Used to display in GridView.
	 */
	static ChessPiece[] horizon_board;

	/**
	 * Checks if player has selected a piece to move.
	 */
	static boolean moving = false;

	/**
	 * Stores previous position (origin) of selected piece.
	 */
	static int prev_pos[] = new int[2];

	/**
	 * ChessPiece selected by player.
	 */
	static ChessPiece prev_piece = null;

	/**
	 * Stores current player's color.
	 */
	static char turn_color = '\0';

	/**
	 * Prevents player from undoing more than once.
	 */
	static boolean canUndo = false;

	/**
	 * Stores black king's current position.
	 */
	static int[] black_king = new int[]{0, 4};

	/**
	 * Stores white king's current position.
	 */
	static int[] white_king = new int[]{7, 4};

	/**
	 * Checks if either black or white king is in check.
	 */
	static boolean isInCheck = false;

	/**
	 * Resets board to default state when called.
	 */
	public void reset() {

		// Create board and initialize
		board = new ChessPiece[8][8];
		horizon_board = new ChessPiece[64];
		initBoard();

		// Set flags
		turn = 1;
		zone_check_mode = false;
		escape_check = null;
		deny_check = null;
		prev_board = null;
		moving = false;
		prev_pos = new int[2];
		prev_piece = null;
		turn_color = 'w';
		canUndo = false;
		black_king = new int[]{0, 4};
		white_king = new int[]{7, 4};
		isInCheck = false;
	}

	/**
	 * Display our chessboard and set actions on click.
	 *
	 * @param savedInstanceState State of application
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// Create our display
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chess_board);

		// Setup toolbar
		Toolbar myToolbar = findViewById(R.id.my_toolbar);
		myToolbar.setTitle("Cancel Game");
		setSupportActionBar(myToolbar);
		ActionBar ab = null;
		while (ab == null) { ab = getSupportActionBar(); }
		ab.setDisplayHomeAsUpEnabled(true);

		// Reset board to default state.
		reset();

		// Display board
		adapter = new ChessBoardAdapter(ChessBoardActivity.this, horizon_board);
		convertToHorizon();
		board_grid = findViewById(R.id.board_grid);
		board_grid.setAdapter(adapter);

		// Set action on user input
		board_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				makeMove(position);
			}
		});
	}

	public void makeMove(int position) {

		// Sets turn color based on turn number
		turn_color = (turn % 2 == 0) ? 'b' : 'w';

		// If player is selecting a piece, set prev (origin) globals and moving state
		if (!moving && horizon_board[position] != null &&
				horizon_board[position].getColor() == turn_color) {

			prev_pos[0] = position / 8;
			prev_pos[1] = position % 8;
			prev_piece = horizon_board[position];
			moving = true;
			return;
		}

		// If player has selected a piece
		if (moving) {

			// If user selected another of his own pieces, change selected piece
			if (horizon_board[position] != null &&
					horizon_board[position].getColor() == turn_color) {

				prev_pos[0] = position / 8;
				prev_pos[1] = position % 8;
				prev_piece = horizon_board[position];
				return;
			}

			// Set up destination coordinates in 2D
			int dest[] = new int[2];
			dest[0] = position / 8;
			dest[1] = position % 8;

			// Check that if player is in check (not mate), move will let him escape
			boolean pass = true;
			if (isInCheck) {
				pass = false;
				if(prev_piece.getName() == 'K'){
					for (int[] item : escape_check) {
						if (Arrays.equals(item, dest)) { pass = true; }
					}
				}else{
					for (int[] item : deny_check) {
						if (Arrays.equals(item, dest)) { pass = true; }
					}
				}
			}

			// If we have selected our own piece and it passes
			if (prev_piece != null && prev_piece.getColor() == turn_color && pass) {

				// Check that move is valid
				if (!prev_piece.isValidMove(dest)) {
					return;
				}

				// Copy board and move
				getBoardState();
				prev_piece.move(dest);

				// Check for promotion
				if (prev_piece.getName() == 'P' && (dest[0] == 7 || dest[0] == 0)) {
					promotePiece(dest);
					return;
				}

				// Update board state
				convertToHorizon();
				board_grid.setAdapter(adapter);
				turn++;
				moving = false;

				// If a king is moved, update it's global position
				if (prev_piece.getName() == 'K') {
					if (prev_piece.getColor() == 'w') {
						white_king = dest.clone();
					} else {
						black_king = dest.clone();
					}
				}

				// Check if a king has been placed in check
				if (!ChessPiece.isSafe(white_king, 'w')) {

					// If white player placed himself in check, undo
					if (turn_color == 'w') {
						canUndo = true;
						undo(null);
						canUndo = true;
						return;
					}
					isInCheck = true;
					if (board[white_king[0]][white_king[1]].mateChecker()) {
						endGameNotification("Checkmate, Black Wins!");
					} else {
						showNotification("White King in Check");
					}
				} else if (!ChessPiece.isSafe(black_king, 'b')) {

					if (turn_color == 'b') {
						canUndo = true;
						undo(null);
						return;
					}

					isInCheck = true;
					if (board[black_king[0]][black_king[1]].mateChecker()) {
						endGameNotification("Checkmate, White Wins!");
					} else {
						showNotification("Black King in Check");
					}
				} else {
					isInCheck = false;
				}

				canUndo = true;
			}
		}
	}

	/**
	 * Shows dialog to select newly promoted piece type.
	 */
	public void promotePiece(final int dest[]) {
		AlertDialog.Builder d = new AlertDialog.Builder(this);
		d.setCancelable(false);
		d.setTitle("Select piece to promote to");
		final CharSequence[] pieces = {"Rook", "Bishop", "Knight", "Queen"};
		d.setItems(pieces, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				switch (pieces[i].toString()) {
					case "Rook":
						placePiece(dest, new Rook(turn_color));
						break;
					case "Bishop":
						placePiece(dest, new Bishop(turn_color));
						break;
					case "Knight":
						placePiece(dest, new Knight(turn_color));
						break;
					default:
						placePiece(dest, new Queen(turn_color));
						break;
				}

				// Update board state
				convertToHorizon();
				board_grid.setAdapter(adapter);
				turn++;
				moving = false;

				// If a king is moved, update it's global position
				if (prev_piece.getName() == 'K') {
					if (prev_piece.getColor() == 'w') {
						white_king = dest.clone();
					} else {
						black_king = dest.clone();
					}
				}

				// Check if a king has been placed in check
				if (!ChessPiece.isSafe(white_king, 'w')) {

					if (turn_color == 'w') {
						canUndo = true;
						undo(null);
						return;
					}
					isInCheck = true;
					if (board[white_king[0]][white_king[1]].mateChecker()) {
						endGameNotification("Checkmate, Black Wins!");
					} else {
						showNotification("Check");
					}
				} else if (!ChessPiece.isSafe(black_king, 'b')) {

					if (turn_color == 'b') {
						canUndo = true;
						undo(null);
						return;
					}

					isInCheck = true;
					if (board[black_king[0]][black_king[1]].mateChecker()) {
						endGameNotification("Checkmate, White Wins!");
					} else {
						showNotification("Check");
					}
				} else {
					isInCheck = false;
				}

				canUndo = true;
			}
		});
		d.create().show();
	}

	/**
	 * Show dialog with given text.
	 *
	 * @param msg Message to display in dialog.
	 */
	public void showNotification(String msg) {
		// Setup basic dialog properties
		AlertDialog.Builder d = new AlertDialog.Builder(this);
		d.setCancelable(false);
		d.setMessage(msg);

		// Create OK button
		d.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		// Display dialog
		d.create().show();
	}

	/**
	 * Show end of game results in dialog with option to save game.
	 *
	 * @param msg Message to display in dialog.
	 */
	public void endGameNotification(String msg) {
		// Setup basic dialog properties
		AlertDialog.Builder d = new AlertDialog.Builder(this);
		d.setCancelable(false);
		d.setMessage(msg);
		d.setTitle("Game over!");

		// Create return to home button
		d.setPositiveButton("Return to home", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Close dialog, end chessboard activity and send back to homescreen
				dialog.cancel();
				Intent intent = new Intent(ChessBoardActivity.this, HomeScreenActivity.class);
				finish();
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		// Create save game button
		d.setNeutralButton("Save Game", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				saveGame();
			}
		});

		// Display dialog
		d.create().show();
	}

	/**
	 * Display dialog to save game with given title
	 */
	public void saveGame() {
		// Setup basic dialog properties
		AlertDialog.Builder d = new AlertDialog.Builder(this);
		d.setCancelable(false);
		d.setTitle("Enter game title");

		// Create title text box
		final EditText edittext = new EditText(this);
		d.setView(edittext);

		// Setup save button
		d.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			} // Gets over written later
		});

		// Setup cancel button
		d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Close dialog, end chessboard activity and send back to homescreen
				dialog.cancel();
				Intent intent = new Intent(ChessBoardActivity.this, HomeScreenActivity.class);
				finish();
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		// Save created dialog for save button overwriting
		final AlertDialog dialog = d.create();

		// Show dialog
		dialog.show();

		// Overwrites save button onclick to stop it from closing on empty input
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String title = edittext.getText().toString().trim();
				if (!title.isEmpty()) {
					// Do stuff to save game...

					// Close dialog, end chessboard activity and send back to homescreen
					dialog.cancel();
					Intent intent = new Intent(ChessBoardActivity.this, HomeScreenActivity.class);
					finish();
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * Create dialog to ask for draw
	 *
	 * @param v View calling confirm
	 */
	public void confirmDraw(View v) {
		// Setup basic dialog properties
		AlertDialog.Builder d = new AlertDialog.Builder(this);
		d.setCancelable(false);
		String caller_color = turn % 2 == 0 ? "Black" : "White";
		d.setMessage(caller_color + " has requested a draw.");
		d.setTitle("Draw Requested");

		// Create return to home button
		d.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Close dialog, call endgame dialog
				dialog.cancel();
				endGameNotification("Draw!");
			}
		});

		// Create save game button
		d.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		// Display dialog
		d.create().show();
	}

	/**
	 * End game, called by forfeit button
	 *
	 * @param v View calling forfeit
	 */
	public void forfeit(View v) {
		String caller_color = turn % 2 == 0 ? "Black" : "White";
		endGameNotification(caller_color + " has forfeited.");
	}

	/**
	 * Randomly move a piece on AI move button press
	 *
	 * @param v View calling moveAI
	 */
	public void moveAI(View v) {
		if (prev_piece != null) {
			prev_piece.listUpdate();
			LinkedList<int[]> possible_moves = prev_piece.possible_moves;
			if (possible_moves.size() > 0) {
				int choice = new Random().nextInt(possible_moves.size());
				int[] selected_move = possible_moves.get(choice); // (row*8) + col
				makeMove((selected_move[0] * 8) + selected_move[1]);
			} else {
				showNotification("This piece has no valid moves.");
			}
		} else {
			showNotification("Please select a piece to move first.");
		}
	}

	/**
	 * Adapt 2D chessboard to 1D
	 */
	public static void convertToHorizon() {
		for (int i = 0; i < 64; i++) {
			horizon_board[i] = board[i / 8][i % 8];
		}
		adapter.notifyDataSetChanged();
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
	 * @param pos   Position to place piece.
	 * @param piece ChessPiece to be placed.
	 */
	public static void placePiece(int[] pos, ChessPiece piece) {
		board[pos[0]][pos[1]] = piece;
		piece.setPos(pos);
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
	public static ChessPiece cloner(ChessPiece item) {
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
	 * Checks that the coordinates given are inside the bounds
	 *
	 * @param input Coordinates to check.
	 * @return <code>true</code> if input is within the bounds of the board.
	 */
	public static boolean inBounds(int[] input) {
		return input[0] >= 0 && input[1] >= 0 && input[0] <= 7 && input[1] <= 7;
	}

	public void getBoardState() {
		prev_board = new ChessPiece[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] != null) {
					prev_board[i][j] = cloner(board[i][j]);
				}
			}
		}

	}

	/**
	 * Undo last move made.
	 *
	 * @param v View calling undo
	 */
	public void undo(View v) {
		if (canUndo) {

			// Copy over previous board to current board
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (prev_board[i][j] != null) {
						board[i][j] = cloner(prev_board[i][j]);
					} else {
						board[i][j] = null;
					}
				}
			}

			// Update global states
			turn--;
			convertToHorizon();
			board_grid.setAdapter(adapter);
			canUndo = false;

			// Verify check states
			if (!ChessPiece.isSafe(white_king, 'w')) {
				isInCheck = true;
				if (board[white_king[0]][white_king[1]].mateChecker()) {
					endGameNotification("Checkmate, Black Wins!");
				} else {
					showNotification("White King in Check");
				}
			} else if (!ChessPiece.isSafe(black_king, 'b')) {
				isInCheck = true;
				isInCheck = true;
				if (board[black_king[0]][black_king[1]].mateChecker()) {
					endGameNotification("Checkmate, White Wins!");
				} else {
					showNotification("Black King in Check");
				}
			} else {
				isInCheck = false;
			}
		}
	}
}
