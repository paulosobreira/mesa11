package br.mesa11.servidor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.hibernate.CampeonatoMesa11;
import br.hibernate.JogadoresCampeonatoMesa11;
import br.hibernate.RodadaCampeonatoMesa11;
import br.hibernate.Time;
import br.hibernate.TimesCampeonatoMesa11;
import br.hibernate.Usuario;
import br.mesa11.ConstantesMesa11;
import br.mesa11.ProxyComandos;
import br.nnpe.HibernateUtil;
import br.nnpe.tos.ErroServ;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.MsgSrv;
import br.recursos.Lang;
import br.tos.DadosMesa11;

public class ControleCampeonatoServidor {
	private ProxyComandos proxyComandos;
	private ControlePersistencia controlePersistencia;
	private DadosMesa11 dadosMesa11;
	private ControleJogosServidor controleJogosServidor;

	public ControleCampeonatoServidor(DadosMesa11 dadosMesa11,
			ControlePersistencia controlePersistencia,
			ProxyComandos proxyComandos,
			ControleJogosServidor controleJogosServidor) {
		super();
		this.dadosMesa11 = dadosMesa11;
		this.controlePersistencia = controlePersistencia;
		this.proxyComandos = proxyComandos;
		this.controleJogosServidor = controleJogosServidor;
	}

	public ControleCampeonatoServidor() {
		// TODO Auto-generated constructor stub
	}

	public Object criarCampeonato(NnpeTO mesa11to) {

		CampeonatoMesa11 campeonatoMesa11 = (CampeonatoMesa11) mesa11to
				.getData();
		boolean criadorNaLista = false;
		Collection<JogadoresCampeonatoMesa11> jogadoresCampeonatoMesa11List = campeonatoMesa11
				.getJogadoresCampeonatoMesa11();
		for (Iterator iterator = jogadoresCampeonatoMesa11List.iterator(); iterator
				.hasNext();) {
			JogadoresCampeonatoMesa11 jogadoresCampeonatoMesa11 = (JogadoresCampeonatoMesa11) iterator
					.next();
			Usuario usuario = controlePersistencia
					.obterJogadorPorLogin(jogadoresCampeonatoMesa11
							.getUsuario().getLogin());
			jogadoresCampeonatoMesa11.setUsuario(usuario);
			if (mesa11to.getSessaoCliente().getNomeJogador().equals(
					jogadoresCampeonatoMesa11.getUsuario().getLogin())) {
				criadorNaLista = true;
			}
		}
		if (!criadorNaLista) {
			return new MsgSrv(Lang.msg("criadorJogoDeveEstarEntreJogadores"));
		}
		Collection<TimesCampeonatoMesa11> timesCampeonatoMesa11List = campeonatoMesa11
				.getTimesCampeonatoMesa11();
		for (Iterator iterator = timesCampeonatoMesa11List.iterator(); iterator
				.hasNext();) {
			TimesCampeonatoMesa11 timesCampeonatoMesa11 = (TimesCampeonatoMesa11) iterator
					.next();
			Time time = controlePersistencia.obterTime(timesCampeonatoMesa11
					.getTime().getNome());
			timesCampeonatoMesa11.setTime(time);
		}

		if (timesCampeonatoMesa11List.size() % 2 != 0) {
			return new MsgSrv(Lang.msg("qtdeTimesNoCampeonatoDeveSerPar"));
		}

		/**
		 * Gera as rodadas do campeonato.
		 */
		gerarRodadas(timesCampeonatoMesa11List, campeonatoMesa11);

		try {
			controlePersistencia.gravarDados(campeonatoMesa11);
		} catch (Exception e) {
			return new ErroServ(e);
		} finally {
			HibernateUtil.closeSession();
		}

		return (new MsgSrv(Lang.msg("campeonatoCriado")));
	}

