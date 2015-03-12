package animals.functional;

import animals.Animal;
import creator2DTree.PrimitiveInstance2DTree;

public abstract class FunctionalUnit {	

	protected PrimitiveInstance2DTree boundModule;
	protected Animal animal;
	
	public FunctionalUnit(PrimitiveInstance2DTree boundModule, Animal animal) {
		rebindModule(boundModule,animal);
	}
	
	public void rebindModule(PrimitiveInstance2DTree boundModule, Animal animal) {
		this.boundModule = boundModule;
		this.animal = animal;
	}
	
	public abstract void tick();
	public abstract FunctionalUnit clone();
	public abstract String encode();
	public abstract FunctionalUnit decode(String str);
		
}
