package br.applet;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import br.mesa11.ConstantesMesa11;
import br.mesa11.servidor.ControleChatCliente;
import br.nnpe.Logger;
import br.nnpe.ZipUtil;
import br.recursos.Lang;
import br.tos.ErroServ;
import br.tos.MsgSrv;

public class Mesa11Applet extends JApplet {

	private static final long serialVersionUID = 1L;

	private URL url;
	private Properties properties;
	private String urlSufix;

	private int latenciaMinima = 120;
	private int latenciaReal;

	private ControleChatCliente controleChatCliente;

	private List pacotes = new LinkedList();

	private boolean comunicacaoServer = true;

	private Sequencer sequencer;

	private LookAndFeelInfo[] looks;

	public static void main(String[] args) {
		LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
		for (int i = 0; i < looks.length; i++) {
			System.out.println(looks[i].getClassName());

		}
	}

	@Override
	public void init() {
		super.init();
		try {
			looks = UIManager.getInstalledLookAndFeels();
			if (!ConstantesMesa11.debug) {
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				SwingUtilities.updateComponentTreeUI(this);
			}
		} catch (Exception e) {
			Logger.logarExept(e);
			try {
				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e1) {
				Logger.logarExept(e1);
			}
		}

		try {

			url = getCodeBase();
			properties = new Properties();

			properties.load(this.getClass().getResourceAsStream(
					"client.properties"));
			this.urlSufix = properties.getProperty("servidor");
			controleChatCliente = new ControleChatCliente(this);
			controleChatCliente.logar();
		} catch (Exception e) {
			StackTraceElement[] trace = e.getStackTrace();
			StringBuffer retorno = new StringBuffer();
			int size = ((trace.length > 10) ? 10 : trace.length);

			for (int i = 0; i < size; i++)
				retorno.append(trace[i] + "\n");
			JOptionPane.showMessageDialog(this, retorno.toString(), Lang
					.msg("erroEnviando"), JOptionPane.ERROR_MESSAGE);
			Logger.logarExept(e);
		}

	}

	public Object enviarObjeto(Object enviar) {
		return enviarObjeto(enviar, false);
	}

	public Object enviarObjeto(Object enviar, boolean timeout) {
		try {
			String protocol = url.getProtocol();
			String host = url.getHost();
			int port = url.getPort();
			URL dataUrl;
			long envioT = System.currentTimeMillis();
			Object retorno = null;
			dataUrl = new URL(protocol, host, port, urlSufix);

			URLConnection connection = dataUrl.openConnection();

			try {
				connection.setUseCaches(false);
				connection.setDoOutput(true);

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream stream = new ObjectOutputStream(
						byteArrayOutputStream);
				if (latenciaReal > 0 && timeout
						&& latenciaReal > latenciaMinima)
					connection.setReadTimeout(latenciaReal);
				stream.writeObject(enviar);
				stream.flush();
				connection.setRequestProperty("Content-Length", String
						.valueOf(byteArrayOutputStream.size()));
				connection.setRequestProperty("Content-Length",
						"application/x-www-form-urlencoded");
				connection.getOutputStream().write(
						byteArrayOutputStream.toByteArray());
				if (ConstantesMesa11.modoZip) {
					retorno = ZipUtil.descompactarObjeto(connection
							.getInputStream());
				} else {
					ObjectInputStream ois = new ObjectInputStream(connection
							.getInputStream());
					retorno = ois.readObject();
				}
			} catch (java.net.SocketTimeoutException e) {
				return null;
			} catch (java.io.IOException e) {
				return null;
			}
			long retornoT = System.currentTimeMillis();
			if (!timeout) {
				atualizarLantenciaMinima(envioT, retornoT);
			}
			if (retorno instanceof ErroServ) {
				ErroServ erroServ = (ErroServ) retorno;
				Logger.logar(erroServ.obterErroFormatado());
				JOptionPane.showMessageDialog(this, Lang.decodeTexto(erroServ
						.obterErroFormatado()), Lang.msg("erroRecebendo"),
						JOptionPane.ERROR_MESSAGE);
				return erroServ;
			}
			if (retorno instanceof MsgSrv) {
				MsgSrv msgSrv = (MsgSrv) retorno;
				JOptionPane.showMessageDialog(this, Lang.msg(msgSrv
						.getMessageString()), Lang.msg("msgServidor"),
						JOptionPane.INFORMATION_MESSAGE);
				return msgSrv;
			}
			return retorno;
		} catch (Exception e) {
			setComunicacaoServer(false);
			StackTraceElement[] trace = e.getStackTrace();
			StringBuffer retorno = new StringBuffer();
			int size = ((trace.length > 10) ? 10 : trace.length);

			for (int i = 0; i < size; i++)
				retorno.append(trace[i] + "\n");
			JOptionPane.showMessageDialog(this, retorno.toString(), Lang
					.msg("erroEnviando"), JOptionPane.ERROR_MESSAGE);
			Logger.logarExept(e);
		}

		return null;
	}

	private void atualizarLantenciaMinima(long envioT, long retornoT) {
		if (pacotes.size() > 10) {
			pacotes.remove(0);
		}
		pacotes.add(new Long(retornoT - envioT));
		if (pacotes.size() >= 10) {
			long somatorio = 0;
			for (Iterator iter = pacotes.iterator(); iter.hasNext();) {
				Long longElement = (Long) iter.next();
				somatorio += longElement.longValue();
			}
			int media = (int) (somatorio / 10);
			if (media > 240) {
				setLatenciaMinima(240);
			} else {
				setLatenciaMinima(media);
			}
			if (media < 120)
				setLatenciaMinima(120);
			else if (media < 240) {
				setLatenciaMinima(media);
			}
			setLatenciaReal(media);
		}
	}

	public int getLatenciaMinima() {
		return latenciaMinima;
	}

	public void setLatenciaMinima(int latenciaMinima) {
		this.latenciaMinima = latenciaMinima;
	}

	public int getLatenciaReal() {
		return latenciaReal;
	}

	public void setLatenciaReal(int latenciaReal) {
		this.latenciaReal = latenciaReal;
	}

	public boolean isComunicacaoServer() {
		return comunicacaoServer;
	}

	public void setComunicacaoServer(boolean comunicacaoServer) {
		this.comunicacaoServer = comunicacaoServer;
	}

}
