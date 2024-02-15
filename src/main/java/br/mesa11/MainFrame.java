package br.mesa11;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import br.applet.Mesa11Applet;
import br.hibernate.Time;
import br.hibernate.Usuario;
import br.mesa11.conceito.ControleJogo;
import br.mesa11.visao.EditorTime;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.recursos.Lang;

public class MainFrame {

	private JFrame frame;
	private ControleJogo controleJogo;
	private String codeBase;
	private Mesa11Applet ver = new Mesa11Applet();

	public MainFrame(JApplet mesa11Applet, Usuario usuario, String codeBase) {
		this.codeBase = codeBase;
		frame = new JFrame() {
			@Override
			public String getTitle() {
				return Lang.msg("titulo") + " " + ver.getVersao();
			}
		};
		if (mesa11Applet == null) {
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		} else {

		}
		gerarMenus();
		frame.setVisible(true);
		frame.addWindowStateListener(new WindowStateListener() {

			@Override
			public void windowStateChanged(WindowEvent e) {
				if (6 == e.getNewState()) {
					Toolkit kit = frame.getToolkit();
					Dimension screenSize = kit.getScreenSize();
					int meio = (int) (screenSize.getWidth() / 2);
					frame.setLocation(meio - 500, 0);
					frame.setSize(1000, frame.getHeight() - 15);
				}
			}
		});
		iniciaJogoLivre();
	}

	private void gerarMenus() {
		JMenuBar bar = new JMenuBar();
		frame.setJMenuBar(bar);
		JMenu menuJogoLivre = new JMenu() {
			public String getText() {
				return Lang.msg("menuJogoLivre");
			}

		};

		bar.add(menuJogoLivre);
		gerarMenusJogoLivre(menuJogoLivre);

		JMenu menuDebug = new JMenu() {
			public String getText() {
				return Lang.msg("menuDebug");
			}

		};

		bar.add(menuDebug);
		gerarMenusDebug(menuDebug);

		JMenu menuEditarTime = new JMenu() {
			public String getText() {
				return Lang.msg("menuEditarTime");
			}

		};

		bar.add(menuEditarTime);
		gerarMenusEditarTime(menuEditarTime);
		JMenu info = new JMenu() {
			public String getText() {
				return Lang.msg("info");
			}

		};
		bar.add(info);
		gerarMenusSobre(info);
	}

	private void gerarMenusDebug(JMenu menuJogo) {
		JMenuItem bolaPenaltiCima = new JMenuItem() {
			public String getText() {
				return Lang.msg("bolaPenaltiCima");
			}

		};
		bolaPenaltiCima.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (controleJogo == null)
					return;
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
				if (controleJogo == null)
					return;
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
				if (controleJogo == null)
					return;
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
				if (controleJogo == null)
					return;
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
				if (controleJogo == null)
					return;
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
				if (controleJogo == null)
					return;
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
				if (controleJogo == null)
					return;
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
				if (controleJogo == null)
					return;
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
				if (controleJogo == null)
					return;
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
				if (controleJogo == null)
					return;
				controleJogo.lateral();
			}
		});
		menuJogo.add(lateral);

	}

	private void gerarMenusEditarTime(JMenu menuEditarTime) {
		JMenuItem criarTime = new JMenuItem("") {
			public String getText() {
				return Lang.msg("criarTime");
			}

		};
		criarTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Time time = new Time();
				time.setQtdePontos(Util.intervalo(100, 1000));
				if (controleJogo == null) {
					controleJogo = new ControleJogo(frame);
				}
				EditorTime editorTime = new EditorTime(time, controleJogo);
				JOptionPane.showMessageDialog(frame, editorTime);
			}
		});
		menuEditarTime.add(criarTime);
		JMenuItem carregarTime = new JMenuItem("") {
			public String getText() {
				return Lang.msg("carregarTime");
			}

		};
		carregarTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (controleJogo == null) {
					controleJogo = new ControleJogo(frame);
				}
				controleJogo.carregarTime();
			}
		});
		menuEditarTime.add(carregarTime);
	}

	private void gerarMenusSobre(JMenu menu2) {
		JMenuItem logs = new JMenuItem("Ver Logs") {
			public String getText() {
				return Lang.msg("verLogs");
			}

		};
		logs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JTextArea area = new JTextArea(20, 50);
					Set top = Logger.topExceptions.keySet();
					for (Iterator iterator = top.iterator(); iterator.hasNext();) {
						String exept = (String) iterator.next();
						area.append("Quantidade : "
								+ Logger.topExceptions.get(exept));
						area.append("\n");
						area.append(exept.replaceAll("<br>", "\n"));
						area.append("\n");
					}
					area.setCaretPosition(0);
					JOptionPane.showMessageDialog(MainFrame.this.frame,
							new JScrollPane(area), Lang.msg("listaDeErros"),
							JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception ex) {
					Logger.logarExept(ex);
				}
			}
		});
		menu2.add(logs);

		JMenuItem sobre = new JMenuItem("Sobre o autor do jogo") {
			public String getText() {
				return Lang.msg("sobreAutor");
			}

		};
		sobre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = Lang.msg("feitoPor")
						+ " Paulo Sobreira \n sowbreira@gmail.com \n"
						+ "http://sowbreira.appspot.com \n" + "2008-2010";
				JOptionPane.showMessageDialog(frame, msg, Lang
						.msg("sobreAutor"), JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menu2.add(sobre);
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
				if (controleJogo != null) {
					controleJogo.matarTodasThreads();
				}
				iniciaJogoLivre();
			}
		});
		menuJogo.add(iniciarLivre);

		JMenuItem iniciarLivreAssis = new JMenuItem() {
			public String getText() {
				return Lang.msg("iniciarJogoLivreAssis");
			}

		};
		iniciarLivreAssis.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (controleJogo != null) {
					controleJogo.matarTodasThreads();
				}
				controleJogo = new ControleJogo(frame);
				controleJogo.inicializaVideo();
				frame.setVisible(true);
				controleJogo.centroCampo();
				controleJogo.setZoom(0.4);
				controleJogo.setCodeBase(codeBase);
				controleJogo.iniciaJogoLivreAssistido();
				frame.setSize(1000, frame.getHeight() - 15);
			}
		});
		menuJogo.add(iniciarLivreAssis);

	}

	public static void main(String[] args) {
		String codeBase = File.separator + "WebContent" + File.separator;
		if (args != null && args.length > 0) {
			codeBase = args[0];
		}
		MainFrame frame = new MainFrame(null, null, codeBase);
	}

	public String getCodeBase() {
		return codeBase;
	}

	public void setCodeBase(String codeBase) {
		this.codeBase = codeBase;
	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}

	private void iniciaJogoLivre() {
		if(controleJogo!=null){
			controleJogo.matarTodasThreads();
		}
		controleJogo = new ControleJogo(frame);
		controleJogo.inicializaVideo();
		Logger.logar(codeBase);
		controleJogo.setCodeBase(codeBase);
		controleJogo.iniciaJogoLivre();
		controleJogo.centroCampo();
		controleJogo.setZoom(0.4);
	}
}
