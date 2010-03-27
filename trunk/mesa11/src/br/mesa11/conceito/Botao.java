package br.mesa11.conceito;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class Botao {

	private BufferedImage imgBotao;

	private int diamentro = 50;
	private Point position;
	private Point destino;

	public Point getDestino() {
		return destino;
	}

	public void setDestino(Point destino) {
		this.destino = destino;
	}

	public int getRaio() {
		return diamentro / 2;
	}

	public Point getCentro() {
		return new Point(position.x + (diamentro / 2), position.y
				+ (diamentro / 2));
	}

	public int getDiamentro() {
		return diamentro;
	}

	public void setTamanho(int tamanho) {
		this.diamentro = tamanho;
	}

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
