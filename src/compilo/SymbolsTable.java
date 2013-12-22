package compilo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <b>Class holding the table of symbols.</b>
 * <p>
 * This class receives the different variables and labels from the
 * lexical analyzer, store them in arrays, sort those arrays and
 * output them if required.
 * </p>
 *  
 * @author Quentin Delhaye
 *
 */
public class SymbolsTable {
	/**
	 * <p>ArrayList containing the variables.</p>
	 * <p>The <em>i th</em> element is an identifier,
	 * the <em>i+1 th</em> is the corresponding image.
	 * </p>
	 */
	private List<String> variables;
	
	/**
	 * <p>ArrayList containing the labels.<p>
	 * <p>The <em>i th</em> element is an identifier,
	 * the <em>i+1 th</em> is the corresponding line number
	 * at which it occurred.
	 * </p>
	 */
	private List<String> labels;
	
	/**
	 * <p>Table of symbols</p>
	 * <p>Name, Initialization, Type (signed int, unsigned int), digits</p>
	 */
	private List<String[]> tos;
	
	/**
	 * <p>Constructor.</p>
	 * <p>The lists are simply created.</p>
	 */
	SymbolsTable() {
		this.variables = new ArrayList<String>();
		this.labels = new ArrayList<String>();
		this.tos = new ArrayList<String[]>();
	}
	
	/**
	 * <p>Add a new variable to the list.</p>
	 * <p>If the list does not already contains the identifier,
	 * the identifier and its corresponding image are appended
	 * to the list.
	 * </p>
	 * @param newVar
	 * 				Array of two elements:
	 * 				<ul>
	 * 					<li>[0]: identifier</li>
	 * 					<li>[1]: image</li>
	 * 				</ul>
	 * @see SymbolsTable#variables
	 */
	public void addVariable(String[] newVar) {
		if(!this.variables.contains(newVar[0])){
			this.variables.add(newVar[0]);
			this.variables.add(newVar[1]);
		}
	}
	
	/**
	 * <p>Add a new label to the list.</p>
	 * <p>The method first checks if the list already contains
	 * the identifier. In the case it does, it needs to check if
	 * it's not on the same line as given in parameter. It can
	 * then add it to the list.
	 * If the list does not already contain the identifier,
	 * the method directly append the parameter to the list
	 * without any further question.
	 * </p>
	 * @param newLab
	 * 				Array of two Strings:
	 * 				<ul>
	 * 					<li>[0]: identifier.</li>
	 * 					<li>[1]: line the identifier occurred on.</li>
	 * 				</ul>
	 * @see SymbolsTable#labels
	 */
	public void addLabel(String[] newLab) {
		System.out.println("symbolsTable.addLabel: line "+newLab[1]+ " with '"+newLab[0]+"'");
		if(this.labels.contains(newLab[0])) {
			if(!this.labels.get(this.labels.lastIndexOf(newLab[0])+1).equals(newLab[1])) {
				this.labels.add(newLab[0]);
				this.labels.add(newLab[1]);
			}
		}
		else {
			this.labels.add(newLab[0]);
			this.labels.add(newLab[1]);
		}
	}
	
	/**
	 * <p>Production and formating of the output String.</p>
	 * 
	 * @return The String correctly formatted.
	 */
	public String toString() {
		sortList(this.variables);
		sortList(this.labels);
		String output = "variables\n";
		for(int i=0;i<this.variables.size();i+=2) {
			output += this.variables.get(i) + "\t" + this.variables.get(i+1) + "\n";
		}
		output += "\nlabels\n";
		for(int i=0;i<this.labels.size();i+=2) {
			output += this.labels.get(i) + "\t" + this.labels.get(i+1) + "\n";
		}
		output+="\n";
		output+="Name \t Initial value \t Type \t Digits\n";
		
		Iterator<String[]> it = this.tos.iterator();
		while(it.hasNext()) {
			String[] str = it.next();
		//for(String[] str : this.tos) {
			output+=str[0]+"\t"+str[1]+"\t"+str[2]+"\t"+str[3]+"\n";
		}
		return output;
	}

	/**
	 * <p>Sorting of the list.</p>
	 * <p>The algorithm used is the <b>bubble sort</b>.
	 * </p>
	 * @param list
	 * 				Can be either list, they are both similarly structured.
	 */
	private void sortList(List<String> list) {
		boolean ordered = false;
		int size = list.size();
		String temp[] = {"",""};
		while(!ordered) {
			ordered = true;
			for(int i=0;i<size-3;i+=2) {
				if(list.get(i).compareTo(list.get(i+2)) > 0) {
					temp[0] = list.get(i+2);
					temp[1] = list.get(i+3);
					list.set(i+2, list.get(i));
					list.set(i+3, list.get(i+1));
					list.set(i, temp[0]);
					list.set(i+1, temp[1]);
					ordered = false;
				}
			}
			size-=2;
		}
	}
	
	public void newEntry(String[] entry) {
		//Adding directly entry doesn't work, we would give the address of the array, not its values.
		String[] tmp = {"","","", ""};
		tmp[0] = entry[0];
		tmp[1] = entry[1];
		tmp[2] = entry[2];
		tmp[3] = entry[3];
		this.tos.add(tmp);
	}
}
