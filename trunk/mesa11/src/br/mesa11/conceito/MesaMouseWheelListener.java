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
		if ((System.currentTimeMillis() - lastScrool) < 30)
			return;
		double newzoom = mesaPanel.mouseZoom;
		newzoom += e.getWheelRotation() / 100.0;

		mesaPanel.mouseZoom = newzoom;
		if (mesaPanel.mouseZoom >= 1) {
			mesaPanel.mouseZoom = 1;
		}
		Rectangle rectangle = (Rectangle) controleJogo.limitesViewPort();
		if (rectangle != null
				&& mesaPanel.LARGURA_MESA * mesaPanel.mouseZoom * 1.05 < rectangle.width) {
			mesaPanel.mouseZoom += .025;
		}
		lastScrool = System.currentTimeMillis();
		controleJogo.centralizaBola();

	}

}
