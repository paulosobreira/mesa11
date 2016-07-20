package br.mesa11.conceito;

import javax.swing.SwingUtilities;

import br.hibernate.Botao;
import br.mesa11.visao.MesaPanel;
import br.nnpe.Logger;

public class AtualizadorVisual extends Thread {
	private ControleJogo controleJogo;
	private MesaPanel mesaPanel;
	private boolean alive = true;
	private int espera = 16;

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
					Thread.sleep(espera);
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
								controleJogo.getScrollPane().getViewport()
										.setViewPosition(controleJogo
												.getNovoPontoTela());
							}
						}
					});
					controleJogo
							.setVelhoPontoTela(controleJogo.getNovoPontoTela());
				}
				if (controleJogo.isAnimando() || controleJogo.isProcessando()) {
					Thread.sleep(espera);
					controleJogo.setPontoPasando(
							controleJogo.getBola().getCentro());
				} else {
					Thread.sleep(espera);
				}
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

				Botao botao = controleJogo
						.obterBotao(controleJogo.getPontoPasando());
				if (!controleJogo.isAnimando() && botao == null
						&& controleJogo.getPontoClicado() != null
						&& mesaPanel.zoom == mesaPanel.mouseZoom
						&& (System.currentTimeMillis()
								- mesaPanel.lastZoomChange) > 5000
						&& !(controleJogo.getPontoPasandoZoom() != null
								&& controleJogo.miniViewPort().contains(
										controleJogo.getPontoPasandoZoom()))) {
					controleJogo
							.centralizaPonto(controleJogo.getPontoPasando());
				}

			} catch (Exception e) {
				Logger.logarExept(e);
			}
		}
	}
}
