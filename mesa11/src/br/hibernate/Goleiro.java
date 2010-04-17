package br.hibernate;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import org.hibernate.loader.custom.Return;

public class Goleiro extends Botao {

	private transient int diamentro = 400;
	private double rotacao;

	public int getDiamentro() {
		return diamentro;
	}

	public void setDiamentro(int diamentro) {
		this.diamentro = diamentro;
	}

	public double getRotacao() {
		return rotacao;
	}

	public void setRotacao(double rotacao) {
		this.rotacao = rotacao;
	}

	public Goleiro(int i) {
		super(i);
	}

	public Shape getRetangulo() {
		Rectangle2D r2D = new Rectangle2D.Double(getPosition().x,
				getPosition().y, getDiamentro(), 80);
		GeneralPath generalPath = new GeneralPath(r2D);

		AffineTransform affineTransform = AffineTransform
				.getScaleInstance(1, 1);
		double rad = Math.toRadians((double) rotacao);
		affineTransform.setToRotation(rad, r2D.getCenterX(), r2D.getCenterY());

		return generalPath.createTransformedShape(affineTransform);
	}

	public Point getCentro() {
		return new Point(getPosition().x + (getDiamentro() / 2),
				getPosition().y + 40);
	}

	public void setCentro(Point p) {
		setPosition(new Point(p.x - (getDiamentro() / 2), p.y - 40));
	}
}
