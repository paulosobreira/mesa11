package br.mesa11.conceito;

import java.awt.Point;
import java.util.List;

import br.hibernate.Bola;
import br.hibernate.Botao;
import br.mesa11.ConstantesMesa11;
import br.nnpe.Logger;
import br.tos.Mesa11TO;
import br.tos.PosicaoBtnsSrvMesa11;

public class Animador implements Runnable {

	private Animacao animacao;
	private ControleJogo controleJogo;

	public Animador(Animacao animacao, ControleJogo controleJogo) {
		super();
		this.animacao = animacao;
		this.controleJogo = controleJogo;
	}

	@Override
	public void run() {
		try {
			animar(animacao);
		} finally {
			controleJogo.getBotoesComThread().remove(
					animacao.getObjetoAnimacao());
		}
	}

	private void animar(Animacao anim) {
		if (anim == null)
			return;
		Botao botao = (Botao) controleJogo.getBotoes().get(
				anim.getObjetoAnimacao());
		List elements = anim.getPontosAnimacao();
		for (int i = 0; i < elements.size(); i++) {
			Object object = (Object) elements.get(i);
			if (object instanceof Point) {
				Point point = (Point) object;
				if (i % 2 == 0) {
					continue;
				}
				if (controleJogo.verificaForaDosLimites(point)) {
					return;
				}
				botao.setCentroInicio(point);
				botao.setCentro(point);

				try {
					if (botao instanceof Bola) {
						if (!controleJogo.isJogoOnlineCliente()) {
							if (!controleJogo.isBolaFora()) {
								if (controleJogo.verificaGol(botao)) {
									controleJogo.setGol(botao);
									Logger.logar("animar gol");
								} else if (controleJogo
										.verificaMetaEscanteio(botao)) {
									controleJogo.setMetaEscanteio(botao);
									Logger.logar("animar MetaEscanteio");
								} else if (!controleJogo
										.verificaDentroCampo(botao)) {
									if (controleJogo.getLateral() == null) {
										controleJogo.setLateral(botao
												.getCentro());
										Logger.logar("animar lateral");
									}
								}
							}
						}
						if (i % 3 == 0) {
							controleJogo
									.centralizaBotao(controleJogo.getBola());
							Thread.sleep(10);
						}

					} else {
						if (i % 3 == 0) {
							Thread.sleep(15);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if (object instanceof Animacao) {
				Animacao animIn = (Animacao) object;
				Thread threadRodando = (Thread) controleJogo
						.getBotoesComThread().get(animIn.getObjetoAnimacao());
				if (threadRodando == null) {
					Animador animador = new Animador(animIn, controleJogo);
					Thread thread = new Thread(animador);
					controleJogo.getBotoesComThread().put(
							animIn.getObjetoAnimacao(), thread);
					thread.start();
				}
			}
		}
	}

	public static void main(String[] args) {
		double val = 100;
		val *= 0.2;
		System.out.println(val);
	}

}
