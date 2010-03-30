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
import br.hibernate.Botao;
import br.hibernate.Usuario;
import br.mesa11.conceito.ControleJogo;
import br.mesa11.visao.MesaPanel;

public class MainFrame {

	private static Point p = new Point(0, 0);
	private JFrame frame;

	public MainFrame(Mesa11Applet mesa11Applet, Usuario usuario) {
		frame = new JFrame("mesa11");
	}

	public static void main(String[] args) {
		ControleJogo controleJogo = new ControleJogo();
		controleJogo.test();
	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
}
