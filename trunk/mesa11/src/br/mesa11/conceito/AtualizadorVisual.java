package br.mesa11.conceito;

import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import br.mesa11.visao.MesaPanel;
import br.nnpe.Logger;

public class AtualizadorVisual implements Runnable {
	private ControleJogo controleJogo;
	private MesaPanel mesaPanel;
	private int cont = 60, contAnimando = 20;
	private Rectangle rectangle;

	public AtualizadorVisual(ControleJogo controleJogo) {
		super();
		this.controleJogo = controleJogo;
		this.mesaPanel = controleJogo.getMesaPanel();
	}

	public void run() {
		while (controleJogo.isTelaAtualizando()) {
			try {
				boolean scrooll = false;
				if (controleJogo.getVelhoPontoTela() != controleJogo
						.getNovoPontoTela()) {
					scrooll = true;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							mesaPanel.repaint();
							controleJogo.getScrollPane().getViewport()
									.setViewPosition(
											controleJogo.getNovoPontoTela());
						}
					});
					controleJogo.setVelhoPontoTela(controleJogo
							.getNovoPontoTela());
				}
				try {
					if (controleJogo.isAnimando())
						Thread.sleep(contAnimando);
					else
						Thread.sleep(cont);
					if (!scrooll) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								mesaPanel.repaint();
							}
						});
					}
				} catch (InterruptedException e) {
				}

			} catch (Exception e) {
				e.printStackTrace();
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