package tools;

import java.awt.geom.Point2D;

import creator2DTree.PrimitiveInstance2DConnection;
import creator2DTree.PrimitiveInstance2DTree;


/**
 * A match point contains a primitive instance and a connection pattern to it.
 * It also contains a distance for that connection.
 * It is used to find the best matching connection in the drag.
 * READY
 * @author bkievitk
 */

public class MatchPoint {
	
	// The object that we are attaching to and the chosen connection.
	public PrimitiveInstance2DConnection connection;
	
	// Distance to connection.
	public double distance;
	
	/**
	 * We need to know the parent and child attachment information.
	 * @param parent
	 * @param controlPoints
	 * @param childAttachmentPoint
	 * @param parentAttachmentPoint
	 */
	public MatchPoint(PrimitiveInstance2DTree parent, Point2D[][] controlPoints, short[] childAttachmentPoint, short[] parentAttachmentPoint) {
		connection = new PrimitiveInstance2DConnection(null,parent,childAttachmentPoint,parentAttachmentPoint);
		distance = distance(controlPoints);
	}
	
	/**
	 * Get the shortest distance given these control points.
	 * @param controlPoints Set of 3x3 points of the bounding rectangle.
	 * @return
	 */
	public double distance(Point2D[][] controlPoints) {
		Point2D p1 = controlPoints[connection.childAttachmentPoint[0]+1][connection.childAttachmentPoint[1]+1];
		Point2D p2 = connection.parent.controlPoints[connection.parentAttachmentPoint[0]+1][connection.parentAttachmentPoint[1]+1];
		double dx = p1.getX() - p2.getX();
		double dy = p1.getY() - p2.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}
}
