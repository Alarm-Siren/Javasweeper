import java.util.Random;
import java.util.LinkedList;
import java.util.List;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single FieldSpace object, which contains the details of that space.
 * 
 * @author  Nicholas Parks Young
 * @version 2015-04-02
 */
public class Minefield
{
    //Once the minefield is setup, it will never change, only the status of the objects contained within will.
    private final int width, height;
    private final FieldSpace[][] field;
    private final Random random;
    
    /**
     * Creates a new Minefield of the specified size, and hides quantityOfMines mines inside it.
     * 
     * @param width The width of the minefield
     * @param height The height of the minefield
     * @param quantityOfMines How many mines to hide in the minefield.
     */
    protected Minefield(int width, int height, short quantityOfMines)
    {
        if (height <= 0) {
            throw new IllegalArgumentException("height was smaller than or equal to zero");
        }
        if (width <= 0) {
            throw new IllegalArgumentException("width was smaller than or equal to zero");
        }
        
        //seed the RNG with system time, so that games will not always be the same!
        random = new Random(System.currentTimeMillis());  
        
        this.width = width;
        this.height = height;
        field = new FieldSpace[width][height];
        hideMines(quantityOfMines);
    }
    
    /**
     * Check the validity of the Location object, with respect to the size of this minefield.
     * 
     * @param location The location to check
     * @return True if valid
     */
    protected boolean validLocation(Location location)
    {
        return validLocation(location.getX(), location.getY());
    }
    
    /**
     * Check the validity of an X,Y co-ordinate, with respect to the size of this minefield.
     * 
     * @param x The X co-ordinate to check
     * @param y The Y co-ordinate to check
     * @return True if valid
     */
    protected boolean validLocation(int x, int y)
    {
        if (x < 0) return false;
        if (y < 0) return false;
        if (x >= width) return false;
        if (y >= height) return false;
        return true;
    }
    
    /**
     * @return the width of the minefield
     */
    protected int getWidth()
    {
        return width;
    }
    
    /**
     * @return the height of the minefield
     */
    protected int getHeight()
    {
        return height;
    }
    
    /**
     * @return A random location that currently contains no object (i.e. is null).
     */
    private Location randomFreeLocation()
    {
        Location newLocation;
        do {
            newLocation = new Location(random.nextInt(width), random.nextInt(height));
        } while (getObjectAt(newLocation) != null);
        assert validLocation(newLocation) : "selected location was not actually valid";
        return newLocation;
    }
    
    /**
     * Wrapper method for getObjectAt(int x, int y) for location objects.
     * 
     * @param location The location whose object we want
     * @return The object at the given location
     */
    protected FieldSpace getObjectAt(Location location)
    {
        return getObjectAt(location.getX(), location.getY());
    }
    
    /**
     * Returns the FieldSpace stored at the coordinates X,Y.
     * 
     * @param x The X-coordinate
     * @param y The Y-coordinate
     * @return The object at the given coordinates
     */
    protected FieldSpace getObjectAt(int x, int y)
    {
        if (!validLocation(x, y)) {
            throw new IndexOutOfBoundsException("location specified is outside of minefield");
        }
        return field[x][y];
    }
    
    /**
     * Wrapper method for getAdjacentLocations(int x, int y) for location objects.
     * 
     * @param location The location we want the adjacent locations of
     * @return List of adjacent locations
     */
    protected List<Location> getAdjacentLocations(Location location)
    {
        return getAdjacentLocations(location.getX(), location.getY());
    }

    /**
     * Get a list containing all valid locations adjacent to the given X,Y co-ordinate
     * Includes diagonal locations, but not the original location.
     * 
     * @param x The x-coordinate we want adjacent locations to
     * @param y The y-coordinate we want adjacent locations to
     * @return List of adjacent locations
     */
    protected List<Location> getAdjacentLocations(int x, int y)
    {
        if (!validLocation(x, y)) {
            throw new IndexOutOfBoundsException("location specified is outside of minefield");
        }
        
        List<Location> locations = new LinkedList<Location>();
        for (short xOffset = -1; xOffset <= 1; xOffset++) {
            for (short yOffset = -1; yOffset <= 1; yOffset++) {
                //ignore the original cell
                if (xOffset != 0 || yOffset != 0) {
                    int proposedX = x + xOffset;
                    int proposedY = y + yOffset;
                    //ignore adjacent cells which are actually outside the minefield
                    if (validLocation(proposedX, proposedY)) {
                        //add the adjacent cell.
                        locations.add(new Location(proposedX, proposedY));
                    }
                }
            }
        }
        return locations;
    }
    
    /**
     * Wrapper method for the place(FieldSpace objectToPlace, int x, int y) method for use with Location objects
     * 
     * @param objectToPlace The object to place at location
     * @param location the Location to place the object at
     */
    private void place(FieldSpace objectToPlace, Location location)
    {
        place(objectToPlace, location.getX(), location.getY());
    }
    
    /**
     * Wrapper method for the place(FieldSpace objectToPlace, int x, int y) method for use with Location objects
     * 
     * @param objectToPlace The object to place at (X,Y)
     * @param x The X-coordinate to place the object at
     * @param y The Y-coordinate to place the object at
     */
    private void place(FieldSpace objectToPlace, int x, int y)
    {
        if (!validLocation(x, y)) {
            throw new IndexOutOfBoundsException("location specified is outside of minefield");
        }
        if (objectToPlace == null) {
            throw new IllegalArgumentException("cannot set cell to null");
        }
        field[x][y] = objectToPlace;
    }
    
    /**
     * Create a given quantity of mines and hide them randomly throughout the Minefield.
     * Also fill in the non-mine FieldSpace objects, and update them with their quantity of neighbouring mines.
     * 
     * @param quantityOfMines The quantity of mines to hide in this minefield.
     */
    private void hideMines(short quantityOfMines)
    {
        List<Location> mineLocations = new LinkedList<Location>();
        
        if (quantityOfMines <= 0) {
            throw new IllegalArgumentException("quantityOfMines (to make) was less than one");
        }
        
        //Place the mines, and record where we put them.
        for (short i = 0; i < quantityOfMines; i++) {
            Location mineLocation = this.randomFreeLocation();
            mineLocations.add(mineLocation);
            this.place(new FieldSpace(true), mineLocation);
        }
        
        //Now we fill out all the other spaces...
        for (int x = 0; x < this.getWidth(); x++) {
            for (int y = 0; y < this.getHeight(); y++) {
                //Make sure not to overwrite the mines!
                if (this.getObjectAt(x, y) == null) {
                    this.place(new FieldSpace(false), x, y);
                }
            }
        }
        
        //and finally update all spaces neighbouring a mine to have the correct NeighboursQuantity.
        for (Location mineLoc : mineLocations) {
            List<Location> adjacentLocations = this.getAdjacentLocations(mineLoc);
            for (Location adjacentLocation : adjacentLocations) {
                this.getObjectAt(adjacentLocation).incrementQtyNeighbourMines();
            }
        }
    }
}
