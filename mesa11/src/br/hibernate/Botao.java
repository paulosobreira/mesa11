package br.hibernate;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import br.nnpe.GeoUtil;
import br.nnpe.Logger;

public class Botao extends Mesa11Dados {

	private BufferedImage imgBotao;

	private int diamentro = 128;
	private Point position;
	private Point centroInicio;
	private Point destino;
	private String imagem;
	private double angulo;
	private Time time;

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public double getAngulo() {
		return angulo;
	}

	public void setAngulo(double angulo) {
		this.angulo = angulo;
	}

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
		if (getPosition() == null) {
			return null;
		}
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
		setCentroInicio(null);
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
		List reta = GeoUtil.drawBresenhamLine(getCentro(), getDestino());
		if (reta.size() > 1500) {
			Logger.logar("getTrajetoria()" + reta.size());
			Point novoP = (Point) reta.get(1500);
			reta = GeoUtil.drawBresenhamLine(getCentro(), novoP);
			setDestino(novoP);
		}
		return reta;
	}

	@Override
	public String toString() {
		return "Id :" + getId();
	}

	public Shape getShape(double zoom) {
		Ellipse2D e2D = new Ellipse2D.Double(getPosition().x * zoom,
				getPosition().y * zoom, getDiamentro() * zoom, getDiamentro()
						* zoom);
		return e2D;
	}

}
