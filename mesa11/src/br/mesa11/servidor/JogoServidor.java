package br.mesa11.servidor;

import java.sql.Date;

import br.hibernate.PartidaMesa11;
import br.hibernate.Time;
import br.mesa11.ProxyComandos;
import br.mesa11.conceito.ControleJogo;
import br.nnpe.Logger;
import br.tos.DadosJogoSrvMesa11;
import br.tos.SessaoCliente;

public class JogoServidor {
	private DadosJogoSrvMesa11 dadosJogoSrvMesa11;
	private Time timeCasa;
	private Time timeVisita;
	private ControleJogo controleJogo;
	private boolean saiuJogoNaoIniciado;
	private ProxyComandos proxyComandos;
	private long tempoCriacao = System.currentTimeMillis();

	public long getTempoCriacao() {
		return tempoCriacao;
	}

	public JogoServidor(DadosJogoSrvMesa11 dadosJogoSrvMesa11,
			ProxyComandos proxyComandos) {
		super();
		this.dadosJogoSrvMesa11 = dadosJogoSrvMesa11;
		this.proxyComandos = proxyComandos;
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

	public void jogadorSaiuJogo(String nomeJogador) {
		if (nomeJogador.equals(dadosJogoSrvMesa11.getNomeCriador())) {
			dadosJogoSrvMesa11.setSaiuCriador(true);
		}
		if (nomeJogador.equals(dadosJogoSrvMesa11.getNomeVisitante())) {
			dadosJogoSrvMesa11.setSaiuVisitante(true);
		}
		if (controleJogo != null) {
			controleJogo.setJogoTerminado(true);
			controleJogo.setDica("WO");
		}
		saiuJogoNaoIniciado = true;
	}

	public boolean isSaiuJogoNaoIniciado() {
		return saiuJogoNaoIniciado;
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

	public long getTempoIniciado() {
		if (controleJogo != null) {
			return controleJogo.getTempoIniciado();
		}
		return 0;
	}

	public boolean jogoIniciado() {
		if (controleJogo != null) {
			return controleJogo.isJogoIniciado();
		}
		return false;
	}

	public void fimJogoServidor() {
		if (proxyComandos
				.verificaSemSessao(dadosJogoSrvMesa11.getNomeCriador())
				|| proxyComandos.verificaSemSessao(dadosJogoSrvMesa11
						.getNomeVisitante())
				|| dadosJogoSrvMesa11.isSaiuCriador()
				|| dadosJogoSrvMesa11.isSaiuCriador() || controleJogo == null) {
			return;
		}
		PartidaMesa11 partidaMesa11 = new PartidaMesa11();
		partidaMesa11.setInicio(new Date(controleJogo.getTempoIniciado()));
		partidaMesa11.setFim(new Date(controleJogo.getTempoTerminado()));
		partidaMesa11.setGolsTimeCasa(dadosJogoSrvMesa11.getGolsCasa());
		partidaMesa11.setGolsTimeVisita(dadosJogoSrvMesa11.getGolsVisita());
		partidaMesa11.setNomeJogadorCasa(dadosJogoSrvMesa11.getNomeCriador());
		partidaMesa11.setNomeJogadorVisita(dadosJogoSrvMesa11
				.getNomeVisitante());
		partidaMesa11.setNomeTimeCasa(dadosJogoSrvMesa11.getTimeCasa());
		partidaMesa11.setNomeTimeVisita(dadosJogoSrvMesa11.getTimeVisita());
		try {
			proxyComandos.gravarDados(partidaMesa11);
		} catch (Exception e) {
			Logger.logarExept(e);
		}

	}
}
