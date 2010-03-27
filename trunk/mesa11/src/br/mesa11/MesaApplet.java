package br.mesa11;

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

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import br.mesa11.conceito.Botao;
import br.nnpe.GeoUtil;
import br.nnpe.Logger;

public class MesaApplet extends JApplet {
	private Point p = new Point(0, 0);
	List botoes = new ArrayList();

	@Override
	public void init() {
		super.init();

		JFrame frame = new JFrame("mesa11");
		frame.getContentPane().setLayout(new BorderLayout());

		Botao botao = new Botao();
		botao.setPosition(new Point(1400, 2600));
		botoes.add(botao);
		final List jogada = new LinkedList();
		final MesaPanel mesaPanel = new MesaPanel(botoes, jogada);
		final JScrollPane scrollPane = new JScrollPane(mesaPanel,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.setSize(1200, 700);

		p = mesaPanel.pointCentro();
		scrollPane.getViewport().setViewPosition(p);
		mesaPanel.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				MesaPanel.ZOOM += e.getWheelRotation();
				if (MesaPanel.ZOOM <= 0) {
					MesaPanel.ZOOM = 1;
				}
				p = mesaPanel.pointCentro();
				scrollPane.getViewport().setViewPosition(p);
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
					List retaBota = GeoUtil.drawBresenhamLine(p1, botao
							.getCentro());
					if (retaBota.size() <= botao.getRaio()) {
						double angulo = GeoUtil.calculaAngulo(p1, botao
								.getCentro());
						Logger.logar(angulo);
						Point destino = GeoUtil.calculaPonto(angulo, reta
								.size() * 2, botao.getCentro());
						botao.setDestino(destino);
						break;
					}

				}
				mesaPanel.repaint();
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
}
