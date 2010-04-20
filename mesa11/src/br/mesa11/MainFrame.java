package br.mesa11;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import br.applet.Mesa11Applet;
import br.hibernate.Usuario;
import br.mesa11.conceito.ControleJogo;
import br.recursos.Lang;

public class MainFrame {

	private JFrame frame;

	public MainFrame(Mesa11Applet mesa11Applet, Usuario usuario) {
		frame = new JFrame("mesa11");
		gerarMenus();
		ControleJogo controleJogo = new ControleJogo(frame);
	}

	private void gerarMenus() {
		JMenuBar bar = new JMenuBar();
		frame.setJMenuBar(bar);
		JMenu menuJogo = new JMenu() {
			public String getText() {
				return Lang.msg("menuJogo");
			}

		};

		bar.add(menuJogo);
	}

	public static void main(String[] args) {
		MainFrame frame = new MainFrame(null, null);
	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
}
