package br.tos;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import br.nnpe.tos.SessaoCliente;

/**
 * @author paulo.sobreira
 * 
 */
public class DadosMesa11 implements Serializable {
	private static final long serialVersionUID = 2200481566401284586L;
	private Long dataTime;
	private Collection<SessaoCliente> clientes = new HashSet<SessaoCliente>();
	private Collection<String> jogosCriados = new HashSet<String>();
	private Collection<String> jogosAndamento = new HashSet<String>();
	private String linhaChat = "";

	public Collection<String> getJogosAndamento() {
		return jogosAndamento;
	}

	public void setJogosAndamento(Collection<String> jogosAndamento) {
		this.jogosAndamento = jogosAndamento;
	}

	public Long getDataTime() {
		return dataTime;
	}

	public void setDataTime(Long dataTime) {
		this.dataTime = dataTime;
	}

	public String getLinhaChat() {
		return linhaChat;
	}

	public void setLinhaChat(String linhaChat) {
		this.linhaChat = linhaChat;
	}

	public Collection<SessaoCliente> getClientes() {
		return clientes;
	}

	public Collection getJogosCriados() {
		return jogosCriados;
	}

	public void atualizaAtividade(String nomeJogador) {
		for (Iterator iterator = clientes.iterator(); iterator.hasNext();) {
			SessaoCliente sessaoCliente = (SessaoCliente) iterator.next();
			if (nomeJogador.equals(sessaoCliente.getNomeJogador())) {
				sessaoCliente.setUlimaAtividade(System.currentTimeMillis());
				break;
			}
		}
	}

	public SessaoCliente obterSessaoPeloNome(String nomeJogador) {
		for (Iterator iterator = clientes.iterator(); iterator.hasNext();) {
			SessaoCliente sessaoCliente = (SessaoCliente) iterator.next();
			if (sessaoCliente.getNomeJogador().equals(nomeJogador)) {
				return sessaoCliente;
			}
		}
		return null;
	}

}
