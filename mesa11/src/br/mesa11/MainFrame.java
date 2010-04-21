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
		JMenuItem escCimaDir = new JMenuItem() {
			public String getText() {
				return Lang.msg("escCimaDir");
			}

		};
		escCimaDir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				controleJogo.escCimaDir();
			}
		});
		menuJogo.add(escCimaDir);

		JMenuItem escCimaEsc = new JMenuItem() {
			public String getText() {
				return Lang.msg("escCimaEsc");
			}

		};
		escCimaEsc.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				controleJogo.escCimaEsc();
			}
		});
		menuJogo.add(escCimaEsc);

		JMenuItem escBaixoDir = new JMenuItem() {
			public String getText() {
				return Lang.msg("escBaixoDir");
			}

		};
		escBaixoDir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				controleJogo.escBaixoDir();
			}
		});
		menuJogo.add(escBaixoDir);

		JMenuItem escBaixoEsc = new JMenuItem() {
			public String getText() {
				return Lang.msg("escBaixoEsc");
			}

		};
		escBaixoEsc.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				controleJogo.escBaixoEsc();
			}
		});
		menuJogo.add(escBaixoEsc);

		JMenuItem metaCima = new JMenuItem() {
			public String getText() {
				return Lang.msg("metaCima");
			}

		};
		metaCima.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				controleJogo.metaCima();
			}
		});
		menuJogo.add(metaCima);

		JMenuItem metaBaixo = new JMenuItem() {
			public String getText() {
				return Lang.msg("metaBaixo");
			}

		};
		metaBaixo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				controleJogo.metaBaixo();
			}
		});
		menuJogo.add(metaBaixo);

		JMenuItem lateral = new JMenuItem() {
			public String getText() {
				return Lang.msg("lateral");
			}

		};
		lateral.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				controleJogo.lateral();
			}
		});
		menuJogo.add(lateral);

	}

	public static void main(String[] args) {
		MainFrame frame = new MainFrame(null, null);
	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
}
