import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.soap.Node;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class head implements headInterface{

	static public String Dir;
	private int operatingSystemNum;

	public head() {
		String operatingSystem = System.getProperty("os.name");
		operatingSystem = operatingSystem.substring(0, 3);
		if (operatingSystem.equals("Win")) {
			operatingSystemNum = 1;
			Dir = Paths.get("").toAbsolutePath().toString().replace("\\", "\\\\") + "\\\\";
		} else {
			operatingSystemNum = 2;
			Dir = System.getProperty("user.dir") + "/";
		}

	}

	@Override
	public void MakeDB(final String Name) {
		File x = new File(Dir + Name);
		x.mkdir();
	}

	@Override
	public void DeleteDB(final String Name) {
		File file = new File(Dir + Name);
		File[] files = file.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory())
					file.delete();
				else
					f.delete();
			}
		}
		file.delete();
	}

	@Override
	public void MakeTable(final String Db, final String tableName, final ArrayList<DBNode> s) throws Exception {
		String DirExtended = null;
		switch (operatingSystemNum) {
		case 1:
			DirExtended = Dir + Db + "\\";
			break;
		case 2:
			DirExtended = Dir + Db + "/";
		}
		org.w3c.dom.Document doc;
		DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder build = docFact.newDocumentBuilder();
		doc = build.newDocument();
		DOMImplementation domImpl = doc.getImplementation();
		DocumentType doctype = domImpl.createDocumentType(tableName, "SYSTEM", tableName + ".dtd");
		doc.appendChild(doctype);
		Element root = doc.createElement(tableName);
		doc.appendChild(root);
		for (int i = 0; i < s.size(); i++) {
			Element DBnode = doc.createElement("DBNode");
			root.appendChild(DBnode);
			Element name = doc.createElement("Name");
			name.appendChild(doc.createTextNode(s.get(i).getName()));
			Element type = doc.createElement("Type");
			String t = s.get(i).getType() ? "integer" : "string";
			type.appendChild(doc.createTextNode(t));
			Element column = doc.createElement("Column");
			for (int j = 0; j < s.get(i).getColumn().size(); j++) {
				Element x = doc.createElement("Row");
				x.appendChild(doc.createTextNode(String.valueOf(s.get(i).getColumn().get(j))));
				column.appendChild(x);
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
		FileWriter fos = new FileWriter(DirExtended + tableName + ".xml");
		StreamResult result = new StreamResult(fos);
		aTransformer.transform(source, result);
		makeDtd(tableName, DirExtended);
		fos.close();
	}

	@Override
	public void makeDtd(final String tabelName, final String path) {
		try {
			PrintWriter writer = new PrintWriter(path + tabelName + ".dtd", "UTF-8");
			writer.println("<!ELEMENT " + tabelName + " (DBNode+)>");
			writer.println("<!ELEMENT DBNode (Name, Type, Column)>");
			writer.println("<!ELEMENT Name (#PCDATA)>");
			writer.println("<!ELEMENT Type (#PCDATA)>");
			writer.println("<!ELEMENT Column (Row+)>");
			writer.println("<!ELEMENT Row (#PCDATA)>");
			writer.close();
		} catch (Exception e) {
			// do something
		}
	}

	@Override
	public void DeleteTable(final String Db, final String Name) {
		String DirExtended = null;
		switch (operatingSystemNum) {
		case 1:
			DirExtended = Dir + Db + "\\";
			break;
		case 2:
			DirExtended = Dir + Db + "/";
		}
		File file = new File(DirExtended + Name + ".xml");
		File file2 = new File(DirExtended + Name + ".dtd");
		if (!file2.exists())
			if (!file.exists())
				throw new RuntimeException();
			else
				file.delete();
		else {
			file2.delete();
			if (!file.exists())
				throw new RuntimeException();
			else
				file.delete();
		}
	}

	@Override
	public void insertIntoTable(final ArrayList<Object> x, final int index, final String dBase, final String tableName)
			throws Exception {
		xmlFactory aa = new xmlFactory(Dir + dBase);
		ArrayList<DBNode> fields = (aa.ReadXML(tableName)).getTable();
		boolean type = false;
		if (x.size() != fields.size() || fields.get(0).getColumn().size() < index) {
			throw new RuntimeException("Invalid Input");
		}
		int i = 0;
		for (i = 0; i < x.size(); i++) {
			if (x.get(i) != null) {
				if (x.get(i) instanceof String) {
					type = false;
				} else if (x.get(i) instanceof Integer) {
					type = true;
				} else {
					throw new RuntimeException("Input of Invalid type");
				}
				if (type == fields.get(i).getType()) {
					fields.get(i).getColumn().add(index, x.get(i));
				} else {
					throw new RuntimeException("Invalid Input");
				}
			} else {
				fields.get(i).getColumn().add(index, x.get(i));
			}
		}
		TabelImp xxx = new TabelImp(tableName, fields);
		aa.WriteXML(xxx);
	}

	@Override
	public void insertIntoTable(final ArrayList<Object> x, final String dBase, final String tableName)
			throws Exception {
		xmlFactory aa = new xmlFactory(Dir + dBase);
		ArrayList<DBNode> fields = (aa.ReadXML(tableName)).getTable();
		boolean type = false;
		/*
		 * if (x.size() != fields.size()) { throw new RuntimeException(
		 * "Invalid Input"); }
		 */
		int i = 0;
		for (i = 0; i < x.size(); i++) {
			if (x.get(i) != null) {
				if (x.get(i) instanceof String) {
					type = false;
				} else if (x.get(i) instanceof Integer) {
					type = true;
				} else {
					throw new RuntimeException("Input of Invalid type");
				}
				if (type == fields.get(i).getType()) {
					fields.get(i).getColumn().add(x.get(i));
				} else {
					throw new RuntimeException("Invalid Input");
				}
			} else {
				fields.get(i).getColumn().add(x.get(i));
			}
		}
		TabelImp xxx = new TabelImp(tableName, fields);
		aa.WriteXML(xxx);
	}

	@Override
	public void deleteFromTable(final int z, final String dBase, final String tableName) throws Exception {
		xmlFactory aa = new xmlFactory(Dir + dBase);
		ArrayList<DBNode> fields = (aa.ReadXML(tableName)).getTable();
		int limits = fields.get(0).getColumn().size();
		if (fields.size() > 0) {
			int i = 0;
			for (i = 0; i < fields.size(); i++) {
				if (limits >= z) {
					fields.get(i).getColumn().remove(z);
				} else {
					throw new RuntimeException("List has no such element");
				}
			}
			TabelImp xxx = new TabelImp(tableName, fields);
			aa.WriteXML(xxx);
		} else {
			throw new RuntimeException("List is Empty");
		}
	}

	@Override
	public void deleteAll(final String dBase, final String tableName) throws Exception {
		xmlFactory xmlParser = new xmlFactory(Dir + dBase);
		ArrayList<DBNode> fields = (xmlParser.ReadXML(tableName)).getTable();

		for (int i = 0; i < fields.size(); i++) {
			fields.get(i).getColumn().clear();
		}
		TabelImp rewrite = new TabelImp(tableName, fields);
		xmlParser.WriteXML(rewrite);
	}

	/*
	 * public void modifyTable(final String tableName, final String dBase, final
	 * ArrayList<ArrayList> modifications, final ArrayList<String> conditions)
	 * throws Exception { TabelImp table = null; xmlFactory aa = new
	 * xmlFactory(Dir + dBase); try { ArrayList<DBNode> fields =
	 * (aa.ReadXML(tableName)).getTable(); table = new TabelImp(tableName,
	 * fields); } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } if(conditions != null) { if
	 * (!table.isIn(conditions.get(0))) { throw new RuntimeException(); } for
	 * (int i = 0; i < modifications.get(0).size(); i++) { if
	 * (!table.isIn((String)modifications.get(0).get(i))) { throw new
	 * RuntimeException(); } } int index = table.getIndex(conditions.get(0));
	 * DBNode tmp = table.getFromTable(index); index = 0; for (Object element :
	 * tmp.getColumn()) { if (conditions.get(1).equals("=")) { if
	 * (conditions.get(2).equals((element !=
	 * null)?element.toString():(index++))) { modify(index, modifications,
	 * table); } } else if (conditions.get(1).equals(">")) { if
	 * (conditions.get(2).equals((element !=
	 * null)?element.toString():(index++))) { modifyBigger(index+1,
	 * modifications, table); } } else { if (conditions.get(2).equals((element
	 * != null)?element.toString():(index++))) { modifySmaller(index-1,
	 * modifications, table); } } index++; } } else { modifyAll(modifications,
	 * table); } aa.WriteXML(table); }
	 *
	 * private void modifyAll(final ArrayList<ArrayList> modifications, final
	 * TabelImp table) { // TODO Auto-generated method stub for(int y = 0; y <
	 * table.getTable().get(0).getColumn().size();y++) { modify(y,
	 * modifications, table); } }
	 *
	 * private void modifySmaller(final int i, final ArrayList<ArrayList>
	 * modifications, final TabelImp table) { // TODO Auto-generated method stub
	 * for(int y = 0; y < i+1;y++) { modify(y, modifications, table); } }
	 *
	 * private void modifyBigger(final int i, final ArrayList<ArrayList>
	 * modifications, final TabelImp table) { // TODO Auto-generated method stub
	 * for(int y = i; y < table.getTable().get(0).getColumn().size();y++) {
	 * modify(y, modifications, table); } }
	 *
	 * private void modify(final int index, final ArrayList<ArrayList>
	 * modifications, final TabelImp table) { for(int i = 0; i <
	 * modifications.get(0).size(); i++) { table.SetinRow(index,
	 * modifications.get(0).get(i), modifications.get(1).get(i)); } } /*private
	 * void modify(final int index, final ArrayList<ArrayList<String>>
	 * modifications) { ArrayList<String> tmpRow; LinkedList<Object> tmpCol; for
	 * (int i = 0; i < modifications.size(); i++) { int indexCol =
	 * table.getIndex(modifications.get(i).get(0)); tmpRow =
	 * table.getRow(index); tmpRow.set(indexCol, modifications.get(i).get(1));
	 * tmpCol = table.getTable().get(indexCol).getColumn(); tmpCol.set(index,
	 * modifications.get(i).get(1)); } }
	 */
	TabelImp table2;

	private void validateElement(final String target) {
		if (!table2.isIn(target)) {
			throw new RuntimeException("Invalid input");
		}
	}

	@Override
	public void modify(final String Db, final String Name, final ArrayList<ArrayList<String>> wantedCol,
			final ArrayList<Object> conditions) throws Exception {
		table2 = new xmlFactory(Db).ReadXML(Name);
		for (int i = 0; i < wantedCol.size(); i++) {
			validateElement(wantedCol.get(i).get(0));
		}
		if (conditions == null) {
			for (int i = 0; i < table2.getNumberOfRows(); i++) {
				for (int j = 0; j < wantedCol.size(); j++) {
					table2.SetinRow(i, wantedCol.get(j).get(0), wantedCol.get(j).get(1));
				}
			}
		} else {
			validateElement(conditions.get(0).toString());
			for (int i = 0; i < table2.getNumberOfRows(); i++) {
				String tmpString = table2.getRow(i).get(table2.getIndex(conditions.get(0).toString()));
				if (conditions.get(1).equals("=")) {
					if (tmpString.equals(conditions.get(2).toString()))
						applyModifiation(wantedCol, i);
				} else if (conditions.get(2) instanceof Integer
						&& table2.getTable().get(table2.getIndex(conditions.get(0).toString())).getType()) {
					if (conditions.get(1).equals(">")) {
						if (Integer.parseInt(tmpString) > Integer.parseInt(conditions.get(2).toString()))
							applyModifiation(wantedCol, i);
					} else if (conditions.get(1).equals("<")) {
						if (Integer.parseInt(tmpString) < Integer.parseInt(conditions.get(2).toString()))
							applyModifiation(wantedCol, i);
					} else
						throw new RuntimeException("Invalid operation");
				} else if (conditions.get(2) instanceof String
						&& !table2.getTable().get(table2.getIndex(conditions.get(0).toString())).getType()) {
					if (conditions.get(1).equals(">")) {
						if (tmpString.compareTo(conditions.get(2).toString()) > 0) {
							applyModifiation(wantedCol, i);
						}
					} else if (conditions.get(1).equals("<")) {
						if (tmpString.compareTo(conditions.get(2).toString()) < 0)
							applyModifiation(wantedCol, i);
					} else
						throw new RuntimeException("Invalid operation");
				} else
					throw new RuntimeException("Invalid Types");
			}
		}
		new xmlFactory(Db).WriteXML(table2);
	}

	private void applyModifiation(final ArrayList<ArrayList<String>> wantedCol, final int rowIndex) {
		for (int i = 0; i < wantedCol.size(); i++) {
			table2.SetinRow(rowIndex, wantedCol.get(i).get(0), wantedCol.get(i).get(1));
		}
	}

	@Override
	public  ArrayList<DBNode> select(final String tableName, final String dBase, final ArrayList<String> wanted)
			throws Exception {
		xmlFactory aa = new xmlFactory(Dir + dBase);
		ArrayList<DBNode> fields = (aa.ReadXML(tableName)).getTable();
		TabelImp table = new TabelImp(tableName, fields);
		ArrayList<DBNode> tmp = new ArrayList<>();
		for (String element : wanted) {
			if (!table.isIn(element)) {
				return null;
			}
			tmp.add(table.getFromTable(table.getIndex(element)));
		}
		return tmp;
	}

	public static void main(final String[] args) throws Exception {
		head x = new head();
		DBNode a = new DBNode("Name", false);
		DBNode b = new DBNode("ID", true);
		DBNode c = new DBNode("Salary", true);
		DBNode d = new DBNode("Date", false);
		ArrayList<DBNode> xx = new ArrayList<DBNode>();
		LinkedList<Object> z = new LinkedList<>();
		z.add("Ahmed");
		z.add("Dowo");
		z.add("Hana");
		a.setColumn(z);
		xx.add(a);
		LinkedList<Object> r = new LinkedList<>();
		r.add(5);
		r.add(6);
		r.add(7);
		b.setColumn(r);
		xx.add(b);
		LinkedList<Object> t = new LinkedList<>();
		t.add(9);
		t.add(7);
		t.add(15);
		c.setColumn(t);
		xx.add(c);
		LinkedList<Object> y = new LinkedList<>();
		y.add("dasdsa");
		y.add("sadsaf");
		y.add(null);
		d.setColumn(y);
		xx.add(d);
		x.MakeDB("Neww");
		try {
			x.MakeTable("Neww", "Ahmede", xx);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// x.DeleteTable("Ne", "Ahme");
		/*
		 * xmlFactory ahme = new xmlFactory(Dir + "Neww"); ahme.WriteXML(new
		 * TabelImp("Ahmede", xx)); x.deleteFromTable(2, "Neww", "Ahmede");
		 * ArrayList<Object> oo = new ArrayList<>(); oo.add("REda");
		 * oo.add(null); oo.add(9); oo.add(null); x.insertIntoTable(oo, "Neww",
		 * "Ahmede"); ArrayList<Object> oo2 = new ArrayList<>();
		 * oo2.add("REdaasda"); oo2.add(null); oo2.add(null); oo2.add(null);
		 * x.insertIntoTable(oo2, "Neww", "Ahmede"); ahme.ReadXML("Ahmede");
		 * x.deleteFromTable(2, "Neww", "Ahmede"); ArrayList<Object> oo3 = new
		 * ArrayList<>(); oo3.add("zucc"); oo3.add(null); oo3.add(null);
		 * oo3.add("testbench"); x.insertIntoTable(oo3,2, "Neww", "Ahmede");
		 * x.insertIntoTable(oo3,4, "Neww", "Ahmede"); //x.deleteAll("Neww",
		 * "Ahmede"); /*TabelImp as = ahme.ReadXML("Ahmede"); ArrayList<String>
		 * wanted = new ArrayList<>(); wanted.add("ID"); ArrayList<DBNode> tmp =
		 * select("Ahmede","Neww", wanted); for(int i = 0 ; i < tmp.size() ;
		 * i++){ for(int j = 0 ; j < tmp.get(i).getColumn().size();j++){
		 * System.out.println("Res    :  "+tmp.get(i).getColumn().get(j)); } }
		 */
	}
}