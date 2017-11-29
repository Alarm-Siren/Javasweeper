
/**
 * Enumeration class SpaceStatus - represents the status of a space.
 * 
 * HIDDEN is the default status, FLAGGED and QUESTIONED are set by the user and REVEALED is when
 * the space has been trodden on and its quantity of neighbours and whether or not it is a mine
 * can then be accessed.
 * 
 * @author  Nicholas Parks Young
 * @version 2015-04-01
 */
public enum SpaceStatus
{
    HIDDEN, FLAGGED, QUESTIONED, REVEALED
}
