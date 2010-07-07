package br.mesa11.conceito;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import br.hibernate.Botao;
import br.hibernate.Goleiro;
import br.hibernate.Time;
import br.mesa11.BotaoUtils;
import br.mesa11.ConstantesMesa11;
import br.mesa11.visao.MesaPanel;
import br.nnpe.GeoUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.recursos.CarregadorRecursos;
import br.recursos.Lang;

public class ControlePartida {

	private ControleJogo controleJogo;
	private int tempoJogoMinutos;
	private Hashtable times;
	private int tempoJogoMilis;
	private long inicioJogoMilis;
	private long fimJogoMilis;
	private int tempoJogadaSegundos;
	private long tempoJogadaAtualMilis;
	private long tempoJogadaFimMilis;
	private String campoTimeComBola;
	private Time timeCima;
	private Time timeBaixo;
	private ControlePosicionamento controleFormacao;
	private MesaPanel mesaPanel;

	public ControlePartida(ControleJogo controleJogo) {
		super();
		this.controleJogo = controleJogo;
		final Properties properties = new Properties();
		try {
			times = new Hashtable();
			properties.load(CarregadorRecursos
					.recursoComoStream("times.properties"));
			Enumeration propName = properties.propertyNames();
			while (propName.hasMoreElements()) {
				final String name = (String) propName.nextElement();
				times.put(name, properties.getProperty(name));

			}
		} catch (IOException e) {
			Logger.logarExept(e);
		}
	}

