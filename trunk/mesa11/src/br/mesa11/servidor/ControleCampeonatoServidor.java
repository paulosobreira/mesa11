package br.mesa11.servidor;

import java.util.HashMap;
import java.util.Map;

import br.hibernate.CampeonatoMesa11;
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

	public Object criarCampeonato(CampeonatoMesa11 data) {
		// TODO Auto-generated method stub
		return null;
	}

}
