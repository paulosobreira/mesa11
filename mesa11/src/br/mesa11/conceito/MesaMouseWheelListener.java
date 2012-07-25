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
		if (controleJogo.isAnimando()) {
			return;
		}

		double newzoom = mesaPanel.mouseZoom;
		newzoom += e.getWheelRotation() / 150.0;

		if (newzoom >= 1) {
			mesaPanel.mouseZoom = 1;
		} else if (newzoom <= 0.3) {
			mesaPanel.mouseZoom = 0.3;
		} else {
			mesaPanel.mouseZoom = newzoom;
		}
		lastScrool = System.currentTimeMillis();
		controleJogo.centralizaBola();

	}

}
