/**
 * Represent a location in a rectangular grid.
 *
 * @author  Nicholas Parks Young
 * @version 2015-04-01
 */
public class Location
{
    private int x, y;

    /**
     * Represent a point in a rectangular 2D grid.
     * Pre-condition: both X and Y coordinates are positive numbers.
     * @param x The X-coordinate.
     * @param y The Y-coordinate.
     */
    public Location(int x, int y)
    {
        if (x < 0) {
            throw new IllegalArgumentException("Location with negative x-coordinate");
        }
        if (y < 0) {
            throw new IllegalArgumentException("Location with negative y-coordinate");
        }

        this.x = x;
        this.y = y;
    }
    
    /**
     * Implement content equality.
     */
    public boolean equals(Object obj)
    {
        if(obj instanceof Location) {
            Location other = (Location) obj;
            return x == other.getX() && y == other.getY();
        }
        else {
            return false;
        }
    }
    
    /**
     * Return a string of the form (x, y)
     * @return A string representation of the location.
     */
    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }
    
    /**
     * Use the top 16 bits for the row value and the bottom for
     * the column. Except for very big grids, this should give a
     * unique hash code for each (row, col) pair.
     * @return A hashcode for the location.
     */
    public int hashCode()
    {
        return (x << 16) + y;
    }
    
    /**
     * @return The X-coordinate.
     */
    public int getX()
    {
        return x;
    }
    
    /**
     * @return The Y-coordinate.
     */
    public int getY()
    {
        return y;
    }
}
