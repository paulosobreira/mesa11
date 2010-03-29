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

import br.hibernate.Botao;
import br.mesa11.conceito.ControleJogo;
import br.nnpe.GeoUtil;
import br.nnpe.Logger;

public class MesaApplet extends JApplet {

	@Override
	public void init() {
		super.init();
		ControleJogo controleJogo = new ControleJogo();
		controleJogo.test();
	}

}
