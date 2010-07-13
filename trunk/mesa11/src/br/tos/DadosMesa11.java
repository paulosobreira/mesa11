package br.tos;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

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

}
