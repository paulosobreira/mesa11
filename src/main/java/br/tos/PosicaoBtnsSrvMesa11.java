package br.tos;

import java.io.Serializable;
import java.util.List;

public class PosicaoBtnsSrvMesa11 implements Serializable {

	long timeStamp;
	private List<BotaoPosSrvMesa11> botoes;

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public List<BotaoPosSrvMesa11> getBotoes() {
		return botoes;
	}

	public void setBotoes(List<BotaoPosSrvMesa11> botoes) {
		this.botoes = botoes;
	}

}
