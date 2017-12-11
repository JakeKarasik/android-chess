package com.example.group55.androidchess55.activities.ChessBoard;

import android.content.Context;
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

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;

public class ChessBoardActivity extends AppCompatActivity {

	// Android widgets
	static BaseAdapter adapter;
	GridView board_grid;

	// Boards
	public static ChessPiece[][] board;
	static ChessPiece[] horizon_board;
	static LinkedList<ChessPiece[]> recording;

	// Valid coordinates for escaping check
	public static LinkedList<int[]> escape_check;
	public static LinkedList<int[]> deny_check;

	// Flags
	public static int turn = 1;
	public static boolean zone_check_mode = false;
	static boolean moving = false;
	static int prev_pos[] = new int[2];
	static ChessPiece prev_piece = null;
	static char turn_color = '\0';
	static boolean canUndo = false;
	static int[] black_king = new int[]{0, 4};
	static int[] white_king = new int[]{7, 4};
	static boolean isInCheck = false;

	// Resets board to default state when called
	public void reset() {

		// Create new board and initialize
		board = new ChessPiece[8][8];
		horizon_board = new ChessPiece[64];
		recording = new LinkedList<>();
		initBoard();

		// Reset escape coordinates
		escape_check = null;
		deny_check = null;

		// Set flags
		turn = 1;
		zone_check_mode = false;
		moving = false;
		prev_pos = new int[2];
		prev_piece = null;
		turn_color = 'w';
		canUndo = false;
		black_king = new int[]{0, 4};
		white_king = new int[]{7, 4};
		isInCheck = false;
	}

	// Syncs horizon with current state of board
	public static void convertToHorizon() {
		for (int i = 0; i < 64; i++) {
			horizon_board[i] = board[i / 8][i % 8];
		}
		adapter.notifyDataSetChanged();
	}

	// Returns a copy of board as a 1-D array
	public ChessPiece[] copyBoard() {
		ChessPiece[] temp_board = new ChessPiece[64];
		for (int i = 0; i < 64; i++) {
			if (board[i/8][i%8] != null) {
				temp_board[i] = cloner(board[i/8][i%8]);
			}
		}
		return temp_board;
	}

	// Takes a 1-D array, loads it into board and then syncs with horizon
	public void loadBoard(ChessPiece[] horizon){

		board = new ChessPiece[8][8];
		for(int i = 0; i < 64; i++){
			if(horizon[i] != null) {
				board[i / 8][i % 8] = cloner(horizon[i]);
			}
		}
		convertToHorizon();
	}

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

		// Record initial state of board
		recording.add(copyBoard());

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

			// Move requested, set up destination coordinates in 2-D
			int dest[] = new int[2];
			dest[0] = position / 8;
			dest[1] = position % 8;

			// Check that if player is in check, move will let him escape
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

