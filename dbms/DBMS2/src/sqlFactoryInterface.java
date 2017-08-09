import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface sqlFactoryInterface {
	/*
	 * functions to modify the input string
	 */
	public String[] DivideCommaVal(String group);

	public String[] DivideCommaColm(String group);

	public String[] SplitUpdate(String group);

	public boolean isString(String z);

	/*
	 * this function parses distributes the input string into the query
	 * functions
	 */
	public void Distributor() throws Exception;

	/*
	 * this function does the parsing of select query
	 */
	public ArrayList<ArrayList<String>> SelectMachine() throws Exception;

	/*
	 * this function does the parsing of create table query using regular
	 * expression
	 */
	public ArrayList<ArrayList<String>> CreateTableMachine() throws Exception;

	/*
	 * this function does the parsing of create database query using regular
	 * expression
	 */
	public  ArrayList<ArrayList<String>> CreateBaseMachine();

	/*
	 * this function does the parsing of drop table query using regular
	 * expression
	 */
	public  ArrayList<ArrayList<String>> DropTableMachine();

	/*
	 * this function does the parsing of drop database query using regular
	 * expression
	 */
	public  ArrayList<ArrayList<String>> DropDBMachine();

	/*
	 * this function does the parsing of delete query using regular expression
	 */
	public  ArrayList<ArrayList<String>> DeleteMachine() throws Exception;

	/*
	 * this function does the parsing of update table query using regular
	 * expression
	 */
	public  ArrayList<ArrayList<String>> UpdateMachine() throws Exception;

	/*
	 * this function does the parsing of insert into query using regular
	 * expression
	 */
	public  ArrayList<ArrayList<String>> InsertMachine() throws Exception;

}
