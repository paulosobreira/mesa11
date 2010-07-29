package br.mesa11.servidor;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.mesa11.ProxyComandos;
import br.nnpe.Logger;
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
				sleep((5000 + ((int) Math.random() * 1000)));
				long timeNow = System.currentTimeMillis();
				Collection<SessaoCliente> clientes = proxyComandos
						.getDadosMesa11().getClientes();
				SessaoCliente sessaoClienteRemover = null;
				for (Iterator iter = clientes.iterator(); iter.hasNext();) {
					SessaoCliente sessaoCliente = (SessaoCliente) iter.next();
					if ((timeNow - sessaoCliente.getUlimaAtividade()) > 150000) {
						sessaoClienteRemover = sessaoCliente;
						break;
					}
				}
				if (sessaoClienteRemover != null) {
					proxyComandos.removerClienteInativo(sessaoClienteRemover);
				}
				Map<String, JogoServidor> jogos = proxyComandos
						.getControleJogosServidor().getMapaJogos();
				for (Iterator iter = jogos.keySet().iterator(); iter.hasNext();) {
					// SessaoCliente key = (SessaoCliente) iter.next();
					// JogoServidor jogoServidor = (JogoServidor)
					// jogos.get(key);
					// if ((timeNow - jogoServidor.getTempoCriacao()) > 300000)
					// {
					// jogoServidor.iniciarJogo();
					// }
					// regra remover jogo por inatividade
				}
			} catch (Exception e) {
				Logger.logarExept(e);
			}
		}

	}

	private void dormir(long l) {
		try {
			Thread.sleep(l);
		} catch (InterruptedException e) {
			Logger.logarExept(e);
		}

	}

}
