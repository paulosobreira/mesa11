package br.mesa11;

import javax.swing.JApplet;

import br.nnpe.Logger;
import br.recursos.Lang;

public class MesaApplet extends JApplet {

	private javax.swing.JButton jButton1;
	private javax.swing.JPanel jPanel1;

	public void init() {
		initComponents();
	}

	private void initComponents() {
		// String param = getParameter("lang");
		// if (!Util.isNullOrEmpty(param)) {
		// Lang.mudarIdioma(param);
		// }
		jPanel1 = new javax.swing.JPanel();
		jButton1 = new javax.swing.JButton() {
			@Override
			public String getText() {
				return Lang.msg("iniciaJogo");
			}
		};
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});
		jPanel1.add(jButton1);
		getContentPane().add(jPanel1);
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {

		try {
			final MainFrame frame = new MainFrame(this, null);
		} catch (Exception e) {
			Logger.logarExept(e);
		}
	}
}
