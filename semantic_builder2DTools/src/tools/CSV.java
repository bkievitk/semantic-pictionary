package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public class CSV {
	public BufferedReader r;
	public Vector<String[]> lines = new Vector<String[]>();
	public String splitExpression = ",";
	
	public void applyLog(int row) {
		for(String[] line : lines) {
			try {
				double val = Double.parseDouble(line[row]);
				line[row] = Math.log(val) + "";
			} catch(NumberFormatException e) {
			}
		}
	}
	
	public Vector<double[]> extractRow(int[] rows) {
		Vector<double[]> values = new Vector<double[]>();
		
		for(String[] line : lines) {
			try {
				double[] lineVal = new double[rows.length];
				for(int i=0;i<rows.length;i++) {
					lineVal[i] = Double.parseDouble(line[rows[i]]);
				}
				values.add(lineVal);
			} catch(NumberFormatException e) {
			}
		}
		
		return values;
	}
	
	/*public void removeTopN(int row, int n) {
		KBox<Integer> topN = new KBox<Integer>(n, true);
		
		int i = 0;
		for(String[] line : lines) {
			try {
				double val = Double.parseDouble(line[row]);
				topN.add(new WeightedObject<Integer>(i, val));
			} catch(NumberFormatException e) {
			}
			i++;
		}
		
		Integer[] indexes = topN.getRankObjects();
		Arrays.sort(indexes);
		
		for(int j=indexes.length-1;j>=0;j--) {
			lines.remove(indexes[j]);
		}
		
	}*/
	
	public void normalizeRow(int row) {
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		
		for(String[] line : lines) {
			try {
				double val = Double.parseDouble(line[row]);
				min = Math.min(min, val);
				max = Math.max(max, val);
			} catch(NumberFormatException e) {
			}
		}
		
		for(String[] line : lines) {
			try {
				double val = Double.parseDouble(line[row]);
				val = (val - min) / (max - min);
				line[row] = val + "";
			} catch(NumberFormatException e) {
			}
		}
	}
	
	public void dump() {
		for(String[] s : lines) {
			System.out.println(join(s, splitExpression));
		}
	}
	
	public void dump(Vector<Integer> goodLines) {
		for(int i : goodLines) {
			String[] s = lines.get(i);
			System.out.println(join(s, splitExpression));
		}
	}
	
	public boolean learned(int lineNum, int start, int stop) {
		String[] line = lines.get(lineNum);
		int timesMissed = 0;
		int lastValue = Integer.parseInt(line[start].trim());
		for(int i=start+1;i<stop;i++) {
			int value = Integer.parseInt(line[i].trim());
			if(value < lastValue) {
				timesMissed++;
			}
			lastValue = value;
		}
		return timesMissed < 3;
	}
	
	public void replaceNumber(int lineNum) {
		String[] line = lines.get(lineNum);
		int num = Integer.parseInt(line[0].trim());
		int condition = (num-1) % 8;
		
		int say 		= 1 - ((condition >> 0) & 1);
		int personality = 1 - ((condition >> 1) & 1);
		int color 		= 1 - ((condition >> 2) & 1);
			
		line[0] = num + "," + color +"," +personality+","+say;
	}
	
	public void combineBlocks(int size, int start) {
		
		String[] line = lines.get(0);
		String[] newLine = new String[(line.length - start) / size + start];
		
		for(int i=0;i<start;i++) {
			newLine[i] = line[i];
		}
		for(int i=start;i<newLine.length;i++) {
			newLine[i] = (i-start+1) * size + "";
		}

		lines.set(0, newLine);
		
		for(int i=1;i<lines.size();i++) {
			combineBlocks(i,size,1);
		}
	}
	
	public void combineBlocks(int lineNum, int size, int start) {
		String[] line = lines.get(lineNum);
		
		String[] newLine = new String[(line.length - start) / size + start];
		
		for(int i=0;i<start;i++) {
			newLine[i] = line[i];
		}
		
		int sum = 0;
		for(int i=start;i<line.length;i++) {
			sum += Integer.parseInt(line[i].trim());
			if((i-start) % size == size - 1) {
				newLine[(i-start)/size + start] = sum + "";
				sum = 0;
			}
		}
		lines.set(lineNum, newLine);
	}
	
	/**
	 * Initialize with the file that you are linking to.
	 * @param f
	 */
	public CSV(File f) {
		try {
			r = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public CSV() {
	}
	
	public void removeLine(int index) {
		lines.remove(index);
	}
	
	public void removeLine(int start, int step) {
		for(int i=start;i<lines.size();i+= (step - 1)) {
			lines.remove(i);
		}
	}
	
	public void removeLineSize(int start, int size) {
		for(int i=start;i<lines.size();i++) {
			if(lines.get(i).length < size) {
				System.out.println("Removing line " + i + " " + lines.get(i).length);
				lines.remove(i);
				i--;
			}
		}
	}
	
	public static String join(Object[] words, String spacer) {
		String s = "";
		for(int i=0;i<words.length;i++) {
			s = s + '"' + words[i] + '"';
			if(i < words.length-1) {
				s = s + spacer;
			}
		}
		return s;
	}
	
	public void write(File f) {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			for(String[] str : lines) {
				w.write(join(str,splitExpression) + "\n");
			}
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(File f, Vector<Integer> goodLines) {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			for(int i : goodLines) {
				w.write(join(lines.get(i),splitExpression) + "\n");
			}
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieve a line from the file.
	 * Do not save to memory.
	 * @return
	 */
	public String[] getLine() {
		try {

			Vector<String> tmp = new Vector<String>();
			
			int mark = 0;
			int count = 0;
			String line = "";
			int i = 0;
			
			do {

				String tmpLine = r.readLine();
				if(tmpLine == null) {
					close();
					return null;
				}
				if(line.length() == 0) {
					line = line + tmpLine;
				} else {
					line = line + "\n" + tmpLine;
				}
				
				for(;i<line.length();i++) {
					if(line.charAt(i) == '"') {
						count = 1 - count;
					} else if(line.charAt(i) == ',' && count == 0) {
						String entry = line.substring(mark, i);				
						if(entry.length() > 0) {
							if(entry.charAt(0) == '"') {
								entry = entry.substring(1);
							} if(entry.charAt(entry.length() - 1) == '"') {
								entry = entry.substring(0,entry.length()-1);
							}
						}				
						tmp.add(entry);
						mark = i+1;
					}
				}
				
			} while(count != 0);
			
			String entry = line.substring(mark);				
			if(entry.length() > 0) {
				if(entry.charAt(0) == '"') {
					entry = entry.substring(1);
				} if(entry.charAt(entry.length() - 1) == '"') {
					entry = entry.substring(0,entry.length()-1);
				}
			}				
			tmp.add(entry);
						
			return tmp.toArray(new String[0]);
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	/*
	public static String[] parseSVNLine(String line) {
		
		Vector<String> tmp = new Vector<String>();
		
		int mark = 0;
		int count = 0;
		for(int i=0;i<line.length();i++) {
			if(line.charAt(i) == '"') {
				count = 1 - count;
			} else if(line.charAt(i) == ',' && count == 0) {
				String entry = line.substring(mark, i);				
				if(entry.length() > 0) {
					if(entry.charAt(0) == '"') {
						entry = entry.substring(1);
					} if(entry.charAt(entry.length() - 1) == '"') {
						entry = entry.substring(0,entry.length()-1);
					}
				}				
				tmp.add(entry);
				mark = i+1;
			}
		}
		
		String entry = line.substring(mark);				
		if(entry.length() > 0) {
			if(entry.charAt(0) == '"') {
				entry = entry.substring(1);
			} if(entry.charAt(entry.length() - 1) == '"') {
				entry = entry.substring(0,entry.length()-1);
			}
		}				
		tmp.add(entry);
		
		return tmp.toArray(new String[0]);
	}*/
	
	/**
	 * Retrieve a line from the file into buffer.
	 * @return False if done.
	 */
	public boolean readLine() {
		String[] line = getLine();
		if(line == null) {
			return false;
		}
		lines.add(line);
		return true;
	}
	
	/**
	 * Real all line in the file.
	 */
	public void readAll() {
		while(readLine());
		close();
	}
	
	/**
	 * Clean up.
	 */
	public void close() {
		try {
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public CSV joinOnRow(CSV other, int rowThis, int rowThat, boolean removeSingletons) {
		CSV ret = new CSV();
		
		System.out.println("Join on row " + other.lines.size() + " " + lines.size());
		
		
		Hashtable<String,String[]> key = new Hashtable<String,String[]>();
		for(String[] line : lines) {
			for(String element : line) {
				System.out.print("[" + element + "]");
			}
			System.out.println();
			
			key.put(line[rowThis], line);
		}
				
		for(String[] line : other.lines) {
			String[] otherRow = key.remove(line[rowThat]);
			if(otherRow != null) {
				ret.lines.add(jointRow(line,otherRow,rowThis,rowThat));
			}
		}
		
		return ret;
	}
	
	public static String[] jointRow(String[] lineThis, String[] lineThat, int rowThis, int rowThat) {
		String[] ret = new String[lineThis.length + lineThat.length - 1];
		for(int i=0;i<lineThis.length;i++) {
			if(i < rowThis) {
				ret[i] = lineThis[i];
			} else if(i > rowThis) {
				ret[i-1] = lineThis[i];
			}
		}		
		for(int i=0;i<lineThat.length;i++) {
			if(i < rowThat) {
				ret[i + lineThis.length - 1] = lineThat[i];
			} else if(i > rowThat) {
				ret[i-1 + lineThis.length - 1] = lineThat[i];
			}
		}
		ret[ret.length-1] = lineThis[rowThis];
		
		return ret;
	}
}
