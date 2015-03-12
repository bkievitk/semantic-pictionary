package my3D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class Object3D {
	
	// Distance from the user to the screen.
	// This is used for perspective warp.
	public static final double SCREEN_DISTANCE = -50.0;
	
	// Axis to normalize to.
	private static final double[][] AXIS = {{0,0,1},{1,0,0},{1,1,1}};
	
	// Transform for this object.
	public TransformMy3D transform = new TransformMy3D();
	
	// Children objects take this transform and then their own.
	public Vector<Object3D> children = new Vector<Object3D>();
	
	// The points defining each triangle.
	protected Vector<int[]> triangles = new Vector<int[]>();
	protected Vector<Material> triangleMaterial = new Vector<Material>();
	
	// Points and their norms.
	protected Vector<double[]> points = new Vector<double[]>();
	protected Vector<double[]> ptNormals = new Vector<double[]>();

	// Define light.
	private double[] distLight_World = {.5,-.4,.5};
	//private double[] distLight_World = {1,0,1};
	private double[] distLight = new double[3];
	
	public boolean highlight = false;
	
	/**
	 * Apply perspective transform to all of these points.
	 * This also centers the points in the window frame and scales them appropriately.
	 * @param points
	 * @param width
	 * @param height
	 * @return
	 */
	public Vector<double[]> applyPerspective(Vector<double[]> points, int width, int height) {
		Vector<double[]> newPoints = new Vector<double[]>();
		for(double[] point : points) {
			newPoints.add(applyPerspective(point,width,height));
		}
		return newPoints;
	}
	
	public double[] applyPerspective(double[] point, int width, int height) {
		double x = point[0];
		double y = point[1];
		double z = point[2];
		
		double multiplier = SCREEN_DISTANCE / (z + SCREEN_DISTANCE);
		double dx = multiplier * x;
		double dy = multiplier * y;
		
		double[] newPoint = new double[3];
		newPoint[0] = dx * width / 80 + width / 2;
		newPoint[1] = dy * width / 80 + height / 2;
		newPoint[2] = z * width / 80;
		
		return newPoint;
	}
	
	public void drawZPoint(double[][] zBuffer, int x, int y, double z) {
		if(x >= 0 && y >= 0 && x < zBuffer.length && y <zBuffer[0].length) {
			zBuffer[x][y] = z;
		}
	}
	
	/**
	 * http://www.cs.unc.edu/~mcmillan/comp136/Lecture6/Lines.html
	 * @param zBuffer
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 * @param z
	 */
	public void drawZLine(double[][] zBuffer, int x0, int y0, int x1, int y1, double z) {
	
        int dy = y1 - y0;
        int dx = x1 - x0;
        int stepx, stepy;

        if (dy < 0) { dy = -dy;  stepy = -1; } else { stepy = 1; }
        if (dx < 0) { dx = -dx;  stepx = -1; } else { stepx = 1; }

        drawZPoint(zBuffer, x0, y0, z);
        drawZPoint(zBuffer, x1, y1, z);
        
        if (dx > dy) {
            int length = (dx - 1) >> 2;
            int extras = (dx - 1) & 3;
            int incr2 = (dy << 2) - (dx << 1);
            if (incr2 < 0) {
                int c = dy << 1;
                int incr1 = c << 1;
                int d =  incr1 - dx;
                for (int i = 0; i < length; i++) {
                    x0 += stepx;
                    x1 -= stepx;
                    if (d < 0) {						// Pattern:
                    	drawZPoint(zBuffer, x0, y0, z);			//
                    	drawZPoint(zBuffer, x0 += stepx, y0, z);	//  x o o
                    	drawZPoint(zBuffer, x1, y1, z);			//
                    	drawZPoint(zBuffer, x1 -= stepx, y1, z);
                        d += incr1;
                    } else {
                        if (d < c) {							// Pattern:
                        	drawZPoint(zBuffer, x0, y0, z);				//      o
                        	drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);		//  x o
                        	drawZPoint(zBuffer, x1, y1, z);				//
                        	drawZPoint(zBuffer, x1 -= stepx, y1 -= stepy, z);
                        } else {
                        	drawZPoint(zBuffer, x0, y0 += stepy, z);			// Pattern:
                        	drawZPoint(zBuffer, x0 += stepx, y0, z);			//    o o 
                        	drawZPoint(zBuffer, x1, y1 -= stepy, z);			//  x
                        	drawZPoint(zBuffer, x1 -= stepx, y1, z);			//
                        }
                        d += incr2;
                    }
                }
                if (extras > 0) {
                    if (d < 0) {
                    	drawZPoint(zBuffer, x0 += stepx, y0, z);
                        if (extras > 1) drawZPoint(zBuffer, x0 += stepx, y0, z);
                        if (extras > 2) drawZPoint(zBuffer, x1 -= stepx, y1, z);
                    } else
                    if (d < c) {
                    	drawZPoint(zBuffer, x0 += stepx, y0, z);
                        if (extras > 1) drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);
                        if (extras > 2) drawZPoint(zBuffer, x1 -= stepx, y1, z);
                    } else {
                    	drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);
                        if (extras > 1) drawZPoint(zBuffer, x0 += stepx, y0, z);
                        if (extras > 2) drawZPoint(zBuffer, x1 -= stepx, y1 -= stepy, z);
                    }
                }
            } else {
                int c = (dy - dx) << 1;
                int incr1 = c << 1;
                int d =  incr1 + dx;
                for (int i = 0; i < length; i++) {
                    x0 += stepx;
                    x1 -= stepx;
                    if (d > 0) {
                    	drawZPoint(zBuffer, x0, y0 += stepy, z);			// Pattern:
                    	drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);		//      o
                    	drawZPoint(zBuffer, x1, y1 -= stepy, z);			//    o
                    	drawZPoint(zBuffer, x1 -= stepx, y1 -= stepy, z);		//  x
                        d += incr1;
                    } else {
                        if (d < c) {
                        	drawZPoint(zBuffer, x0, y0, z);				// Pattern:
                        	drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);       //      o
                        	drawZPoint(zBuffer, x1, y1, z);                         //  x o
                        	drawZPoint(zBuffer, x1 -= stepx, y1 -= stepy, z);       //
                        } else {
                        	drawZPoint(zBuffer, x0, y0 += stepy, z);			// Pattern:
                        	drawZPoint(zBuffer, x0 += stepx, y0, z);			//    o o
                        	drawZPoint(zBuffer, x1, y1 -= stepy, z);			//  x
                        	drawZPoint(zBuffer, x1 -= stepx, y1, z);			//
                        }
                        d += incr2;
                    }
                }
                if (extras > 0) {
                    if (d > 0) {
                    	drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);
                        if (extras > 1) drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);
                        if (extras > 2) drawZPoint(zBuffer, x1 -= stepx, y1 -= stepy, z);
                    } else
                    if (d < c) {
                    	drawZPoint(zBuffer, x0 += stepx, y0, z);
                        if (extras > 1) drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);
                        if (extras > 2) drawZPoint(zBuffer, x1 -= stepx, y1, z);
                    } else {
                    	drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);
                        if (extras > 1) drawZPoint(zBuffer, x0 += stepx, y0, z);
                        if (extras > 2) {
                            if (d > c)
                            	drawZPoint(zBuffer, x1 -= stepx, y1 -= stepy, z);
                            else
                            	drawZPoint(zBuffer, x1 -= stepx, y1, z);
                        }
                    }
                }
            }
        } else {
            int length = (dy - 1) >> 2;
            int extras = (dy - 1) & 3;
            int incr2 = (dx << 2) - (dy << 1);
            if (incr2 < 0) {
                int c = dx << 1;
                int incr1 = c << 1;
                int d =  incr1 - dy;
                for (int i = 0; i < length; i++) {
                    y0 += stepy;
                    y1 -= stepy;
                    if (d < 0) {
                        drawZPoint(zBuffer, x0, y0, z);
                        drawZPoint(zBuffer, x0, y0 += stepy, z);
                        drawZPoint(zBuffer, x1, y1, z);
                        drawZPoint(zBuffer, x1, y1 -= stepy, z);
                        d += incr1;
                    } else {
                        if (d < c) {
                            drawZPoint(zBuffer, x0, y0, z);
                            drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);
                            drawZPoint(zBuffer, x1, y1, z);
                            drawZPoint(zBuffer, x1 -= stepx, y1 -= stepy, z);
                        } else {
                            drawZPoint(zBuffer, x0 += stepx, y0, z);
                            drawZPoint(zBuffer, x0, y0 += stepy, z);
                            drawZPoint(zBuffer, x1 -= stepx, y1, z);
                            drawZPoint(zBuffer, x1, y1 -= stepy, z);
                        }
                        d += incr2;
                    }
                }
                if (extras > 0) {
                    if (d < 0) {
                        drawZPoint(zBuffer, x0, y0 += stepy, z);
                        if (extras > 1) drawZPoint(zBuffer, x0, y0 += stepy, z);
                        if (extras > 2) drawZPoint(zBuffer, x1, y1 -= stepy, z);
                    } else
                    if (d < c) {
                        drawZPoint(zBuffer, stepx, y0 += stepy, z);
                        if (extras > 1) drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);
                        if (extras > 2) drawZPoint(zBuffer, x1, y1 -= stepy, z);
                    } else {
                        drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);
                        if (extras > 1) drawZPoint(zBuffer, x0, y0 += stepy, z);
                        if (extras > 2) drawZPoint(zBuffer, x1 -= stepx, y1 -= stepy, z);
                    }
                }
            } else {
                int c = (dx - dy) << 1;
                int incr1 = c << 1;
                int d =  incr1 + dy;
                for (int i = 0; i < length; i++) {
                    y0 += stepy;
                    y1 -= stepy;
                    if (d > 0) {
                        drawZPoint(zBuffer, x0 += stepx, y0, z);
                        drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);
                        drawZPoint(zBuffer, x1 -= stepy, y1, z);
                        drawZPoint(zBuffer, x1 -= stepx, y1 -= stepy, z);
                        d += incr1;
                    } else {
                        if (d < c) {
                            drawZPoint(zBuffer, x0, y0, z);
                            drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);
                            drawZPoint(zBuffer, x1, y1, z);
                            drawZPoint(zBuffer, x1 -= stepx, y1 -= stepy, z);
                        } else {
                            drawZPoint(zBuffer, x0 += stepx, y0, z);
                            drawZPoint(zBuffer, x0, y0 += stepy, z);
                            drawZPoint(zBuffer, x1 -= stepx, y1, z);
                            drawZPoint(zBuffer, x1, y1 -= stepy, z);
                        }
                        d += incr2;
                    }
                }
                if (extras > 0) {
                    if (d > 0) {
                        drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);
                        if (extras > 1) drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);
                        if (extras > 2) drawZPoint(zBuffer, x1 -= stepx, y1 -= stepy, z);
                    } else
                    if (d < c) {
                        drawZPoint(zBuffer, x0, y0 += stepy, z);
                        if (extras > 1) drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);
                        if (extras > 2) drawZPoint(zBuffer, x1, y1 -= stepy, z);
                    } else {
                        drawZPoint(zBuffer, x0 += stepx, y0 += stepy, z);
                        if (extras > 1) drawZPoint(zBuffer, x0, y0 += stepy, z);
                        if (extras > 2) {
                            if (d > c)
                                drawZPoint(zBuffer, x1 -= stepx, y1 -= stepy, z);
                            else
                                drawZPoint(zBuffer, x1, y1 -= stepy, z);
                        }
                    }
                }
            }
        }
    }

	
	public void render(BufferedImage img, double[][] zBuffer, Object[][] objBuffer, TransformMy3D transformOld, int renderType) {
		
		TransformMy3D transform = transformOld.combineNew(this.transform);
		
		Vector<double[]> transformed = transform.apply(points);
		Vector<double[]> transformedNormals = transform.applyNoShift(ptNormals);
		Vector<double[]> perspective = applyPerspective(transformed, img.getWidth(), img.getHeight());
		/*
		double[] root = {0,0,0};
		root = transform.apply(root);
		double[] light = distLight_World;
		light = transform.apply(light);

		distLight[0] = light[0] - root[0];
		distLight[1] = light[1] - root[1];
		distLight[2] = light[2] - root[2];
		*/
		distLight = distLight_World;
		
		distLight = normalize(distLight);
		
		//System.out.println();
		for(int t=0;t<triangles.size();t++) {

			double[] t1 = transformed.get(triangles.get(t)[0]);
			double[] t2 = transformed.get(triangles.get(t)[1]);
			double[] t3 = transformed.get(triangles.get(t)[2]);
						
			double[] p1 = perspective.get(triangles.get(t)[0]);
			double[] p2 = perspective.get(triangles.get(t)[1]);
			double[] p3 = perspective.get(triangles.get(t)[2]);
			
			if(t1[2] < -SCREEN_DISTANCE && t2[2] < -SCREEN_DISTANCE && t3[2] < -SCREEN_DISTANCE) {
				
				if(renderType == Universe.RENDER_WIREFRAME) {
					Graphics g = img.getGraphics();
					g.setColor(triangleMaterial.get(t).color);
					g.drawLine((int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1]);
					g.drawLine((int)p2[0], (int)p2[1], (int)p3[0], (int)p3[1]);
					g.drawLine((int)p3[0], (int)p3[1], (int)p1[0], (int)p1[1]);
					
					drawZLine(zBuffer, (int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1], Double.MAX_VALUE);
					drawZLine(zBuffer, (int)p2[0], (int)p2[1], (int)p3[0], (int)p3[1], Double.MAX_VALUE);
					drawZLine(zBuffer, (int)p3[0], (int)p3[1], (int)p1[0], (int)p1[1], Double.MAX_VALUE);
				} else {
					double[] normal = getNormal(t1, t2, t3);
					double theta = getTheta(normal);
					
					if(theta <= Math.PI / 2) {
					
						double[] n1 = transformedNormals.get(triangles.get(t)[0]);
						double[] n2 = transformedNormals.get(triangles.get(t)[1]);
						double[] n3 = transformedNormals.get(triangles.get(t)[2]);

						Material triMaterial = triangleMaterial.get(t);
						double[][] triTransformed = {t1,t2,t3};
						double[][] triTransformedNormals = {n1,n2,n3};
						double[][] triPerspective = {p1,p2,p3};
						double[] triNormal = normal;
						
						Triangle3D triangle = new Triangle3D(triMaterial, triTransformed, triTransformedNormals, triPerspective, triNormal);
						
						fasterFill(zBuffer, objBuffer, img, triangle, renderType);
					}
				}
			}
		}
		
		for(Object3D p : children) {
			p.render(img, zBuffer,objBuffer,transform,renderType);
		}		
	}
	
	public double[] avg(double[] p1, double[] p2, double[] p3) {
		double[] avg = {	(p1[0] + p2[0] + p3[0]) / 3,
							(p1[1] + p2[1] + p3[1]) / 3,
							(p1[2] + p2[2] + p3[2]) / 3};
		return avg;
	}
	
	public double[] getNormal(double[] p1, double[] p2, double[] p3) {
		
		double ax = p1[0] - p2[0];
		double ay = p1[1] - p2[1];
		double az = p1[2] - p2[2];
		
		double bx = p3[0] - p2[0];
		double by = p3[1] - p2[1];
		double bz = p3[2] - p2[2];
		
		// A is the cross product.					
		double[] normal = {	ay * bz - az * by,
							az * bx - ax * bz,
							ax * by - ay * bx};
		
		normal = normalize(normal);
		
		return normal;
	}
	
	public double getAngle(double[] pt1, double[] pt2) {
		double len1 = Math.sqrt(pt1[0] * pt1[0] + pt1[1] * pt1[1] + pt1[2] * pt1[2]);
		double len2 = Math.sqrt(pt2[0] * pt2[0] + pt2[1] * pt2[1] + pt2[2] * pt2[2]);
		double dot = pt2[0] * pt1[0] + pt2[1] * pt1[1] + pt2[2] * pt1[2];
		double angle = dot / (len1 * len2);
		return Math.acos(angle);
	}
	
	public double getColor(double[] normal, Material material) {
		double lAngleN = getAngle(normal,distLight);
		
		double LOnN = lAngleN;
		double LOnNx = normal[0] * LOnN;
		double LOnNy = normal[1] * LOnN;
		double LOnNz = normal[2] * LOnN;
	
		// Reflection.
		double Rx = LOnNx + LOnNx - distLight[0];
	 	double Ry = LOnNy + LOnNy - distLight[1];
	 	double Rz = LOnNz + LOnNz - distLight[2];
	 	
	 	double rDotV = -(Rx * 0 + Ry * 0 + Rz * 1);
	 	
		int s =  material.s;  // Is a shininess constant for this material, which decides how "evenly" light is reflected from a shiny spot.
		double ia = material.ia; // Ambiant intensity.
		double id = material.id;  //3 Diffusion intensity of light. 
		double is = material.is;  // Specular intensity of light. 		 			
	 	
		if(highlight) {
			ia *= 5;
		}

		//return ia + lAngleN * id + Math.pow(rDotV,s) * is;
		return ia + lAngleN * id + intPower(rDotV,s) * is;
	}
	
	public double intPower(double base, int power) {
		double sum = 1;
		for(int i=0;i<power;i++) {
			sum *= base;
		}
		return sum;
		
	}
	
	public double dotProduct(Point.Double p1, Point.Double p2) {
		return p1.x * p2.x + p1.y * p2.y;
	}
	
	public Point.Double sub(Point.Double p1, Point.Double p2) {
		return new Point.Double(p1.x-p2.x,p1.y-p2.y);
	}
	
	public double crossProduct(Point.Double p1, Point.Double p2) {
		return p1.x * p2.y - p1.y * p2.x;
	}
	
	public boolean sameSide(Point.Double p1, Point.Double p2, Point.Double a, Point.Double b) {
		double cp1 = crossProduct(sub(b,a), sub(p1,a));
		double cp2 = crossProduct(sub(b,a), sub(p2,a));
		return (cp1 * cp2 >= 0);
	}

	public boolean pointInTriangle(Point.Double p, Point.Double a, Point.Double b, Point.Double c) {
		/*
		if(crossProduct(sub(b,a), sub(p,a)) > 0) {
			if(crossProduct(sub(c,b), sub(p,b)) > 0) {
				if(crossProduct(sub(a,c), sub(p,c)) > 0) {
					return true;
				}
			}
		}
		return false;
		*/
		

		
		return sameSide(p,a, b,c) && sameSide(p,b, a,c) && sameSide(p,c, a,b);
	}

    
	public void fasterFill(double[][] zBuffer, Object[][] objBuffer, BufferedImage img, Triangle3D triangle3D, int renderType) {
		
		double[] p1 = triangle3D.perspective[0];
		double[] p2 = triangle3D.perspective[1];
		double[] p3 = triangle3D.perspective[2];
				
		double[] n1 = triangle3D.transformedNormals[0];
		double[] n2 = triangle3D.transformedNormals[1];
		double[] n3 = triangle3D.transformedNormals[2];
		
		int minX = (int)Math.floor(Math.min(Math.min(p1[0],p2[0]),p3[0]));
		int minY = (int)Math.floor(Math.min(Math.min(p1[1],p2[1]),p3[1]));
		int maxX = (int)Math.ceil(Math.max(Math.max(p1[0],p2[0]),p3[0]));
		int maxY = (int)Math.ceil(Math.max(Math.max(p1[1],p2[1]),p3[1]));
		
		minX = Math.max(minX, 0);
		minY = Math.max(minY, 0);
		maxX = Math.min(maxX, img.getWidth());
		maxY = Math.min(maxY, img.getHeight());
		
		if(maxX == minX || maxY == minY) {
			return;
		}
				
		// Calculate transform matrix.
		double[][] m3t = {{p1[0],p2[0],p3[0]},{p1[1],p2[1],p3[1]},{1,1,1}};
		double[][] m3ti = MatrixMath.invert(m3t);
		double[][] m2t = MatrixMath.multiply(m3ti,AXIS);
		double[][] m2 = MatrixMath.transpose(m2t);

		Color faceColor = triangle3D.material.color;
		
		// Default color for this face.
		double col = 0;
		col = getColor(triangle3D.normal, triangle3D.material);

		// Used for checking if in triangle.
		Point.Double pt1 = new Point.Double(p1[0],p1[1]);
		Point.Double pt2 = new Point.Double(p2[0],p2[1]);
		Point.Double pt3 = new Point.Double(p3[0],p3[1]);
		
		// For every point within.
		for(int y=minY;y<=maxY;y++) {
			for(int x=minX;x<=maxX;x++) {

				// In triangle and in screen.
				if(x >= 0 && y >= 0 && x < zBuffer.length && y < zBuffer[0].length && pointInTriangle(new Point.Double(x,y),pt1,pt2,pt3)) {
					
					// Calculate a,b and c from the transform matrix.
					double c = x * m2[0][0] + y * m2[1][0] + m2[2][0];
					double a = x * m2[0][1] + y * m2[1][1] + m2[2][1];
					double b = 1 - c - a;

					// Calculate z.
					double z = a * p1[2] + b * p2[2] + c * p3[2];

					// Closer than closest point.
					if(zBuffer[x][y] < z) {
						
						if(renderType == Universe.RENDER_DEAPTH) {

							// Color is a function of depth.
							int color = (int)(z/2);
							color = Math.min(Math.max(0, color),255);
							int rgb = new Color(color,color,color).getRGB(); 
							
							img.setRGB(x, y, rgb);
							objBuffer[x][y] = triangle3D.material.object;
							zBuffer[x][y] = z;
							
							
						} else {
							Color base;
							
							if(renderType == Universe.RENDER_FLAT) {
								
								// Use flat color.
								base = triangle3D.material.color;
							} else {
								
								// Get light from new normal.
								if(n1 != null && n2 != null && n3 != null) {
									
									double[] newNormal = {	-a * n1[0] - b * n2[0] - c * n3[0],
															-a * n1[1] - b * n2[1] - c * n3[1],
															-a * n1[2] - b * n2[2] - c * n3[2]};
									
									triangle3D.normal = normalize(newNormal);
								 	col = getColor(triangle3D.normal, triangle3D.material);
								}

								// Use face color or image color.
								base = triangle3D.material.getColor(a, c); 
							}
							
							int red = (int)(base.getRed() * col);
							int green = (int)(base.getGreen() * col);
							int blue = (int)(base.getBlue() * col);
							
							Color old = new Color(img.getRGB(x, y));
							int alpha = triangle3D.material.color.getAlpha();
							red = (red * alpha + old.getRed() * (255 - alpha)) / 255;
							green = (green * alpha + old.getGreen() * (255 - alpha)) / 255;
							blue = (blue * alpha + old.getBlue() * (255 - alpha)) / 255;
														
							red = Math.max(Math.min(red, 255),0);
							green = Math.max(Math.min(green, 255),0);
							blue = Math.max(Math.min(blue, 255),0);
							faceColor = new Color(red,green,blue);
													
							img.setRGB(x, y, faceColor.getRGB());
							objBuffer[x][y] = triangle3D.material.object;
							zBuffer[x][y] = z;
						}
					}
				}
			}
		}
	}
	

	public double getTheta(double ax, double ay, double az) {
		double adb = -az;
		double lenB = Math.sqrt(ax * ax + ay * ay + az * az);
		return Math.acos(adb/lenB);
	}
	
	public double getTheta(double[] p) {
		double adb = -p[2];
		double lenB = getLen(p);
		return Math.acos(adb/lenB);
	}
	
	public double getTheta(double[] p1, double[] p2) {
		double adb = p1[0] * p2[0] + p1[1] * p2[1] + p1[2] * p2[2];
		double len1 = Math.sqrt(p1[0] * p1[0] + p1[1] * p1[1] + p1[2] * p1[2]);
		double len2 = Math.sqrt(p2[0] * p2[0] + p2[1] * p2[1] + p2[2] * p2[2]);
		return Math.acos(adb/(len1*len2));
	}
	
	public double getDot(double[] p1, double[] p2) {
		return p1[0] * p2[0] + p1[1] * p2[1] + p1[2] * p2[2];
	}
	
	public double getLen(double[] p) {
		return Math.sqrt(p[0] * p[0] + p[1] * p[1] + p[2] * p[2]);
	}
	
	public double[] normalize(double[] p) {
		double len  = getLen(p);
		double[] norm = new double[3];
		norm[0] = p[0] / len;
		norm[1] = p[1] / len;
		norm[2] = p[2] / len;
		return norm;
	}
	
	public double[][] getABC(double[] p1, double[] p2, double p3[], double x, double y) {
		double[][] m = {{1,p1[0],p1[1]},{1,p2[0],p2[1]},{1,p3[0],p3[1]}};
		double[][] minv = MatrixMath.invert(m);
		double[][] m2 = {{1,x,y}};
		return MatrixMath.multiply(minv, m2);
	}
	
	
	
	public BufferedImage rotate(BufferedImage img) {
		BufferedImage ret = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_BGR);
		for(int x=0;x<img.getWidth();x++) {
			for(int y=0;y<img.getHeight();y++) {
				ret.setRGB(x, y, img.getRGB(img.getWidth()-x-1, img.getHeight()-y-1));
			}
		}
		return ret;
	}
	
	public BufferedImage flip(BufferedImage img) {
		BufferedImage ret = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_BGR);
		for(int x=0;x<img.getWidth();x++) {
			for(int y=0;y<img.getHeight();y++) {
				ret.setRGB(x, y, img.getRGB(img.getWidth()-x-1, y));
			}
		}
		return ret;
	}
	
	
	
	
	
	
	
	
		
	public void addTriangle(int start, int a, int b, int c, Material material) {
		int[] newT = new int[3];
		newT[0] = a + start;
		newT[1] = b + start;
		newT[2] = c + start;
		triangles.add(newT);
		triangleMaterial.add(material);
	}
}
