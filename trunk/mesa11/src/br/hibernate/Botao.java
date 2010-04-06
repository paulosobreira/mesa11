package br.hibernate;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import br.nnpe.GeoUtil;

public class Botao extends Mesa11Dados {

	private transient BufferedImage imgBotao;

	private transient int diamentro = 128;
	private transient Point position;
	private transient Point centroInicio;
	private transient Point destino;
	private transient String imagem;

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public Point getCentroInicio() {
		return centroInicio;
	}

	public void setCentroInicio(Point controInicio) {
		this.centroInicio = controInicio;
	}

	public Point getDestino() {
		return destino;
	}

	public void setDestino(Point destino) {
		this.destino = destino;
	}

	public int getRaio() {
		return getDiamentro() / 2;
	}

	public Point getCentro() {
		return new Point(getPosition().x + (getDiamentro() / 2),
				getPosition().y + (getDiamentro() / 2));
	}

	public void setCentro(Point p) {
		position = new Point(p.x - (getDiamentro() / 2), p.y
				- (getDiamentro() / 2));
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

	public Botao(Long id) {
		this.id = id;
	}

	public Botao(int id) {
		this.id = new Long(id);
	}

	public BufferedImage getImgBotao() {
		return imgBotao;
	}

	public void setImgBotao(BufferedImage imgBotao) {
		this.imgBotao = imgBotao;
	}

	public static void main(String[] args) {
	}

	public List getTrajetoria() {
		return GeoUtil.drawBresenhamLine(getCentro(), getDestino());
	}

	@Override
	public String toString() {
		return "Id :" + getId();
	}

}
