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

	private boolean bolaFora;

	public boolean isBolaFora() {
		return bolaFora;
	}

	public void setBolaFora(boolean bolaFora) {
		this.bolaFora = bolaFora;
	}

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
		if (ultimoContato.getId() == 0) {
			return;
		}
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

		Logger.logar("setEventoCod " + eCod);

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
		if (ConstantesMesa11.GOL.equals(eCod)
				|| ConstantesMesa11.META_ESCANTEIO.equals(eCod)
				|| ConstantesMesa11.LATERAL.equals(eCod)) {
			bolaFora = true;
		}
		this.eventoCod = eCod;
	}
}
