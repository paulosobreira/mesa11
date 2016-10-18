package br.mesa11.conceito;

import br.hibernate.Time;
import br.nnpe.Logger;
import br.nnpe.Util;

public class AtualizadorJogadaCPU extends Thread {
	private ControleJogo controleJogo;
	private long ultJogada;
	private long intervaloEntreJogadas = 1000;
	private Thread jogadaCpu;
	private boolean jogadaCpuAtivo;
	protected long iniJogada;

	public AtualizadorJogadaCPU(ControleJogo controleJogo) {
		super();
		this.controleJogo = controleJogo;
		intervaloEntreJogadas = Util.intervalo(3000, 5000);
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
				if ((System.currentTimeMillis()
						- ultJogada) < intervaloEntreJogadas) {
					continue;
				}
				if (controleJogo.isAnimando()) {
					sleep(1000);
					continue;
				}
				if (controleJogo.isProcessando()) {
					sleep(1000);
					continue;
				}
				if (timeJogadaVez != null && (controleJogo.isAssistido()
						|| timeJogadaVez.isControladoCPU())) {
					controleJogo.setProcessando(true);
					ultJogada = System.currentTimeMillis();
					if (jogadaCpuAtivo) {
						sleep(1000);
						continue;
					} else {
						jogadaCpu = new Thread(new Runnable() {
							@Override
							public void run() {
								jogadaCpuAtivo = true;
								while (controleJogo.isAnimando()
										|| controleJogo.isProcessando()) {
									try {
										if (controleJogo
												.isJogoOnlineSrvidor()) {
											sleep(2000);
											Logger.logar(
													"isAnimando Jogada CPU  sleep(2000);");
										} else {
											sleep(1000);
											Logger.logar(
													"isAnimando Jogada CPU  sleep(1000);");
										}
									} catch (InterruptedException e) {
										Logger.logarExept(e);
										jogadaCpuAtivo = false;
									}
								}
								iniJogada = System.currentTimeMillis();
								Logger.logar("Inicia Jogada CPU "
										+ timeJogadaVez.getNome());
								controleJogo.jogadaCPU();
								Logger.logar("Tempo Jogada Cpu "
										+ (System.currentTimeMillis()
												- iniJogada));
								String tempo = controleJogo
										.tempoJogadaRestanteJogoFormatado();
								try {
									Integer t = new Integer(tempo);
									if (t > 5) {
										sleep(2000);
										Logger.logar(
												"isAnimando Jogada CPU  t > 5 sleep(2000);");
									}
								} catch (Exception e) {
									Logger.logarExept(e);
								}
								controleJogo.zeraBtnAssistido();
								controleJogo.setProcessando(false);
								jogadaCpuAtivo = false;
							}
						});
						jogadaCpu.setPriority(MIN_PRIORITY);
						jogadaCpu.start();
					}
				}
				controleJogo.setProcessando(false);
			} catch (Exception e) {
				Logger.logarExept(e);
			}

		}
	}
}
