package br.mesa11.conceito;

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
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			controlePartida.verificaIntervalo();
			controlePartida.verificaFimJogo();
			controlePartida.verificaFalhaPorTempo();
		}
		controlePartida.verificaFimJogo();
	}
}
