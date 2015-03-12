package animals.functional;

import animals.Animal;
import animals.PlayerPortal;
import creator2DTree.PrimitiveInstance2DTree;

public class FunctionalUnitStinger extends FunctionalUnit {

	public FunctionalUnitStinger(PrimitiveInstance2DTree boundModule, Animal animal) {
		super(boundModule, animal);
	}

	public void tick() {
		if(animal != null && animal.world != null && boundModule != null) {
			Animal touching = animal.world.touching(boundModule, animal);
			if(touching != null) {
				PlayerPortal portal = animal.world.portal;
				portal.messages.append("touching\r\n");
				
			}
		}
	}
	
	public String toString() {
		return "Stinger: Marginal damage to creature.";
	}
	
	public FunctionalUnitStinger clone() {
		return new FunctionalUnitStinger(null,null);
	}

	public String encode() {
		return "s";
	}
	
	public FunctionalUnit decode(String str) {
		if(str.startsWith("s")) {
			return new FunctionalUnitStinger(null,null);
		}
		return null;
	}
}
