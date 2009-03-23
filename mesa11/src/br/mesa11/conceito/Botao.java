package br.mesa11.conceito;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class Botao {

	private BufferedImage imgBotao;
	private Point position;

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public Botao() {
		imgBotao = null;
	}

	public BufferedImage getImgBotao() {
		return imgBotao;
	}

	public void setImgBotao(BufferedImage imgBotao) {
		this.imgBotao = imgBotao;
	}

	public static void main(String[] args) {
	}
}
