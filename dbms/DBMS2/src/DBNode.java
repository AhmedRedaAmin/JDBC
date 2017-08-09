import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBNode {
    private String name ;
    private boolean type;
    private LinkedList<Object> column;

    public DBNode(final String name,final boolean type) {
        // TODO Auto-generated constructor stub
        this.name=name;
        this.type=type;
        column = new LinkedList<Object>();
    }

  public void setColumn(final LinkedList<Object> column){
        this.column = column;
    }

  public String getName (){
    return name;
  }

  public boolean getType (){
        return type;
      }

  public LinkedList<Object> getColumn (){
        return column;
      }
}