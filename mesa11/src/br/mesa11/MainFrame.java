package br.mesa11;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import br.applet.Mesa11Applet;
import br.hibernate.Usuario;
import br.mesa11.conceito.Botao;
import br.mesa11.conceito.Mesa;

public class MainFrame {

	private static Point p = new Point(0, 0);

	public MainFrame(Mesa11Applet mesa11Applet, Usuario usuario) {

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("mesa11");
		frame.getContentPane().setLayout(new BorderLayout());
		Mesa mesa = new Mesa();
		final MesaPanel mesaPanel = mesa.getMesaPanel();
		final JScrollPane scrollPane = new JScrollPane(mesaPanel,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.setSize(600, 800);
		p = mesaPanel.pointCentro();
		Botao botao = new Botao();
		// mesaPanel.add(botao.getImgBotao());
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
		mesaPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("click");
				if (mesaPanel.getGrandeAreaCima().contains(e.getPoint())) {
					System.out.println("grandeAreaCima");
				}
				super.mouseClicked(e);
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
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public void setVisible(boolean b) {
		// TODO Auto-generated method stub
		
	}
}
