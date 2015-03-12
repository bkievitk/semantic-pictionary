package tools;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Hashtable;


public class SemanticSpaceDiskReader {
	
	public Hashtable<String,Integer> words = new Hashtable<String,Integer>();
	private RandomAccessFile raf;
	private int lineLength;
	
	public double[] getVector(String s) {
		Integer i = words.get(s);
		if(i == null) {
			return null;
		} else {
			return getVector(i);
		}
	}
	
	public double[] getVector(int i) {
		if(i < 0 || i >= words.size()) {
			return null;
		}
		
		try {
			raf.seek((long)lineLength * i);
			
			byte[] buffer = new byte[(int)lineLength];
			raf.read(buffer);
			
			String str = new String(buffer);
			String[] valuesS = str.trim().split(" +");
			double[] valuesD = new double[valuesS.length];
			for(int j=0;j<valuesS.length;j++) {
				valuesD[j] = Double.parseDouble(valuesS[j]);
			}
			return valuesD;
			
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public SemanticSpaceDiskReader(File labels, File matrix, int lineLength) {
		this.lineLength = lineLength;
		
		try {
			// Read labels.
			BufferedReader r = new BufferedReader(new FileReader(new File("word_labels.txt")));
			int count = Integer.parseInt(r.readLine().trim());
			for(int i=0;i<count;i++) {
				words.put(r.readLine().trim(), i);
			}
			r.close();
			raf = new RandomAccessFile(new File("matrix.txt"), "r"); 
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SemanticSpaceDiskReader reader = new SemanticSpaceDiskReader(new File("word_labels.txt"),new File("matrix.txt"),14002);

		double[] grass = reader.getVector("grass");
		double[] uncovered = reader.getVector("uncovered");
		double[] grassUncovered = VectorTools.getAdd(
				VectorTools.normalize(reader.getVector("grass")),
				VectorTools.normalize(reader.getVector("uncovered")));

		System.out.println("green");
		System.out.println(VectorTools.getCosine(grass, reader.getVector("green")));
		System.out.println(VectorTools.getCosine(uncovered, reader.getVector("green")));
		System.out.println(VectorTools.getCosine(grassUncovered, reader.getVector("green")));
		System.out.println();
		
		System.out.println("blades");
		System.out.println(VectorTools.getCosine(grass, reader.getVector("blades")));
		System.out.println(VectorTools.getCosine(uncovered, reader.getVector("blades")));
		System.out.println(VectorTools.getCosine(grassUncovered, reader.getVector("blades")));
		System.out.println();

		System.out.println("root");
		System.out.println(VectorTools.getCosine(grass, reader.getVector("root")));
		System.out.println(VectorTools.getCosine(uncovered, reader.getVector("root")));
		System.out.println(VectorTools.getCosine(grassUncovered, reader.getVector("root")));
		System.out.println();

		System.out.println("dirt");
		System.out.println(VectorTools.getCosine(grass, reader.getVector("dirt")));
		System.out.println(VectorTools.getCosine(uncovered, reader.getVector("dirt")));
		System.out.println(VectorTools.getCosine(grassUncovered, reader.getVector("dirt")));
		System.out.println();

		
		/*
		VectorTools.show(reader.getVector("uncovered"));
		VectorTools.show(reader.getVector("rolled"));
		VectorTools.show(reader.getVector("removed"));
		
		VectorTools.show(reader.getVector("root"));
		VectorTools.show(reader.getVector("dirt"));
		
		VectorTools.show(reader.getVector("green"));
		VectorTools.show(reader.getVector("blades"));
		*/
	}
}
