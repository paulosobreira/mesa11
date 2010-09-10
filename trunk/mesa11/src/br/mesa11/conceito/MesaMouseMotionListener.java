package br.mesa11.conceito;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.List;

import br.hibernate.Goleiro;
import br.mesa11.visao.MesaPanel;

public class MesaMouseMotionListener implements MouseMotionListener {
	private ControleJogo controleJogo;
	private MesaPanel mesaPanel;

	public MesaMouseMotionListener(ControleJogo controleJogo) {
		this.controleJogo = controleJogo;
		this.mesaPanel = controleJogo.getMesaPanel();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point p = new Point((int) (e.getPoint().x / mesaPanel.zoom), (int) (e
				.getPoint().y / mesaPanel.zoom));
		if (controleJogo.getBotaoSelecionado() != null
				&& controleJogo.isCarregaBotao()) {
			controleJogo.getBotaoSelecionado().setCentro(p);
		}
		controleJogo.setPontoPasando(p);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point p = new Point((int) (e.getPoint().x / mesaPanel.zoom), (int) (e
				.getPoint().y / mesaPanel.zoom));
		controleJogo.setPontoPasando(p);
	}

}
