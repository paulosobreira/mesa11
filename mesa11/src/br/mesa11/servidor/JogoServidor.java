package br.mesa11.servidor;

import java.sql.Date;

import br.hibernate.CampeonatoMesa11;
import br.hibernate.PartidaMesa11;
import br.hibernate.RodadaCampeonatoMesa11;
import br.hibernate.Time;
import br.mesa11.ProxyComandos;
import br.mesa11.conceito.ControleJogo;
import br.nnpe.HibernateUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.tos.DadosJogoSrvMesa11;
import br.tos.SessaoCliente;

public class JogoServidor {
	private DadosJogoSrvMesa11 dadosJogoSrvMesa11;
	private Time timeCasa;
	private Time timeVisita;
	private ControleJogo controleJogo;
	private boolean saiuJogoNaoIniciado;
	private boolean finalizado;
	private boolean jogoCampeonato;
	private ProxyComandos proxyComandos;
	private long tempoCriacao = System.currentTimeMillis();

	public long getTempoCriacao() {
		return tempoCriacao;
	}

	public boolean isJogoCampeonato() {
		return jogoCampeonato;
	}

	public void setJogoCampeonato(boolean jogoCampeonato) {
		this.jogoCampeonato = jogoCampeonato;
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
		if (nomeJogador == null) {
			return;
		}
		if (nomeJogador.equals(dadosJogoSrvMesa11.getNomeCriador())) {
			dadosJogoSrvMesa11.setSaiuCriador(true);
		}
		if (nomeJogador.equals(dadosJogoSrvMesa11.getNomeVisitante())) {
			dadosJogoSrvMesa11.setSaiuVisitante(true);
		}
		if (controleJogo != null) {
			controleJogo.setDica("WO");
			dadosJogoSrvMesa11.setWo(true);
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
		if (finalizado) {
			return;
		}
		Logger.logar("proxyComandos.verificaSemSessao(dadosJogoSrvMesa11.getNomeCriador())"
				+ proxyComandos.verificaSemSessao(dadosJogoSrvMesa11
						.getNomeCriador()));
		Logger.logar("proxyComandos.verificaSemSessao(dadosJogoSrvMesa11.getNomeVisitante()"
				+ proxyComandos.verificaSemSessao(dadosJogoSrvMesa11
						.getNomeVisitante()));
		Logger.logar("dadosJogoSrvMesa11.isSaiuCriador()"
				+ dadosJogoSrvMesa11.isSaiuCriador());
		Logger.logar("dadosJogoSrvMesa11.isSaiuVisitante()"
				+ dadosJogoSrvMesa11.isSaiuVisitante());
		if (dadosJogoSrvMesa11.getIdRodadaCampeonato() == 0
				&& (proxyComandos.verificaSemSessao(dadosJogoSrvMesa11
						.getNomeCriador())
						|| (!dadosJogoSrvMesa11.isJogoVsCpu() && proxyComandos
								.verificaSemSessao(dadosJogoSrvMesa11
										.getNomeVisitante()))
						|| (!dadosJogoSrvMesa11.isJogoVsCpu() && dadosJogoSrvMesa11
								.isSaiuVisitante())
						|| dadosJogoSrvMesa11.isSaiuCriador() || controleJogo == null)) {
			return;
		}
		RodadaCampeonatoMesa11 rodadaCampeonatoMesa11 = null;
		PartidaMesa11 partidaMesa11 = new PartidaMesa11();
		partidaMesa11.setInicio(new Date(controleJogo.getTempoIniciado()));
		partidaMesa11.setFim(new Date(controleJogo.getTempoTerminado()));
		partidaMesa11.setGolsTimeCasa(dadosJogoSrvMesa11.getGolsCasa());
		partidaMesa11.setGolsTimeVisita(dadosJogoSrvMesa11.getGolsVisita());
		partidaMesa11.setNomeJogadorCasa(Util.isNullOrEmpty(dadosJogoSrvMesa11
				.getNomeCriador()) ? "CPU" : dadosJogoSrvMesa11
				.getNomeCriador());
		partidaMesa11.setNomeJogadorVisita(Util
				.isNullOrEmpty(dadosJogoSrvMesa11.getNomeVisitante()) ? "CPU"
				: dadosJogoSrvMesa11.getNomeVisitante());
		partidaMesa11.setNomeTimeCasa(dadosJogoSrvMesa11.getTimeCasa());
		partidaMesa11.setNomeTimeVisita(dadosJogoSrvMesa11.getTimeVisita());
		partidaMesa11.setVsCpu(dadosJogoSrvMesa11.isJogoVsCpu());
		if (dadosJogoSrvMesa11.getIdRodadaCampeonato() != 0) {
			rodadaCampeonatoMesa11 = proxyComandos
					.pesquisarRodadaPorId(dadosJogoSrvMesa11
							.getIdRodadaCampeonato());
			partidaMesa11.setCampeonato(rodadaCampeonatoMesa11
					.getCampeonatoMesa11().getNome());
			rodadaCampeonatoMesa11.setGolsCasa(partidaMesa11.getGolsTimeCasa());
			rodadaCampeonatoMesa11.setGolsVisita(partidaMesa11
					.getGolsTimeVisita());
			rodadaCampeonatoMesa11.setRodadaEfetuda(true);
		}
		if (dadosJogoSrvMesa11.isJogoVsCpu()) {
			partidaMesa11.setNomeJogadorVisita("CPU");
		}
		try {
			if (rodadaCampeonatoMesa11 != null) {
				proxyComandos
						.gravarDados(partidaMesa11, rodadaCampeonatoMesa11);
			} else {
				proxyComandos.gravarDados(partidaMesa11);
			}
			HibernateUtil.closeSession();
			finalizado = true;
		} catch (Exception e) {
			Logger.logarExept(e);
		}

	}
}
