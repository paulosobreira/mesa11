package br.mesa11.conceito;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import br.hibernate.Bola;
import br.hibernate.Botao;
import br.hibernate.Goleiro;
import br.hibernate.Time;
import br.mesa11.ConstantesMesa11;
import br.mesa11.visao.MesaPanel;
import br.nnpe.GeoUtil;
import br.nnpe.Logger;
import br.nnpe.PopupListener;
import br.nnpe.Util;
import br.recursos.CarregadorRecursos;
import br.recursos.Lang;

public class ControleJogo {
	private Map botoes = new HashMap();
	private Map botoesComThread = new HashMap();
	private Botao bola;
	private MesaPanel mesaPanel;
	private JScrollPane scrollPane;
	private Point velhoPontoTela;
	private Point novoPontoTela;
	private Point lateral;
	private JFrame frame;
	private boolean animando;
	private Botao botaoSelecionado;
	private Point pontoClicado;
	private Point pontoBtnDirClicado;
	private Point pontoPasando;
	private boolean carregaBotao;
	private boolean chutaBola;
	private boolean telaAtualizando = true;
	private int numRecursoes;

	public ControleJogo(JFrame frame) {
		this.frame = frame;
		mesaPanel = new MesaPanel(this);
		mesaPanel.setDoubleBuffered(true);
		criarPopupMenu();
		scrollPane = new JScrollPane(mesaPanel,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		adicinaListentesEventosMouse();
		adicinaListentesEventosTeclado();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosed(WindowEvent e) {
				setTelaAtualizando(false);

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});
		bola = new Bola(0);
		bola.setImagem("bola.png");
		botoes.put(bola.getId(), bola);
		Thread atualizadorTela = new Thread(new AtualizadorVisual(this));
		// atualizadorTela.setPriority(Thread.MAX_PRIORITY);
		atualizadorTela.start();

	}

