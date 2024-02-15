package br.poc;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import br.nnpe.ImageUtil;

public class TesteWindow {
	public static void main(String[] args) throws IOException {
		final JFrame frame = new JFrame() {
			// @Override
			// public Dimension getPreferredSize() {
			// return new Dimension(500, super.getPreferredSize().height);
			// }

			// @Override
			// public Dimension getSize() {
			// return new Dimension(500, super.getSize().height);
			// }
			//
			// @Override
			// public Rectangle getMaximizedBounds() {
			// Rectangle m = super.getMaximizedBounds();
			// if (m.getWidth() > 500) {
			// m.setBounds(m.x, m.y, 500, m.height);
			// }
			// return m;
			// }

		};

		final BufferedImage cruzeiro = ImageIO.read(TesteWindow.class
				.getResource("esp2010-7.jpg"));
		final BufferedImage transpb4 = ImageUtil.gerarFade(
				ImageIO.read(TesteWindow.class.getResource("transp-b7.png")),
				150);
		frame.setSize(300, 300);
		JPanel jPanel = new JPanel() {
			protected void paintComponent(java.awt.Graphics g) {
				super.paintComponent(g);
				g.drawImage(cruzeiro, 50, 50, null);
				g.drawImage(transpb4, 48, 48, null);

			};
		};
		frame.getContentPane().add(jPanel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
}
