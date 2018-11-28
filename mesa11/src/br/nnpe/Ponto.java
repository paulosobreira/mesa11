package br.nnpe;

import java.awt.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ponto {
	@JsonIgnore
	private Point point;

	public Ponto(Point point) {
		super();
		this.point = point;
	}

	public Ponto(int x, int y) {
		this.point = new Point(x, y);
	}

	public Ponto(double x, double y) {
		this.point = new Point((int) x, (int) y);
	}

	public double getX() {
		return point.getX();
	}

	public double getY() {
		return point.getY();
	}

	public void setPoint(Point point) {
		this.point = point;
	}
	
	@JsonIgnore
	public Point getPoint() {
		return point;
	}

}
