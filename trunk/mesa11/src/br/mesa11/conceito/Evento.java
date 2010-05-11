package br.mesa11.conceito;

import java.awt.Point;

import br.hibernate.Botao;
import br.mesa11.ConstantesMesa11;
import br.nnpe.Logger;

public class Evento {

	private Point ponto;

	private Botao ultimoContato;

	private Botao botaoEvento;

	private String eventoCod;

	private boolean naBola;

	public Botao getBotaoEvento() {
		return botaoEvento;
	}

	public void setBotaoEvento(Botao botaoEvento) {
		this.botaoEvento = botaoEvento;
	}

	public Point getPonto() {
		return ponto;
	}

	public void setPonto(Point ponto) {
		this.ponto = ponto;
	}

	public Botao getUltimoContato() {
		return ultimoContato;
	}

	public void setUltimoContato(Botao ultimoContato) {
		this.ultimoContato = ultimoContato;
	}

	public String getEventoCod() {
		return eventoCod;
	}

	public boolean isNaBola() {
		return naBola;
	}

	public void setNaBola(boolean naBola) {
		this.naBola = naBola;
	}

	@Override
	public String toString() {
		return "Evento :" + eventoCod + " " + ultimoContato + " " + naBola
				+ " Btn Evt" + botaoEvento;
	}

	public void setEventoCod(String eCod) {
		Logger.logar("setEventoCod" + eventoCod);
		if (ConstantesMesa11.CONTATO_BOTAO_BOLA.equals(eCod)
				&& eventoCod != null) {
			return;
		}
		if (ConstantesMesa11.CONTATO_BOTAO_BOLA.equals(eCod)
				&& eventoCod == null) {
			naBola = true;
		}
		if (naBola && ConstantesMesa11.CONTATO_BOTAO_BOTAO.equals(eCod)) {
			return;
		}
		this.eventoCod = eCod;
	}

}
