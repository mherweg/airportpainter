package airportpainter;

/**
 * A layout node is used to describe a point on a taxiway or other airport feature.
 * An Ordered list of nodes allows for the drawing of the feature.
 * @author trevor
 *
 */
public class LayoutNode {
	/**
	 * Type of node.
	 * 111 Node (plain)
	 * 112 Node with Bezier control point
	 * 113 Node (close loop), to close boundary
	 * 114 Node (close loop) with Bezier control point
	 * 115 Node (end) to terminate a line
	 * 116 Node (end) with Bezier control point
	 */
	protected String _type;
	/**
	 * Latitude of the point
	 */
	protected double _lat;
	/**
	 * Longitude of the point
	 */
	protected double _long;
	/**
	 */
	protected double _bezierLat;
	/**
	 * Longitude of the bezier control point (types 112, 114, 116)
	 */
	protected double _bezierLong;
	
	public LayoutNode(String type, double latitude, double longitude, double bezierLat, double bezierLong) {
		super();
		_type = type;
		_lat = latitude;
		_long = longitude;
		_bezierLat = bezierLat;
		_bezierLong = bezierLong;
	}
	
	public LayoutNode(String type, double latitude, double longitude) {
		super();
		_type = type;
		_lat = latitude;
		_long = longitude;
	}

	public String getType() {
		return _type;
	}

	public void setType(String type) {
		_type = type;
	}

	public double getLat() {
		return _lat;
	}

	public void setLat(double lat) {
		_lat = lat;
	}

	public double getLong() {
		return _long;
	}

	public void setLong(double l) {
		_long = l;
	}

	public double getBezierLat() {
		return _bezierLat;
	}

	public void setBezierLat(double bezierLat) {
		_bezierLat = bezierLat;
	}

	public double getBezierLong() {
		return _bezierLong;
	}

	public void setBezierLong(double bezierLong) {
		_bezierLong = bezierLong;
	}

}

