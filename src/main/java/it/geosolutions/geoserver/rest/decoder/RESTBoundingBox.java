package it.geosolutions.geoserver.rest.decoder;

import org.jdom.Element;

/**
 * Parse a Boundingbox of the following structure
 *
 * @author nmandery
 */
public class RESTBoundingBox {

	protected Element bboxElem;
	
	public RESTBoundingBox(Element bboxElem) {
		this.bboxElem = bboxElem;
	}
	
	public String getCRS() {
		return this.bboxElem.getChildText("crs");
	}
	
	protected double getEdge(String edge) {
		return Double.parseDouble(this.bboxElem.getChildText(edge));
	}

	public double getMinX() {
		return this.getEdge("minx");
	}
	
	public double getMaxX() {
		return this.getEdge("maxx");
	}

	public double getMinY() {
		return this.getEdge("miny");
	}

	public double getMaxY() {
		return this.getEdge("maxy");
	}

}
