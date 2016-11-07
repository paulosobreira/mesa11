package br.mesa11.conceito;

import java.awt.Point;
import java.util.List;

import javax.swing.SwingUtilities;

import br.mesa11.visao.MesaPanel;
import br.nnpe.GeoUtil;
import br.nnpe.Logger;

public class AtualizadorVisual extends Thread {
	private ControleJogo controleJogo;
	private MesaPanel mesaPanel;
	private boolean alive = true;
	private int espera = 17;

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
				Thread.sleep(espera);
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						mesaPanel.repaint();
						if (controleJogo.getScrollPane() != null
								&& controleJogo.getScrollPane()
										.getViewport() != null
								&& controleJogo.getNovoPontoTela() != null) {
							List linha = GeoUtil.drawBresenhamLine(
									controleJogo.getScrollPane().getViewport()
											.getViewPosition(),
									controleJogo.getNovoPontoTela());
							if (linha.isEmpty()) {
								controleJogo.getScrollPane().getViewport()
										.setViewPosition(controleJogo
												.getNovoPontoTela());
							} else {
								Point point = (Point) linha
										.get(linha.size() - 1);
								controleJogo.getScrollPane().getViewport()
										.setViewPosition(point);
							}
						}
					}
				});

				controleJogo.setVelhoPontoTela(controleJogo.getNovoPontoTela());
				if (controleJogo.isAnimando() || controleJogo.isProcessando()) {
					controleJogo.setPontoPasando(
							controleJogo.getBola().getCentro());
				}

				if (!controleJogo.isAnimando()
						&& controleJogo.getPontoArrastando() != null
						&& mesaPanel.zoom == mesaPanel.mouseZoom
						&& (System.currentTimeMillis()
								- mesaPanel.lastZoomChange) > 100
						&& !(controleJogo.getPontoPasandoZoom() != null
								&& controleJogo.miniViewPort().contains(
										controleJogo.getPontoPasandoZoom()))) {
					controleJogo
							.centralizaPonto(controleJogo.getPontoArrastando());
					controleJogo.setPontoArrastando(null);
				}
			} catch (Exception e) {
				Logger.logarExept(e);
			}
		}
	}
}
