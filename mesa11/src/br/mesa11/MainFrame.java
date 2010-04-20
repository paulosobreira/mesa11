package br.mesa11;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import br.applet.Mesa11Applet;
import br.hibernate.Usuario;
import br.mesa11.conceito.ControleJogo;
import br.recursos.Lang;

public class MainFrame {

	private JFrame frame;
	private ControleJogo controleJogo;

	public MainFrame(Mesa11Applet mesa11Applet, Usuario usuario) {
		frame = new JFrame("mesa11");
		gerarMenus();
		controleJogo = new ControleJogo(frame);
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

		gerarMenusJogoLivre(menuJogo);
	}

	private void gerarMenusJogoLivre(JMenu menuJogo) {
		JMenuItem iniciarLivre = new JMenuItem() {
			public String getText() {
				return Lang.msg("iniciarJogoLivre");
			}

		};
		iniciarLivre.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				controleJogo.iniciaJogoLivre();
			}
		});
		menuJogo.add(iniciarLivre);
		JMenuItem bolaPenaltiCima = new JMenuItem() {
			public String getText() {
				return Lang.msg("bolaPenaltiCima");
			}

		};
		bolaPenaltiCima.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				controleJogo.bolaPenaltiCima();
			}
		});
		menuJogo.add(bolaPenaltiCima);
		JMenuItem bolaPenaltiBaixo = new JMenuItem() {
			public String getText() {
				return Lang.msg("bolaPenaltiBaixo");
			}

		};
		bolaPenaltiBaixo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				controleJogo.bolaPenaltiBaixo();
			}
		});
		menuJogo.add(bolaPenaltiBaixo);
		JMenuItem bolaCentro = new JMenuItem() {
			public String getText() {
				return Lang.msg("bolaCentro");
			}

		};
		bolaCentro.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				controleJogo.bolaCentro();
			}
		});
		menuJogo.add(bolaCentro);
	}

	public static void main(String[] args) {
		MainFrame frame = new MainFrame(null, null);
	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
}
