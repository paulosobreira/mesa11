package br.mesa11.servidor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.hibernate.CampeonatoMesa11;
import br.hibernate.JogadoresCampeonatoMesa11;
import br.hibernate.Time;
import br.hibernate.TimesCampeonatoMesa11;
import br.hibernate.Usuario;
import br.mesa11.ProxyComandos;
import br.nnpe.HibernateUtil;
import br.recursos.Lang;
import br.tos.DadosMesa11;
import br.tos.ErroServ;
import br.tos.Mesa11TO;
import br.tos.MsgSrv;

public class ControleCampeonatoServidor {
	private ProxyComandos proxyComandos;
	private ControlePersistencia controlePersistencia;
	private DadosMesa11 dadosMesa11;

	public ControleCampeonatoServidor(DadosMesa11 dadosMesa11,
			ControlePersistencia controlePersistencia,
			ProxyComandos proxyComandos) {
		super();
		this.dadosMesa11 = dadosMesa11;
		this.controlePersistencia = controlePersistencia;
		this.proxyComandos = proxyComandos;
	}

	public Object criarCampeonato(CampeonatoMesa11 campeonatoMesa11) {
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
		try {
			controlePersistencia.gravarDados(campeonatoMesa11);
		} catch (Exception e) {
			return new ErroServ(e);
		}
		return (new MsgSrv(Lang.msg("campeonatoCriado")));
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
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(retorno);
		return mesa11to;
	}

	private Object verificaCampeonatoConcluido(CampeonatoMesa11 campeonato) {
		return false;
	}

}
