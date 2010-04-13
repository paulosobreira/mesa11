package br.mesa11.conceito;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import br.hibernate.Botao;
import br.mesa11.visao.MesaPanel;
import br.nnpe.GeoUtil;
import br.nnpe.Util;

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

	public void test() {

		final JFrame frame = new JFrame("mesa11");
		frame.setResizable(false);
		frame.getContentPane().setLayout(new BorderLayout());

		Botao botao = new Botao(1);
		botao.setImagem("azul.png");
		botao.setPosition(new Point(1500, 2300));
		Botao botao2 = new Botao(2);
		botao2.setImagem("azul.png");
		botao2.setPosition(new Point(1500, 2500));
		Botao botao3 = new Botao(3);
		botao3.setImagem("azul.png");
		botao3.setPosition(new Point(1300, 2500));
		Botao botao4 = new Botao(4);
		botao4.setImagem("azul.png");
		botao4.setPosition(new Point(1700, 2200));
		Botao botao5 = new Botao(5);
		botao5.setImagem("azul.png");
		botao5.setPosition(new Point(1600, 2900));

		bola = new Bola(0);
		bola.setImagem("bola.png");

		botoes.put(botao.getId(), botao);
		botoes.put(botao2.getId(), botao2);
		botoes.put(botao3.getId(), botao3);
		botoes.put(botao4.getId(), botao4);
		botoes.put(botao5.getId(), botao5);
		botoes.put(bola.getId(), bola);
		mesaPanel = new MesaPanel(this);
		scrollPane = new JScrollPane(mesaPanel,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.setSize(1024, 740);

		mesaPanel.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if ((System.currentTimeMillis() - lastScrool) < 50)
					return;
				double newzoom = MesaPanel.ZOOM;
				newzoom += e.getWheelRotation() / 25.0;
				// if (newp.x < 0) {
				// }
				// if (newp.y < 0) {
				// }
				// double maxX = ((mesaPanel.getWidth() * newzoom) - scrollPane
				// .getViewport().getWidth());
				// if (newp.x > maxX) {
				// System.out.println("newp.x" + newp.x);
				// System.out.println("maxX" + maxX);
				// newp.x = Util.inte(maxX * newzoom) - 1;
				// System.out.println("newp.x'" + newp.x);
				// }
				// if (newp.y > ((mesaPanel.getHeight() * newzoom) - (scrollPane
				// .getViewport().getHeight()))) {
				// }
				// // if (((newp.x < 0 || newp.y < 0) || (newp.x > ((mesaPanel
				// // .getWidth() * newzoom) - scrollPane.getViewport()
				// // .getWidth()) || newp.y > ((mesaPanel.getHeight() *
				// newzoom) -
				// // (scrollPane
				// // .getViewport().getHeight()))))) {
				// // centralizaBotao(getBola());
				// // return;
				// // }

				MesaPanel.ZOOM = newzoom;
				if (MesaPanel.ZOOM <= 0.3) {
					MesaPanel.ZOOM = 0.3;
				}
				if (MesaPanel.ZOOM >= 1) {
					MesaPanel.ZOOM = 1;
				}
				centralizaBola();
				lastScrool = System.currentTimeMillis();
			}
		});
		mesaPanel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDragged(MouseEvent e) {
				jogada.add(new Point((int) (e.getPoint().x / MesaPanel.ZOOM),
						(int) (e.getPoint().y / MesaPanel.ZOOM)));
			}

		});
		mesaPanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (jogada.isEmpty())
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
					List raioPonto = GeoUtil.drawBresenhamLine(p1, botao
							.getCentro());
					if (raioPonto.size() <= botao.getRaio()) {
						if (botao instanceof Bola) {
							return;
						}
						double angulo = GeoUtil.calculaAngulo(p1, botao
								.getCentro(), 90);

						Point destino = GeoUtil.calculaPonto(angulo, reta
								.size() * 2, botao.getCentro());
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
			}

			@Override
			public void mousePressed(MouseEvent e) {
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
				if (p.x > ((mesaPanel.getWidth() * mesaPanel.ZOOM) - scrollPane
						.getViewport().getWidth())
						|| p.y > ((mesaPanel.getHeight() * mesaPanel.ZOOM) - (scrollPane
								.getViewport().getHeight()))) {
					return;
				}
				newp = p;
				super.keyPressed(e);
			}

		});
		frame.setVisible(true);
		frame.requestFocus();
		bola.setPosition(mesaPanel.pointCentro());
		centralizaBola();
		Thread thread2 = new Thread(new Runnable() {

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
		thread2.start();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public Map getBotoesComThread() {
		return botoesComThread;
	}

	public void setBotoesComThread(Map botoesComThread) {
		this.botoesComThread = botoesComThread;
	}

	protected void centralizaBola() {
		Point p = new Point((int) (bola.getCentro().x * mesaPanel.ZOOM)
				- (scrollPane.getViewport().getWidth() / 2), (int) (bola
				.getCentro().y * mesaPanel.ZOOM)
				- (scrollPane.getViewport().getHeight() / 2));
		if (p.x < 0) {
			p.x = 1;
		}
		double maxX = ((mesaPanel.getWidth() * mesaPanel.ZOOM) - scrollPane
				.getViewport().getWidth());
		if (p.x > maxX) {
			p.x = Util.inte(maxX) - 1;
		}
		if (p.y < 0) {
			p.y = 1;
		}
		double maxY = ((mesaPanel.getHeight() * mesaPanel.ZOOM) - (scrollPane
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
		for (int i = 0; i < trajetoriaBotao.size(); i++) {
			Object objTrajetoria = trajetoriaBotao.get(i);
			if (objTrajetoria instanceof Point) {
				Point point = (Point) objTrajetoria;
				botao.setCentro(point);
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
					List raioPonto = GeoUtil.drawBresenhamLine(point,
							botaoAnalisado.getCentro());
					if ((raioPonto.size() - (botao.getRaio())) == (botaoAnalisado
							.getRaio())) {
						// if ((botao instanceof Bola) && Math.random() > .7) {
						// bolaIngnora.add(botaoAnalisado);
						// continue;
						// }
						double angulo = GeoUtil.calculaAngulo(point,
								botaoAnalisado.getCentro(), 90);
						Point destino = null;
						if ((botaoAnalisado instanceof Bola)) {
							destino = GeoUtil.calculaPonto(angulo,
									trajetoriaBotao.size() * 2, botaoAnalisado
											.getCentro());
						} else {
							int div = 2;
							if (botaoAnalisado instanceof Bola) {
								div = 1;
							}
							if ((botao instanceof Bola)) {
								div = 100;
							}
							destino = GeoUtil.calculaPonto(angulo,
									trajetoriaBotao.size() / div,
									botaoAnalisado.getCentro());
						}

						botaoAnalisado.setDestino(destino);
						animacao = new Animacao();
						animacao.setObjetoAnimacao(botaoAnalisado);
						animacao.setPontosAnimacao(botaoAnalisado
								.getTrajetoria());
						trajetoriaBotao.set(i, animacao);
						if ((botaoAnalisado instanceof Bola)) {
							while (i + 1 < (trajetoriaBotao.size() / 2)) {
								trajetoriaBotao
										.remove(trajetoriaBotao.size() - 1);
							}
						} else {
							while (i + 1 < trajetoriaBotao.size()) {
								trajetoriaBotao
										.remove(trajetoriaBotao.size() - 1);
							}

							int dest = 0;
							if ((botao instanceof Bola)) {
								dest = trajetoriaBotao.size();
							} else {
								dest = trajetoriaBotao.size() / 3;
							}
							angulo = GeoUtil.calculaAngulo(botaoAnalisado
									.getCentro(), point, 0);

							destino = GeoUtil.calculaPonto(angulo, dest, botao
									.getCentro());
							botao.setDestino(destino);
							List novaTrajetoria = GeoUtil.drawBresenhamLine(
									point, destino);
							for (Iterator iterator2 = novaTrajetoria.iterator(); iterator2
									.hasNext();) {
								trajetoriaBotao.add(iterator2.next());
							}
						}

						propagaColisao(animacao, botao);

						break;
					}
				}
			}
		}
	}

	public void centralizaBotao(Botao b) {

		Point ori = new Point(
				(int) (scrollPane.getViewport().getViewPosition().x),
				(int) (scrollPane.getViewport().getViewPosition().y));
		Point des = new Point((int) (b.getCentro().x * mesaPanel.ZOOM)
				- (scrollPane.getViewport().getWidth() / 2), (int) (b
				.getCentro().y * mesaPanel.ZOOM)
				- (scrollPane.getViewport().getHeight() / 2));
		List reta = GeoUtil.drawBresenhamLine(ori, des);
		Point p = des;
		if (!reta.isEmpty()) {
			for (int i = 25; i > 0; i--) {
				if (reta.size() > i) {
					p = (Point) reta.get(i);
					break;
				}
			}

		}
		if (!((p.x < 0 || p.y < 0) || (p.x > ((mesaPanel.getWidth() * mesaPanel.ZOOM) - scrollPane
				.getViewport().getWidth()) || p.y > ((mesaPanel.getHeight() * mesaPanel.ZOOM) - (scrollPane
				.getViewport().getHeight()))))) {
			newp = p;
		}
	}

	protected void centralizaBotao2(Botao b) {
		System.out.println("centralizaBotao2");
		Point ori = new Point(
				(int) (scrollPane.getViewport().getViewPosition().x),
				(int) (scrollPane.getViewport().getViewPosition().y));
		Point des = new Point((int) (b.getCentro().x * mesaPanel.ZOOM)
				- (scrollPane.getViewport().getWidth() / 2), (int) (b
				.getCentro().y * mesaPanel.ZOOM)
				- (scrollPane.getViewport().getHeight() / 2));
		List reta = GeoUtil.drawBresenhamLine(ori, des);
		Point p = des;
		if (!reta.isEmpty()) {
			p = (Point) reta.get(reta.size() - 1);
		}
		if (!((p.x < 0 || p.y < 0) || (p.x > ((mesaPanel.getWidth() * mesaPanel.ZOOM) - scrollPane
				.getViewport().getWidth()) || p.y > ((mesaPanel.getHeight() * mesaPanel.ZOOM) - (scrollPane
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

}
