package br.mesa11.conceito;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import br.hibernate.Bola;
import br.hibernate.Botao;
import br.hibernate.Goleiro;
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
	private Animacao animacao;
	private Botao bola;
	private MesaPanel mesaPanel;
	private JScrollPane scrollPane;
	private Point oldp;
	private Point newp;
	private List jogada = new LinkedList();
	private long lastScrool = System.currentTimeMillis();
	private Point lateral;
	private JFrame frame;
	private boolean animando;
	private Botao botaoSelecionado;
	private Point pontoClicado;
	private boolean carregaBotao;

	public ControleJogo(JFrame frame) {
		this.frame = frame;
		mesaPanel = new MesaPanel(this);
		criarPopupMenu();
		scrollPane = new JScrollPane(mesaPanel,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		adicinaListentesEventosMouse();
		adicinaListentesEventosTeclado();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.setSize(1024, 740);
		frame.setVisible(true);
		frame.requestFocus();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		centroCampo();
		Thread atualizadorTela = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						if (oldp != newp) {
							scrollPane.getViewport().setViewPosition(newp);
							oldp = newp;
						}
						mesaPanel.repaint();
					} catch (Exception e) {
						e.printStackTrace();
						try {
							Thread.sleep(20);
						} catch (InterruptedException e2) {

						}
					}

				}

			}
		});
		atualizadorTela.setPriority(Thread.MIN_PRIORITY);
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
				newp = p;
				super.keyPressed(e);
			}

		});
	}

	private void adicinaListentesEventosMouse() {
		mesaPanel.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if ((System.currentTimeMillis() - lastScrool) < 30)
					return;
				double newzoom = mesaPanel.zoom;
				newzoom += e.getWheelRotation() / 100.0;

				if ((mesaPanel.LARGURA_MESA * newzoom) < scrollPane
						.getViewport().getWidth()) {
					mesaPanel.zoom -= e.getWheelRotation() / 100.0;
					centralizaBola();
					return;
				}
				if ((mesaPanel.ALTURA_MESA * newzoom) < scrollPane
						.getViewport().getHeight()) {
					mesaPanel.zoom -= e.getWheelRotation() / 100.0;
					centralizaBola();
					return;
				}

				mesaPanel.zoom = newzoom;
				if (mesaPanel.zoom <= 0.3) {
					mesaPanel.zoom = 0.3;
				}
				if (mesaPanel.zoom >= 1) {
					mesaPanel.zoom = 1;
				}
				centralizaBola();
				lastScrool = System.currentTimeMillis();
			}
		});
		mesaPanel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				Point p = new Point((int) (e.getPoint().x / mesaPanel.zoom),
						(int) (e.getPoint().y / mesaPanel.zoom));
				if (botaoSelecionado != null && carregaBotao) {
					botaoSelecionado.setCentro(p);
				}

			}

			@Override
			public void mouseDragged(MouseEvent e) {
				jogada.add(new Point((int) (e.getPoint().x / mesaPanel.zoom),
						(int) (e.getPoint().y / mesaPanel.zoom)));
			}

		});
		mesaPanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (jogada.isEmpty() || animando)
					return;
				Point p1 = (Point) jogada.get(0);
				Point p2 = (Point) jogada.get(jogada.size() - 1);
				List reta = GeoUtil.drawBresenhamLine(p1, p2);
				jogada.clear();
				jogada.addAll(reta);
				for (Iterator iterator = botoes.keySet().iterator(); iterator
						.hasNext();) {
					Long id = (Long) iterator.next();
					Botao botao = (Botao) botoes.get(id);
					if (botao instanceof Goleiro) {
						Goleiro goleiro = (Goleiro) botao;
						if (goleiro.getRetangulo(1).contains(p1)) {
							List retaGoleiro = GeoUtil.drawBresenhamLine(
									goleiro.getCentro(), p1);
							if (retaGoleiro.size() > (goleiro.getRaio() / 2)) {
								goleiro.setRotacao(GeoUtil.calculaAngulo(
										goleiro.getCentro(), p2, 0));
							} else {
								goleiro.setCentro(p2);
							}
							return;
						}
					}
					List raioPonto = GeoUtil.drawBresenhamLine(p1, botao
							.getCentro());
					if (raioPonto.size() <= botao.getRaio()) {
						// if (botao instanceof Bola) {
						// return;
						// }
						setLateral(null);
						if (botao instanceof Goleiro) {
							return;
						}
						double angulo = GeoUtil.calculaAngulo(p1, botao
								.getCentro(), 90);

						Point destino = GeoUtil.calculaPonto(angulo, Util
								.inte(reta.size() * 10), botao.getCentro());
						botao.setDestino(destino);
						animacao = new Animacao();
						animacao.setObjetoAnimacao(botao);
						animacao.setPontosAnimacao(botao.getTrajetoria());
						propagaColisao(animacao, botao);
						break;
					}

				}
				for (Iterator iterator = botoes.keySet().iterator(); iterator
						.hasNext();) {
					Long id = (Long) iterator.next();
					Botao botao = (Botao) botoes.get(id);
					if (botao.getCentroInicio() != null)
						botao.setCentro(botao.getCentroInicio());
				}
				botoesComThread.clear();
				Animador animador = new Animador(animacao, mesaPanel,
						ControleJogo.this);
				Thread thread = new Thread(animador);
				botoesComThread.put(animacao.getObjetoAnimacao(), thread);
				thread.start();
				animando = true;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				pontoClicado = new Point(
						(int) (e.getPoint().x / mesaPanel.zoom), (int) (e
								.getPoint().y / mesaPanel.zoom));
				System.out.println("pontoClicado" + pontoClicado);
				for (Iterator iterator = botoes.keySet().iterator(); iterator
						.hasNext();) {
					Long id = (Long) iterator.next();
					Botao botao = (Botao) botoes.get(id);
					if (botao instanceof Bola || botao instanceof Goleiro) {
						continue;
					}
					List raioPonto = GeoUtil.drawBresenhamLine(pontoClicado,
							botao.getCentro());
					if (raioPonto.size() <= botao.getRaio()) {
						botaoSelecionado = botao;
						System.out.println("botaoSelecionado"
								+ botaoSelecionado);
						break;
					}
				}
				jogada.clear();

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	public boolean isAnimando() {
		return animando;
	}

	public void setAnimando(boolean animando) {
		this.animando = animando;
	}

	public void test() {

		final JFrame frame = new JFrame("mesa11");
		frame.setResizable(false);
		frame.getContentPane().setLayout(new BorderLayout());
	}

	public void iniciaJogoLivre() {
		Long cima[] = new Long[10];
		for (int i = 0; i < 10; i++) {
			cima[i] = new Long(i + 1);
		}
		desenhaTimeCima("azul.png", cima);

		Long baixo[] = new Long[10];
		for (int i = 0; i < 10; i++) {
			baixo[i] = new Long(i + 11);
		}
		desenhaTimeBaixo("vermelho.png", baixo);

		Goleiro goleiro1 = new Goleiro(100);
		goleiro1.setCentro(mesaPanel.golCima());
		Goleiro goleiro2 = new Goleiro(200);
		goleiro2.setCentro(mesaPanel.golBaixo());

		bola = new Bola(0);
		bola.setImagem("bola.png");
		botoes.put(bola.getId(), bola);
		botoes.put(goleiro1.getId(), goleiro1);
		botoes.put(goleiro2.getId(), goleiro2);
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao b = (Botao) botoes.get(id);
			if (b instanceof Goleiro) {
				continue;
			}
			b.setImgBotao(CarregadorRecursos.carregaImg(b.getImagem()));
		}
		bolaCentro();
	}

	private void desenhaTimeCima(String imagem, Long ids[]) {
		int distHCima = (int) (mesaPanel.getCampoCima().getWidth() / 5);
		int distVCima = (int) (mesaPanel.getCampoCima().getHeight() / 2);
		int xpenal = mesaPanel.getPenaltyCima().x;

		Botao botao1 = new Botao(ids[0]);
		botao1.setCentro(new Point(xpenal, distVCima));
		botao1.setImagem(imagem);
		botoes.put(botao1.getId(), botao1);

		Botao botao2 = new Botao(ids[1]);
		botao2.setCentro(new Point(xpenal - (2 * distHCima), Util
				.inte(distVCima * 1.5)));
		botao2.setImagem(imagem);
		botoes.put(botao2.getId(), botao2);

		Botao botao3 = new Botao(ids[2]);
		botao3.setCentro(new Point(xpenal - (distHCima), Util
				.inte(distVCima * 1.5)));
		botao3.setImagem(imagem);
		botoes.put(botao3.getId(), botao3);

		Botao botao4 = new Botao(ids[3]);
		botao4.setCentro(new Point(xpenal + (2 * distHCima), Util
				.inte(distVCima * 1.5)));
		botao4.setImagem(imagem);
		botoes.put(botao4.getId(), botao4);

		Botao botao5 = new Botao(ids[4]);
		botao5.setCentro(new Point(xpenal + (distHCima), Util
				.inte(distVCima * 1.5)));
		botao5.setImagem(imagem);
		botoes.put(botao5.getId(), botao5);

		Botao botao6 = new Botao(ids[5]);
		botao6.setCentro(new Point(xpenal, Util.inte(distVCima * 1.5)));
		botao6.setImagem(imagem);
		botoes.put(botao6.getId(), botao6);

		Botao botao7 = new Botao(ids[6]);
		botao7.setCentro(new Point(xpenal - (2 * distHCima), Util
				.inte(distVCima * 2)));
		botao7.setImagem(imagem);
		botoes.put(botao7.getId(), botao7);

		Botao botao8 = new Botao(ids[7]);
		botao8.setCentro(new Point(xpenal - (distHCima), Util
				.inte(distVCima * 2)));
		botao8.setImagem(imagem);
		botoes.put(botao8.getId(), botao8);

		Botao botao9 = new Botao(ids[8]);
		botao9.setCentro(new Point(xpenal + (2 * distHCima), Util
				.inte(distVCima * 2)));
		botao9.setImagem(imagem);
		botoes.put(botao9.getId(), botao9);

		Botao botao10 = new Botao(ids[9]);
		botao10.setCentro(new Point(xpenal + (distHCima), Util
				.inte(distVCima * 2)));
		botao10.setImagem(imagem);
		botoes.put(botao10.getId(), botao10);
	}

	private void desenhaTimeBaixo(String imagem, Long ids[]) {
		int distHBaixo = (int) (mesaPanel.getCampoCima().getWidth() / 5);
		int distVBaixo = (int) (mesaPanel.getCampoCima().getHeight() / 2);
		int y = mesaPanel.golBaixo().y + 200;
		int xpenal = mesaPanel.getPenaltyCima().x;

		Botao botao1 = new Botao(ids[0]);
		botao1.setCentro(new Point(xpenal, y - distVBaixo));
		botao1.setImagem(imagem);
		botoes.put(botao1.getId(), botao1);

		Botao botao2 = new Botao(ids[1]);
		botao2.setCentro(new Point(xpenal - (2 * distHBaixo), y
				- Util.inte(distVBaixo * 1.5)));
		botao2.setImagem(imagem);
		botoes.put(botao2.getId(), botao2);

		Botao botao3 = new Botao(ids[2]);
		botao3.setCentro(new Point(xpenal - (distHBaixo), y
				- Util.inte(distVBaixo * 1.5)));
		botao3.setImagem(imagem);
		botoes.put(botao3.getId(), botao3);

		Botao botao4 = new Botao(ids[3]);
		botao4.setCentro(new Point(xpenal + (2 * distHBaixo), y
				- Util.inte(distVBaixo * 1.5)));
		botao4.setImagem(imagem);
		botoes.put(botao4.getId(), botao4);

		Botao botao5 = new Botao(ids[4]);
		botao5.setCentro(new Point(xpenal + (distHBaixo), y
				- Util.inte(distVBaixo * 1.5)));
		botao5.setImagem(imagem);
		botoes.put(botao5.getId(), botao5);

		Botao botao6 = new Botao(ids[5]);
		botao6.setCentro(new Point(xpenal, y - Util.inte(distVBaixo * 1.5)));
		botao6.setImagem(imagem);
		botoes.put(botao6.getId(), botao6);

		Botao botao7 = new Botao(ids[6]);
		botao7.setCentro(new Point(xpenal - (2 * distHBaixo), y
				- Util.inte(distVBaixo * 2)));
		botao7.setImagem(imagem);
		botoes.put(botao7.getId(), botao7);

		Botao botao8 = new Botao(ids[7]);
		botao8.setCentro(new Point(xpenal - (distHBaixo), y
				- Util.inte(distVBaixo * 2)));
		botao8.setImagem(imagem);
		botoes.put(botao8.getId(), botao8);

		Botao botao9 = new Botao(ids[8]);
		botao9.setCentro(new Point(xpenal + (2 * distHBaixo), y
				- Util.inte(distVBaixo * 2)));
		botao9.setImagem(imagem);
		botoes.put(botao9.getId(), botao9);

		Botao botao10 = new Botao(ids[9]);
		botao10.setCentro(new Point(xpenal + (distHBaixo), y
				- Util.inte(distVBaixo * 2)));
		botao10.setImagem(imagem);
		botoes.put(botao10.getId(), botao10);
	}

	public Map getBotoesComThread() {
		return botoesComThread;
	}

	public void setBotoesComThread(Map botoesComThread) {
		this.botoesComThread = botoesComThread;
	}

	protected void centralizaBola() {
		if (bola == null) {
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
		newp = p;
	}

	protected void centroCampo() {

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
		newp = p;
	}

	protected void propagaColisao(Animacao animacao, Botao causador) {
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
					if (!bolaBateu
							&& (mesaPanel.verificaIntersectsGol(rectangle) || defesaGoleiro(
									rectangle, bolaIngnora))) {
						bolaBateu = true;
						double angulo = GeoUtil.calculaAngulo(point, botao
								.getDestino(), 0);
						while (i < trajetoriaBotao.size()) {
							trajetoriaBotao.remove(trajetoriaBotao.size() - 1);
						}
						Point destino = GeoUtil.calculaPonto(angulo, Util
								.inte(trajetoriaBotao.size() * 0.2), botao
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
					if ((botao instanceof Bola)
							&& bolaIngnora.contains(botaoAnalisado)) {
						continue;
					}
					if (botaoAnalisado instanceof Goleiro) {
						continue;
					}
					List raioPonto = GeoUtil.drawBresenhamLine(point,
							botaoAnalisado.getCentro());
					if ((raioPonto.size() - (botao.getRaio())) == (botaoAnalisado
							.getRaio())) {

						if ((botao instanceof Bola) && Math.random() > .5) {
							bolaIngnora.add(botaoAnalisado);
							continue;
						}
						double angulo = GeoUtil.calculaAngulo(point,
								botaoAnalisado.getCentro(), 90);
						Point destino = null;
						if ((botaoAnalisado instanceof Bola)) {
							destino = GeoUtil.calculaPonto(angulo, Util
									.inte(trajetoriaBotao.size() * .7),
									botaoAnalisado.getCentro());
						} else {
							double per = 0.05;
							if (botaoAnalisado instanceof Bola) {
								Logger.logar("Botão Acerta Bola");
								per = 0.3;
							}
							if ((botao instanceof Bola)) {
								Logger.logar("Bola Acerta Botão");
								per = 0.02;
							}
							destino = GeoUtil.calculaPonto(angulo, Util
									.inte(trajetoriaBotao.size() * per),
									botaoAnalisado.getCentro());
						}

						botaoAnalisado.setDestino(destino);
						animacao = new Animacao();
						animacao.setObjetoAnimacao(botaoAnalisado);
						animacao.setPontosAnimacao(botaoAnalisado
								.getTrajetoria());
						trajetoriaBotao.set(i, animacao);
						List novaTrajetoria = new ArrayList();

						int dest = 0;
						if ((botao instanceof Bola)) {
							angulo = GeoUtil.calculaAngulo(botaoAnalisado
									.getCentro(), point, 0);
							dest = trajetoriaBotao.size();
						} else if ((botaoAnalisado instanceof Bola)) {
							angulo = GeoUtil.calculaAngulo(botao.getCentro(),
									botao.getDestino(), 90);
							dest = Util.inte(trajetoriaBotao.size() * .3);
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

	boolean verificaForaDosLimites(Point point) {
		if (point.x < (mesaPanel.BORDA_CAMPO / 2)) {
			return true;
		}
		if (point.x > (mesaPanel.LARGURA_MESA - (mesaPanel.BORDA_CAMPO / 2))) {
			return true;
		}
		if (point.y < (mesaPanel.BORDA_CAMPO / 2)) {
			return true;
		}
		if (point.y > (mesaPanel.ALTURA_MESA - (mesaPanel.BORDA_CAMPO / 2))) {
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
				if (goleiro.getRetangulo(1).intersects(r)) {
					if (Math.random() > .7) {
						bolaIngnora.add(goleiro);
						System.out.println("frango");
						return false;
					}
					return true;
				}
			}
		}
		return false;
	}

	public void centralizaBotao(Botao b) {

		Point ori = new Point(
				(int) (scrollPane.getViewport().getViewPosition().x),
				(int) (scrollPane.getViewport().getViewPosition().y));
		Point des = new Point((int) (b.getCentro().x * mesaPanel.zoom)
				- (scrollPane.getViewport().getWidth() / 2), (int) (b
				.getCentro().y * mesaPanel.zoom)
				- (scrollPane.getViewport().getHeight() / 2));
		List reta = GeoUtil.drawBresenhamLine(ori, des);
		Point p = des;
		if (!reta.isEmpty()) {
			for (int i = 20; i > 0; i--) {
				if (reta.size() > i) {
					p = (Point) reta.get(i);
					break;
				}
			}

		}
		if (!((p.x < 0 || p.y < 0) || (p.x > ((mesaPanel.getWidth() * mesaPanel.zoom) - scrollPane
				.getViewport().getWidth()) || p.y > ((mesaPanel.getHeight() * mesaPanel.zoom) - (scrollPane
				.getViewport().getHeight()))))) {
			newp = p;
		}
	}

	protected void centralizaBotao2(Botao b) {
		System.out.println("centralizaBotao2");
		Point ori = new Point(
				(int) (scrollPane.getViewport().getViewPosition().x),
				(int) (scrollPane.getViewport().getViewPosition().y));
		Point des = new Point((int) (b.getCentro().x * mesaPanel.zoom)
				- (scrollPane.getViewport().getWidth() / 2), (int) (b
				.getCentro().y * mesaPanel.zoom)
				- (scrollPane.getViewport().getHeight() / 2));
		List reta = GeoUtil.drawBresenhamLine(ori, des);
		Point p = des;
		if (!reta.isEmpty()) {
			p = (Point) reta.get(reta.size() - 1);
		}
		if (!((p.x < 0 || p.y < 0) || (p.x > ((mesaPanel.getWidth() * mesaPanel.zoom) - scrollPane
				.getViewport().getWidth()) || p.y > ((mesaPanel.getHeight() * mesaPanel.zoom) - (scrollPane
				.getViewport().getHeight()))))) {
			newp = p;
		}

	}

	public Botao getBola() {
		return (Botao) botoes.get(new Long(0));
	}

	public Point getOldp() {
		return oldp;
	}

	public Point getNewp() {
		return newp;
	}

	public Map getBotoes() {
		return botoes;
	}

	public List getJogada() {
		return jogada;
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

		JMenuItem chutarGol = new JMenuItem() {
			public String getText() {
				return Lang.msg("chutarGol");
			}
		};
		popup.add(chutarGol);
		chutarGol.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chutarGol();
			}
		});
		MouseListener popupListener = new PopupListener(popup, this);
		mesaPanel.addMouseListener(popupListener);
	}

	protected void soltarBotao() {
		if (botaoSelecionado != null && pontoClicado != null) {
			botaoSelecionado.setCentro(pontoClicado);
			carregaBotao = false;
		}

	}

	protected void chutarGol() {
		// TODO Auto-generated method stub

	}

	protected void moverBotao() {
		carregaBotao = true;

	}
}
