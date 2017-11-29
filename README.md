# Nopac
*Version 1.0*

A simple clone of Minesweeper implemented using Java and Swing.

## Comments, Requests, Bugs & Contributions
All are welcome.  
Please file an Issue or Pull Request at https://github.com/Alarm-Siren/Javasweeper

## License
Copyright 2015, Nicholas Parks Young. Some Rights Reserved.  
This program is licensed under the GNU LGPL v2.1, which can be found in file LICENSE.txt.

## Building Javasweeper
The Javasweeper code is designed to compile with the [BlueJ](https://www.bluej.org/). There are no other dependencies. It may work with other IDEs / Java compilers, though I have not checked.

## Can I play it without building it myself?
Yes! I have uploaded an executable JAR, Have a look in [Releases](https://github.com/Alarm-Siren/Javasweeper/releases).

## Game Instructions

### Game Objective
To reveal all the spaces on the minefield, except those containing mines. If you reveal a space with a mine on it, you will lose the game.

### Controls
Left-click on an unrevealed space to reveal it.  
Right-click on an unrevealed space to toggle it between normal, flagged and marked as questionable.

### Details of Interface

After launching the game, you will be asked which difficulty of play you would like: selectan answer from the list provided and click OK. Clicking cancel will terminate the program.

You will now be presented with the minefield:
* Along the top of the window is the statistics bar. This contains the difficulty level, the amount of  time (in seconds) since you started the game, and the number of mines present in the minefield minus the number of flags you have placed on the minefield.
* Dark Green blocks are unrevealed spaces.
* Light Green blocks are revealed spaces:
  * If it contains a number, that number represents the quantity of mines present in adjacent spaces (including diagonally adjacent spaces). If it is blank, then it has an implicit number of zero.
* Light Red blocks, which will contain the letter "F", are unrevealed spaces which the player has flagged as having a mine in. In all respects they behave the same as unrevealed spaces, the flagged  status is for player convenience.
* Yellow blocks, which will contain the character "?", are unrevealed spaces which the player has marked as questionable. In all respects they behave the same as unrevealed spaces, the questionable  status is for player convenience.
* Dark Red blocks, which will contain the letter "M", are revealed mines.

You may resize the Javasweeper game window, but there is an enforced minimum size which varies dependingon the size of the minefield, which itself varies with difficulty level.

If you win or lose the game, all hidden mines will be revealed and a dialog box will appear telling youthat you have won or lost as appropriate. When you dismiss this dialog the program will terminate.You can terminate the game early at any time by closing the Javasweeper game window.
