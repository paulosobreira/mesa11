package br.mesa11;

import java.io.Serializable;

public class Mesa11TO implements Serializable {

	private String comando;

	private Object Data;

	private byte[] dataBytes;

	public byte[] getDataBytes() {
		return dataBytes;
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

}