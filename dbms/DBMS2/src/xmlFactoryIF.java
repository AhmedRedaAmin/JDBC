
public interface xmlFactoryIF {

    /**
     * Read An XML file in the Database File
     * which have been initialized in the creation of the object
     * then convert it to Table Object to used at any time.
     * @param TableName The Table Name (XML Name).
     * @return Table with all its parameters.
     * @throws Exception 
     */
    public TabelImp ReadXML(String TableName) throws Exception;
    
    /**
     * Takes a table and convert it to XML file
     * by deleting the XML file which have the same name
     * and create a new one with the new parameters.
     * @param Table table which full of data.
     * @throws Exception 
     */
    public void WriteXML(TabelImp Table) throws Exception;
    
    /**
     * Change the Directory of the Current DataBase 
     * To another Directory Which i now using.
     * @param NewDir the Directory of the New DataBase.
     */
    public void SetNewDB(String NewDir);
}
