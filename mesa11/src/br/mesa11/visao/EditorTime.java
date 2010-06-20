/**
 * 
 */
package br.mesa11.visao;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import br.hibernate.Botao;
import br.nnpe.Util;

/**
 * @author Sobreira 19/06/2010
 */
public class EditorTime extends JPanel {

	private JLabel labelCor1 = new JLabel("Cor da equipe 1:");
	private JLabel labelCor2 = new JLabel("Cor da equipe 2:");
	private JLabel labelCor3 = new JLabel("Cor da equipe 3:");
	private JLabel labelCor4 = new JLabel("Cor da equipe 4:");
	private JLabel labelCor5 = new JLabel("Cor da equipe 5:");
	private JLabel labelCor6 = new JLabel("Cor da equipe 6:");
	private JLabel imgUn1 = new JLabel("");;
	private JLabel imgUn2 = new JLabel("");;

	/**
	 * 
	 */
	public EditorTime() {
		JPanel jPanel = new JPanel(new GridLayout(2, 4));
		labelCor1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this,
						"Escolha uma cor", Color.WHITE);
				setCor(color, labelCor1);
				desenhaUniforme(labelCor1.getBackground(), labelCor2
						.getBackground(), labelCor3.getBackground(), imgUn1);
			}

		});
		labelCor2.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this,
						"Escolha uma cor", Color.WHITE);
				setCor(color, labelCor2);
				desenhaUniforme(labelCor1.getBackground(), labelCor2
						.getBackground(), labelCor3.getBackground(), imgUn1);
			}

		});
		labelCor3.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this,
						"Escolha uma cor", Color.WHITE);
				setCor(color, labelCor3);
				desenhaUniforme(labelCor1.getBackground(), labelCor2
						.getBackground(), labelCor3.getBackground(), imgUn1);
			}

		});
		labelCor4.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this,
						"Escolha uma cor", Color.WHITE);
				setCor(color, labelCor4);
				desenhaUniforme(labelCor4.getBackground(), labelCor5
						.getBackground(), labelCor6.getBackground(), imgUn2);
			}

		});
		labelCor5.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this,
						"Escolha uma cor", Color.WHITE);
				setCor(color, labelCor5);
				desenhaUniforme(labelCor4.getBackground(), labelCor5
						.getBackground(), labelCor6.getBackground(), imgUn2);			}

		});
		labelCor6.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this,
						"Escolha uma cor", Color.WHITE);
				setCor(color, labelCor6);
				desenhaUniforme(labelCor4.getBackground(), labelCor5
						.getBackground(), labelCor6.getBackground(), imgUn2);			}

		});
		jPanel.add(labelCor1);
		jPanel.add(labelCor2);
		jPanel.add(labelCor3);
		jPanel.add(imgUn1);
		jPanel.add(labelCor4);
		jPanel.add(labelCor5);
		jPanel.add(labelCor6);
		jPanel.add(imgUn2);
		add(jPanel);

	}

	private void setarHints(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
				RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	}

	protected void desenhaUniforme(Color cor1, Color cor2, Color cor3,
			JLabel icon) {
		Botao botao = new Botao();
		BufferedImage botaoImg = new BufferedImage(botao.getDiamentro(), botao
				.getDiamentro(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) botaoImg.getGraphics();
		setarHints(graphics);
		graphics.fillOval(0, 0, botao.getDiamentro(), botao.getDiamentro());
		AlphaComposite composite = AlphaComposite
				.getInstance(AlphaComposite.SRC_IN);
		graphics.setComposite(composite);
		graphics.setColor(cor1);
		graphics.fillRect(0, 0, botao.getDiamentro(), Util.inte(botao
				.getDiamentro() * .7));
		graphics.setColor(cor2);
		graphics.fillRect(0, Util.inte(botao.getDiamentro() * .7), botao
				.getDiamentro(), Util.inte(botao.getDiamentro() * .35));
		graphics.setColor(cor3);
		graphics.setStroke(new BasicStroke(5.0f));
		graphics.drawOval(0, 0, botao.getDiamentro(), botao.getDiamentro());
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 16));
		graphics.setColor(cor2);
		if (cor2.equals(cor1)) {
			graphics.setColor(cor3);
		}
		graphics.drawString("Mesa", 12, Util.inte(botao.getDiamentro() * .5));
		graphics.setColor(cor1);
		if (cor1.equals(cor2)) {
			graphics.setColor(cor3);
		}
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 20));
		graphics.drawString("11", Util.inte(botao.getDiamentro() * .4), Util
				.inte(botao.getDiamentro() * .9));

		graphics.dispose();
		icon.setIcon(new ImageIcon(botaoImg));
	}

	public void setCor(Color color, JLabel label) {
		label.setOpaque(true);
		label.setBackground(color);
		int valor = (color.getRed() + color.getGreen() + color.getBlue()) / 2;
		if (valor > 250) {
			label.setForeground(Color.BLACK);
		} else {
			label.setForeground(Color.WHITE);
		}
		try {
			label.repaint();
			this.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return new Dimension(700, 300);
	}

	public static void main(String[] args) {
		EditorTime editorTime = new EditorTime();
		// JFrame frame = new JFrame();
		// frame.add(editorTime);
		// frame.setVisible(true);
		JOptionPane.showMessageDialog(null, editorTime);

	}

}
