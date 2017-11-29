import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.concurrent.*;
import java.util.Set;

/**
 * Implementation of a Graphical User Interface for the Javasweeper "Minesweeper" game implemented
 * by the GameLogic class.
 * 
 * @author  Nicholas Parks Young
 * @version 2015-04-03
 */
public class GUI
{
    //Game information
    private static final String TITLE = "Javasweeper";
    private static final String VERSION = "1.0";
    
    //Colours, borders and font sizes used in display of the minefield.
    private static final Border WHITE_BORDER = new LineBorder(Color.WHITE, 1);
    private static final Color HIDDEN_COLOR = new Color(34, 177, 76);
    private static final Color REVEALED_COLOR = new Color(158, 237, 182);
    private static final Color FLAGGED_COLOR = new Color(255, 174, 201);
    private static final Color QUESTIONED_COLOR = new Color(255, 233, 113);
    private static final Color ACTUAL_MINE_COLOR = new Color(128, 0, 0);
    private static final int BASE_FONT_SIZE = 12;
    
    //The game engine this GUI is displaying
    private GameLogic gameEngine;
    //The root of the Javasweeper window
    private JFrame frame;
    //The three statistics labels
    private JLabel difficultyLabel, timerLabel, minesLeftLabel;
    //Array containing all of the minefield squares/buttons
    private JButton[][] minefieldButtons;
    //Storage for the thread which refreshes the time elapsed asynchronously
    private ScheduledFuture<?> statsRefresher;
    
    /**
     * The main entry point into this program. Run this function to play Javasweeper!
     * 
     * @param args  Command-line arguments. These are ignored.
     */
    public static void main(String[] args)
    {
        GUI gui = new GUI();
    }
    
    /**
     * Immediately quits the program.
     */
    private static void quitProgram()
    {
        System.exit(0);
    }
    
    /**
     * Create the Javasweeper GUI.
     */
    public GUI()
    {
        //Allow player to choose what level of difficulty
        GameLevel level = chooseLevel();
        if (level == null) {
            quitProgram();  //If they pressed cancel, kill the program.
        }
        
        //create the game engine for that level of difficulty.
        gameEngine = new GameLogic(level);
        
        //create the GUI proper
        makePrimaryFrame(gameEngine.getWidth(), gameEngine.getHeight());
        
        //Setup stats autorefresher - every 250ms should be enough.
        Runnable statsRefresh = new Runnable() {
            @Override
            public void run() {
                refreshStatsBar();
            }};
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        statsRefresher = executor.scheduleAtFixedRate(statsRefresh, 0, 250, TimeUnit.MILLISECONDS);
    }
    
    /**
     * The action to do when a minefield space is left-clicked: reveal the space.
     * 
     * @param x the x-coordinate of the space that was clicked
     * @param y the y-coordinate of the space that was clicked
     */
    private void buttonLeftClick(int x, int y)
    {
        gameEngine.revealAt(x, y);
        refreshEverything();
    }
    
    /**
     * The action to do when a minefield space is right-clicked: toggle its flag status.
     * 
     * @param x the x-coordinate of the space that was clicked
     * @param y the y-coordinate of the space that was clicked
     */
    private void buttonRightClick(int x, int y)
    {
        gameEngine.toggleStatusOfSquare(x, y);
        refreshEverything();
    }
    
