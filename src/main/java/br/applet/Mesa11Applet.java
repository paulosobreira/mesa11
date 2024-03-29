package br.applet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.sound.midi.Sequencer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import br.mesa11.ConstantesMesa11;
import br.mesa11.cliente.ControleChatCliente;
import br.nnpe.Constantes;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.ZipUtil;
import br.nnpe.tos.ErroServ;
import br.nnpe.tos.MsgSrv;
import br.recursos.Lang;

public class Mesa11Applet {

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

	DecimalFormat decimalFormat = new DecimalFormat("#,##");
	private String versao;

	URL codeBase;

	private JFrame frame;

	/**
	 * @param args
	 * @throws MalformedURLException
	 */

	public static void main(String[] args) throws MalformedURLException {
		Mesa11Applet mesa11Applet = new Mesa11Applet();
		String host = JOptionPane.showInputDialog("Host");
		if(Util.isNullOrEmpty(host)){
			host = "http://localhost";
		}
		mesa11Applet.setCodeBase(new URL(host));
		mesa11Applet.init();
	}

	public URL getCodeBase() {
		return codeBase;
	}

	public void setCodeBase(URL codeBase) {
		this.codeBase = codeBase;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public void init() {
		try {
			frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			url = getCodeBase();
			properties = new Properties();

			properties.load(
					this.getClass().getResourceAsStream("/application.properties"));
			this.urlSufix = "/mesa11/mesa11";
			this.versao = properties.getProperty("versao");
			controleChatCliente = new ControleChatCliente(this);
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					controleChatCliente.logar();
				}
			});
			thread.start();
		} catch (Exception e) {
			StackTraceElement[] trace = e.getStackTrace();
			StringBuffer retorno = new StringBuffer();
			int size = ((trace.length > 10) ? 10 : trace.length);

			for (int i = 0; i < size; i++)
				retorno.append(trace[i] + "\n");
			JOptionPane.showMessageDialog(frame, retorno.toString(),
					Lang.msg("erroEnviando"), JOptionPane.ERROR_MESSAGE);
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
				connection.setRequestProperty("Content-Length",
						String.valueOf(byteArrayOutputStream.size()));
				connection.setRequestProperty("Content-Length",
						"application/x-www-form-urlencoded");
				connection.getOutputStream()
						.write(byteArrayOutputStream.toByteArray());
				if (Constantes.modoZip) {
					retorno = ZipUtil
							.descompactarObjeto(connection.getInputStream());
				} else {
					ObjectInputStream ois = new ObjectInputStream(
							connection.getInputStream());
					retorno = ois.readObject();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			long retornoT = System.currentTimeMillis();
			if (!timeout) {
				atualizarLantenciaMinima(envioT, retornoT);
			}
			if (retorno instanceof ErroServ) {
				ErroServ erroServ = (ErroServ) retorno;
				Logger.logar(erroServ.obterErroFormatado());
				JOptionPane.showMessageDialog(frame,
						Lang.decodeTexto(erroServ.obterErroFormatado()),
						Lang.msg("erroRecebendo"), JOptionPane.ERROR_MESSAGE);
				return erroServ;
			}
			if (retorno instanceof MsgSrv) {
				MsgSrv msgSrv = (MsgSrv) retorno;
				JOptionPane.showMessageDialog(frame,
						Lang.msg(Lang.decodeTexto(msgSrv.getMessageString())),
						Lang.msg("msgServidor"),
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
			JOptionPane.showMessageDialog(frame, retorno.toString(),
					Lang.msg("erroEnviando"), JOptionPane.ERROR_MESSAGE);
			Logger.logarExept(e);
		}

		return null;
	}

	private void atualizarLantenciaMinima(long envioT, long retornoT) {
		synchronized (pacotes) {
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
				if (media > ConstantesMesa11.LATENCIA_MAX) {
					setLatenciaMinima(ConstantesMesa11.LATENCIA_MAX);
				} else {
					setLatenciaMinima(media);
				}
				if (media < ConstantesMesa11.LATENCIA_MIN)
					setLatenciaMinima(ConstantesMesa11.LATENCIA_MIN);
				else if (media < ConstantesMesa11.LATENCIA_MAX) {
					setLatenciaMinima(media);
				}
				setLatenciaReal(media);
				if (controleChatCliente != null) {
					controleChatCliente.atualizaInfo();
				}
			}
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

	public String getVersao() {
		if (versao == null) {
			properties = new Properties();

			try {
				properties.load(this.getClass()
						.getResourceAsStream("/application.properties"));
			} catch (IOException e) {
				Logger.logarExept(e);
			}
			this.urlSufix = properties.getProperty("servidor");
			this.versao = properties.getProperty("versao");
		}
		return " " + decimalFormat.format(new Integer(versao));
	}

}
