/**
 * Enumeration of possible game levels and some related utility methods.
 * 
 * @author  Nicholas Parks Young
 * @version 2015-04-02
 */
public enum GameLevel
{
    SUPEREASY("Super Easy", 0, 9, 9, (short)4),
    EASY("Easy", 1, 9, 9, (short)9), 
    MODERATE("Moderate", 2, 16, 16, (short)40), 
    HARD("Hard", 3, 30, 16, (short)99);   
    
    private String description;
    private int levelNumber;
    private int fieldWidth;
    private int fieldHeight;
    private short quantityOfMines;
    
    /**
     * Constructor for game level objects. Sets up the human-readable description
     * and the level number.
     */
    private GameLevel(String description, int levelNumber, int fieldWidth, int fieldHeight, short quantityOfMines)
    {
        this.description = description;
        this.levelNumber = levelNumber;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.quantityOfMines = quantityOfMines;
    }
    
    /**
     * @return the height of the Minefield for the level
     */
    public int getFieldHeight()
    {
        return fieldHeight;
    }
    
    /**
     * @return the width of the number of mines in the minefield for this level
     */
    public short getQuantityOfMines()
    {
        return quantityOfMines;
    }
    
    /**
     * @return the width of the Minefield for the level
     */
    public int getFieldWidth()
    {
        return fieldWidth;
    }
    
    /**
     * @return string version of the level.
     */
    public String toString()
    {
        return description;
    }
    
    /**
     * @return level number as integer.
     */
    public int getLevelNumber()
    {
        return levelNumber;
    }
    
    /**
     * Converts an integer into a GameLevel object.
     * Returns null if no GameLevel maps to the given integer.
     * 
     * @return the GameLevel object which maps to the given integer, or null
     */
    public static GameLevel intToLevel(int levelNumber)
    {
        for (GameLevel i : GameLevel.values()) {
            if (i.getLevelNumber() == levelNumber) {
                return i;
            }
        };
        return null;
    }
}
