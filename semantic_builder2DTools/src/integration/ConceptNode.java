package integration;

import java.io.Serializable;

public class ConceptNode implements Serializable {
	private static final long serialVersionUID = 1100140393290857418L;
	public String concept;
	public double[][] knowledge;
	
	public ConceptNode(String concept, int knowledgeDomains) {
		knowledge = new double[knowledgeDomains][];
	}
	
}
