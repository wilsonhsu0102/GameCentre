package fall2018.csc2017.slidingtiles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import fall2018.csc2017.gamecentre.GameManager;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 * Manage a slidingTileBoard, including swapping tiles, checking for a win, and managing taps.
 */
public class SlidingTileBoardManager implements Serializable, GameManager {

    /**
     * The slidingTileBoard being managed.
     */
    private SlidingTileBoard slidingTileBoard;
    /**
     * the moves that have been made in this game
     */
    private Stack<ArrayList<Integer>> undoStack = new Stack<>();
    /**
     * The current number of moves that the player has made.
     */
    private int move = 0;

    /**
     * Manage a slidingTileBoard that has been pre-populated.
     *
     * @param slidingTileBoard the slidingTileBoard
     */
    SlidingTileBoardManager(SlidingTileBoard slidingTileBoard) {
        this.slidingTileBoard = slidingTileBoard;
    }

    /**
     * Return the current slidingTileBoard.
     */
    SlidingTileBoard getSlidingTileBoard() {
        return slidingTileBoard;
    }

    /**
     * Manage a slidingTileBoard with varies complexities.
     *
     * @param boardSize the size of the slidingTileBoard.
     */
    SlidingTileBoardManager(int boardSize) {
        List<Tile> tiles = new ArrayList<>();
        int numTiles = boardSize * boardSize;
        for (int tileNum = 0; tileNum != numTiles; tileNum++) {
            tiles.add(new Tile(tileNum));
        }
        if (boardSize == 3) {
            Collections.sort(tiles);
            tiles.remove(0);
            Tile blankTile = new Tile(24);
            tiles.add(blankTile);
        }
        else if (boardSize == 4) {
            Collections.sort(tiles);
            tiles.remove(0);
            Tile blankTile = new Tile(24);
            tiles.add(blankTile);
        }
        Collections.shuffle(tiles);
        slidingTileBoard = new SlidingTileBoard(tiles, boardSize);
        if (!checkSolvability(tiles, boardSize)){
            makeSolvableBoard(tiles);
            slidingTileBoard = new SlidingTileBoard(tiles, boardSize);
        }
    }

    /**
     * Checks if the current sliding tile board is solvable. If it is not solvable, make it solvable
     *
     * @param boardSize size of the board
     * @param tiles a list containing tiles for sliding tile game
     * reference: https://www.sitepoint.com/randomizing-sliding-puzzle-tiles/
     * reference: https://www.geeksforgeeks.org/check-instance-15-puzzle-solvable/
     */
    private boolean checkSolvability(List<Tile> tiles, int boardSize) {
        int blankId = 25;
        int blankRow = slidingTileBoard.findTile(blankId) / boardSize;
        int inversionCount = getInversionCount(tiles);
        return (boardSize % 2 == 1 && inversionCount % 2 == 0 ||
                boardSize % 2 == 0 && (inversionCount + boardSize - 1 - blankRow) % 2 == 0);
    }

    /**
     * Calculates the number of inversions in sliding tile board.
     *
     * @param tiles a list containing tiles for sliding tile game
     * @return number of inversions
     */
    private int getInversionCount(List<Tile> tiles) {
        int inversionCount = 0;
        for (int i = 0; i != tiles.size() - 1; i++)
            for (int j = i + 1; j != tiles.size(); j++)
                if (tiles.get(i).getId() != 25 && tiles.get(j).getId() != 25)
                    if (tiles.get(i).getId() > tiles.get(j).getId())
                        inversionCount++;
        return inversionCount;
    }

    /**
     * Modifies the board, so the inversion count gets +/- by 1 to make the board solvable.
     *
     * @param tiles a list containing tiles for sliding tile game
     */
    private void makeSolvableBoard(List<Tile> tiles) {
        if (tiles.get(0).getId() ==25 || tiles.get(1).getId() ==25){
            Collections.swap(tiles, tiles.size()-1, tiles.size()-2);
        }
        else{
            Collections.swap(tiles, 0, 1 );
        }
    }

