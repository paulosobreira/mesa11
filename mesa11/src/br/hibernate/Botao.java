package br.hibernate;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import br.mesa11.ConstantesMesa11;
import br.nnpe.GeoUtil;
import br.nnpe.Logger;
import br.nnpe.Ponto;
import br.nnpe.Util;

@Entity
@Table(name = "m11_botao")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Botao extends Mesa11Dados {

	private int diamentro = ConstantesMesa11.DIAMENTRO_BOTAO;
	private String nome;
	private String imagem;
	private Integer numero;
	private double angulo;
	@ManyToOne
	@JoinColumn(nullable = false)
	private Time time;
	private Integer precisao;
	private Integer forca;
	private Integer defesa;
	private boolean titular;
	private boolean goleiro;

	@JsonIgnore
	private Point position;
	@JsonIgnore
	private Point centroInicio;
	@JsonIgnore
	private Point destino;

	public Ponto getPositionJS() {
		return new Ponto(position);
	}

	public Ponto getCentroInicioJS() {
		return new Ponto(centroInicio);
	}

	public Ponto getDestinoJS() {
		return new Ponto(destino);
	}

	public Integer getNumero() {
		if (numero == null) {
			return new Integer(0);
		}
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Integer getPrecisao() {
		if (precisao == null) {
			return new Integer(500);
		}
		return precisao;
	}

	public void setPrecisao(Integer precisao) {
		this.precisao = precisao;
	}

	public Integer getForca() {
		if (forca == null) {
			return new Integer(500);
		}
		return forca;
	}

	public void setForca(Integer forca) {
		this.forca = forca;
	}

	public Integer getDefesa() {
		if (defesa == null) {
			return new Integer(500);
		}
		return defesa;
	}

	public void setDefesa(Integer defesa) {
		this.defesa = defesa;
	}

	public boolean isTitular() {
		return titular;
	}

	public void setTitular(boolean titular) {
		this.titular = titular;
	}

	public boolean isGoleiro() {
		return goleiro;
	}

	public void setGoleiro(boolean goleiro) {
		this.goleiro = goleiro;
	}

	public void setDiamentro(int diamentro) {
		this.diamentro = diamentro;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public String getNome() {
		if (Util.isNullOrEmpty(nome)) {
			return "";
		}
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public double getAngulo() {
		return angulo;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public void setAngulo(double angulo) {
		this.angulo = angulo;
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
		position = new Point(p.x - (getDiamentro() / 2),
				p.y - (getDiamentro() / 2));
	}

	public void setCentroTodos(Point2D p) {
		setCentro(new Point((int) p.getX(), (int) p.getY()));
		setCentroInicio(new Point((int) p.getX(), (int) p.getY()));
	}

	public void setCentroTodos(Point p) {
		setCentro(p);
		setCentroInicio(p);
	}

	public int getDiamentro() {
		return diamentro;
	}

	public void setTamanho(int tamanho) {
		this.diamentro = tamanho;
	}

	public Point getPosition() {
		if (position == null) {
			return new Point(0, 0);
		}
		return position;
	}

	public void setPosition(Point position) {
		setCentroInicio(null);
		this.position = position;
	}

	public Botao() {
	}

	public Botao(Long id) {
		this.id = id;
	}

	public Botao(int id) {
		this.id = new Long(id);
	}

	public static void main(String[] args) {
	}

	@JsonIgnore
	public List getTrajetoria() {
		List reta = GeoUtil.drawBresenhamLine(getCentro(), getDestino());
		if (reta.size() > 1500) {
			// Logger.logar("getTrajetoria()" + reta.size());
			Point novoP = (Point) reta.get(1500);
			reta = GeoUtil.drawBresenhamLine(getCentro(), novoP);
			setDestino(novoP);
		}
		return reta;
	}

	@Override
	public String toString() {
		return "Id :" + getId() + " " + getClass().getSimpleName() + " Time "
				+ (getTime() == null
						? "Bola"
						: getTime().getNome() + " " + getNumero());
	}

	public Shape getShape(double zoom) {
		if (getPosition() == null) {
			Logger.logar(
					"getShape getPosition null id " + id + " " + getClass());
		}
		Ellipse2D e2D = new Ellipse2D.Double(getPosition().x * zoom,
				getPosition().y * zoom, getDiamentro() * zoom,
				getDiamentro() * zoom);
		return e2D;
	}

}
