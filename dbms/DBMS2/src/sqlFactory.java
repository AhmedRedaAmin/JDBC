import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class sqlFactory implements sqlFactoryInterface {
    
    String Input;

    public sqlFactory(String Input) throws Exception {
        this.Input = Input;
        Distributor();
    }

    public void Distributor () throws Exception {
        Pattern sele = Pattern.compile("(?i)\\s*((?:select|create\\s+table|create\\s+database|drop\\s+database|drop\\s+table|delete|update|insert\\s+into))");
        Matcher matcher = sele.matcher(Input.trim().replaceAll(" +", " "));
        if (matcher.find()) {
            if (matcher.group(1).toLowerCase().equals("select"))
                SelectMachine();
            else if (matcher.group(1).toLowerCase().equals("create table"))
                CreateTableMachine();
            else if (matcher.group(1).toLowerCase().equals("create database"))
                CreateBaseMachine();
            else if (matcher.group(1).toLowerCase().equals("drop table"))
                DropTableMachine();
            else if (matcher.group(1).toLowerCase().equals("drop database"))
                DropDBMachine();
            else if (matcher.group(1).toLowerCase().equals("delete"))
                DeleteMachine();
            else if (matcher.group(1).toLowerCase().equals("update"))
                UpdateMachine();
            else if (matcher.group(1).toLowerCase().equals("insert into"))
                InsertMachine();
            else
                throw new RuntimeException("InValid Query");
        } else {
            throw new RuntimeException("InValid Query");
        }
    }
    
    public ArrayList<ArrayList<String>> SelectMachine() throws Exception {
        // TODO Auto-generated method stub
        Pattern SelecCol = Pattern.compile("(?i)\\s*select\\s+(.+)(\\s*,\\s*(\\w+))*\\s+from\\s+(\\w+).(\\w+)(\\s+where\\s+(\\w+)\\s*(\\W)\\s*(?:'(\\w+(\\s*\\w+)*)'|(\\d+)))?\\s*;");
        Matcher matcherCol = SelecCol.matcher(Input);
        if (matcherCol.find()){
            String Columns = matcherCol.group(1);
            String[] Colms = DivideComma(Columns);
            String DBName = matcherCol.group(4);
            String TableName = matcherCol.group(5);
            if (matcherCol.group(6) != null) {
                String ColName = matcherCol.group(7);
                String Condition = matcherCol.group(8);
                String RowName = (matcherCol.group(11) != null)?matcherCol.group(11):matcherCol.group(9);
                ArrayList<Integer> Indexies = getIndexies(ColName, Condition, RowName, DBName, TableName);
                return DoSelectWhere(DBName, TableName, Colms, Condition, Indexies);
            } else {
                return DoSelectSpec(DBName, TableName, Colms);
            }
        } else
            throw new RuntimeException("Invalid Input");
    }

    private ArrayList<Integer> getIndexies(String colName, String condition, String rowName, String dBName, String tableName) throws Exception {
        // TODO Auto-generated method stub
        ArrayList<Integer> Indexies = new ArrayList<>();
        TabelImp table = new xmlFactory(dBName).ReadXML(tableName);
        int ColIndex = table.getIndex(colName);
        LinkedList<Object> rows = table.getFromTable(ColIndex).getColumn();
        boolean type = table.getFromTable(ColIndex).getType();
        if (condition.equals("=") || condition.equals(">") || condition.equals("<")) {
            for (int i = 0; i < rows.size(); i++) {
                if (!type){
                    if(condition.equals("=")){
                    if (rows.get(i).equals(rowName)) {
                        Indexies.add(i);
                    }
                    } else if (condition.equals(">")) {
                        if (rows.get(i).toString().compareTo(rowName) > 0) {
                            Indexies.add(i);
                        }
                    } else if (condition.equals("<")) {
                        if (rows.get(i).toString().compareTo(rowName) < 0) {
                            Indexies.add(i);
                        }
                    }
                }else {
                    if(condition.equals("=")){
                        if (rows.get(i).equals(Integer.parseInt(rowName))) {
                            Indexies.add(i);
                        }
                        } else if (condition.equals(">")) {
                            if ((int)rows.get(i) > (Integer.parseInt(rowName))) {
                                Indexies.add(i);
                            }
                        } else if (condition.equals("<")) {
                            if ((int)rows.get(i) < (Integer.parseInt(rowName))) {
                                Indexies.add(i);
                            }
                        }
                }
            }
        } else {
            throw new RuntimeException("Invalid operator");
        }
        return Indexies;
    }

    private ArrayList<ArrayList<String>> DoSelectWhere(String dBName, String tableName, String[] colms, String condition, ArrayList<Integer> indexies) throws Exception {
        // TODO Auto-generated method stub
        TabelImp Table = new xmlFactory(dBName).ReadXML(tableName);
        if (colms.length == 1 && colms[0].equals("*")) {
            if (condition.equals("=") || condition.equals(">") || condition.equals("<")) {
                ArrayList<ArrayList<String>> x = new ArrayList<>();
            for (int i = 0; i < indexies.size(); i++) {
                ArrayList<String> y = new ArrayList<>();
                for (int j = 0; j < Table.GetTableSize(); j++) {
                    System.out.println(Table.getFromTable(j).getColumn().get(indexies.get(i)));
                    y.add((Table.getFromTable(j).getColumn().get(indexies.get(i)) != null)?Table.getFromTable(j).getColumn().get(indexies.get(i)).toString() : null);
                }
                x.add(y);
            }
            return x;
            } else {
                throw new RuntimeException("Invalid Operator");
            }
            /*else if (){
                ArrayList<ArrayList<String>> x = new ArrayList<>();
                for (int i = indexies.get(0); i < Table.GetTableSize(); i++){
                    ArrayList<String> y = new ArrayList<>();
                    for (int j = 0; j < Table.GetTableSize(); j++){
                        System.out.println(Table.getFromTable(j).getColumn().get(indexies.get(i)));
                        y.add((Table.getFromTable(j).getColumn().get(indexies.get(i)) != null)?Table.getFromTable(j).getColumn().get(indexies.get(i)).toString() : null);
                    }
                    x.add(y);
                }
                return x;
            }
            else if () {
                ArrayList<ArrayList<String>> x = new ArrayList<>();
                for (int i = 0; i < indexies.get(0)+1; i++) {
                    ArrayList<String> y = new ArrayList<>();
                    for (int j = 0; j < Table.GetTableSize(); j++) {
                        System.out.println(Table.getFromTable(j).getColumn().get(indexies.get(i)));
                        y.add((Table.getFromTable(j).getColumn().get(indexies.get(i)) != null)?Table.getFromTable(j).getColumn().get(indexies.get(i)).toString() : null);
                    }
                    x.add(y);
                }
                return x;
            }*/
        }else {
            ArrayList<ArrayList<String>> x = new ArrayList<>();
        for(String visit : colms) {
            if (Table.isIn(visit)){
                DBNode elements = Table.getFromTable(Table.getIndex(visit));
                if (condition.equals("=") || condition.equals(">") || condition.equals("<")) {
                    ArrayList<String> y = new ArrayList<>();
                    for (int i = 0; i < indexies.size(); i++) {
                        System.out.println(elements.getColumn().get(indexies.get(i)));
                        y.add((elements.getColumn().get(indexies.get(i)) != null)? elements.getColumn().get(indexies.get(i)).toString() : null);
                    }
                    x.add(y);
                } else {
                    throw new RuntimeException("Invalid Operator");
                }/*
                }
                    else if (condition.equals(">")) {
                        ArrayList<String> y = new ArrayList<>();
                        for (int i = indexies.get(0); i < elements.getColumn().size(); i++) {
                                System.out.println(elements.getColumn().get(indexies.get(i)));
                                y.add((elements.getColumn().get(indexies.get(i)) != null)? elements.getColumn().get(indexies.get(i)).toString() : null);
                        }
                        x.add(y);
                    }
                    else if (condition.equals("<")) {
                        ArrayList<String> y = new ArrayList<>();
                        for (int i = 0; i < indexies.get(0); i++) {
                            System.out.println(elements.getColumn().get(indexies.get(i)));
                            y.add((elements.getColumn().get(indexies.get(i)) != null)? elements.getColumn().get(indexies.get(i)).toString() : null);
                        }
                        x.add(y);
                    }*/
            } else {
                throw new RuntimeException("Column " + visit + "not found");
            }
        }
        return x;
        }
    }
    
    private ArrayList<ArrayList<String>> DoSelectSpec(String dBName, String tableName, String[] colms) throws Exception {
        // TODO Auto-generated method stub
        TabelImp Table = new xmlFactory(dBName).ReadXML(tableName);
        if (colms.length == 1 && colms[0].equals("*")) {
            for (int i = 0; i < Table.GetTableSize(); i++) {
                System.out.println(Table.getFromTable(i).getColumn());
            }
            return LastReturn(dBName, tableName);
        }else {
            ArrayList<ArrayList<String>> y = new ArrayList<>();
        for(String visit : colms) {
            if (Table.isIn(visit)){
                ArrayList<String> z = new ArrayList<>();
                DBNode elements = Table.getFromTable(Table.getIndex(visit));
                System.out.println(elements.getColumn());
                for(int i = 0; i < elements.getColumn().size(); i++) {
                    z.add((elements.getColumn().get(i) != null)?elements.getColumn().get(i).toString():null);
                }
                y.add(z);
            } else {
                throw new RuntimeException("Column " + visit + "not found");
            }
        }
        return y;
        }
    }

    private String[] DivideComma(String columns) {
        // TODO Auto-generated method stub
        return columns.split(",");
    }

    public ArrayList<ArrayList<String>> CreateTableMachine() throws Exception {
        // TODO Auto-generated method stub
        Pattern createTable = Pattern.compile("(?i)\\s*create\\s+table\\s+(\\w+).(\\w+)\\s+\\(\\s*((\\w+)\\s*(\\w+)\\((\\d+)\\)(\\s*,\\s*(\\w+)\\s+(\\w+)\\((\\d+)\\))*)\\s*\\)\\s*;");
        Matcher matcherCreateTable = createTable.matcher(Input);
        String DBName = null;
        String TableName = null;
        if(matcherCreateTable.find()){
            DBName = matcherCreateTable.group(1);
            TableName = matcherCreateTable.group(2);
            int size = matcherCreateTable.groupCount();
            head x= new head();
            ArrayList<DBNode> nodes = ToNodes(matcherCreateTable.group(3));
            try {
                x.MakeTable(DBName, TableName, nodes);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            throw new RuntimeException("Invalid input");
        }
        return LastReturn(DBName, TableName);
    }

    private ArrayList<DBNode> ToNodes(String group) {
        // TODO Auto-generated method stub
        String[] node = group.split("\\((\\d+)\\)\\s*(,)?\\s*");
        String[] nodes;
        ArrayList<DBNode> AllNodes = new ArrayList<>();
        for(String x : node) {
            nodes = x.split("\\s+");
            DBNode z;
            if(nodes[1].trim().equals("int") || nodes[1].trim().equals("varchar"))
                z = new DBNode(nodes[0], nodes[1].trim().equals("int")? true : false);
            else
                throw new RuntimeException("Invalid Type");
            AllNodes.add(z);
        }
        return AllNodes;
    }

    public ArrayList<ArrayList<String>> CreateBaseMachine() {
        // TODO Auto-generated method stub
        Pattern createDB = Pattern.compile("(?i)\\s*create\\s+database\\s+(\\w+)\\s*;");
        Matcher matcherCreateDB = createDB.matcher(Input);
       if(matcherCreateDB.find()){
            String DBName = matcherCreateDB.group(1);
            head x = new head ();
            x.MakeDB(DBName);
       }
       return null;
    }

    public ArrayList<ArrayList<String>> DropTableMachine() {
     // TODO Auto-generated method stub
        Pattern dropTable = Pattern.compile("(?i)\\s*drop\\s*table\\s*(\\w+).(\\w+)\\s*;");
        Matcher matcherDropTable = dropTable.matcher(Input);
        String DBName=null;
         String TableName=null;
        if(matcherDropTable.find()){
             DBName = matcherDropTable.group(1);
             TableName = matcherDropTable.group(2);
        }
        if(DBName!=null&&TableName!=null){
            try{
         head x = new head ();
         x.DeleteTable( DBName, TableName);
        }
        catch (Exception e){
            throw new RuntimeException("table not found");
        }
    }
        return null;
    }

    public ArrayList<ArrayList<String>> DropDBMachine() {
        // TODO Auto-generated method stub
        Pattern dropDB = Pattern.compile("(?i)\\s*drop\\s*database\\s*(\\w+)\\s*;");
        Matcher matcherDropDB = dropDB.matcher(Input);
        head x = new head ();
          String DBname = null;
          if( matcherDropDB .find()){
            DBname =  matcherDropDB .group(1);
          }
          if(DBname !=null){
          try{
        x.DeleteDB(DBname);
          }
          catch (Exception e){
             throw new RuntimeException("Data base not found");
          }
          }
          return null;
    }

    public ArrayList<ArrayList<String>> DeleteMachine() throws Exception {
        // TODO Auto-generated method stub
        Pattern DeleteAll = Pattern.compile("(?i)\\s*delete\\s+\\*\\s+from\\s+(\\w+).(\\w+)\\s*;");
        Matcher matcherAll = DeleteAll.matcher(Input);
        Pattern DeleteRow = Pattern.compile("(?i)\\s*delete\\s+from\\s+(\\w+).(\\w+)\\s+where\\s+(\\w+)\\s*(\\W)\\s*(?:'(\\w+(\\s*\\w+)*)'|(\\d+))\\s*;");
        Matcher matcherRow = DeleteRow.matcher(Input);
        if (matcherAll.find()) {
            String DBName = matcherAll.group(1);
            String TableName = matcherAll.group(2);
            new head().deleteAll(DBName, TableName);
            return LastReturn(DBName, TableName);
        } else if (matcherRow.find()) {
            String DBName = matcherRow.group(1);
            String TableName = matcherRow.group(2);
            String ColmnName = matcherRow.group(3);
            String condition = matcherRow.group(4);
            String Element = (matcherRow.group(7)!=null)?matcherRow.group(7):matcherRow.group(5);
            DeleteIndex(DBName, TableName, ColmnName, condition, Element);
            return LastReturn(DBName, TableName);
        } else {
            throw new RuntimeException("Invalid Input");
        }
    }

    private void DeleteIndex(String dBName, String tableName, String colmnName, String condition, String element) throws Exception {
        // TODO Auto-generated method stub
        TabelImp Table = new xmlFactory(dBName).ReadXML(tableName);
        boolean type = Table.getFromTable(Table.getIndex(colmnName)).getType();
        LinkedList<Object> rows = Table.getFromTable(Table.getIndex(colmnName)).getColumn();
        ArrayList<Integer> rowindex = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            if (!type){
                if(condition.equals("=")){
                if (rows.get(i).equals(element)) {
                    rowindex.add(i);
                }
                } else if (condition.equals(">")) {
                    if (rows.get(i).toString().compareTo(element) > 0) {
                        rowindex.add(i);
                    }
                } else if (condition.equals("<")) {
                    if (rows.get(i).toString().compareTo(element) < 0) {
                        rowindex.add(i);
                    }
                }
            }else {
                if(condition.equals("=")){
                    if (rows.get(i).equals(Integer.parseInt(element))) {
                        rowindex.add(i);
                    }
                    } else if (condition.equals(">")) {
                        if ((int)rows.get(i) > (Integer.parseInt(element))) {
                            rowindex.add(i);
                        }
                    } else if (condition.equals("<")) {
                        if ((int)rows.get(i) < (Integer.parseInt(element))) {
                            rowindex.add(i);
                        }
                    }
                }
            }
        DeleteCondetion(dBName, tableName, rowindex, condition, rows.size());
    }
    

    private void DeleteCondetion(String dBName, String tableName, ArrayList<Integer> rowindex, String condition, int size) throws Exception {
        // TODO Auto-generated method stub
        if (rowindex.size() == 0)
            throw new RuntimeException("Invalid Row");
        head x = new head();
        if (condition.equals("="))
            for(int i = 0; i < rowindex.size(); i++){
            x.deleteFromTable(rowindex.get(i)-i, dBName, tableName);
            }
        else if (condition.equals(">")) {
            for(int i = 0; i < rowindex.size(); i++){
                x.deleteFromTable(rowindex.get(i)-i, dBName, tableName);
                }
        } else if (condition.equals("<")) {
            for(int i = 0; i < rowindex.size(); i++){
                x.deleteFromTable(rowindex.get(i)-i, dBName, tableName);
                }
        } else {
            throw new RuntimeException("Invalid Condition");
        }
    }

    public ArrayList<ArrayList<String>> UpdateMachine() throws Exception {
        // TODO Auto-generated method stub
        Pattern Update = Pattern.compile("(?i)\\s*update\\s+(\\w+).(\\w+)\\s+set\\s+((\\w+)\\s*=\\s*(?:'(\\w+)'|(\\d+))(\\s*,\\s*(\\w+)\\s*=\\s*(?:'(\\w+)'|(\\d+)))*)(\\s+where\\s+(\\w+)\\s*(\\W)\\s*(?:'(\\w+)'|(\\d+)))?\\s*;");
        Matcher Updatem = Update.matcher(Input);
        ArrayList<String> Colms = new ArrayList<>();
        ArrayList<String> rows = new ArrayList<>();
        if(Updatem.find()) {
            String DBName = Updatem.group(1);
            String TableName = Updatem.group(2);
            String[] Spliter = SplitUpdate(DummyCheck(Updatem.group(3)));
            for (int i = 0; i < Spliter.length; i+=4) {
                Colms.add(Spliter[i]);
            }
            for (int i = 2; i < Spliter.length; i+=4) {
                rows.add(Spliter[i]);
            }
            if (Updatem.group(11) != null) {
                // There is Where
                UpdateCondition(DBName, TableName, Colms, rows, Updatem.group(12), Updatem.group(13), (Updatem.group(15)!=null)?Updatem.group(15):Updatem.group(14));
            } else {
                //There isn't where
                UpdateAll(DBName, TableName, Colms, rows);
            }
            return LastReturn(DBName, TableName);
        } else {
            throw new RuntimeException("Invalid Input");
        }
    }

    private String DummyCheck(String group) {
        // TODO Auto-generated method stub
        String x = group;
        Matcher m = Pattern.compile("=(\\w+)").matcher(x);
        while (m.find()) {
        x = x.replace(m.group(1),"'"+m.group(1)+"'");
        }
        return x;
    }

    private void UpdateCondition(String dBName, String tableName, ArrayList<String> colms, ArrayList<String> rows,
            String group, String group2, String group3) throws Exception {
        // TODO Auto-generated method stub
        head x = new head();
        ArrayList<ArrayList<String>> modifications = new ArrayList<>();
        for(int i = 0; i < colms.size(); i++) {
            ArrayList<String> node = new ArrayList<>();
            node.add(colms.get(i));
            node.add(rows.get(i));
            modifications.add(node);
        }
        ArrayList<Object> conditions = new ArrayList<>();
        conditions.add(group);
        conditions.add(group2);
        conditions.add(isString(group3)?group3:Integer.parseInt(group3));
        x.modify(dBName, tableName, modifications, conditions);
    }

    private void UpdateAll(String dBName, String tableName, ArrayList<String> colms, ArrayList<String> rows) throws Exception {
        // TODO Auto-generated method stub
        head x = new head();
        ArrayList<ArrayList<String>> modifications = new ArrayList<>();
        for(int i = 0; i < colms.size(); i++) {
            ArrayList<String> node = new ArrayList<>();
            node.add(colms.get(i));
            node.add(rows.get(i));
            modifications.add(node);
        }
        x.modify(dBName, tableName, modifications, null);
    }

    public String[] SplitUpdate(String group) {
        // TODO Auto-generated method stub
        return group.split("\\s*\\W\\s*");
    }

    public ArrayList<ArrayList<String>> InsertMachine() throws Exception {
        // TODO Auto-generated method stub
        Pattern FullInsert = Pattern.compile("(?i)\\s*insert\\s+into\\s+(\\w+).(\\w+)(\\s*\\(\\s*(\\w+)((\\s*,\\s*(\\w+))*)\\s*\\))?\\s*values\\s*\\(\\s*(?:'*(\\w+(\\s*\\w+)*)'*|\\d+)((,(?:'*(\\w+(\\s*\\w+)*)'*|(\\d+)))*)\\s*\\)\\s*;");
        Matcher FullInsertm = FullInsert.matcher(Input);
        if (FullInsertm.find()) {
            String DBName = FullInsertm.group(1);
            String TableName = FullInsertm.group(2);
            if (FullInsertm.group(3) != (null)) {
                //Group 2 Msh Mawgoda
                String FirstColm = FullInsertm.group(4);
                String[] RestColm = DivideCommaColm(FullInsertm.group(5));
                String FirstValue = FullInsertm.group(8);
                String[] RestValues;
                String [] xx = {""};
                if (FullInsertm.group(10) != null) {
                    RestValues = DivideCommaVal(FullInsertm.group(10));
                } else {
                    RestValues = xx;
                }
                if (RestColm.length != RestValues.length)
                    throw new RuntimeException("Invalid parameters");
                InsertNow(DBName, TableName, FirstColm, RestColm,FirstValue, RestValues);
            } else {
              //Group 2 Mawgoda
                String FirstValue = FullInsertm.group(8);
                String[] xx = {""};
                String[] RestValues;
                if (FullInsertm.group(10) != null) {
                    RestValues = DivideCommaVal(FullInsertm.group(10));
                } else {
                    RestValues = xx;
                }
                //String[] RestValues = (FullInsertm.group(9) != null)?DivideCommaVal(FullInsertm.group(9)) : xx;
                if(CheckInsertAll(DBName, TableName, FirstValue, RestValues)) {
                    InsertNow(DBName, TableName, null, null,FirstValue, RestValues);
                } else {
                    throw new RuntimeException("Invalid Parameters");
                }
            }
            return LastReturn(DBName, TableName);
        } else {
            throw new RuntimeException("Invalid Input");
        }
    }

    private void InsertNow(String dBName, String tableName, String firstColm, String[] restColm, String firstValue,
            String[] restValues) throws Exception {
        // TODO Auto-generated method stub
        head x = new head();
        xmlFactory fac = new xmlFactory(dBName);
        TabelImp y = fac.ReadXML(tableName);
        ArrayList<Object> ob = new ArrayList<>();
        if (firstColm != null) {
            for(int i = 0; i < y.GetTableSize(); i++) {
                ob.add(null);
            }
            ob.set(y.getIndex(firstColm), isString(firstValue)?firstValue:Integer.parseInt(firstValue));
                for (int i = 1; i < restColm.length; i++) {
                    ob.set(y.getIndex(restColm[i]), isString(restValues[i])?restValues[i]:Integer.parseInt(restValues[i]));
                }
            x.insertIntoTable(ob, dBName, tableName);
        } else {
            ob.add(firstValue);
            if (!(restValues.length == 1 && restValues[0].equals(""))){
            for(int i = 1; i < restValues.length; i++) {
                ob.add(isString(restValues[i])?restValues[i]:Integer.parseInt(restValues[i]));
            }
            }
            x.insertIntoTable(ob, dBName, tableName);
        }
    }

    public boolean isString(String z) {
        // TODO Auto-generated method stub
        try{
            int x = Integer.parseInt(z);
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    private boolean CheckInsertAll(String dBName, String tableName, String firstValue, String[] restValues) throws Exception {
        // TODO Auto-generated method stub
        TabelImp x = new xmlFactory(dBName).ReadXML(tableName);
        //Check Name
        if (x.getTable().size() == 0)
            return false;
        if (!(restValues.length == 1 && restValues[0].equals(""))) {
        if (x.getTable().size() != restValues.length)
            return false;
        //Check Type
        for(int i = 1; i < x.GetTableSize(); i++) {
            if (isString(restValues[i]) == x.getTable().get(i).getType())
                return false;
        }
        }
        if (isString(firstValue) == x.getTable().get(0).getType())
            return false;
        return true;
    }

    public String[] DivideCommaVal(String group) {
        // TODO Auto-generated method stub
        String x = group.replaceAll("'", "");
        return x.split(",");
    }

    public String[] DivideCommaColm(String group) {
        // TODO Auto-generated method stub
        return group.split("(\\s*),\\s*");
    }

    private ArrayList<ArrayList<String>> LastReturn (String DBName, String TableName) throws Exception {
        TabelImp Table = new xmlFactory(DBName).ReadXML(TableName);
        ArrayList<ArrayList<String>> x = new ArrayList<>();
        for (int i = 0; i < Table.GetTableSize(); i++) {
            ArrayList<String> y = new ArrayList<>();
            for(int j =0; j < Table.getFromTable(i).getColumn().size(); j++) {
                y.add((Table.getFromTable(i).getColumn().get(j) != null)?Table.getFromTable(i).getColumn().get(j).toString():null);
            }
        }
        return x;
    }
    public static void main(String[] args) throws Exception {
        Scanner s = new Scanner(System.in).useDelimiter(";");
        while(true) {
            try{
            String input = s.next();
            sqlFactory as = new sqlFactory(input.replaceAll("[\r\n]+", " ")+";");
            s.nextLine();
            } catch (Exception e) {
                System.out.println("Error :");
                System.out.println(e.getMessage());
            }
        }
    }
}
//insert into Finall.Ezz values ('Dodo');
//delete from Final.Test where Name='Ahmed';
//insert into Final.Test2 (Name, ID) values ('Ahmed','5');
//Select * from Final.Test where Name='Ahmede';
//update Final.Test2 set ID=9 where Name='MAr';