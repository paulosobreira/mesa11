package br.hibernate;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.hibernate.loader.custom.Return;

public class Goleiro extends Botao {

	private int rotacao;

	public int getRotacao() {
		return rotacao;
	}

	public void setRotacao(int rotacao) {
		this.rotacao = rotacao;
	}

	public Goleiro(int i) {
		super(i);
	}

	public Shape getRetangulo() {
		return new Rectangle2D.Double(getPosition().x, getPosition().y,
				getDiamentro() * 2, getRaio());
	}
}
