import java.awt.Color;
import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.Node;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class xmlFactory implements xmlFactoryIF {

    private String DBDir;
    org.w3c.dom.Document doc;
    private ArrayList<DBNode> fields;
    private String DBDirExtended = null;

    public xmlFactory(final String DBDir) {
        this.DBDir = DBDir;
        String operatingSystem = System.getProperty("os.name");
    	operatingSystem = operatingSystem.substring(0, 3);
    	if(operatingSystem.equals("Win")) {
    		DBDirExtended = DBDir + "\\";
    	} else {
    		DBDirExtended = DBDir + "/";
    	}
    }


    @Override
    public TabelImp ReadXML(final String TableName) throws Exception {
        fields = new ArrayList<>();
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            File inputFile = new File(DBDirExtended + TableName + ".xml");
            InputStream inputStream = new FileInputStream(inputFile);
            org.w3c.dom.Document doc = builder.parse(DBDirExtended + TableName + ".xml");
            NodeList rootElement = doc.getElementsByTagName(TableName);
            NodeList tableFields = rootElement.item(0).getChildNodes();
            for (int i = 1; i < tableFields.getLength(); i=i+2) {
                NodeList currentNode = tableFields.item(i).getChildNodes();
                String readName = null;
                boolean readType = false;
                LinkedList<Object> readColumn = new LinkedList<>();
                DBNode element = null;
                org.w3c.dom.Node Name = currentNode.item(1);
                readName = Name.getTextContent();
                org.w3c.dom.Node Type =  currentNode.item(3);
                readType = (Type.getTextContent()).equals("integer")? true : false;
                NodeList Rows = currentNode.item(5).getChildNodes();
                for (int j = 1; j < Rows.getLength(); j=j+2) {
                    org.w3c.dom.Node row =  Rows.item(j);
                    if (!readType) {
                        readColumn.add(row.getTextContent());
                    } else {
                        if(!row.getTextContent().equals("null"))
                            readColumn.add(Integer.parseInt(row.getTextContent()));
                        else
                            readColumn.add(null);
                    }
                }
                element = new DBNode(readName, readType);
                element.setColumn(readColumn);
                fields.add(element);
            }
            inputStream.close();
        return new TabelImp(TableName, fields);
    }

    @Override
    public void WriteXML(final TabelImp Table) throws Exception {
        // TODO Auto-generated method stub
        File file = new File(DBDirExtended+Table.GetTableName()+".xml");
        file.delete();
        DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder build = docFact.newDocumentBuilder();
        doc = build.newDocument();
        DOMImplementation domImpl = doc.getImplementation();
        DocumentType doctype = domImpl.createDocumentType(Table.GetTableName(), "SYSTEM", Table.GetTableName()+".dtd");
        doc.appendChild(doctype);
        Element root = doc.createElement(Table.GetTableName());
        doc.appendChild(root);
        ArrayList<DBNode> s = Table.getTable();
        for(int i=0;i<s.size();i++){
            Element DBnode = doc.createElement("DBNode");
            root.appendChild(DBnode);
            Element name  = doc.createElement("Name");
            name.appendChild(doc.createTextNode(s.get(i).getName()));
            Element type = doc.createElement("Type");
            String t = (s.get(i).getType())?"integer":"string";
            type.appendChild(doc.createTextNode(t));
            Element column = doc.createElement("Column");
            for(int j=0;j<s.get(i).getColumn().size();j++){
                Element row  = doc.createElement("Row");
                row.appendChild(doc.createTextNode(String.valueOf(s.get(i).getColumn().get(j))));
                column.appendChild(row);
            }
            DBnode.appendChild(name);
            DBnode.appendChild(type);
            DBnode.appendChild(column);
          }
        TransformerFactory tranFactory = TransformerFactory.newInstance();
        Transformer aTransformer;
        aTransformer = tranFactory.newTransformer();
        aTransformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
        aTransformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
        DOMSource source = new DOMSource(doc);
        FileWriter fos = new FileWriter(DBDirExtended + Table.GetTableName() + ".xml");
        StreamResult result = new StreamResult(fos);
        aTransformer.transform(source, result);
        new head().makeDtd(Table.GetTableName(),DBDirExtended );
        fos.close();
    }

    @Override
    public void SetNewDB(final String NewDir) {
        // TODO Auto-generated method stub
        DBDir = NewDir;
    }

}