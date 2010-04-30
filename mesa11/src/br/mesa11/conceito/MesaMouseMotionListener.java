package br.mesa11.conceito;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.List;

import br.mesa11.visao.MesaPanel;

public class MesaMouseMotionListener implements MouseMotionListener {
	private ControleJogo controleJogo;
	private MesaPanel mesaPanel;
	private List jogada;

	public MesaMouseMotionListener(ControleJogo controleJogo) {
		this.controleJogo = controleJogo;
		this.mesaPanel = controleJogo.getMesaPanel();
		this.jogada = controleJogo.getJogada();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point p = new Point((int) (e.getPoint().x / mesaPanel.zoom), (int) (e
				.getPoint().y / mesaPanel.zoom));
		if (controleJogo.getBotaoSelecionado() != null
				&& controleJogo.isCarregaBotao()) {
			controleJogo.getBotaoSelecionado().setCentro(p);
		}

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		jogada.add(new Point((int) (e.getPoint().x / mesaPanel.zoom), (int) (e
				.getPoint().y / mesaPanel.zoom)));
	}

}