			// If we have selected our own piece and it passes check
			if (prev_piece.getColor() == turn_color && pass && prev_piece.move(dest)) {

				// Check for promotion
				if (prev_piece.getName() == 'P' && (dest[0] == 7 || dest[0] == 0)) {
					promotePiece(dest);
					return;
				}

				// If a king is moved, update it's global position
				if (prev_piece.getName() == 'K') {
					if (prev_piece.getColor() == 'w') {
						white_king = dest.clone();
					} else {
						black_king = dest.clone();
					}
				}

				// Update and record new board state
				convertToHorizon();
				board_grid.setAdapter(adapter);
				recording.add(copyBoard());
				turn++;
				moving = false;

				// Check if a king has been placed in check
				if (!ChessPiece.isSafe(white_king, 'w')) {

					// If white player placed himself in check, undo
					if (turn_color == 'w') {
						canUndo = true;
						undo(null);
						canUndo = true;
						return;
					}

					// If black placed white in check, test and show status
					isInCheck = true;
					if (board[white_king[0]][white_king[1]].mateChecker()) {
						endGameNotification("Checkmate, Black wins!");
					} else {
						showNotification("White's king is in check.");
					}
				} else if (!ChessPiece.isSafe(black_king, 'b')) {

					// If black player placed himself in check, undo
					if (turn_color == 'b') {
						canUndo = true;
						undo(null);
						canUndo = true;
						return;
					}

					// If white placed black in check, test and show status
					isInCheck = true;
					if (board[black_king[0]][black_king[1]].mateChecker()) {
						endGameNotification("Checkmate, White wins!");
					} else {
						showNotification("Black's king is in check.");
					}
				} else {
					// No check detected, disable alarm
					isInCheck = false;
				}

				// Allow player to undo one turn
				canUndo = true;
			}
		}
	}

	// Undo last move made
	public void undo(View v) {
		if (canUndo) {
			// Remove last board from record
			recording.removeLast();

			// Load the most recent record of chessboard after undoing
			ChessPiece[] temp = recording.getLast();
			loadBoard(temp);
			board_grid.setAdapter(adapter);

			// Update global states
			turn--;
			convertToHorizon();
			board_grid.setAdapter(adapter);
			canUndo = false;
			moving = false;

			// Update king positions
			for(int i = 0; i < 64; i ++){
				if(horizon_board[i] != null && horizon_board[i].getName() == 'K'){
					if(horizon_board[i].getColor() == 'w'){
						white_king = horizon_board[i].getPos();
					}else{
						black_king = horizon_board[i].getPos();
					}
				}
			}

			if (!ChessPiece.isSafe(white_king, 'w')) {
				// If black placed white in check, test and show status
				isInCheck = true;
				if (board[white_king[0]][white_king[1]].mateChecker()) {
					endGameNotification("Checkmate, Black wins!");
				} else {
					showNotification("White's king is in check.");
				}
			} else if (!ChessPiece.isSafe(black_king, 'b')) {
				// If white placed black in check, test and show status
				isInCheck = true;
				if (board[black_king[0]][black_king[1]].mateChecker()) {
					endGameNotification("Checkmate, White wins!");
				} else {
					showNotification("Black's king is in check.");
				}
			} else {
				// No check detected, disable alarm
				isInCheck = false;
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

				// Update and record new board state
				convertToHorizon();
				board_grid.setAdapter(adapter);
				recording.add(copyBoard());
				turn++;
				moving = false;

				// Check if a king has been placed in check
				if (!ChessPiece.isSafe(white_king, 'w')) {

					// If white player placed himself in check, undo
					if (turn_color == 'w') {
						canUndo = true;
						undo(null);
						canUndo = true;
						return;
					}

					// If black placed white in check, test and show status
					isInCheck = true;
					if (board[white_king[0]][white_king[1]].mateChecker()) {
						endGameNotification("Checkmate, Black wins!");
					} else {
						showNotification("White's king is in check.");
					}
				} else if (!ChessPiece.isSafe(black_king, 'b')) {

					// If black player placed himself in check, undo
					if (turn_color == 'b') {
						canUndo = true;
						undo(null);
						canUndo = true;
						return;
					}

					// If white placed black in check, test and show status
					isInCheck = true;
					if (board[black_king[0]][black_king[1]].mateChecker()) {
						endGameNotification("Checkmate, White wins!");
					} else {
						showNotification("Black's king is in check.");
					}
				} else {
					// No check detected, disable alarm
					isInCheck = false;
				}

				// Allow player to undo one turn
				canUndo = true;
			}
		});
		d.create().show();
	}

	// Randomly move a piece on AI move button press
	public void moveAI(View v) {

		int cur_turn = turn;

		// Get current turn color
		char turn_color = (turn % 2 == 0) ? 'b' : 'w';

		// Choose random starting point
		int start_pos = new Random().nextInt(64);

		int i;
		for (i = start_pos; i < 64; i++) {
			// If not null, matching color and has available moves...
			ChessPiece curr_piece = horizon_board[i];
			if (curr_piece != null && curr_piece.getColor() == turn_color) {
				curr_piece.listUpdate();
				// If has possible moves, move this piece!
				if (curr_piece.possible_moves.size() > 0) {
					break;
				}
			}
			// No match was found, start from beginning
			if (i == 63) { i = -1; }
		}

		// Set the previous move globals to allow undoing
		prev_pos[0] = i / 8;
		prev_pos[1] = i % 8;
		prev_piece = board[i/8][i%8];
		moving = true;

		// At this point should have at least 1 possible move
		LinkedList<int[]> possible_moves = prev_piece.possible_moves;
		int choice = new Random().nextInt(possible_moves.size());
		int[] selected_move = possible_moves.get(choice);
		makeMove((selected_move[0]*8)+selected_move[1]);

		// If no move was made, retry moveAI
		if (cur_turn == turn) { moveAI(null); }
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
					if (saveRecording(title)) {
						Log.d("SAVE STATUS", "SAVED");
					} else {
						Log.d("SAVE STATUS", "FAILED TO SAVE");
					}
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

	public boolean saveRecording(String title) {
		Date today = Calendar.getInstance().getTime();
		String current_dt = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss", Locale.US).format(today);
		try{
			// Save game to ser file
			FileOutputStream fos = openFileOutput(title+"~"+current_dt+".ser", Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(recording);
			oos.close();

			return true;
		}catch(Exception e){
			Log.d("savedRecording()", "failed", e);
			return false;
		}
	}

	// Converts a given string coordinate to its position on the board.
	public static int[] strPositionToXY(String pos) {
		//Create array for final pos
		int[] final_pos = new int[2];

		//Convert letter/number to separate ints
		final_pos[0] = 8 - Character.getNumericValue(pos.charAt(1));
		final_pos[1] = pos.charAt(0) - 'a';

		return final_pos;
	}

	// Places given piece on board at given position and sets the piece's position
	public static void placePiece(int[] pos, ChessPiece piece) {
		board[pos[0]][pos[1]] = piece;
		piece.setPos(pos);
	}

	// Initializes board with all the pieces for both Black and White in initial positions.
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

	// Returns a deep copy of a ChessPiece
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

	// Checks that the coordinates given are inside the bounds
	public static boolean inBounds(int[] input) {
		return input[0] >= 0 && input[1] >= 0 && input[0] <= 7 && input[1] <= 7;
	}
}
