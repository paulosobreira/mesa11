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
		if (controleJogo.isJogoOnlineSrvidor()) {
			intervaloEntreJogadas = Util.intervalo(10000, 15000);
		}
		setPriority(Thread.MIN_PRIORITY);
	}

	public void run() {
		try {
			sleep(2000);
		} catch (InterruptedException e1) {
			Logger.logarExept(e1);
		}
		while (!controleJogo.isJogoTerminado()) {
			final Time timeJogadaVez = controleJogo.timeJogadaVez();
			if ((System.currentTimeMillis()
					- ultJogada) < intervaloEntreJogadas) {
				Util.dormir(1000);
				continue;
			}
			if (controleJogo.isAnimando()) {
				Util.dormir(1000);
				continue;
			}
			if (controleJogo.isProcessando()) {
				Util.dormir(1000);
				continue;
			}
			if (timeJogadaVez != null && (controleJogo.isAssistido()
					|| timeJogadaVez.isControladoCPU())) {
				controleJogo.setProcessando(true);
				if (jogadaCpuAtivo) {
					Util.dormir(1000);
					continue;
				} else {
					try {
						jogadaCPU(timeJogadaVez);
						ultJogada = System.currentTimeMillis();
					} finally {
						controleJogo.setProcessando(false);
						jogadaCpuAtivo = false;
					}
				}
			}
			controleJogo.setProcessando(false);

		}
	}

	private void jogadaCPU(final Time timeJogadaVez) {
		jogadaCpu = new Thread(new Runnable() {
			@Override
			public void run() {
				while (controleJogo.isAnimando()
						|| controleJogo.isProcessando()) {
					Util.dormir(1000);
				}
				jogadaCpuAtivo = true;
				iniJogada = System.currentTimeMillis();
				Logger.logar("Inicia Jogada CPU " + timeJogadaVez.getNome());
				controleJogo.jogadaCPU();
				Logger.logar("Tempo Jogada Cpu "
						+ (System.currentTimeMillis() - iniJogada));
				String tempo = controleJogo.tempoJogadaRestanteJogoFormatado();
				Integer t = new Integer(tempo);
				if (t > 5) {
					Util.dormir(2000);
					Logger.logar("isAnimando Jogada CPU  t > 5 sleep(2000);");
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
