package br.mesa11.servidor;

import br.hibernate.Time;
import br.mesa11.conceito.ControleJogo;
import br.tos.DadosJogoSrvMesa11;
import br.tos.SessaoCliente;

public class JogoServidor {
	private DadosJogoSrvMesa11 dadosJogoSrvMesa11;
	private Time timeCasa;
	private Time timeVisita;
	private SessaoCliente sessaoClienteCasa;
	private SessaoCliente sessaoClienteVisita;
	private ControleJogo controleJogo;
	private long tempoCriacao = System.currentTimeMillis();

	public long getTempoCriacao() {
		return tempoCriacao;
	}

	public JogoServidor(DadosJogoSrvMesa11 dadosJogoSrvMesa11) {
		super();
		this.dadosJogoSrvMesa11 = dadosJogoSrvMesa11;
	}

	public DadosJogoSrvMesa11 getDadosJogoSrvMesa11() {
		return dadosJogoSrvMesa11;
	}

	public void setDadosJogoSrvMesa11(DadosJogoSrvMesa11 dadosJogoSrvMesa11) {
		this.dadosJogoSrvMesa11 = dadosJogoSrvMesa11;
	}

	public ControleJogo getControleJogo() {
		return controleJogo;
	}

	public void setControleJogo(ControleJogo controleJogo) {
		this.controleJogo = controleJogo;
	}

	public Time getTimeCasa() {
		return timeCasa;
	}

	public void setTimeCasa(Time timeCasa) {
		this.timeCasa = timeCasa;
	}

	public Time getTimeVisita() {
		return timeVisita;
	}

	public void setTimeVisita(Time timeVisita) {
		this.timeVisita = timeVisita;
	}

	public SessaoCliente getSessaoClienteCasa() {
		return sessaoClienteCasa;
	}

	public void setSessaoClienteCasa(SessaoCliente sessaoClienteCasa) {
		this.sessaoClienteCasa = sessaoClienteCasa;
	}

	public SessaoCliente getSessaoClienteVisita() {
		return sessaoClienteVisita;
	}

	public void setSessaoClienteVisita(SessaoCliente sessaoClienteVisita) {
		this.sessaoClienteVisita = sessaoClienteVisita;
	}

	public void jogadorSaiuJogo(String nomeJogador) {
		if (controleJogo != null) {
			controleJogo.setJogoTerminado(true);
			controleJogo.setDica("WO");
		}

	}

	public long getTempoTerminado() {
		if (controleJogo != null) {
			return controleJogo.getTempoTerminado();
		}
		return 0;
	}

	public boolean jogoTerminado() {
		if (controleJogo != null) {
			return controleJogo.isJogoTerminado();
		}
		return false;
	}

}
