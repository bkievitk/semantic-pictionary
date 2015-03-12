package animals.functional;

import animals.Animal;

import creator2DTree.PrimitiveInstance2DTree;

public class FunctionalManager {
	
	public static FunctionalUnit decode(String str, PrimitiveInstance2DTree module, Animal animal) {
		switch(str.charAt(0)) {
			case 's': return new FunctionalUnitStinger(module,animal);
			case 't': return new FunctionalUnitTwirl(module,animal);
			case 'e': return new FunctionalTelescope(module,animal);
		}
		return null;
	}
}