	private void gerarRodadas(
			Collection<TimesCampeonatoMesa11> timesCampeonatoMesa11List,
			CampeonatoMesa11 campeonatoMesa11) {
		int rodada = timesCampeonatoMesa11List.size();
		if (rodada % 2 != 0) {
			rodada++;
		}
		for (int i = 1; i < rodada; i++) {
			for (Iterator iterator = timesCampeonatoMesa11List.iterator(); iterator
					.hasNext();) {
				TimesCampeonatoMesa11 timesCampeonatoMesa11Casa = (TimesCampeonatoMesa11) iterator
						.next();
				if (jaJogouRodada(i, timesCampeonatoMesa11Casa.getTime(),
						campeonatoMesa11.getRodadaCampeonatoMesa11())) {
					continue;
				}
				for (Iterator iterator2 = timesCampeonatoMesa11List.iterator(); iterator2
						.hasNext();) {
					TimesCampeonatoMesa11 timesCampeonatoMesa11Visita = (TimesCampeonatoMesa11) iterator2
							.next();
					if (timesCampeonatoMesa11Casa.getTime().equals(
							timesCampeonatoMesa11Visita.getTime())) {
						continue;
					}
					if (jaJogouRodada(i, timesCampeonatoMesa11Casa.getTime(),
							campeonatoMesa11.getRodadaCampeonatoMesa11())) {
						continue;
					}
					if (jaJogouRodada(i, timesCampeonatoMesa11Visita.getTime(),
							campeonatoMesa11.getRodadaCampeonatoMesa11())) {
						continue;
					}
					if (jaJogouCampeonato(timesCampeonatoMesa11Casa.getTime(),
							timesCampeonatoMesa11Visita.getTime(),
							campeonatoMesa11.getRodadaCampeonatoMesa11())) {
						continue;
					}
					RodadaCampeonatoMesa11 rodadaCampeonatoMesa11 = new RodadaCampeonatoMesa11();
					rodadaCampeonatoMesa11
							.setCampeonatoMesa11(campeonatoMesa11);
					rodadaCampeonatoMesa11.setLoginCriador(campeonatoMesa11
							.getLoginCriador());
					rodadaCampeonatoMesa11
							.setTimeCasa(timesCampeonatoMesa11Casa.getTime());
					rodadaCampeonatoMesa11
							.setTimeVisita(timesCampeonatoMesa11Visita
									.getTime());
					rodadaCampeonatoMesa11.setRodada(i);
					campeonatoMesa11.getRodadaCampeonatoMesa11().add(
							rodadaCampeonatoMesa11);

				}

			}
		}
	}

	private boolean jaJogouCampeonato(Time casa, Time visita,
			Collection<RodadaCampeonatoMesa11> rodadaCampeonatoMesa11List) {
		boolean jogouCampeonato = false;
		for (Iterator iterator = rodadaCampeonatoMesa11List.iterator(); iterator
				.hasNext();) {
			RodadaCampeonatoMesa11 rodadaCampeonatoMesa11 = (RodadaCampeonatoMesa11) iterator
					.next();
			if (rodadaCampeonatoMesa11.getTimeCasa().equals(casa)
					&& rodadaCampeonatoMesa11.getTimeVisita().equals(visita)) {
				jogouCampeonato = true;
			}
		}
		return jogouCampeonato;
	}

	private boolean jaJogouRodada(int rodada, Time time,
			Collection<RodadaCampeonatoMesa11> rodadaCampeonatoMesa11List) {
		boolean jogouRodada = false;
		for (Iterator iterator = rodadaCampeonatoMesa11List.iterator(); iterator
				.hasNext();) {
			RodadaCampeonatoMesa11 rodadaCampeonatoMesa11 = (RodadaCampeonatoMesa11) iterator
					.next();
			if (rodadaCampeonatoMesa11.getRodada() != rodada) {
				continue;
			}
			if (rodadaCampeonatoMesa11.getTimeCasa().equals(time)
					|| rodadaCampeonatoMesa11.getTimeVisita().equals(time)) {
				jogouRodada = true;
			}
		}
		return jogouRodada;
	}

	public static void main(String[] args) {
		Collection<TimesCampeonatoMesa11> timesCampeonatoMesa11List = new LinkedList<TimesCampeonatoMesa11>();
		Time time1 = new Time(1, "Argentina");
		Time time2 = new Time(2, "Brasil");
		Time time3 = new Time(3, "Alemanha");
		Time time4 = new Time(4, "Italia");
		Time time5 = new Time(5, "Franca");
		Time time6 = new Time(6, "Holanda");
		Time time7 = new Time(7, "Inglaterra");
		Time time8 = new Time(8, "Mexico");

		TimesCampeonatoMesa11 timesCampeonatoMesa11_1 = new TimesCampeonatoMesa11(
				1, time1);
		TimesCampeonatoMesa11 timesCampeonatoMesa11_2 = new TimesCampeonatoMesa11(
				2, time2);
		TimesCampeonatoMesa11 timesCampeonatoMesa11_3 = new TimesCampeonatoMesa11(
				3, time3);
		TimesCampeonatoMesa11 timesCampeonatoMesa11_4 = new TimesCampeonatoMesa11(
				4, time4);
		TimesCampeonatoMesa11 timesCampeonatoMesa11_5 = new TimesCampeonatoMesa11(
				5, time5);
		TimesCampeonatoMesa11 timesCampeonatoMesa11_6 = new TimesCampeonatoMesa11(
				6, time6);
		TimesCampeonatoMesa11 timesCampeonatoMesa11_7 = new TimesCampeonatoMesa11(
				7, time7);
		TimesCampeonatoMesa11 timesCampeonatoMesa11_8 = new TimesCampeonatoMesa11(
				8, time8);

		timesCampeonatoMesa11List.add(timesCampeonatoMesa11_1);
		timesCampeonatoMesa11List.add(timesCampeonatoMesa11_2);
		timesCampeonatoMesa11List.add(timesCampeonatoMesa11_3);
		timesCampeonatoMesa11List.add(timesCampeonatoMesa11_4);
		timesCampeonatoMesa11List.add(timesCampeonatoMesa11_5);
		timesCampeonatoMesa11List.add(timesCampeonatoMesa11_6);
		timesCampeonatoMesa11List.add(timesCampeonatoMesa11_7);
		timesCampeonatoMesa11List.add(timesCampeonatoMesa11_8);

		System.out.println(timesCampeonatoMesa11List);
		ControleCampeonatoServidor controleCampeonatoServidor = new ControleCampeonatoServidor();
		CampeonatoMesa11 campeonatoMesa11 = new CampeonatoMesa11();
		controleCampeonatoServidor.gerarRodadas(timesCampeonatoMesa11List,
				campeonatoMesa11);
		Collection<RodadaCampeonatoMesa11> rodadaCampeonatoMesa11 = campeonatoMesa11
				.getRodadaCampeonatoMesa11();
		for (Iterator iterator = rodadaCampeonatoMesa11.iterator(); iterator
				.hasNext();) {
			RodadaCampeonatoMesa11 rodadaCampeonatoMesa112 = (RodadaCampeonatoMesa11) iterator
					.next();
			System.out.println(rodadaCampeonatoMesa112);
		}

	}

