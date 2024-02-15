package br.hibernate;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import br.nnpe.Ponto;

@Entity
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Goleiro extends Botao {

	private double rotacao;

	public int getDiamentro() {
		return 400;
	}

	public double getRotacao() {
		return rotacao;
	}

	public void setRotacao(double rotacao) {
		this.rotacao = rotacao;
	}

	public Goleiro() {
	}

	public Goleiro(int i) {
		super(i);
	}
	@JsonIgnore
	public Shape getShape(double zoom) {
		Rectangle2D r2D = new Rectangle2D.Double(getPosition().x * zoom,
				getPosition().y * zoom, getDiamentro() * zoom, 60 * zoom);
		GeneralPath generalPath = new GeneralPath(r2D);

		AffineTransform affineTransform = AffineTransform.getScaleInstance(zoom,
				zoom);
		double rad = Math.toRadians((double) rotacao);
		affineTransform.setToRotation(rad, r2D.getCenterX(), r2D.getCenterY());
		return generalPath.createTransformedShape(affineTransform);
	}
	@JsonIgnore
	public Point getCentro() {
		if (getPosition() == null) {
			return new Point(0, 0);
		}
		return new Point(getPosition().x + (getDiamentro() / 2),
				getPosition().y + 30);
	}

	public Ponto getCentroJS() {
		return new Ponto(getCentro());
	}

	public void setCentro(Point p) {
		if (p == null) {
			return;
		}
		setPosition(new Point(p.x - (getDiamentro() / 2), p.y - 30));
	}
}
