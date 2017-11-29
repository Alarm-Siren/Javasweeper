/**
 * An immutable object describing a particular square in the Minefield.
 * NOTE: isMine() and getQtyNeighbourMines() are only meaningful if getStatus() == SpaceStatus.REVEALED
 * This is so that the UI cannot accidentally or maliciously reveal information about squares the
 * player has yet to reveal, and also cannot modify the state of the squares directly - it must go
 * through the GameLogic class.
 * 
 * @author  Nicholas Parks Young
 * @version 2015-04-02
 */
public final class Square
{
    private final SpaceStatus status;
    private final boolean mine;
    private final short qtyNeighbours;
    
    /**
     * Constructor for the immutable object Square.
     * The values set here cannot be changed later.
     * 
     * @param isMine True if this square is a mine
     * @param qtyOfNeighbours the number of mines neighbouring this square
     * @param status the status of the square - e.g. REVEALED, HIDDEN etc
     */
    public Square(boolean isMine, short qtyOfNeighbours, SpaceStatus status)
    {
        this.mine = isMine;
        this.qtyNeighbours = qtyOfNeighbours;
        this.status = status;
    }
    
    /**
     * @return The status of this square
     */
    public final SpaceStatus getStatus()
    {
        return status;
    }
    
    /**
     * If the square is revealed, will return true if this square is a mine, otherwise false.
     * 
     * @throws IllegalStateException if the square is not yet revealed
     * @return True if square is a mine
     */
    public final boolean isMine()
    {
        if (status != SpaceStatus.REVEALED) {
            throw new IllegalStateException("Squares will never reveal their secrets prematurely!");
        }
        return mine;
    }
    
    /**
     * If the square is revealed, will return the quantity of mines that neighbour it.
     * 
     * @throws IllegalStateException if this square is not yet revealed
     * @return Quantity of neighbouring mines.
     */
    public final short getQtyNeighbourMines()
    {
        if (status != SpaceStatus.REVEALED) {
            throw new IllegalStateException("Squares will never reveal their secrets prematurely!");
        }
        return qtyNeighbours;
    }
}
