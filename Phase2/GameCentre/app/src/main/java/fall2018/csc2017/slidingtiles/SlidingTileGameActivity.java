package fall2018.csc2017.slidingtiles;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import fall2018.csc2017.R;
import fall2018.csc2017.gamecentre.CustomAdapter;
import fall2018.csc2017.gamecentre.GameAcitivity;
import fall2018.csc2017.gamecentre.GameCentre;
import fall2018.csc2017.gamecentre.GameManager;
import fall2018.csc2017.gamecentre.GameToSave;
import fall2018.csc2017.gamecentre.GestureDetectGridView;
import fall2018.csc2017.gamecentre.SavedGames;
import fall2018.csc2017.gamecentre.UserManager;
import fall2018.csc2017.gamecentre.YouWinActivity;

/**
 * The game activity for Sliding Tile.
 */
// Excluded from tests because it's a view class
public class SlidingTileGameActivity extends GameAcitivity implements Observer {

    /**
     * The board manager for current sliding tile game.
     */
    private SlidingTileBoardManager slidingTileBoardManager;

//    /**
//     * Gamecentre for managing files
//     */
//    private GameCentre gameCentre;

    /**
     * The buttons to display.
     */
    private ArrayList<Button> tileButtons;

    // Grid View and calculated column height and width based on device size
    /**
     * Grid for sliding tile game.
     */
    private GestureDetectGridView gridView;


    /**
     * The columnWidth and columnHeight for the grids.
     */
    private int columnWidth, columnHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_tile);
        loadManagers(this);
        slidingTileBoardManager = (SlidingTileBoardManager) gameManager;
        createTileButtons(this);

        //Activate buttons
        addUndoButtonListener();
        addSaveButtonListener();

        // Add View to activity
        gridView = findViewById(R.id.SlidingTileGrid);
        gridView.setNumColumns(slidingTileBoardManager.getSlidingTileBoard().getBoardSize());
        gridView.setGameManager(slidingTileBoardManager);
        slidingTileBoardManager.getSlidingTileBoard().addObserver(this);

        // Observer sets up desired dimensions as well as calls our display function
        gridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        gridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int displayWidth = gridView.getMeasuredWidth();
                        int displayHeight = gridView.getMeasuredHeight();
                        columnWidth = displayWidth / slidingTileBoardManager.getSlidingTileBoard().getBoardSize();
                        columnHeight = displayHeight / slidingTileBoardManager.getSlidingTileBoard().getBoardSize();
                        display();
                    }
                });
    }

//    /**
//     * Load necessary managers
//     */
//    private void loadManagers() {
//        gameCentre = new GameCentre(this);
//        gameCentre.loadManager(GameManager.TEMP_SAVE_START);
//    }

    /**
     * Activate the save button.
     */
    private void addSaveButtonListener() {
        Button saveButton = findViewById(R.id.SlidingTileSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameCentre.saveGame(slidingTileBoardManager);
                makeToastSavedText();
            }
        });
    }

    /**
     * Adds an undo button and undoes the previous move when clicked.
     */
    private void addUndoButtonListener() {
        Button undo = findViewById(R.id.Undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean undo = slidingTileBoardManager.undoMove();
                gameCentre.autoSave(slidingTileBoardManager);
                if (!undo) {
                    makeToastNoMoreUndo();
                }
            }
        });
    }

    /**
     * Set up the background image for each button based on the master list
     * of positions, and then call the adapter to set the view.
     */
    public void display() {
        updateTileButtons();
        gridView.setAdapter(new CustomAdapter(tileButtons, columnWidth, columnHeight));
        setMoveCountText(R.id.SlidingTileMoveCount);
    }

    /**
     * Create the buttons for displaying the tiles.
     *
     * @param context the context
     */
    private void createTileButtons(Context context) {
        SlidingTileBoard slidingTileBoard = slidingTileBoardManager.getSlidingTileBoard();
        tileButtons = new ArrayList<>();
        for (int row = 0; row != slidingTileBoard.getBoardSize(); row++) {
            for (int col = 0; col != slidingTileBoard.getBoardSize(); col++) {
                Button tmp = new Button(context);
                tmp.setBackgroundResource(slidingTileBoard.getTile(row, col).getBackground());
                this.tileButtons.add(tmp);
            }
        }
    }

    /**
     * Update the backgrounds on the buttons to match the tiles.
     */
    private void updateTileButtons() {
        SlidingTileBoard slidingTileBoard = slidingTileBoardManager.getSlidingTileBoard();
        int nextPos = 0;
        for (Button b : tileButtons) {
            int row = nextPos / slidingTileBoardManager.getSlidingTileBoard().getBoardSize();
            int col = nextPos % slidingTileBoardManager.getSlidingTileBoard().getBoardSize();
            b.setBackgroundResource(slidingTileBoard.getTile(row, col).getBackground());
            nextPos++;
        }
        gameCentre.autoSave(slidingTileBoardManager);
    }

    /**
     * Updates the screen or jump to game victory screen.
     *
     * @param o observable
     * @param arg object
     */
    @Override
    public void update(Observable o, Object arg) {
        if (!slidingTileBoardManager.puzzleSolved()) {
            display();
        }
        else {
//            gameCentre.gameManagerWin(slidingTileBoardManager);
            switchToWinActivity(this);
        }
    }

//    /**
//     * swaps activity to you win activity
//     */
//    private void switchToWinActivity() {
//        Intent win = new Intent(this, YouWinActivity.class);
//        startActivity(win);
//    }

    /**
     * Give the text no more moves are available.
     */
    private void makeToastNoMoreUndo() {
        Toast.makeText(this, "No more moves are available", Toast.LENGTH_SHORT).show();
    }

    /**
     * Display that a game was saved successfully.
     */
    private void makeToastSavedText() {
        Toast.makeText(this, "Game Saved", Toast.LENGTH_SHORT).show();
    }

//    /**
//     * sets the move count on screen
//     */
//    private void setMoveCountText() {
//        TextView moves = findViewById(R.id.MoveCount);
//        moves.setText(String.format("%s", slidingTileBoardManager.getMove()));
//    }
}