    /**
     * Set the board to be one move away from winning.
     */
    void setBoardOneMoveWin(){
        int boardSize = getSlidingTileBoard().boardSize;
        List<Tile> tiles = new ArrayList<>();
        final int numTiles = boardSize * boardSize;
        for (int tileNum = 0; tileNum != numTiles; tileNum++) {
            tiles.add(new Tile(tileNum));
        }
        tiles.remove(tiles.size() - 1);
        tiles.add(new Tile(24));
        slidingTileBoard = new SlidingTileBoard(tiles, boardSize);
        slidingTileBoard.swapTiles(slidingTileBoard.boardSize - 1, slidingTileBoard.boardSize - 1,
                slidingTileBoard.boardSize - 1, slidingTileBoard.boardSize - 2);
    }

    /**
     * Return whether the tiles are in row-major order.
     *
     * @return whether the tiles are in row-major order
     */
    public boolean puzzleSolved() {
        boolean solved = true;
        int start = 1;
        for (Tile tile : slidingTileBoard) {
            if (tile.getId() != start) {
                solved = false;
            }
            start++;
        }
        return solved;
    }

    /**
     * Return whether any of the four surrounding tiles is the blank tile.
     *
     * @param position the tile to check
     * @return whether the tile at position is surrounded by a blank tile
     */
    public boolean isValidTap(int position) {

        int row = position / slidingTileBoard.boardSize;
        int col = position % slidingTileBoard.boardSize;
        int blankId = 25;

        // Are any of the 4 the blank tile?
        Tile above = row == 0 ? null : slidingTileBoard.getTile(row - 1, col);
        Tile below = row == slidingTileBoard.boardSize - 1 ? null : slidingTileBoard.getTile(row + 1, col);
        Tile left = col == 0 ? null : slidingTileBoard.getTile(row, col - 1);
        Tile right = col == slidingTileBoard.boardSize - 1 ? null : slidingTileBoard.getTile(row, col + 1);
        return (below != null && below.getId() == blankId)
                || (above != null && above.getId() == blankId)
                || (left != null && left.getId() == blankId)
                || (right != null && right.getId() == blankId);
    }

    /**
     * Process a touch at position in the slidingTileBoard, swapping tiles as appropriate.
     * Increment the number of moves.
     *
     * @param position the position
     */
    public void touchMove(int position) {
        int row = position / slidingTileBoard.boardSize;
        int col = position % slidingTileBoard.boardSize;
        int blankId = 25;
        int blankRow = slidingTileBoard.findTile(blankId) / slidingTileBoard.boardSize;
        int blankCol = slidingTileBoard.findTile(blankId) % slidingTileBoard.boardSize;

        if (isValidTap(position)) {
            ArrayList<Integer> undoMove = new ArrayList<>();
            undoMove.add(blankRow);
            undoMove.add(blankCol);
            undoMove.add(row);
            undoMove.add(col);
            this.undoStack.add(undoMove);
            this.move++;
            slidingTileBoard.swapTiles(blankRow, blankCol, row, col);
        }

    }

    /**
     * Return the player's move.
     */
    int getMove() {
        return move;
    }

    /**
     * Return the player's score.
     */
    public int getScore() {
        int score = 1000 - (move * 3 * (6 - slidingTileBoard.boardSize));
        if (score < 0) {
            return 0;
        }
        return score;

    }

    /**
     * Return the game's name.
     */
    public String getGameName() {
        return "Sliding Tile";
    }

    /**
     * Return the date and time of the game being played.
     */
    public String getTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dateTimeFormatter.format(now);
    }

    /**
     * return the game difficulty.
     */
    public String getGameDifficulty() {
        return String.format("%s by %s", slidingTileBoard.boardSize, slidingTileBoard.boardSize);
    }

    /**
     * Undo the previous move made, if possible. Return true if move is undone, otherwise false.
     */
    boolean undoMove() {
        if (!undoStack.empty()) {
            ArrayList<Integer> previousMove = undoStack.pop();
            int row = previousMove.get(2);
            int col = previousMove.get(3);
            int blankRow = previousMove.get(0);
            int blankCol = previousMove.get(1);
            slidingTileBoard.swapTiles(row, col, blankRow, blankCol);
            return true;
        }
        return false;
    }
}
