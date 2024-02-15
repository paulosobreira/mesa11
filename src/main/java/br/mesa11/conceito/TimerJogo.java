package br.mesa11.conceito;

import br.nnpe.Logger;

public class TimerJogo extends Thread {

	private ControlePartida controlePartida;
	private ControleJogo controleJogo;

	public TimerJogo(ControlePartida controlePartida, ControleJogo controleJogo) {
		super();
		this.controlePartida = controlePartida;
		this.controleJogo = controleJogo;
	}

	@Override
	public void run() {
		while (!controleJogo.isJogoTerminado()) {
			try {
				sleep(100);
				controlePartida.verificaIntervalo();
				controlePartida.verificaFalhaPorTempo();
				controlePartida.verificaFimJogo();
			} catch (InterruptedException e) {
				Logger.logarExept(e);
			}
		}
		controlePartida.verificaFimJogo();
	}
}