	public void iniciaJogoLivre() {
		GridLayout gridLayout = new GridLayout(2, 4);
		gridLayout.setHgap(15);
		gridLayout.setVgap(5);
		JPanel escolhaTimesPanel = new JPanel(gridLayout);
		JComboBox timesCima = new JComboBox();
		JComboBox timesBaixo = new JComboBox();
		JRadioButton bolaCima = new JRadioButton();
		JRadioButton bolaBaixo = new JRadioButton();
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(bolaCima);
		buttonGroup.add(bolaBaixo);
		for (Iterator iterator = times.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			timesCima.addItem(times.get(key));
			timesBaixo.addItem(times.get(key));
		}
		escolhaTimesPanel.setBorder(new TitledBorder("") {

			@Override
			public String getTitle() {
				return Lang.msg("baterCentro");
			}
		});
		escolhaTimesPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("timeCima");
			}
		});
		escolhaTimesPanel.add(timesCima);
		escolhaTimesPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("bateCentroCima");
			}
		});
		escolhaTimesPanel.add(bolaCima);
		escolhaTimesPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("timeBaixo");
			}
		});
		escolhaTimesPanel.add(timesBaixo);
		escolhaTimesPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("bateCentroBaixo");
			}
		});
		escolhaTimesPanel.add(bolaBaixo);

		JPanel tempoJogoPanel = new JPanel();
		tempoJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("tempoJogoMinutos");
			}
		});
		JComboBox tempoJogoCombo = new JComboBox();
		tempoJogoCombo.addItem(new Integer(10));
		tempoJogoCombo.addItem(new Integer(20));
		tempoJogoCombo.addItem(new Integer(30));
		tempoJogoPanel.add(tempoJogoCombo);
		tempoJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("tempoJogadaSegundos");
			}
		});
		JComboBox tempoJogadaCombo = new JComboBox();
		tempoJogadaCombo.addItem(new Integer(20));
		tempoJogadaCombo.addItem(new Integer(30));
		tempoJogadaCombo.addItem(new Integer(40));
		tempoJogadaCombo.addItem(new Integer(50));
		tempoJogadaCombo.addItem(new Integer(60));
		tempoJogoPanel.add(tempoJogadaCombo);

		JFrame frame = controleJogo.getFrame();
		Map botoesImagens = controleJogo.getBotoesImagens();
		Map botoes = controleJogo.getBotoes();
		mesaPanel = controleJogo.getMesaPanel();
		JPanel iniciarJogoPanel = new JPanel(new BorderLayout());
		iniciarJogoPanel.add(escolhaTimesPanel, BorderLayout.CENTER);
		iniciarJogoPanel.add(tempoJogoPanel, BorderLayout.SOUTH);

		int val = JOptionPane.showConfirmDialog(frame, iniciarJogoPanel, Lang
				.msg("escolhaTimes"), JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		if (val != JOptionPane.YES_OPTION) {
			return;
		}
		processaTempoJogo(tempoJogoCombo.getSelectedItem());
		String xmlCima = obterKey((String) timesCima.getSelectedItem());
		String xmlBaixo = obterKey((String) timesBaixo.getSelectedItem());
		if (xmlCima.equals(xmlBaixo)) {
			return;
		}

		XMLDecoder xmlDecoder = new XMLDecoder(CarregadorRecursos
				.recursoComoStream(xmlCima));
		timeCima = (Time) xmlDecoder.readObject();
		timeCima.setCampo(ConstantesMesa11.CAMPO_CIMA);
		for (int i = 0; i < 11; i++) {
			Long id = new Long(i + 2);
			Botao botao = (Botao) timeCima.getBotoes().get(i);
			if (botao.getId() == null || MesaPanel.zero.equals(botao.getId())) {
				botao.setId(id);
			}
			if (botao instanceof Goleiro || botao.isGoleiro()) {
				botoesImagens.put(botao.getId(), BotaoUtils
						.desenhaUniformeGoleiro(timeCima, 1, (Goleiro) botao));
				botao.setCentro(mesaPanel.golCima());
				System.out.println("Goleiro Cima");
			} else {
				botoesImagens.put(botao.getId(), BotaoUtils.desenhaUniforme(
						timeCima, 1, botao));
			}
			botoes.put(botao.getId(), botao);

		}
		controleFormacao = new ControlePosicionamento(controleJogo);

		controleFormacao.posicionaTimeCima(timeCima, bolaCima.isSelected());

		xmlDecoder = new XMLDecoder(CarregadorRecursos
				.recursoComoStream(xmlBaixo));
		timeBaixo = (Time) xmlDecoder.readObject();
		timeBaixo.setCampo(ConstantesMesa11.CAMPO_BAIXO);
		for (int i = 0; i < 11; i++) {
			Long id = new Long(i + 20);
			Botao botao = (Botao) timeBaixo.getBotoes().get(i);
			if (botao.getId() == null || MesaPanel.zero.equals(botao.getId())) {
				botao.setId(id);
			}
			if (botao instanceof Goleiro || botao.isGoleiro()) {
				botoesImagens.put(botao.getId(), BotaoUtils
						.desenhaUniformeGoleiro(timeBaixo, 1, (Goleiro) botao));
				botao.setCentro(mesaPanel.golBaixo());
				System.out.println("Goleiro Baixo");
			} else {
				botoesImagens.put(botao.getId(), BotaoUtils.desenhaUniforme(
						timeBaixo, 1, botao));
			}
			botoes.put(botao.getId(), botao);
		}
		controleFormacao.posicionaTimeBaixo(timeBaixo, bolaBaixo.isSelected());
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao botao = (Botao) botoes.get(id);
			// System.out.println(id + " " + botao.getPosition() + " "
			// + botao.getNumero() + " " + botao.getNome() + " "
			// + botao.isGoleiro() + " " + botao.getClass());
		}
		controleJogo.bolaCentro();
		iniciaTempoJogada(tempoJogadaCombo.getSelectedItem(), bolaCima
				.isSelected() ? ConstantesMesa11.CAMPO_CIMA
				: ConstantesMesa11.CAMPO_BAIXO);
	}

	private void iniciaTempoJogada(Object selectedItem, String timeComBola) {
		tempoJogadaSegundos = (Integer) selectedItem;
		this.campoTimeComBola = timeComBola;
		zerarTimerJogada();
		Logger.logar("timeComBola " + timeComBola);
	}

	private void processaTempoJogo(Object selectedItem) {
		tempoJogoMinutos = (Integer) selectedItem;
		tempoJogoMilis = tempoJogoMinutos * 60 * 1000;
		inicioJogoMilis = System.currentTimeMillis();
		fimJogoMilis = inicioJogoMilis + tempoJogoMilis;
		Logger.logar("Inicio Jogo " + df.format(inicioJogoMilis));
		Logger.logar("Tempo Jogo " + formatarTempo(tempoJogoMilis));
		Logger.logar("Fim Jogo " + df.format(fimJogoMilis));
	}

	private static DecimalFormat dez = new DecimalFormat("00");
	private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public static String formatarTempo(long fullnum) {

		long minu = (fullnum / 60000);
		long seg = ((fullnum - (minu * 60000)) / 1000);
		if (minu > 0)
			return (minu) + ":" + dez.format(Math.abs(seg));
		else
			return seg + "";
	}

	private String obterKey(String value) {
		for (Iterator iterator = times.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String val = (String) times.get(key);
			if (value.equals(val)) {
				return key;
			}
		}
		return null;
	}

	public String tempoJogoFormatado() {
		if (Util.isNullOrEmpty(campoTimeComBola)) {
			return "";
		}
		return formatarTempo(tempoJogoMilis);
	}

	public String tempoRestanteJogoFormatado() {
		if (Util.isNullOrEmpty(campoTimeComBola)) {
			return "";
		}
		return formatarTempo(fimJogoMilis - System.currentTimeMillis());
	}

	public String tempoJogadaRestanteJogoFormatado() {
		if (Util.isNullOrEmpty(campoTimeComBola)) {
			return "";
		}
		verificaFalhaPorTempo();
		return formatarTempo(tempoJogadaFimMilis - System.currentTimeMillis());
	}

	public void verificaFalhaPorTempo() {
		if (tempoJogadaFimMilis < System.currentTimeMillis()) {
			campoTimeComBola = (campoTimeComBola == ConstantesMesa11.CAMPO_BAIXO ? ConstantesMesa11.CAMPO_CIMA
					: ConstantesMesa11.CAMPO_BAIXO);
			Logger.logar("verificaFalhaPorTempo");
			zerarTimerJogada();
		}
	}

	public void zerarTimerJogada() {
		tempoJogadaAtualMilis = System.currentTimeMillis();
		tempoJogadaFimMilis = tempoJogadaAtualMilis
				+ (tempoJogadaSegundos * 1000);
		Logger.logar("zerarTimerJogada");
	}

	public boolean veririficaVez(Botao b) {
		System.out.println("veririficaVez " + b.getTime());
		System.out.println("campoTimeComBola " + campoTimeComBola);
		return campoTimeComBola.equals(b.getTime().getCampo());
	}

	public String timeJogadaVez() {
		if (Util.isNullOrEmpty(campoTimeComBola)) {
			return "";
		}
		return timeCima.getCampo().equals(campoTimeComBola) ? timeCima
				.getNome() : timeBaixo.getNome();
	}

	public void reversaoJogada() {
		campoTimeComBola = (campoTimeComBola == ConstantesMesa11.CAMPO_BAIXO ? ConstantesMesa11.CAMPO_CIMA
				: ConstantesMesa11.CAMPO_BAIXO);
		Logger.logar("reversaoJogada");
		zerarTimerJogada();
	}

	public void zeraJogadaTime(Time time) {
		campoTimeComBola = time.getCampo();
		Logger.logar("zeraJogadaTime");
		zerarTimerJogada();

	}

	public Time getTimeCima() {
		return timeCima;
	}

	public void setTimeCima(Time timeCima) {
		this.timeCima = timeCima;
	}

	public Time getTimeBaixo() {
		return timeBaixo;
	}

	public void setTimeBaixo(Time timeBaixo) {
		this.timeBaixo = timeBaixo;
	}

	public boolean verificaBolaPertoGoleiroTime(Time time, Botao bola) {
		Goleiro goleiroTime = obterGoleiroTime(time);
		double distGoleiroTime = GeoUtil.distaciaEntrePontos(goleiroTime
				.getCentro(), bola.getCentro());
		double outraDistancia = 0;
		if (timeCima.equals(time)) {
			goleiroTime = obterGoleiroTime(timeBaixo);
			outraDistancia = GeoUtil.distaciaEntrePontos(goleiroTime
					.getCentro(), bola.getCentro());
		} else {
			goleiroTime = obterGoleiroTime(timeCima);
			outraDistancia = GeoUtil.distaciaEntrePontos(goleiroTime
					.getCentro(), bola.getCentro());
		}
		return distGoleiroTime < outraDistancia;
	}

	public Goleiro obterGoleiroTime(Time time) {
		List botoes = time.getBotoes();
		for (Iterator iterator = botoes.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			if (object instanceof Goleiro) {
				return (Goleiro) object;
			}
		}
		return null;
	}

	public void processarGol(Time time) {
		if (timeCima.equals(time)) {
			controleFormacao.posicionaTimeCima(timeCima, false);
			controleFormacao.posicionaTimeBaixo(timeBaixo, true);
		} else {
			controleFormacao.posicionaTimeCima(timeCima, true);
			controleFormacao.posicionaTimeBaixo(timeBaixo, false);
		}
		contralizaGoleiroBaixo();
		contralizaGoleiroCima();
		controleJogo.bolaCentro();
	}

	private void contralizaGoleiroBaixo() {
		for (int i = 0; i < 11; i++) {
			Long id = new Long(i + 20);
			Botao botao = (Botao) timeBaixo.getBotoes().get(i);
			if (botao instanceof Goleiro || botao.isGoleiro()) {
				botao.setCentro(mesaPanel.golBaixo());
				break;
			}
		}

	}

	private void contralizaGoleiroCima() {
		for (int i = 0; i < 11; i++) {
			Long id = new Long(i + 20);
			Botao botao = (Botao) timeCima.getBotoes().get(i);
			if (botao instanceof Goleiro || botao.isGoleiro()) {
				botao.setCentro(mesaPanel.golCima());
				break;
			}
		}

	}

	public void processarGolContra(Time time) {
		if (timeCima.equals(time)) {
			controleFormacao.posicionaTimeCima(timeCima, true);
			controleFormacao.posicionaTimeBaixo(timeBaixo, false);
		} else {
			controleFormacao.posicionaTimeCima(timeCima, false);
			controleFormacao.posicionaTimeBaixo(timeBaixo, true);
		}
		contralizaGoleiroBaixo();
		contralizaGoleiroCima();
		controleJogo.bolaCentro();
	}

}
