package br.nnpe;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import br.mesa11.conceito.ControleJogo;

public class PopupListener extends MouseAdapter {
	private JPopupMenu popup;
	private ControleJogo controleJogo;

	public PopupListener(JPopupMenu popupMenu, ControleJogo controleJogo) {
		this.popup = popupMenu;
		this.controleJogo = controleJogo;
	}

	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

}
