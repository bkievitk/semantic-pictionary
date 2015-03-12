package shapes3D;

import my3D.Material;
import my3D.Object3D;

/**
 * Draw a cube.
 * @author bkievitk
 */

public class Cube3D extends Object3D {
	
	public Cube3D(double[] center, double radius, Material material) {
		double[] size = {radius, radius, radius};
		addBox(center,size,material);
	}

	public Cube3D(double[] center, double[] size, Material material) {
		addBox(center,size,material);
	}
		
	public void addBox(double[] center, double[] size, Material material) {
		int start = points.size();
		for(int x=0;x<=1;x++) {
			for(int y=0;y<=1;y++) {
				for(int z=0;z<=1;z++) {
					double[] newPoint = new double[3];
					newPoint[0] = center[0] + (x - .5) * size[0];
					newPoint[1] = center[1] + (y - .5) * size[1];
					newPoint[2] = center[2] + (z - .5) * size[2];
					points.add(newPoint);
					ptNormals.add(null);
				}
			}
		}
				
		addTriangle(start,0,6,4,material);		
		addTriangle(start,2,6,0,material);		
		addTriangle(start,1,5,7,material);
		addTriangle(start,1,7,3,material);		
		addTriangle(start,0,3,2,material);
		addTriangle(start,0,1,3,material);		
		addTriangle(start,6,7,4,material);
		addTriangle(start,4,7,5,material);		
		addTriangle(start,0,5,1,material);
		addTriangle(start,0,4,5,material);		
		addTriangle(start,2,3,7,material);
		addTriangle(start,2,7,6,material);
	}
}
