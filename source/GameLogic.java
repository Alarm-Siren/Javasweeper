import java.util.LinkedList;
import java.util.Set;
import java.util.ListIterator;

/**
 * Primary API interface for the Javasweeper game engine.
 * The GUI should only access the game engine via this class, and objects returned or accepted by
 * this class's methods.
 * 
 * Additionally, this class implements the high-level actions relevant to the game,
 * such as revealing a space and determining if the player has won or lost.
 * 
 * @author  Nicholas Parks Young
 * @version 2015-04-03
 */
public class GameLogic
{
    private Minefield field;
    private MinefieldStats stats;
    private GameLevel gameLevel;
    private boolean gameInProgress;
    private boolean gameWon;
    private long playTime = 0;
    
    /**
     * Constructor for objects of type GameLogic.
     * 
     * Creates a new Javasweeper (minesweeper) game engine instance, preparing the minefield
     * according to the passed in difficulty.
     * 
     * @param level The difficulty level desired for this game
     */
    public GameLogic(GameLevel level)
    {
        field = new Minefield(level.getFieldWidth(), level.getFieldHeight(), level.getQuantityOfMines());
        stats = new MinefieldStats(field);
        stats.refresh();
        
        gameInProgress = true;
        gameWon = false;
        gameLevel = level;
    }

    /**
     * Wrapper method for the revealAt(Location location) method for use with X,Y coordinates
     * 
     * @param x The X-coordinate of the space to reveal
     * @param y The Y-coordinate of the space to reveal
     */
    public void revealAt(int x, int y)
    {
        revealAt(new Location(x, y));
    }
    
    /**
     * Wrapper method for the revealAt(Location location) method for use with X,Y coordinates
     *
     * @param location The location to reveal
     * @throws IllegalStateException if the space you want to reveal is already revealed
     */
    public void revealAt(Location location)
    {
        if (field.getObjectAt(location).getStatus() == SpaceStatus.REVEALED) {
            throw new IllegalStateException("Cannot reavel a location that is already revealed");
        }
        
        //We'll need this...
        LinkedList<Location> spacesToReveal = new LinkedList<Location>();
        LinkedList<Location> spacesRevealed = new LinkedList<Location>();
        //Add the location we're revealing to the list of locations to be revealed.
        spacesToReveal.add(location);
        
        while (!spacesToReveal.isEmpty()) {
            //For each location in said list, reveal it.
            ListIterator<Location> i = spacesToReveal.listIterator();
            while (i.hasNext()) {
                Location currentLocation = i.next();
                //Remove the location from the list, so we don't get trapped in an infinite loop :S
                i.remove();
                spacesRevealed.add(currentLocation);
                //Reveal the current location and if it says to reveal neighbours, then we'll add them
                //to the list for processing recursively.
                if (field.getObjectAt(currentLocation).reveal()) {
                    for (Location recurseLocation : field.getAdjacentLocations(currentLocation)) {
                        //Don't re-add it if we've already processed it once.
                        if (!spacesRevealed.contains(recurseLocation)) {
                            i.add(recurseLocation);
                        }
                    }
                }
            }
        }
        
        //Update the statistics, since we've just changed something.
        stats.refresh();
        
        //Check to see if the user has lost/won
        if ((stats.getHidden() + stats.getFlagged() + stats.getQuestioned()) == stats.getMines()) {
            gameWon = true;
            gameInProgress = false;
            revealAllMines();
        } else if (stats.getRevealedMines() > 0) {
            gameWon = false;
            gameInProgress = false;
            revealAllMines();
        }
    }
    
    /**
     * @return List of stale locations, from the MinefieldStats class.
     */
    public Set<Location> getStaleLocations()
    {
        return stats.getStaleLocations();
    }
    
    /**
     * Reveals all mines on the minefield.
     */
    private void revealAllMines()
    {
        //Find all the mines and tell them reveal themselves.
        for (int y = 0; y < field.getHeight(); y++) {
            for (int x = 0; x < field.getWidth(); x++) {
                FieldSpace space = field.getObjectAt(x, y);
                if (space.isMine()) {
                    space.reveal();
                }
            }
        }
        //We've changed things, so refresh stats.
        stats.refresh();
    }
    
    /**
     * @return This game's GameLevel object.
     */
    public GameLevel getLevel()
    {
        return gameLevel;
    }
    
    /**
     * Returns the amount of time since this game was started in seconds. Will stop counting after the game
     * has finished.
     * 
     * @return Game time elapsed, in seconds
     */
    public long getPlayTime()
    {
        if (gameInProgress) {
            playTime = stats.getRunningTime() / 1000;
        }
        return playTime;
    }
    
    /**
     * Returns an immutable, new Square object containing information about the requested space in the minefield
     * 
     * @param x The x-coordinate of the space to fetch a Square about
     * @param y The y-coordinate of the space to fetch a Square about
     * @return A Square containing information about the minefield space at coordinate X,Y
     */
    public Square getSquareAt(int x, int y)
    {
        FieldSpace privateSpace = field.getObjectAt(x, y);
        Square publicSquare = new Square(privateSpace.isMine(), privateSpace.getQtyNeighbourMines(), privateSpace.getStatus());
        return publicSquare;
    }
    
    /**
     * Wrapper function for getSquareAt(int x, int y) for use with Location objects
     * 
     * @param location The location of the space to fetch a Square about
     * @return A Square containing information about the minefield space at Location
     */
    public Square getSquareAt(Location location)
    {
        return getSquareAt(location.getX(), location.getY());
    }
    
    /**
     * @return The width of the minefield
     */
    public int getWidth()
    {
        return field.getWidth();
    }
    
    /**
     * @return The heigh of the minefield
     */
    public int getHeight()
    {
        return field.getHeight();
    }
    
    /**
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return True if the coordinates X,Y are valid in the context of this minefield
     */
    public boolean validLocation(int x, int y)
    {
        return field.validLocation(x, y);
    }
    
    /**
     * @param location The location to check
     * @return True if the given Location is valid in the context of this minefield
     */
    public boolean validLocation(Location location)
    {
        return field.validLocation(location);
    }
    
    /**
     * @return The quantity of mines on the minefield, minus the number of spaces flagged by the user
     */
    public int getQtyMinesRemaining()
    {
        return stats.getMines() - stats.getFlagged();
    }
    
    /**
     * This function returns true if the player has won the game, otherwise false.
     * Best combined with getGameInProgress(),
     * 
     * @return True if the player has won
     */
    public boolean getGameWon()
    {
        return gameWon;
    }
    
    /**
     * Returns false if the game has ended somehow - i.e. the player has won/lost - and true if the game
     * is ongoing. Best combined with getGameWon().
     * 
     * @return False if the game has ended, else true
     */
    public boolean getGameInProgress()
    {
        return gameInProgress;
    }
    
    /**
     * Wrapper function for toggleStatusOfSquare(int x, int y) for use with Location objects
     * 
     * @param location The location of the space whose status is to be toggled
     */
    public void toggleStatusOfSquare(Location location)
    {
        toggleStatusOfSquare(location.getX(), location.getY());
    }
    
    /**
     * Toggles the status of the space at the coordinate X,Y.
     * Doesn't work on revealed spaces, and cannot be used to reveal a space - use revealAt() to reveal a space
     * 
     * @param x The x-coordinate of the space to reveal
     * @param y The y-coordinate of the space to reveal
     */
    public void toggleStatusOfSquare(int x, int y)
    {
        //toggle the space
        FieldSpace privateSpace = field.getObjectAt(x, y);
        privateSpace.toggleStatus();
        //changed something, so refresh stats
        stats.refresh();
    }
    
}
