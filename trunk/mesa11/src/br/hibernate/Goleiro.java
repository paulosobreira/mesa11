package br.hibernate;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.hibernate.loader.custom.Return;

public class Goleiro extends Botao {

	private transient int diamentro = 400;
	private int rotacao;

	public int getRotacao() {
		return rotacao;
	}

	public int getDiamentro() {
		return diamentro;
	}

	public void setDiamentro(int diamentro) {
		this.diamentro = diamentro;
	}

	public void setRotacao(int rotacao) {
		this.rotacao = rotacao;
	}

	public Goleiro(int i) {
		super(i);
	}

	public Shape getRetangulo() {
		return new Rectangle2D.Double(getPosition().x, getPosition().y,
				getDiamentro(), 80);
	}

	public Point getCentro() {
		return new Point(getPosition().x + (getDiamentro() / 2),
				getPosition().y + 40);
	}

	public void setCentro(Point p) {
		setPosition(new Point(p.x - (getDiamentro() / 2), p.y - 40));
	}
}
