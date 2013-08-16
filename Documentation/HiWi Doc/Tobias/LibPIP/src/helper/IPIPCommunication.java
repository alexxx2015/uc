package helper;

import structures.PDPEvent;
import structures.UniqueIntList;



public interface IPIPCommunication
{
    /// <summary>
    /// Initializes the PIP and its data structures.
    /// </summary>
    /// <returns></returns>
    boolean initializePIP();

    /// <summary>
    /// Creates an inital mapping between a provided representation and a new data item.
    /// </summary>
    /// <param name="PID"></param>
    /// <param name="rep"></param>
    /// <returns></returns>
    int initialRepresentation(int PID, String rep);

    /// <summary>
    /// Sends an received event to the PIPSemantics component to update the internal information flow model accordingly.
    /// </summary>
    /// <param name="newEvent"></param>
    /// <returns></returns>
    int updatePIP(PDPEvent newEvent);

    /// <summary>
    /// Checks, whether the provided representation refines the data item, referenced by the provided data ID.
    /// If the strict parameter is set to false, the search for names is done in a relaxed way (returns true even if representation only partially matches).
    /// </summary>
    /// <param name="PID"></param>
    /// <param name="rep"></param>
    /// <param name="dataID"></param>
    /// <param name="strict"></param>
    /// <returns></returns>
    int representationRefinesData(int PID, String rep, int dataID, boolean strict);


    /// <summary>
    /// Returns a list of data IDs that are connected to the provided representation
    /// </summary>
    /// <param name="PID"></param>
    /// <param name="rep"></param>
    /// <param name="strict"></param>
    /// <returns></returns>
    UniqueIntList getDataIDbyRepresentation(int PID, String rep, boolean strict);

    /// <summary>
    /// Resets the status of the PIP.
    /// </summary>
    void resetPIP();

    /// <summary>
    /// Returns a string, containing all mappings of the PIP-internal information flow model.
    /// </summary>
    /// <returns></returns>
    String printModel();
}
