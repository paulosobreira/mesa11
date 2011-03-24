package br.mesa11.conceito;

import br.hibernate.Time;
import br.nnpe.Logger;
import br.nnpe.Util;

public class AtualizadorJogadaCPU extends Thread {
	private ControleJogo controleJogo;
	private long ultJogada;
	private long intervaloEntreJogadas = 1000;

	public AtualizadorJogadaCPU(ControleJogo controleJogo) {
		super();
		this.controleJogo = controleJogo;
		if (controleJogo.isJogoOnlineSrvidor()) {
			intervaloEntreJogadas = Util.intervalo(1000, 2000);
		}
		setPriority(Thread.MIN_PRIORITY);
	}

	public void run() {
		while (!controleJogo.isJogoTerminado()) {

			try {
				if (controleJogo.isJogoOnlineSrvidor()) {
					sleep(Util.intervalo(500, 1000));
				} else {
					sleep(200);
				}
				if ((System.currentTimeMillis() - ultJogada) < intervaloEntreJogadas) {
					continue;
				}
				if (!controleJogo.isAnimando()) {
					try {
						Time timeJogadaVez = controleJogo.timeJogadaVez();
						if (timeJogadaVez != null
								&& timeJogadaVez.isControladoCPU()) {
							controleJogo.setProcessando(true);
							ultJogada = System.currentTimeMillis();
							controleJogo.jogadaCPU();
						}
					} finally {
						controleJogo.setProcessando(false);
					}
				} else {
					sleep(Util.intervalo(1000, 2000));
				}
			} catch (Exception e) {
				Logger.logarExept(e);
			}

		}

	}
}
