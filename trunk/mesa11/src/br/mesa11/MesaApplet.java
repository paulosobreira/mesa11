package br.mesa11;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

public class MesaApplet extends JApplet {
	private Point p = new Point(0, 0);

	@Override
	public void init() {
		super.init();

		JFrame frame = new JFrame("mesa11");
		frame.getContentPane().setLayout(new BorderLayout());
		final MesaPanel mesaPanel = new MesaPanel();
		final JScrollPane scrollPane = new JScrollPane(mesaPanel,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.setSize(800, 600);
		frame.setSize(600, 800);
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
