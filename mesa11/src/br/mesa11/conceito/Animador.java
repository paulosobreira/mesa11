package br.mesa11.conceito;

import java.awt.Point;
import java.util.List;

import br.hibernate.Bola;
import br.hibernate.Botao;
import br.nnpe.Logger;
import br.nnpe.Util;

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
			animacao.setExecutou(true);
			controleJogo.atualizaBotoesCopia();
		} catch (InterruptedException e) {
			Logger.logarExept(e);
		} finally {
			if (animacao != null && animacao.getObjetoAnimacao() != null)
				controleJogo
						.removerBotoesComThread(animacao.getObjetoAnimacao());
		}
	}

	private void animar(Animacao anim) throws InterruptedException {
		if (anim == null || anim.getObjetoAnimacao() == null)
			return;
		// if (!controleJogo.isJogoOnlineCliente() && anim.isExecutou()) {
		// return;
		// }
		if (anim.isExecutou()) {
			return;
		}
		Botao botao = (Botao) controleJogo.getBotoes()
				.get(anim.getObjetoAnimacao());
		List elements = anim.getPontosAnimacao();
		if (elements == null || elements.size() == 0) {
			Thread.sleep(100);
			return;
		}
		double div = elements.size();
		double porcentOld = 0;
		int porcentOldDiv10 = 0;
		for (int i = 0; i < elements.size(); i++) {
			double index = (i / div);
			double porcent = index * 100.0;
			Object object = (Object) elements.get(i);
			if (object instanceof Point) {
				Point point = (Point) object;
				if (controleJogo.verificaForaDosLimites(point)) {
					return;
				}
				botao.setCentroInicio(point);
				botao.setCentro(point);
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
									controleJogo.setLateral(botao.getCentro());
									Logger.logar("animar lateral");
								}
							}
						}
					}
					if (porcent - porcentOld > 1 - index) {
						porcentOld = porcent;
						controleJogo.centralizaBotao(controleJogo.getBola());
						if (!controleJogo.isJogoOnlineSrvidor()) {
							int porcentDiv10 = Util.inte(porcent / 10);
							int sleep = 5 + porcentDiv10 - porcentOldDiv10;
							if (sleep > 15) {
								sleep = 15;
							}
							Thread.sleep(sleep);
							porcentOldDiv10 = porcentDiv10;
						} else {
							Thread.sleep(5);
						}
					}

				} else {
					if (porcent - porcentOld > 1 - index) {
						porcentOld = porcent;
						if (!controleJogo.isJogoOnlineSrvidor()) {
							int porcentDiv10 = Util.inte(porcent / 10);
							int sleep = 7 + porcentDiv10 - porcentOldDiv10;
							if (sleep > 17) {
								sleep = 17;
							}
							Thread.sleep(sleep);
							porcentOldDiv10 = porcentDiv10;
						} else {
							Thread.sleep(7);
						}
					}
				}

			} else if (object instanceof Animacao) {
				Animacao animIn = (Animacao) object;
				Thread animadorCom = (Thread) controleJogo
						.obterBotoesComThread(animIn.getObjetoAnimacao());
				if (animadorCom == null) {
					Animador animador = new Animador(animIn, controleJogo);
					Thread thread = new Thread(animador);
					controleJogo.adicionarBotoesComThread(
							animIn.getObjetoAnimacao(), thread);
					thread.start();
				}
			}

		}
	}

	public static void main(String[] args) {
		double val = 100;
		val *= 0.2;
		Logger.logar(val);
	}

}
