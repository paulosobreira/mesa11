package br.mesa11.cliente;

import br.mesa11.conceito.ControleJogo;
import br.nnpe.Logger;
import br.nnpe.Util;

public class AtualizadorJogadaCPU extends Thread {
	private ControleJogo controleJogo;
	private long ultJogada;

	public AtualizadorJogadaCPU(ControleJogo controleJogo) {
		super();
		this.controleJogo = controleJogo;
	}

	public void run() {
		while (!controleJogo.isJogoTerminado()) {
			try {
				if ((System.currentTimeMillis() - ultJogada) < 1000) {
					sleep(Util.intervalo(1000, 2000));
					continue;
				}
				if (!controleJogo.isAnimando()) {
					try {
						controleJogo.setProcessando(true);
						ultJogada = System.currentTimeMillis();
						controleJogo.jogadaCPU();
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
