package br.mesa11.servidor;

import java.util.Iterator;
import java.util.List;

import br.hibernate.CampeonatoMesa11;
import br.hibernate.JogadoresCampeonatoMesa11;
import br.hibernate.Time;
import br.hibernate.TimesCampeonatoMesa11;
import br.hibernate.Usuario;
import br.mesa11.ProxyComandos;
import br.tos.DadosMesa11;

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
		List<JogadoresCampeonatoMesa11> jogadoresCampeonatoMesa11List = campeonatoMesa11
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
		List<TimesCampeonatoMesa11> timesCampeonatoMesa11List = campeonatoMesa11
				.getTimesCampeonatoMesa11();
		for (Iterator iterator = timesCampeonatoMesa11List.iterator(); iterator
				.hasNext();) {
			TimesCampeonatoMesa11 timesCampeonatoMesa11 = (TimesCampeonatoMesa11) iterator
					.next();
			Time time = controlePersistencia.obterTime(timesCampeonatoMesa11
					.getTime().getNome());
			timesCampeonatoMesa11.setTime(time);
		}
		return null;
	}

}