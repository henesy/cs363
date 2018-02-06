package cyutils.datagenerator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.StringJoiner;

import cyutils.btree.Tuple;
import cyutils.btree.TupleAttribute;

public class BTreePrepareSortedData {
	
	private static final int DATA_SIZE = 10000;
	
	public static void prepare(String tupleConfigXmlFile, String tupleTxtFile) throws FileNotFoundException {
		Tuple tuple = new Tuple(tupleConfigXmlFile);
		List<TupleAttribute> attributes = tuple.getTupleAttributes();
		PrintWriter writer = new PrintWriter(tupleTxtFile);
		
		for (int ii = 0; ii < DATA_SIZE; ii++) {
			StringJoiner joiner = new StringJoiner(",");
			for (TupleAttribute attribute : attributes) {
				if (attribute.getType().equals("Integer")) {
					joiner.add(String.valueOf(increasingInt(ii)));
				} else if (attribute.getType().equals("String")) {
					joiner.add(increasingString(ii));
				}
			}
			String joined = "[" + joiner.toString() + "]";
			writer.println(joined);
		}
		writer.close();
	}
	
	private static int increasingInt(int i) {
		return i;
	}
	
	private static String increasingString(int i) {
		return String.format("%05d", i);
	}
}
