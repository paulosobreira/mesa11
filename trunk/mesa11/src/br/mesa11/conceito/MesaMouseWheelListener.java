package br.mesa11.conceito;

import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import br.mesa11.visao.MesaPanel;

public class MesaMouseWheelListener implements MouseWheelListener {
	private ControleJogo controleJogo;
	private MesaPanel mesaPanel;
	private long lastScrool = System.currentTimeMillis();

	public MesaMouseWheelListener(ControleJogo controleJogo) {
		this.controleJogo = controleJogo;
		this.mesaPanel = controleJogo.getMesaPanel();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if ((System.currentTimeMillis() - lastScrool) < 60)
			return;
		double newzoom = mesaPanel.zoom;
		newzoom += e.getWheelRotation() / 100.0;

		mesaPanel.zoom = newzoom;
		if (mesaPanel.zoom <= 0.3) {
			mesaPanel.zoom = 0.3;
		}
		if (mesaPanel.zoom >= 1) {
			mesaPanel.zoom = 1;
		}
		Rectangle rectangle = (Rectangle) controleJogo.limitesViewPort();
		if (rectangle != null
				&& mesaPanel.LARGURA_MESA * mesaPanel.zoom < rectangle.width) {
			mesaPanel.zoom += .1;
		}
		lastScrool = System.currentTimeMillis();
		controleJogo.centralizaBola();

	}

}
