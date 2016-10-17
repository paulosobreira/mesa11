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
				final Time timeJogadaVez = controleJogo.timeJogadaVez();
				sleep(500);
				if ((System.currentTimeMillis()
						- ultJogada) < intervaloEntreJogadas) {
					continue;
				}
				if (!controleJogo.isAnimando()) {
					try {
						if (controleJogo.isProcessando()) {
							continue;
						}
						if (timeJogadaVez != null && (controleJogo.isAssistido()
								|| timeJogadaVez.isControladoCPU())) {
							controleJogo.setProcessando(true);
							ultJogada = System.currentTimeMillis();
							if ((jogadaCpu != null && jogadaCpu.isAlive())) {
								continue;
							} else {
								jogadaCpu = new Thread(new Runnable() {
									@Override
									public void run() {
										while (controleJogo.isAnimando()) {
											try {
												if (controleJogo
														.isJogoOnlineSrvidor()) {
													sleep(4000);
												} else {
													sleep(1000);
												}
											} catch (InterruptedException e) {
												Logger.logarExept(e);
											}
										}
										iniJogada = System.currentTimeMillis();
										Logger.logar("Inicia Jogada CPU "
												+ timeJogadaVez.getNome());
										String tempo = controleJogo
												.tempoJogadaRestanteJogoFormatado();
										controleJogo.jogadaCPU();
										controleJogo.zeraBtnAssistido();
										try {
											Integer t = new Integer(tempo);
											if (t > 5) {
												sleep(2000);
											}
										} catch (Exception e) {
											Logger.logarExept(e);
										}
										Logger.logar("Tempo Jogada Cpu "
												+ (System.currentTimeMillis()
														- iniJogada));
										controleJogo.setProcessando(false);
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
					sleep(1000);
				}
			} catch (Exception e) {
				Logger.logarExept(e);
			}

		}
	}
}
