package br.mesa11.servidor;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.mesa11.ProxyComandos;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.tos.SessaoCliente;

/**
 * @author Paulo Sobreira Criado em 25/08/2007 as 11:22:46
 */
public class MonitorAtividade extends Thread {

	private ProxyComandos proxyComandos;
	private boolean viva = true;

	public MonitorAtividade(ProxyComandos proxyComandos) {
		this.proxyComandos = proxyComandos;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (viva) {
			try {
				sleep(5000);
				long timeNow = System.currentTimeMillis();
				Collection<SessaoCliente> clientes = proxyComandos
						.getDadosMesa11().getClientes();
				SessaoCliente sessaoClienteRemover = null;
				for (Iterator iter = clientes.iterator(); iter.hasNext();) {
					SessaoCliente sessaoCliente = (SessaoCliente) iter.next();
					if ((timeNow - sessaoCliente.getUlimaAtividade()) > 100000) {
						sessaoClienteRemover = sessaoCliente;
						break;
					}
				}
				if (sessaoClienteRemover != null) {
					proxyComandos.removerClienteInativo(sessaoClienteRemover);
				}
				Map<String, JogoServidor> jogos = proxyComandos
						.getControleJogosServidor().getMapaJogos();
				String jogoRemover = null;
				for (Iterator iter = jogos.keySet().iterator(); iter.hasNext();) {

					String key = (String) iter.next();

					JogoServidor jogoServidor = (JogoServidor) jogos.get(key);

					if (verificaSemSessao(jogoServidor.getDadosJogoSrvMesa11()
							.getNomeCriador())) {
						jogoRemover = key;
					}

					if (jogoServidor.jogoTerminado()) {
						/**
						 * Apaga o jogo em 5 minutos apos termino
						 */

						if ((timeNow - jogoServidor.getTempoTerminado()) > 300000) {
							jogoRemover = key;
						}
					} else {
						/**
						 * Apaga o jogo em 35 minutos apos criacão
						 */
						if ((timeNow - jogoServidor.getTempoCriacao()) > 2100000) {
							jogoRemover = key;
						}
					}
				}
				if (jogoRemover != null) {
					jogos.remove(jogoRemover);
					proxyComandos.getDadosMesa11().getJogosAndamento().remove(
							jogoRemover);
				}
			} catch (Exception e) {
				Logger.logarExept(e);
			}
		}

	}

	private boolean verificaSemSessao(String nomeCriador) {
		if (Util.isNullOrEmpty(nomeCriador)) {
			return true;
		}
		Collection<SessaoCliente> clientes = proxyComandos.getDadosMesa11()
				.getClientes();
		for (Iterator iter = clientes.iterator(); iter.hasNext();) {
			SessaoCliente sessaoCliente = (SessaoCliente) iter.next();
			if (nomeCriador.equals(sessaoCliente.getNomeJogador())) {
				return false;
			}
		}
		return true;

	}

	private void dormir(long l) {
		try {
			Thread.sleep(l);
		} catch (InterruptedException e) {
			Logger.logarExept(e);
		}

	}

}
