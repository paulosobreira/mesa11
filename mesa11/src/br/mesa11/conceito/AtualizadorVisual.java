package br.mesa11.conceito;

import javax.swing.SwingUtilities;

import br.mesa11.visao.MesaPanel;
import br.nnpe.Logger;
import br.nnpe.Util;

public class AtualizadorVisual extends Thread {
	private ControleJogo controleJogo;
	private MesaPanel mesaPanel;
	private boolean alive = true;
	private int cont = 60, contAnimando = 20;

	public AtualizadorVisual(ControleJogo controleJogo) {
		super();
		this.controleJogo = controleJogo;
		this.mesaPanel = controleJogo.getMesaPanel();
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public void run() {
		while (alive) {
			try {
				if (controleJogo.isProcessando()) {
					Thread.sleep(Util.intervalo(contAnimando, cont));
					continue;
				}
				boolean scrooll = false;
				if (controleJogo.getVelhoPontoTela() != controleJogo
						.getNovoPontoTela()) {
					scrooll = true;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							if (!controleJogo.isProcessando()) {
								mesaPanel.repaint();
								controleJogo
										.getScrollPane()
										.getViewport()
										.setViewPosition(
												controleJogo.getNovoPontoTela());
							}
						}
					});
					controleJogo.setVelhoPontoTela(controleJogo
							.getNovoPontoTela());
				}
				try {
					if (controleJogo.isAnimando()
							|| controleJogo.isProcessando()) {
						Thread.sleep(contAnimando);
					} else
						Thread.sleep(cont);
					if (!scrooll) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								if (!controleJogo.isProcessando()) {
									mesaPanel.repaint();
								}
							}
						});
					}
				} catch (InterruptedException e) {
				}

			} catch (Exception e) {
				Logger.logarExept(e);
				try {
					Thread.sleep(100);
					if (cont < 120)
						cont += 10;
					if (contAnimando < 60)
						contAnimando += 10;
					Logger.logar("cont" + cont);
					Logger.logar("contAnimando" + contAnimando);
				} catch (InterruptedException e2) {

				}
			}

		}

	}
}