    /**
     * Setup the frame that contains the actual minefield GUI representation.
     * 
     * @param width how wide the minefield is in spaces
     * @param height how high the minefield is in spaces
     * @return The created JPanel containing the grid of buttons.
     */
    private JPanel makeFieldFrame(int width, int height)
    {
        JPanel minefieldPanel = new JPanel();
        minefieldPanel.setLayout(new GridLayout(height, width));
        
        minefieldButtons = new JButton[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                createMinefieldButton(minefieldPanel, x, y);
            }
        }
        return minefieldPanel;
    }
    
    /**
     * Setup the frame that contains the statistics GUI.
     * Stores all the labels in the appropriate instance variables for later retrieval.
     * 
     * @return The created JPanel containing the statistics labels.
     */
    private JPanel makeStatsFrame()
    {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(1, 3));
        
        difficultyLabel = new JLabel("", SwingConstants.CENTER);
        timerLabel = new JLabel("", SwingConstants.CENTER);
        minesLeftLabel = new JLabel("", SwingConstants.CENTER);
        
        statsPanel.add(difficultyLabel);
        statsPanel.add(minesLeftLabel);
        statsPanel.add(timerLabel);
        
        return statsPanel;
    }
    
    /**
     * Setup the GUI, including subcomponents like the statistics bar and minefield display.
     * 
     * @param width how wide the minefield is in spaces
     * @param height how high the minefield is in spaces
     */
    private void makePrimaryFrame(int x, int y)
    {
        //Deal with the root panel.
        frame = new JFrame(TITLE + " " + VERSION);
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        //Make the statistics panel
        pane.add(makeStatsFrame(), BorderLayout.NORTH);
        //Make the minefield panel
        pane.add(makeFieldFrame(x, y), BorderLayout.CENTER);
        
        //get everything to appropriate initial values
        refreshEverything();
        
        //restrict the minimum size of the window based on the field size.
        frame.setMinimumSize(new Dimension(30*x, (30*y)+30));
        
        //display the GUI!
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Creates an individual minefield space (as a button), sets up some initial values,
     * and adds it to the panel passed to it. The button will also have a listener setup
     * so it can respond to left- and right-clicks which will trigger the appropriate
     * buttonLeftClick and buttonRightClick methods, provided the button itself is enabled.
     * 
     * All of the buttons will be stored in the appropriate location within the minefieldButtons[][]
     * array, for later access. The x and y coordinates are passed to the button*Click() methods
     * when the button is clicked, so that the methods know which button the user clicked on.
     * 
     * @param panel The parent container which the button should be added to.
     * @param x The x-coordinate of the button.
     * @param y The y-coordinate of the button.
     */
    private void createMinefieldButton(Container panel, int x, int y)
    {
        //Create the button and add it to the panel and the minefieldButtons array for later retrieval.
        JButton button = new JButton();
        panel.add(button);
        minefieldButtons[x][y] = button;
        
        //Make the button look right.
        button.setOpaque(true);
        button.setBorder(WHITE_BORDER);
        button.setBorderPainted(true);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBackground(HIDDEN_COLOR);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.PLAIN, BASE_FONT_SIZE));
        
        //Setup the listener so that the button will respond to left- and right-mouse clicks
        button.addMouseListener(new MouseAdapter() {
            private boolean buttonPressed = false;
            
            @Override
            public void mousePressed(MouseEvent e)
            {
                button.getModel().setArmed(true);
                button.getModel().setPressed(true);
                buttonPressed = true;
            }
            
            @Override
            public void mouseReleased (MouseEvent e)
            {
                button.getModel().setArmed(false);
                button.getModel().setPressed(false);
                //Only act if the button is pressed, and also enabled.
                if (buttonPressed && button.isEnabled()) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        //right mouse button is clicked
                        buttonRightClick(x, y);
                    } else {
                        //left mouse button clicked
                        buttonLeftClick(x, y);
                    }
                }
                buttonPressed = false;
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                buttonPressed = false;
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonPressed = true;
            }
        });

    }
    
    /**
     * Updates the displayed minefield, by fetching a list of stale spaces from the game engine,
     * and then iteratively updating the relevant buttons as appropriate.
     */
    private void refreshField()
    {
        Set<Location> staleLocations = gameEngine.getStaleLocations();
        for (Location sl : staleLocations) {
            Square square = gameEngine.getSquareAt(sl);
            switch (square.getStatus()) {
                case HIDDEN:    //square is currently hidden
                    minefieldButtons[sl.getX()][sl.getY()].setText("");
                    minefieldButtons[sl.getX()][sl.getY()].setBackground(HIDDEN_COLOR);
                    break;
                case FLAGGED:   //square is currently flagged
                    minefieldButtons[sl.getX()][sl.getY()].setText("F");
                    minefieldButtons[sl.getX()][sl.getY()].setBackground(FLAGGED_COLOR);
                    break;
                case QUESTIONED:    //square is currently makred as questionable
                    minefieldButtons[sl.getX()][sl.getY()].setText("?");
                    minefieldButtons[sl.getX()][sl.getY()].setBackground(QUESTIONED_COLOR);
                    break;
                case REVEALED:  //square is revealed
                default:
                    //revealed squares can never be clicked
                    minefieldButtons[sl.getX()][sl.getY()].setEnabled(false);
                    minefieldButtons[sl.getX()][sl.getY()].setBackground(REVEALED_COLOR);
                    
                    if (square.isMine()) {
                        //if the square is a mine, we stop here.
                        minefieldButtons[sl.getX()][sl.getY()].setText("M");
                        minefieldButtons[sl.getX()][sl.getY()].setBackground(ACTUAL_MINE_COLOR);
                        minefieldButtons[sl.getX()][sl.getY()].setForeground(Color.WHITE);
                    } else {
                        //if its not a mine, show the adjacent mines number, except if its zero in which case
                        //leave it blank.
                        if (square.getQtyNeighbourMines() == 0) {
                            minefieldButtons[sl.getX()][sl.getY()].setText("");
                        } else {
                            minefieldButtons[sl.getX()][sl.getY()].setText(Short.toString(square.getQtyNeighbourMines()));
                            //As the number of neighbouring mines gets larger, also make the display font correspondingly larger.
                            minefieldButtons[sl.getX()][sl.getY()].setFont(new Font("Arial", Font.PLAIN, BASE_FONT_SIZE + (square.getQtyNeighbourMines() * 2)));
                        }
                    }
            }
        }
    }
    
    /**
     * Refresh only the statistics bar
     */
    private void refreshStatsBar()
    {
        difficultyLabel.setText("Difficulty: " + gameEngine.getLevel().toString() + ".");
        timerLabel.setText("Time Elapsed: " + gameEngine.getPlayTime() + "s.");
        minesLeftLabel.setText("Unflagged Mines: " + gameEngine.getQtyMinesRemaining() + ".");
    }
    
    /**
     * Updates everything on the GUI, and additionally checks if the game has come to an end -
     * if it has it will call the gameOver method.
     */
    private void refreshEverything()
    {
        refreshField();
        refreshStatsBar();

        //Check to see if the game has finished
        if (!gameEngine.getGameInProgress()) {
            gameOver();
        }
    }
    
    /**
     * Called when the game engine has reported that the game has finished. Stop the stats refresh timer
     * (so that the elapsed time displayed remains fixed), determine if the player won or lost and display
     * an appropriate congratulatory or commiserative message, then terminate the program.
     */
    private void gameOver()
    {
        //Stop the stats bar refresher - there's no point no as the game's over.
        statsRefresher.cancel(false);
        
        //Determine if the player won or lost.
        String message;
        if (gameEngine.getGameWon()) {
            message = "Congratulations! You won!";
        } else {
            message = "Too bad, you lost.";
        }
        
        //Tell the player if they won or lost.
        JOptionPane.showMessageDialog(
            frame,
            message,
            "Game Over",
            JOptionPane.INFORMATION_MESSAGE
            );
        
        //All done.
        quitProgram();
    }
    
    /**
     * Displays a dialog box from which the user will choose what level of difficulty they want from
     * a list of available options. If the user cancels this dialog box, returns null.
     * 
     * @return the level the user selected.
     */
    private GameLevel chooseLevel()
    {
        //get level options
        Object[] possibleValues = GameLevel.class.getEnumConstants();
        
        //ask the user what level they want to play
        Object selectedValue = JOptionPane.showInputDialog(
            null,
            "Welcome to " + TITLE + "!\nPlease choose a difficulty level...",
            "Choose difficulty",
            JOptionPane.QUESTION_MESSAGE,
            null,
            possibleValues,
            possibleValues[0]
            );
        
        //Check if the user selected a level, or just cancelled, and act as appropriate.
        if (selectedValue instanceof GameLevel) {
            return (GameLevel)selectedValue;
        }
        return null;
    }
    
}
