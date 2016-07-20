package br.mesa11.conceito;

import br.hibernate.Time;
import br.nnpe.Logger;
import br.nnpe.Util;

public class AtualizadorJogadaCPU extends Thread {
	private ControleJogo controleJogo;
	private long ultJogada;
	private long intervaloEntreJogadas = 1000;
	private Thread jogadaCpu;
	protected long iniJogada;

	public AtualizadorJogadaCPU(ControleJogo controleJogo) {
		super();
		this.controleJogo = controleJogo;
		intervaloEntreJogadas = Util.intervalo(1000, 2000);
		setPriority(Thread.MIN_PRIORITY);
	}

	public void run() {
		try {
			sleep(2000);
		} catch (InterruptedException e1) {
			Logger.logarExept(e1);
		}
		while (!controleJogo.isJogoTerminado()) {
			try {
				if ((System.currentTimeMillis() - iniJogada) > 1000) {
					if ((jogadaCpu != null && jogadaCpu.isAlive())) {
						jogadaCpu.interrupt();
					}
				}

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
						final Time timeJogadaVez = controleJogo.timeJogadaVez();
						if (controleJogo.isProcessando()) {
							continue;
						}
						if (timeJogadaVez != null
								&& (controleJogo.isAssistido() || timeJogadaVez
										.isControladoCPU())) {
							controleJogo.setProcessando(true);
							ultJogada = System.currentTimeMillis();
							if ((jogadaCpu != null && jogadaCpu.isAlive())) {
								jogadaCpu.interrupt();
							} else {
								jogadaCpu = new Thread(new Runnable() {
									@Override
									public void run() {
										iniJogada = System.currentTimeMillis();
										Logger.logar("Inicia Jogada CPU "
												+ timeJogadaVez.getNome());
										controleJogo.jogadaCPU();
										controleJogo.zeraBtnAssistido();
										Logger.logar("Tempo Jogada Cpu "
												+ (System.currentTimeMillis() - iniJogada));
									}
								});
								jogadaCpu.setPriority(MIN_PRIORITY);
								jogadaCpu.start();
							}
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
