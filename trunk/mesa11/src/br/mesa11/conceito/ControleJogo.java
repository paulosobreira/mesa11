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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import br.hibernate.Botao;
import br.mesa11.visao.MesaPanel;
import br.nnpe.GeoUtil;
import br.nnpe.Logger;

public class ControleJogo {
	private List botoes = new ArrayList();
	private Point p = new Point(0, 0);
	private Animacao animacao;

	private MesaPanel mesaPanel;

	public void test() {

		final JFrame frame = new JFrame("mesa11");
		frame.getContentPane().setLayout(new BorderLayout());

		Botao botao = new Botao(1);
		botao.setPosition(new Point(1500, 2300));
		Botao botao2 = new Botao(2);
		botao2.setPosition(new Point(1500, 2500));
		Botao botao3 = new Botao(3);
		botao3.setPosition(new Point(1300, 2500));
		Botao botao4 = new Botao(4);
		botao4.setPosition(new Point(1700, 2200));
		Botao botao5 = new Botao(5);
		botao5.setPosition(new Point(1600, 2900));

		botoes.add(botao);
		botoes.add(botao2);
		botoes.add(botao3);
		botoes.add(botao4);
		botoes.add(botao5);
		final List jogada = new LinkedList();
		mesaPanel = new MesaPanel(botoes, jogada);
		final JScrollPane scrollPane = new JScrollPane(mesaPanel,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.setSize(900, 900);

		p = mesaPanel.pointCentro();
		scrollPane.getViewport().setViewPosition(p);
		mesaPanel.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				MesaPanel.ZOOM += e.getWheelRotation() / 100.0;
				Logger.logar(MesaPanel.ZOOM);
				if (MesaPanel.ZOOM <= 0.5) {
					MesaPanel.ZOOM = 0.5;
				}
				if (MesaPanel.ZOOM >= 1) {
					MesaPanel.ZOOM = 1;
				}
//				p = mesaPanel.pointCentro();
//				scrollPane.getViewport().setViewPosition(p);
				mesaPanel.repaint();
			}
		});
		mesaPanel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDragged(MouseEvent e) {
				jogada.add(e.getPoint());
				// jogada.add(new Point(e.getPoint().x
				// - (int) (e.getPoint().x * MesaPanel.ZOOM),
				// e.getPoint().y
				// - (int) (e.getPoint().y * MesaPanel.ZOOM)));
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
				for (Iterator iterator = botoes.iterator(); iterator.hasNext();) {
					Botao botao = (Botao) iterator.next();
					List raioPonto = GeoUtil.drawBresenhamLine(p1, botao
							.getCentro());
					if (raioPonto.size() * MesaPanel.ZOOM <= botao.getRaio()) {
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
				for (Iterator iterator = botoes.iterator(); iterator.hasNext();) {
					Botao botao = (Botao) iterator.next();
					if (botao.getCentroInicio() != null)
						botao.setCentro(botao.getCentroInicio());
				}
				mesaPanel.repaint();
				Animador animador = new Animador(animacao, mesaPanel);
				Thread thread = new Thread(animador);
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
				if (keycode == KeyEvent.VK_LEFT) {
					p.x -= 10;
				} else if (keycode == KeyEvent.VK_RIGHT) {
					p.x += 10;
				} else if (keycode == KeyEvent.VK_UP) {
					p.y -= 10;
				} else if (keycode == KeyEvent.VK_DOWN) {
					p.y += 10;
				}
				scrollPane.getViewport().setViewPosition(p);
				mesaPanel.repaint();
				super.keyPressed(e);
			}

		});
		frame.setVisible(true);
		frame.requestFocus();
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	}

	protected void propagaColisao(Animacao animacao, Botao causador) {
		Botao botao = animacao.getObjetoAnimacao();
		List trajetoriaBotao = animacao.getPontosAnimacao();
		for (int i = 0; i < trajetoriaBotao.size(); i++) {
			Object objTrajetoria = trajetoriaBotao.get(i);
			if (objTrajetoria instanceof Point) {
				Point point = (Point) objTrajetoria;
				botao.setCentro(point);
				for (Iterator iterator = botoes.iterator(); iterator.hasNext();) {
					Botao botaoAtingido = (Botao) iterator.next();
					if (botao.equals(botaoAtingido)) {
						continue;
					}
					if (causador.equals(botaoAtingido)) {
						continue;
					}
					List raioPonto = GeoUtil.drawBresenhamLine(point,
							botaoAtingido.getCentro());
					if (raioPonto.size() == (botaoAtingido.getRaio() * 2)) {
						double angulo = GeoUtil.calculaAngulo(point,
								botaoAtingido.getCentro(), 90);
						Point destino = GeoUtil.calculaPonto(angulo,
								trajetoriaBotao.size() / 2, botaoAtingido
										.getCentro());
						botaoAtingido.setDestino(destino);
						animacao = new Animacao();
						animacao.setObjetoAnimacao(botaoAtingido);
						animacao.setPontosAnimacao(botaoAtingido
								.getTrajetoria());

						trajetoriaBotao.set(i, animacao);
						while (i + 1 < trajetoriaBotao.size()) {
							trajetoriaBotao.remove(trajetoriaBotao.size() - 1);
						}
						angulo = GeoUtil.calculaAngulo(point,
								botao.getCentro(), 90);
						destino = GeoUtil.calculaPonto(angulo, trajetoriaBotao
								.size() / 3, botao.getCentro());
						botao.setDestino(destino);
						List novaTrajetoria = GeoUtil.drawBresenhamLine(point,
								destino);
						for (Iterator iterator2 = novaTrajetoria.iterator(); iterator2
								.hasNext();) {
							trajetoriaBotao.add(iterator2.next());
						}
						propagaColisao(animacao, botao);

						break;
					}
				}
			} else if (objTrajetoria instanceof Animacao) {
				System.out.println("teste");
			}
		}
	}
}
