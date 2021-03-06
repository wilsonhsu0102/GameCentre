package fall2018.csc2017.gamecentre;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Abstract class for all GameActivity to extend.
 */
// Excluded from tests because it's a view class
public abstract class GameActivity extends AppCompatActivity {

    /**
     * GamCentre for loading, saving the game.
     */
    protected GameCentre gameCentre;

    /**
     * The gameManager responsible for the logic of the game.
     */
    protected GameManager gameManager;

    /**
     * Load necessary managers.
     */
    protected void loadManagers(Context context) {
        gameCentre = new GameCentre(context);
        gameCentre.loadManager(GameManager.TEMP_SAVE_START);
        gameManager = gameCentre.getGameManager();
    }

    /**
     * Swaps activity to you win activity.
     */
    protected void switchToWinActivity(Context context) {
        gameCentre.gameManagerWin(gameManager);
        Intent win = new Intent(context, YouWinActivity.class);
        startActivity(win);
    }

    /**
     * Sets the move count on screen.
     */
    protected void setMoveCountText(int moveCountTextID) {
        TextView moves = findViewById(moveCountTextID);
        moves.setText(String.format("%s", gameManager.getMove()));
    }
}
