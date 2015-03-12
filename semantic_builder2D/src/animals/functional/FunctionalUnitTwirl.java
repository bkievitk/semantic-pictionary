package animals.functional;

import animals.Animal;
import creator2DTree.PrimitiveInstance2DTree;

public class FunctionalUnitTwirl extends FunctionalUnit {
	
	public FunctionalUnitTwirl(PrimitiveInstance2DTree boundModule, Animal animal) {
		super(boundModule, animal);
	}
	
	public void tick() {				
		if(boundModule != null) {
			boundModule.rotation[0] += 1;
			if(boundModule.rotation[0] > 628) {
				boundModule.rotation[0] = 0;
			}
		}
	}
	
	public String toString() {
		return "Twirl: Turns around automatically.";
	}
	
	public FunctionalUnitTwirl clone() {
		return new FunctionalUnitTwirl(null,null);
	}

	public String encode() {
		return "t";
	}
	

	public FunctionalUnit decode(String str) {
		if(str.startsWith("t")) {
			return new FunctionalUnitTwirl(null,null);
		}
		return null;
	}
}
