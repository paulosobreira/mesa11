import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class TesteWindow {
	public static void main(String[] args) {
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
		frame.addWindowStateListener(new WindowStateListener() {

			@Override
			public void windowStateChanged(WindowEvent e) {
				if (6 == e.getNewState()) {
					Toolkit kit = frame.getToolkit();
					Dimension screenSize = kit.getScreenSize();
					int meio = (int) (screenSize.getWidth() / 2);
					frame.setLocation(meio - 400, 0);
					frame.setSize(800, frame.getHeight() - 15);
				}
			}
		});
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
}