	private void adicinaListentesEventosTeclado() {
		frame.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				Point p = new Point((int) (scrollPane.getViewport()
						.getViewPosition().x), (int) (scrollPane.getViewport()
						.getViewPosition().y));

				if (keycode == KeyEvent.VK_LEFT) {
					p.x -= 10;
				} else if (keycode == KeyEvent.VK_RIGHT) {
					p.x += 10;
				} else if (keycode == KeyEvent.VK_UP) {
					p.y -= 10;
				} else if (keycode == KeyEvent.VK_DOWN) {
					p.y += 10;
				}
				if (p.x < 0 || p.y < 0) {
					return;
				}
				if (p.x > ((mesaPanel.getWidth() * mesaPanel.zoom) - scrollPane
						.getViewport().getWidth())
						|| p.y > ((mesaPanel.getHeight() * mesaPanel.zoom) - (scrollPane
								.getViewport().getHeight()))) {
					return;
				}
				novoPontoTela = p;
				super.keyPressed(e);
			}

		});
	}

	private void adicinaListentesEventosMouse() {
		mesaPanel.addMouseWheelListener(new MesaMouseWheelListener(this));
		mesaPanel.addMouseMotionListener(new MesaMouseMotionListener(this));
		mesaPanel.addMouseListener(new MesaMouseListener(this));
	}

	public void test() {

		final JFrame frame = new JFrame("mesa11");
		frame.setResizable(false);
		frame.getContentPane().setLayout(new BorderLayout());
	}

	public void iniciaJogoLivre() {
		GenrenciadorBotoes controleJogoLivre = new GenrenciadorBotoes(this);
		controleJogoLivre.iniciaJogoLivre();
	}

	protected void centralizaBola() {
		if (bola == null || bola.getCentro() == null) {
			centroCampo();
			return;
		}
		Point p = new Point((int) (bola.getCentro().x * mesaPanel.zoom)
				- (scrollPane.getViewport().getWidth() / 2), (int) (bola
				.getCentro().y * mesaPanel.zoom)
				- (scrollPane.getViewport().getHeight() / 2));
		if (p.x < 0) {
			p.x = 1;
		}
		double maxX = ((mesaPanel.getWidth() * mesaPanel.zoom) - scrollPane
				.getViewport().getWidth());
		if (p.x > maxX) {
			p.x = Util.inte(maxX) - 1;
		}
		if (p.y < 0) {
			p.y = 1;
		}
		double maxY = ((mesaPanel.getHeight() * mesaPanel.zoom) - (scrollPane
				.getViewport().getHeight()));
		if (p.y > maxY) {
			p.y = Util.inte(maxY) - 1;
		}
		novoPontoTela = p;
	}

	public Shape limitesViewPort() {
		if (velhoPontoTela == null) {
			return null;
		}
		Rectangle rectangle = scrollPane.getViewport().getBounds();
		rectangle.width += 50;
		rectangle.height += 50;
		rectangle.x = velhoPontoTela.x;
		rectangle.y = velhoPontoTela.y;
		return rectangle;
	}

	public void centroCampo() {

		Point p = new Point(
				(int) (mesaPanel.getCentro().getLocation().x * mesaPanel.zoom)
						- (scrollPane.getViewport().getWidth() / 2),
				(int) (mesaPanel.getCentro().getLocation().y * mesaPanel.zoom)
						- (scrollPane.getViewport().getHeight() / 2));
		double maxX = ((mesaPanel.getWidth() * mesaPanel.zoom) - scrollPane
				.getViewport().getWidth());
		if (p.x > maxX) {
			p.x = Util.inte(maxX) - 1;
		}
		if (p.y < 0) {
			p.y = 1;
		}
		double maxY = ((mesaPanel.getHeight() * mesaPanel.zoom) - (scrollPane
				.getViewport().getHeight()));
		if (p.y > maxY) {
			p.y = Util.inte(maxY) - 1;
		}
		novoPontoTela = p;
	}

	protected void propagaColisao(Animacao animacao, Botao causador) {
		if (numRecursoes > 22) {
			return;
		}
		numRecursoes++;
		Botao botao = animacao.getObjetoAnimacao();
		List trajetoriaBotao = animacao.getPontosAnimacao();
		Set bolaIngnora = new HashSet();
		boolean bolaBateu = false;
		for (int i = 0; i < trajetoriaBotao.size(); i++) {
			Object objTrajetoria = trajetoriaBotao.get(i);
			if (objTrajetoria instanceof Point) {
				Point point = (Point) objTrajetoria;
				/**
				 * Bola
				 */
				if (botao instanceof Bola) {
					Rectangle rectangle = new Rectangle(point.x
							- bola.getRaio(), point.y - bola.getRaio(), bola
							.getDiamentro(), bola.getDiamentro());
					boolean defesaGoleiro = defesaGoleiro(rectangle,
							bolaIngnora);
					if (!bolaBateu
							&& (mesaPanel.verificaIntersectsGol(rectangle) || defesaGoleiro)) {
						bolaBateu = true;
						double angulo = GeoUtil.calculaAngulo(point, botao
								.getDestino(), 0);
						double rebatimentoBola = trajetoriaBotao.size();
						if (defesaGoleiro) {
							angulo = GeoUtil.calculaAngulo(botao
									.getCentroInicio(), point, 90);
							angulo = 180 - angulo;
							if (!verificaDentroCampo(botao))
								rebatimentoBola *= .3;
							else {
								rebatimentoBola *= .5;
							}
						}

						while (i < trajetoriaBotao.size()) {
							trajetoriaBotao.remove(trajetoriaBotao.size() - 1);
						}
						Point destino = GeoUtil
								.calculaPonto(angulo, Util
										.inte(rebatimentoBola * 0.3), botao
										.getCentro());
						botao.setDestino(destino);
						List novaTrajetoria = GeoUtil.drawBresenhamLine(point,
								destino);
						trajetoriaBotao.addAll(novaTrajetoria);
					} else {
						bolaBateu = false;
					}
				}
				botao.setCentro(point);
				/**
				 * Colisão com botões
				 */
				for (Iterator iterator = botoes.keySet().iterator(); iterator
						.hasNext();) {
					Long id = (Long) iterator.next();
					Botao botaoAnalisado = (Botao) botoes.get(id);
					if (botao.equals(botaoAnalisado)) {
						continue;
					}
					if (causador.equals(botaoAnalisado)) {
						continue;
					}
					if (botaoAnalisado instanceof Goleiro) {
						continue;
					}
					if (!verificaDentroCampo(botaoAnalisado)) {
						continue;
					}
					if ((botao instanceof Bola && bolaIngnora
							.contains(botaoAnalisado))
							|| (botaoAnalisado instanceof Bola && bolaIngnora
									.contains(botao))) {
						continue;
					}

					List raioPonto = GeoUtil.drawBresenhamLine(point,
							botaoAnalisado.getCentro());
					if ((raioPonto.size() - (botao.getRaio())) <= (botaoAnalisado
							.getRaio())) {

						if ((botao instanceof Bola) && Math.random() > .8) {
							Logger.logar("Passou pelo jogador");
							bolaIngnora.add(botaoAnalisado);
							continue;
						}
						double angulo = GeoUtil.calculaAngulo(point,
								botaoAnalisado.getCentro(), 90);
						Point destino = null;
						double detAtingido = trajetoriaBotao.size();
						if ((botaoAnalisado instanceof Bola)) {
							Logger.logar("Botão Acerta Bola");
							detAtingido *= (1 - (i / detAtingido));
							Logger.logar("Botão Acerta Bola detAtingido="
									+ detAtingido);
						} else {
							if ((botao instanceof Bola)) {
								Logger.logar("Bola Acerta Botão");
								detAtingido *= 0.05;
							} else {
								Logger.logar("Teste");
								detAtingido *= 0.3;
							}

						}
						destino = GeoUtil.calculaPonto(angulo, Util
								.inte(detAtingido), botaoAnalisado.getCentro());
						botaoAnalisado.setDestino(destino);
						animacao = new Animacao();
						animacao.setObjetoAnimacao(botaoAnalisado);
						animacao.setPontosAnimacao(botaoAnalisado
								.getTrajetoria());
						trajetoriaBotao.set(i, animacao);
						List novaTrajetoria = new ArrayList();

						/**
						 * Rebatimento de bola em botão
						 */
						int dest = 0;
						if ((botao instanceof Bola)) {
							angulo = GeoUtil.calculaAngulo(botaoAnalisado
									.getCentro(), bola.getCentro(), 90);
							dest = Util.inte(trajetoriaBotao.size() * .2);
							Logger
									.logar("Rebatimento de bola em botão (Botao Bola) dest="
											+ dest);
						} else if ((botaoAnalisado instanceof Bola)) {
							angulo = GeoUtil.calculaAngulo(botao.getCentro(),
									botao.getDestino(), 90);
							dest = Util.inte(detAtingido * .4);
							Logger
									.logar("Rebatimento de bola em botão (BotaoAnalizado Bola) dest="
											+ dest);
						} else {
							angulo = GeoUtil.calculaAngulo(botaoAnalisado
									.getCentro(), point, 90);
							dest = Util.inte(trajetoriaBotao.size() * .1);
							Logger.logar("Botão Com Botão");
						}

						destino = GeoUtil.calculaPonto(angulo, dest, botao
								.getCentro());
						botao.setDestino(destino);
						novaTrajetoria = GeoUtil.drawBresenhamLine(point,
								destino);
						while (i + 1 < trajetoriaBotao.size()) {
							trajetoriaBotao.remove(trajetoriaBotao.size() - 1);
						}
						trajetoriaBotao.addAll(novaTrajetoria);
						propagaColisao(animacao, botao);

						break;
					}
				}
			}
		}
	}

	private boolean verificaPosseGoleiro(Botao botao) {
		if (!(botao instanceof Bola))
			return false;
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao g = (Botao) botoes.get(id);
			if (g instanceof Goleiro) {
				Goleiro goleiro = (Goleiro) g;
				if (GeoUtil.drawBresenhamLine(botao.getCentro(),
						goleiro.getCentro()).size() < goleiro.getRaio()) {
					return true;
				}
			}
		}
		return false;
	}

	boolean verificaForaDosLimites(Point point) {
		if (point.x < (mesaPanel.BORDA_CAMPO / 4)) {
			return true;
		}
		if (point.x > (mesaPanel.LARGURA_MESA - (mesaPanel.BORDA_CAMPO / 4))) {
			return true;
		}
		if (point.y < (mesaPanel.BORDA_CAMPO / 4)) {
			return true;
		}
		if (point.y > (mesaPanel.ALTURA_MESA - (mesaPanel.BORDA_CAMPO / 4))) {
			return true;
		}
		return false;
	}

	private boolean defesaGoleiro(Rectangle r, Set bolaIngnora) {
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao botao = (Botao) botoes.get(id);
			if (botao instanceof Goleiro) {
				Goleiro goleiro = (Goleiro) botao;
				if (bolaIngnora.contains(goleiro)) {
					return false;
				}
				if (goleiro.getShape(1).intersects(r)) {
					// if (Math.random() > .7) {
					// bolaIngnora.add(goleiro);
					// System.out.println("frango");
					// return false;
					// }
					Logger.logar("Goleiro Defendeu");
					return true;
				}
			}
		}
		return false;
	}

	public void centralizaBotao(Botao b) {
		Rectangle rectangle = (Rectangle) limitesViewPort();
		if (rectangle == null)
			return;

		Point ori = new Point((int) rectangle.getCenterX() - 25,
				(int) rectangle.getCenterY() - 25);
		Point des = new Point((int) (b.getCentro().x * mesaPanel.zoom),
				(int) (b.getCentro().y * mesaPanel.zoom));
		List reta = GeoUtil.drawBresenhamLine(ori, des);
		Point p = des;
		if (!reta.isEmpty()) {
			int cont = reta.size() / 10;
			for (int i = cont; i < reta.size(); i += cont) {
				p = (Point) reta.get(i);
				if (rectangle.contains(p)) {
					p.x -= ((rectangle.width - 50) / 2);
					p.y -= ((rectangle.height - 50) / 2);
					break;
				}
			}
		}
		if (p.x < 0) {
			p.x = 1;
		}
		if (p.y < 0) {
			p.y = 1;
		}
		int largMax = (int) ((mesaPanel.getWidth() * mesaPanel.zoom) - scrollPane
				.getViewport().getWidth());
		if (p.x > largMax) {
			p.x = largMax - 1;
		}
		int altMax = (int) ((mesaPanel.getHeight() * mesaPanel.zoom) - (scrollPane
				.getViewport().getHeight()));
		if (p.y > altMax) {
			p.y = altMax - 1;
		}
		novoPontoTela = p;
	}

	public Botao getBola() {
		return (Botao) botoes.get(new Long(0));
	}

	public void bolaPenaltiCima() {
		if (bola == null || mesaPanel == null) {
			return;
		}
		bola.setPosition(mesaPanel.getPenaltyCima().getLocation());
		centralizaBola();

	}

	public void bolaPenaltiBaixo() {
		if (bola == null || mesaPanel == null) {
			return;
		}
		bola.setPosition(mesaPanel.getPenaltyBaixo().getLocation());
		centralizaBola();
	}

	public void bolaCentro() {
		if (bola == null || mesaPanel == null) {
			return;
		}
		bola.setPosition(mesaPanel.getCentro().getLocation());
		centralizaBola();
	}

	public void escCimaDir() {
		if (bola == null || mesaPanel == null) {
			return;
		}
		Point p = mesaPanel.getCampoCima().getLocation();
		p.x += mesaPanel.getCampoCima().getWidth() - bola.getDiamentro();
		bola.setPosition(p);
		centralizaBola();
	}

	public void escCimaEsc() {
		if (bola == null || mesaPanel == null) {
			return;
		}
		bola.setPosition(mesaPanel.getCampoCima().getLocation());
		centralizaBola();
	}

	public void escBaixoDir() {
		if (bola == null || mesaPanel == null) {
			return;
		}
		Point p = mesaPanel.getCampoBaixo().getLocation();
		p.x += mesaPanel.getCampoBaixo().getWidth() - bola.getDiamentro();
		p.y += mesaPanel.getCampoBaixo().getHeight() - bola.getDiamentro();
		bola.setPosition(p);
		centralizaBola();

	}

	public void escBaixoEsc() {
		if (bola == null || mesaPanel == null) {
			return;
		}
		Point p = mesaPanel.getCampoBaixo().getLocation();
		p.y += mesaPanel.getCampoBaixo().getHeight() - bola.getDiamentro();
		bola.setPosition(p);
		centralizaBola();
	}

	public void metaCima() {
		if (bola == null || mesaPanel == null) {
			return;
		}
		Point p = mesaPanel.getPequenaAreaCima().getLocation();
		if (Math.random() > .5)
			p.x += mesaPanel.getPequenaAreaCima().getWidth()
					- bola.getDiamentro();
		p.y += mesaPanel.getPequenaAreaCima().getHeight() - bola.getDiamentro();
		bola.setPosition(p);
		centralizaBola();

	}

	public void metaBaixo() {
		if (bola == null || mesaPanel == null) {
			return;
		}
		Point p = mesaPanel.getPequenaAreaBaixo().getLocation();
		if (Math.random() > .5)
			p.x += mesaPanel.getPequenaAreaBaixo().getWidth()
					- bola.getDiamentro();

		bola.setPosition(p);
		centralizaBola();

	}

	public void lateral() {
		if (bola == null || mesaPanel == null) {
			return;
		}
		if (lateral != null) {
			bola.setCentroInicio(lateral);
			bola.setCentro(lateral);
			centralizaBola();
		}

	}

	public Point getLateral() {
		return lateral;
	}

	public void setLateral(Point lateral) {
		if (lateral != null) {
			if (lateral.x < 3000) {
				lateral.x += 20;
			} else {
				lateral.x -= 20;
			}
		}
		this.lateral = lateral;
	}

	public void criarPopupMenu() {

		JPopupMenu popup = new JPopupMenu();
		JMenuItem moverBotao = new JMenuItem() {
			public String getText() {
				return Lang.msg("moverBotao");
			}
		};
		popup.add(moverBotao);
		moverBotao.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				moverBotao();
			}
		});
		JMenuItem soltarBotao = new JMenuItem() {
			public String getText() {
				return Lang.msg("soltarBotao");
			}
		};
		popup.add(soltarBotao);
		soltarBotao.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				soltarBotao();
			}
		});

		JMenuItem chutarBola = new JMenuItem() {
			public String getText() {
				return Lang.msg("chutarBola", new String[] { (chutaBola ? Lang
						.msg("sim") : Lang.msg("nao")) });
			}
		};
		popup.add(chutarBola);
		chutarBola.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chutarBola();
			}
		});

		JMenuItem limparPerimetro = new JMenuItem() {
			public String getText() {
				return Lang.msg("limparPerimetro");
			}
		};
		popup.add(limparPerimetro);
		limparPerimetro.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				limparPerimetro(pontoBtnDirClicado);
			}
		});

		JMenuItem limparPerimetroCirculo = new JMenuItem() {
			public String getText() {
				return Lang.msg("limparPerimetroCirculo");
			}
		};
		popup.add(limparPerimetroCirculo);
		limparPerimetroCirculo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				limparPerimetroCirculo(pontoBtnDirClicado);
			}
		});

		MouseListener popupListener = new PopupListener(popup, this);
		mesaPanel.addMouseListener(popupListener);
	}

	protected void limparPerimetro(Point p) {
		if (p == null) {
			return;
		}
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao b = (Botao) botoes.get(id);
			if (b instanceof Bola || b instanceof Goleiro) {
				continue;
			}
			List reta = GeoUtil.drawBresenhamLine(p, b.getCentro());
			if (reta.size() < ConstantesMesa11.PERIMETRO) {
				posicionaBotaoAleatoriamenteNoSeuCampo(b);
			}
		}
	}

	protected void limparPerimetroCirculo(Point p) {
		if (p == null) {
			return;
		}
		List circulo = GeoUtil.drawCircle(p.x, p.y, ConstantesMesa11.PERIMETRO);
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao b = (Botao) botoes.get(id);
			if (b instanceof Bola || b instanceof Goleiro) {
				continue;
			}
			List reta = GeoUtil.drawBresenhamLine(p, b.getCentro());
			if (reta.size() < ConstantesMesa11.PERIMETRO) {
				boolean botaoPosicionado = false;
				int cont = 0;
				while (!botaoPosicionado) {
					cont++;
					if (cont > 100) {
						Logger
								.logar("limparPerimetroCirculo - posicionaBotaoAleatoriamenteNoSeuCampo");
						posicionaBotaoAleatoriamenteNoSeuCampo(b);
						botaoPosicionado = true;
					}

					Point point = (Point) circulo.get(Util.intervalo(0, circulo
							.size() - 1));
					if (!mesaPanel.getCampoBaixo().contains(point)
							&& !mesaPanel.getCampoCima().contains(point)) {
						continue;
					}
					if (verificaTemBotao(point)) {
						continue;
					}

					b.setCentroTodos(point);
					botaoPosicionado = true;
				}
			}
		}
	}

	private void posicionaBotaoAleatoriamenteNoSeuCampo(Botao b) {
		boolean botaoPosicionado = false;

		while (!botaoPosicionado) {
			if (ConstantesMesa11.CAMPO_CIMA == b.getTime().getCampo()) {

				int valx = Util.intervalo(mesaPanel.getCampoCima().x, mesaPanel
						.getCampoCima().x
						+ mesaPanel.getCampoCima().width);
				int valy = Util.intervalo(mesaPanel.getCampoCima().y, mesaPanel
						.getCampoCima().y
						+ mesaPanel.getCampoCima().height);
				Point point = new Point(valx, valy);
				if (verificaTemBotao(point)) {
					continue;
				}
				b.setCentroTodos(point);
				botaoPosicionado = true;
			} else {
				int valx = Util.intervalo(mesaPanel.getCampoBaixo().x,
						mesaPanel.getCampoBaixo().x
								+ mesaPanel.getCampoCima().width);
				int valy = Util.intervalo(mesaPanel.getCampoBaixo().y,
						mesaPanel.getCampoBaixo().y
								+ mesaPanel.getCampoCima().height);
				Point point = new Point(valx, valy);
				if (verificaTemBotao(point)) {
					continue;
				}
				b.setCentroTodos(point);
				botaoPosicionado = true;
			}
		}

	}

	public boolean verificaTemBotao(Point p) {
		return verificaTemBotao(p, null);
	}

	public boolean verificaTemBotao(Point p, Botao exceto) {
		Rectangle2D rectangle2d = new Rectangle2D.Double(p.x
				- ConstantesMesa11.RAIO_BOTAO, p.y
				- ConstantesMesa11.RAIO_BOTAO,
				ConstantesMesa11.DIAMENTRO_BOTAO,
				ConstantesMesa11.DIAMENTRO_BOTAO);
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao b = (Botao) botoes.get(id);
			if (exceto != null && exceto.equals(b)) {
				continue;
			}
			if (b.getShape(1).intersects(rectangle2d)) {
				return true;
			}
		}
		return false;
	}

	protected void soltarBotao() {
		if (botaoSelecionado != null) {
			if (verificaTemBotao(botaoSelecionado.getCentro(), botaoSelecionado)) {
				return;
			}
			botaoSelecionado.setCentroInicio(botaoSelecionado.getCentro());
			botaoSelecionado = null;
			carregaBotao = false;
		}

	}

	public boolean verificaDentroCampo(Botao botao) {
		if (mesaPanel == null)
			return true;
		return (mesaPanel.getCampoBaixo().intersects(
				botao.getShape(1).getBounds2D()) || mesaPanel.getCampoCima()
				.intersects(botao.getShape(1).getBounds2D()));
	}

	protected void chutarBola() {
		chutaBola = !chutaBola;

	}

	protected void moverBotao() {
		carregaBotao = true;
	}

	public MesaPanel getMesaPanel() {
		return mesaPanel;

	}

	public Point getPontoClicado() {
		return pontoClicado;
	}

	public void setPontoClicado(Point pontoClicado) {
		this.pontoClicado = pontoClicado;
	}

	public Botao getBotaoSelecionado() {
		return botaoSelecionado;
	}

	public void setBotaoSelecionado(Botao botaoSelecionado) {
		this.botaoSelecionado = botaoSelecionado;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public boolean isCarregaBotao() {
		return carregaBotao;
	}

	public void setCarregaBotao(boolean carregaBotao) {
		this.carregaBotao = carregaBotao;
	}

	public boolean isChutaBola() {
		return chutaBola;
	}

	public void setChutaBola(boolean chutaBola) {
		this.chutaBola = chutaBola;
	}

	public boolean isAnimando() {
		return animando;
	}

	public void setAnimando(boolean animando) {
		this.animando = animando;
	}

	public Map getBotoes() {
		return botoes;
	}

	public Point getVelhoPontoTela() {
		return velhoPontoTela;
	}

	public void setVelhoPontoTela(Point velhoPontoTela) {
		this.velhoPontoTela = velhoPontoTela;
	}

	public Point getNovoPontoTela() {
		return novoPontoTela;
	}

	public void setNovoPontoTela(Point novoPontoTela) {
		this.novoPontoTela = novoPontoTela;
	}

	public boolean isTelaAtualizando() {
		return telaAtualizando;
	}

	public void setTelaAtualizando(boolean telaAtualizando) {
		this.telaAtualizando = telaAtualizando;
	}

	public Map getBotoesComThread() {
		return botoesComThread;
	}

	public void setBotoesComThread(Map botoesComThread) {
		this.botoesComThread = botoesComThread;
	}

	public Point getPontoPasando() {
		return pontoPasando;
	}

	public void setPontoPasando(Point pontoPasando) {
		this.pontoPasando = pontoPasando;
	}

	public void setZoom(double d) {
		mesaPanel.zoom = d;
		centralizaBola();

	}

	public int getNumRecursoes() {
		return numRecursoes;
	}

	public void setNumRecursoes(int numRecursoes) {
		this.numRecursoes = numRecursoes;
	}

	public JFrame getFrame() {
		return frame;
	}

	public Point getPontoBtnDirClicado() {
		return pontoBtnDirClicado;
	}

	public void setPontoBtnDirClicado(Point pontoBtnDirClicado) {
		this.pontoBtnDirClicado = pontoBtnDirClicado;
	}

}
