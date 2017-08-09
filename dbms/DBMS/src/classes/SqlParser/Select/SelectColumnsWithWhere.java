package classes.SqlParser.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.SQLException;
import java.util.regex.Pattern;

import classes.DBNode;
import classes.FileSystemFactory;
import classes.TabelImp;
import classes.XMLParser;
import interfaces.WriterInterface;

public class SelectColumnsWithWhere extends Select {

    String tableName;
    ArrayList<ArrayList<String>> x = new ArrayList<>();

    public SelectColumnsWithWhere(String input) {
        super(input);
    }

    @Override
    public ArrayList<ArrayList<String>> select() throws Exception {
        pattern = Pattern.compile(
                Messages.getString("SelectColumnsWithWhere.0")); //$NON-NLS-1$
        matcher = pattern.matcher(input);
        ArrayList<ArrayList<ArrayList<String>>> resu = new ArrayList<>();
        while (matcher.find()) {
            String columns = matcher.group(1);
            String[] colms = divideComma(columns);
            tableName = matcher.group(4);
            String colName = matcher.group(6);
            String condition = matcher.group(7);
            String rowName = (matcher.group(10) != null) ? matcher.group(10) : matcher.group(8);
            ArrayList<Integer> indexies = getIndexiesForWhere(tableName, colName, condition, rowName);
            TabelImp table = parser.read(tableName);
            for (String visit : colms) {
                if (table.isIn(visit)) {
                    DBNode elements = table.getFromTable(table.getIndex(visit));
                    if (condition.equals(Messages.getString("SelectColumnsWithWhere.1")) || condition.equals(Messages.getString("SelectColumnsWithWhere.2")) || condition.equals(Messages.getString("SelectColumnsWithWhere.3"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ArrayList<String> y = new ArrayList<>();
                        for (int i = 0; i < indexies.size(); i++) {
                            if (elements.getColumn().get(indexies.get(i)) instanceof Date) {
                                y.add(new SimpleDateFormat(Messages.getString("SelectColumnsWithWhere.4")) //$NON-NLS-1$
                                        .format(elements.getColumn().get(indexies.get(i))));
                            } else {
                                y.add((elements.getColumn().get(indexies.get(i)) != null)
                                        ? elements.getColumn().get(indexies.get(i)).toString() : Messages.getString("SelectColumnsWithWhere.5")); //$NON-NLS-1$
                            }
                        }
                        x.add(y);
                    } else {
                        throw new RuntimeException(Messages.getString("SelectColumnsWithWhere.6")); //$NON-NLS-1$
                    }
                } else {
                    throw new RuntimeException(Messages.getString("SelectColumnsWithWhere.7") + visit + Messages.getString("SelectColumnsWithWhere.8")); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            drawTable(colms, x);
            setColumms(colms);
            if (matcher.group(11) != null) {
                // there is order by
                String[] orderColumn = divideComma(matcher.group(12));
                String retColum[] = new String[orderColumn.length];
                String rettype[] = new String[orderColumn.length];
                for (int i = 0; i < orderColumn.length; i++) {
                    String[] test = dividespace(orderColumn[i]);
                    retColum[i] = test[0];
                    rettype[i] = test[1];
                }
                resu.add(orderBy(x, retColum, rettype));
                continue;
            }
            resu.add(x);
        }
        if (resu.size() == 2) {
            if (resu.get(0).size() == resu.get(1).size() && resu.get(0).get(0).size() != 0
                    && resu.get(1).get(0).size() != 0) {
                for (int i = 0; i < resu.get(0).size(); i++) {
                    if (convertToType(resu.get(0).get(i).get(0)) instanceof String
                            && convertToType(resu.get(1).get(i).get(0)) instanceof String) {

                    } else if (convertToType(resu.get(0).get(i).get(0)) instanceof Integer
                            && convertToType(resu.get(1).get(i).get(0)) instanceof Integer) {

                    } else if (convertToType(resu.get(0).get(i).get(0)) instanceof Float
                            && convertToType(resu.get(1).get(i).get(0)) instanceof Float) {

                    } else if (convertToType(resu.get(0).get(i).get(0)) instanceof Date
                            && convertToType(resu.get(1).get(i).get(0)) instanceof Date) {

                    } else {
                        throw new SQLException();
                    }
                }
                ArrayList<ArrayList<String>> conca = new ArrayList<>();
                conca.addAll(resu.get(0));
                for (int i = 0; i < resu.get(1).size(); i++) {
                    for (int j = 0; j < resu.get(1).get(0).size(); j++) {
                        conca.get(i).add(resu.get(1).get(i).get(j));
                    }
                }
                return ellimenate(conca);
            }
        } else if (resu.size() == 1) {
            return resu.get(0);
        } else {
            throw new RuntimeException();
        }
        throw new RuntimeException();
    }

    public ArrayList<ArrayList<String>> getSelected() {
        return x;
    }

}
