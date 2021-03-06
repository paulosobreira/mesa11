package br.nnpe.tos;

import java.io.Serializable;
import java.util.Arrays;

import br.mesa11.conceito.GolJogador;

public class NnpeTO implements Serializable {

	private String comando;

	private SessaoCliente sessaoCliente;

	private Object Data;

	private int tamListaGols;

	private Long indexProxJogada;

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

	@Override
	public String toString() {
		return "Mesa11TO [comando=" + comando + ", Data=" + Data
				+ ", sessaoCliente=" + sessaoCliente + ", tamListaGols="
				+ tamListaGols + ", dataBytes=" + Arrays.toString(dataBytes)
				+ "]";
	}

	public Long getIndexProxJogada() {
		return indexProxJogada;
	}

	public void setIndexProxJogada(Long indexProxJogada) {
		this.indexProxJogada = indexProxJogada;
	}

}
