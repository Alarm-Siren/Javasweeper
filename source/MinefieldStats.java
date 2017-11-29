import java.util.Set;
import java.util.LinkedHashSet;

/**
 * This class, on command, analyzes the Minefield class passed to it in its constructor
 * and creates statistics reflecting that analysis and stores that information until
 * the next refresh.
 * 
 * As an efficiency measure (since it can detect these things whilst compiling the statistics)
 * it also creates a list of locations that are stale - i.e. they have changed since they
 * were last analysed. The list of stale spaces is forgotten after it is accessed, to ensure
 * that spaces are not refreshed by higher-up code unnecessarily.
 * 
 * @author  Nicholas Parks Young
 * @version 2015-04-03
 */
public class MinefieldStats
{
    private int qtyFlagged;
    private int qtyQuestioned;
    private int qtyHidden;
    private short qtyMines;
    private short qtyRevealedMines;
    private long startTime;
    private Minefield field;
    private LinkedHashSet<Location> staleLocations;
    
    /**
     * Constructor for objects of type MinefieldStats
     * 
     * @param field The minefield this object will analyse
     */
    protected MinefieldStats(Minefield field)
    {
        if (field == null) {
            throw new IllegalArgumentException("field must not be null");
        }
        qtyFlagged = 0;
        qtyQuestioned = 0;
        qtyMines = 0;
        qtyHidden = 0;
        qtyRevealedMines = 0;
        startTime = System.currentTimeMillis();
        this.field = field;
        staleLocations = new LinkedHashSet<Location>();
    }

    /**
     * Returns a Set of Locations where the space at that location was marked as stale the
     * last time the minefield was analysed. When the set is returned by this method, it is
     * forgotten by this class as the locations are no longer stale. Therefore subsequent
     * invocations of this method (without calling refresh() first) will return an empty set.
     * 
     * @return List of stale locations
     */
    protected Set<Location> getStaleLocations()
    {
        LinkedHashSet<Location> currentList = staleLocations;
        staleLocations = new LinkedHashSet<Location>();
        return currentList;
    }
    
    /**
     * @return The amount of time since this stats obect was created, in seconds.
     */
    protected long getRunningTime()
    {
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * @return Quantity of flagged spaces present in the minefield
     */
    protected int getFlagged()
    {
        return qtyFlagged;
    }
    
    /**
     * @return Quantity of questioned spaces present in the minefield
     */
    protected int getQuestioned()
    {
        return qtyQuestioned;
    }
    
    /**
     * @return Quantity of mines present in the minefield
     */
    protected int getMines()
    {
        return qtyMines;
    }
    
    /**
     * @return Quantity of hidden spaces present in the minefield
     */
    protected int getHidden()
    {
        return qtyHidden;
    }
    
    /**
     * @return Quantity of mines present in the minefield, which have also been revealed
     */
    protected short getRevealedMines()
    {
        return qtyRevealedMines;
    }
    
    /**
     * Call this method to update the statistics stored by this object, and also
     * to create a new list of stale locations.
     */
    protected void refresh()
    {
        //clear the list.
        qtyFlagged = 0;
        qtyQuestioned = 0;
        qtyMines = 0;
        qtyRevealedMines = 0;
        qtyHidden = 0;
        
        //For every space in the minefield...
        for (int y = 0; y < field.getHeight(); y++) {
            for (int x = 0; x < field.getWidth(); x++) {
                //fetch the space
                FieldSpace space = field.getObjectAt(x, y);
                
                //If the space is stale, add its location to the list of stale locations
                if (space.isStale()) {
                    //Avoid re-adding to the list of stale Locations if its already in it
                    Location l = new Location(x, y);
                    if (!staleLocations.contains(l)) {
                        staleLocations.add(l);
                    }
                    //Tell the space that its not stale anymore.
                    space.resetStale();
                }

                if (space.isMine()) {
                    //If the space is a mine, increment the mine counter.
                    qtyMines++;
                    if (space.getStatus() == SpaceStatus.REVEALED) {
                        //if it is already revealed, increment the revealed mine counter.
                        qtyRevealedMines++;
                    }
                }

                switch (space.getStatus()) {
                    case HIDDEN:
                        //space is hidden
                        qtyHidden++;
                        break;
                    case FLAGGED:
                        //space is flagged
                        qtyFlagged++;
                        break;
                    case QUESTIONED:
                        //space is questioned
                        qtyQuestioned++;
                        break;
                    default:
                        ;   //do nothing  
                }
            }
        }
    }
}
