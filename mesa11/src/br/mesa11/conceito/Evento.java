package br.mesa11.conceito;

import java.awt.Point;

import br.hibernate.Botao;

public class Evento {

	private Point ponto;

	private Botao ultimoContatoBola;

	private String eventoCod;

	public Point getPonto() {
		return ponto;
	}

	public void setPonto(Point ponto) {
		this.ponto = ponto;
	}

	public Botao getUltimoContatoBola() {
		return ultimoContatoBola;
	}

	public void setUltimoContatoBola(Botao ultimoContatoBola) {
		this.ultimoContatoBola = ultimoContatoBola;
	}

	public String getEventoCod() {
		return eventoCod;
	}

	public void setEventoCod(String eventoCod) {
		this.eventoCod = eventoCod;
	}

}
