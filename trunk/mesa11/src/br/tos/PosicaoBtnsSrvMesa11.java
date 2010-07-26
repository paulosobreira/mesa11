package br.tos;

import java.io.Serializable;
import java.util.Map;

public class PosicaoBtnsSrvMesa11 implements Serializable {

	long timeStamp;
	private Map botoes;

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Map getBotoes() {
		return botoes;
	}

	public void setBotoes(Map botoes) {
		this.botoes = botoes;
	}

}
