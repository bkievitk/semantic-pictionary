package animals.functional;

import animals.Animal;
import creator2DTree.PrimitiveInstance2DTree;

public class FunctionalTelescope extends FunctionalUnit {

	public FunctionalTelescope(PrimitiveInstance2DTree boundModule, Animal animal) {
		super(boundModule,animal);
	}

	transient public boolean movingOut = true;
	
	public void tick() {				
		if(boundModule != null) {
			if(movingOut) {
				boundModule.scale[0] += 1;
				if(boundModule.scale[0] > 300) {
					boundModule.scale[0] = 300;
					movingOut = !movingOut;
				}
			} else {
				boundModule.scale[0] -= 1;
				if(boundModule.scale[0] < 0) {
					boundModule.scale[0] = 0;
					movingOut = !movingOut;
				}
			}
		}
	}
	
	public String toString() {
		return "Telescope: Telescopes automatically.";
	}
	
	public FunctionalTelescope clone() {
		return new FunctionalTelescope(null,null);
	}

	public String encode() {
		return "e";
	}	

	public FunctionalUnit decode(String str) {
		if(str.startsWith("e")) {
			return new FunctionalTelescope(null,null);
		}
		return null;
	}

}
