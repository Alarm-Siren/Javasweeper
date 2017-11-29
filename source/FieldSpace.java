/**
 * Stores details about a particular space in the minefield...
 * 
 * @author  Nicholas Parks Young
 * @version 2015-04-03
 */
public class FieldSpace
{
    private final boolean mine;
    private SpaceStatus status;
    private short qtyNeighbourMines;
    private boolean stale;
    
    /**
     * Constructor for objects of type FieldSpace.
     * Initially sets the space to Hidden and zero neighbouring mines.
     * 
     * @param isMine Whether this space will be a mine
     */
    protected FieldSpace(boolean isMine)
    {
        mine = isMine;
        qtyNeighbourMines = 0;
        stale = true;
        status = SpaceStatus.HIDDEN;
    }
    
    /**
     * Call this function to clear the stale flag after this space has been analysed.
     */
    protected void resetStale()
    {
        stale = false;
    }
    
    /**
     * @return True if the space has changed since the stale flag was last reset.
     */
    protected boolean isStale()
    {
        return stale;
    }
    
    /**
     * @return True if space is a mine
     */
    protected boolean isMine()
    {
        return mine;
    }
    
    /**
     * @return Quantity of neighbouring mines.
     */
    protected short getQtyNeighbourMines()
    {
        return qtyNeighbourMines;
    }
    
    /**
     * @return The current status of this space
     */
    protected SpaceStatus getStatus()
    {
        return status;
    }
    
    /**
     * Reveal this space (making getNeighbourMines() and isMine() accessible).
     * Returns true if neighbouring spaces should also be revealed.
     * 
     * If the space is already revealed, then the command is ignored and
     * logically any neighbouring spaces have also already been revealed, so returns false.
     * 
     * @return True if neighbouring spaces should be revealed
     */
    protected boolean reveal()
    {
        if (status != SpaceStatus.REVEALED) {
            status = SpaceStatus.REVEALED;
            stale = true;
            return (qtyNeighbourMines == 0);
        } else {
            return false;
        }
    }
    
    /**
     * Toggles between the HIDDEN, FLAGGED and QUESTIONED statuses - if it is not already REVEALED status.
     * NOTE you CANNOT use this method to set status to REVEALED - use reveal() for that.
     */
    protected void toggleStatus()
    {
        switch (status) {
            case HIDDEN:
                status = SpaceStatus.FLAGGED;
                stale = true;
                break;
            case FLAGGED:
                status = SpaceStatus.QUESTIONED;
                stale = true;
                break;
            case QUESTIONED:
                status = SpaceStatus.HIDDEN;
                stale = true;
                break;
            default:
                ;   //do nothing
        }
    }

    /**
     * Increments the quantity of mines this space believes it is next to.
     */
    protected void incrementQtyNeighbourMines()
    {
        qtyNeighbourMines++;
    }
    
}