	public Object listarCampeonatos() {
		List<CampeonatoMesa11> campeonatos = controlePersistencia
				.listarCampeonatos();
		List retorno = new ArrayList();
		for (Iterator iterator = campeonatos.iterator(); iterator.hasNext();) {
			CampeonatoMesa11 campeonatoMesa11 = (CampeonatoMesa11) iterator
					.next();
			Object[] row = new Object[4];
			row[0] = campeonatoMesa11.getNome();
			row[1] = campeonatoMesa11.getLoginCriador();
			row[2] = verificaCampeonatoConcluido(campeonatoMesa11);
			row[3] = campeonatoMesa11.getDataCriacao();
			retorno.add(row);
		}
		HibernateUtil.closeSession();
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setData(retorno);
		return mesa11to;
	}

	private Object verificaCampeonatoConcluido(CampeonatoMesa11 campeonato) {
		Collection<RodadaCampeonatoMesa11> rodadaCampeonatoMesa11List = campeonato
				.getRodadaCampeonatoMesa11();
		for (Iterator iterator = rodadaCampeonatoMesa11List.iterator(); iterator
				.hasNext();) {
			RodadaCampeonatoMesa11 rodadaCampeonatoMesa11 = (RodadaCampeonatoMesa11) iterator
					.next();
			if (rodadaCampeonatoMesa11.getRodadaEfetuda() == null
					|| !rodadaCampeonatoMesa11.getRodadaEfetuda()) {
				return false;
			}
		}
		return true;
	}

	public Object verCampeonato(String campeonato) {
		try {
			NnpeTO mesa11to = (NnpeTO) controleJogosServidor
					.obterClassificacao(campeonato);
			Map map = (Map) mesa11to.getData();
			Object[] dadoscampeonato = controlePersistencia
					.pesquisarDadosCampeonato(campeonato);
			CampeonatoMesa11 campeonatoMesa11 = controlePersistencia
					.pesquisaCampeonato(campeonato);
			map.put(ConstantesMesa11.NUMERO_RODADAS, campeonatoMesa11
					.getTimesCampeonatoMesa11().size() - 1);
			map.put(ConstantesMesa11.DADOS_CAMPEONATO, dadoscampeonato);
			List<RodadaCampeonatoMesa11> rodadaCampeonatoMesa11Lista = new ArrayList<RodadaCampeonatoMesa11>(
					(Collection<RodadaCampeonatoMesa11>) campeonatoMesa11
							.getRodadaCampeonatoMesa11());
			Collections.sort(rodadaCampeonatoMesa11Lista,
					new Comparator<RodadaCampeonatoMesa11>() {
						@Override
						public int compare(RodadaCampeonatoMesa11 o1,
								RodadaCampeonatoMesa11 o2) {
							return new Integer(o1.getRodada())
									.compareTo(new Integer(o2.getRodada()));
						}
					});
			int rodadaAtual = 1;
			for (Iterator iterator = rodadaCampeonatoMesa11Lista.iterator(); iterator
					.hasNext();) {
				RodadaCampeonatoMesa11 rodadaCampeonatoMesa11 = (RodadaCampeonatoMesa11) iterator
						.next();
				if (rodadaCampeonatoMesa11.getRodadaEfetuda() == null
						|| rodadaCampeonatoMesa11.getRodadaEfetuda() == false) {
					rodadaAtual = rodadaCampeonatoMesa11.getRodada();
					break;
				}
			}
			map.put(ConstantesMesa11.RODADA_ATUAL_CAMPEONATO, rodadaAtual);
			return mesa11to;
		} finally {
			HibernateUtil.closeSession();
		}

	}

	public Object dadosCampeonato(String campeonato) {
		Object[] dadoscampeonato = controlePersistencia
				.pesquisarDadosCampeonato(campeonato);
		return dadoscampeonato;
	}
}
