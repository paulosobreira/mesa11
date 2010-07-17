package br.tos;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author paulo.sobreira
 * 
 */
public class DadosMesa11 implements Serializable {
	private static final long serialVersionUID = 2200481566401284586L;
	private Long dataTime;
	private Collection clientes = new HashSet();
	private Collection jogosCriados = new HashSet();
	private String linhaChat = "";

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

	public Collection getClientes() {
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

}
