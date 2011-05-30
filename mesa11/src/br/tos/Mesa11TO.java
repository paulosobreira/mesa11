package br.tos;

import java.io.Serializable;

import br.mesa11.conceito.GolJogador;

public class Mesa11TO implements Serializable {

	private String comando;

	private SessaoCliente sessaoCliente;

	private Object Data;

	private int tamListaGols;


	private byte[] dataBytes;

	public byte[] getDataBytes() {
		return dataBytes;
	}

	public int getTamListaGols() {
		return tamListaGols;
	}

	public void setTamListaGols(int tamListaGols) {
		this.tamListaGols = tamListaGols;
	}

	public void setDataBytes(byte[] dataBytes) {
		this.dataBytes = dataBytes;
	}

	public String getComando() {
		return comando;
	}

	public void setComando(String comando) {
		this.comando = comando;
	}

	public Object getData() {
		return Data;
	}

	public void setData(Object data) {
		Data = data;
	}

	public SessaoCliente getSessaoCliente() {
		return sessaoCliente;
	}

	public void setSessaoCliente(SessaoCliente sessaoCliente) {
		this.sessaoCliente = sessaoCliente;
	}

}
