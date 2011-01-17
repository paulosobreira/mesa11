package br.mesa11;

import javax.swing.JApplet;

import br.nnpe.Logger;

public class MesaApplet extends JApplet {

	public void init() {
		initComponents();
	}

	private void initComponents() {
		try {
			final MainFrame frame = new MainFrame(this, null);
		} catch (Exception e) {
			Logger.logarExept(e);
		}
	}

}
