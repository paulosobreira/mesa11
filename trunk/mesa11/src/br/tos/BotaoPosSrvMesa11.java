package br.tos;

import java.awt.Point;
import java.io.Serializable;

public class BotaoPosSrvMesa11 implements Serializable {

	private long id;
	private Point pos;
	private double rotacao;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Point getPos() {
		return pos;
	}

	public void setPos(Point pos) {
		this.pos = pos;
	}

	public double getRotacao() {
		return rotacao;
	}

	public void setRotacao(double rotacao) {
		this.rotacao = rotacao;
	}

}
