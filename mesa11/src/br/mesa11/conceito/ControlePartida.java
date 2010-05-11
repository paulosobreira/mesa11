package br.mesa11.conceito;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
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
import br.mesa11.ConstantesMesa11;
import br.mesa11.visao.MesaPanel;
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
		Map botoes = controleJogo.getBotoes();
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
		timeCima = new Time();
		timeCima.setId(1);
		timeCima.setCampo(ConstantesMesa11.CAMPO_CIMA);
		timeCima.setNome((String) timesCima.getSelectedItem());
		for (int i = 0; i < 10; i++) {
			Long id = new Long(i + 1);
			Botao botao = new Botao(id);
			botao.setTime(timeCima);
			botao.setImagem(obterKey((String) timesCima.getSelectedItem()));
			timeCima.getBotoes().add(botao);
			botoes.put(botao.getId(), botao);
		}
		ControlePosicionamento controleFormacao = new ControlePosicionamento(
				controleJogo);

		controleFormacao.posicionaTimeCima(timeCima, bolaCima.isSelected());

		timeBaixo = new Time();
		timeBaixo.setId(2);
		timeBaixo.setCampo(ConstantesMesa11.CAMPO_BAIXO);
		timeBaixo.setNome((String) timesBaixo.getSelectedItem());
		for (int i = 0; i < 10; i++) {
			Long id = new Long(i + 11);
			Botao botao = new Botao(id);
			botao.setTime(timeBaixo);
			botao.setImagem(obterKey((String) timesBaixo.getSelectedItem()));
			timeBaixo.getBotoes().add(botao);
			botoes.put(botao.getId(), botao);

		}
		controleFormacao.posicionaTimeBaixo(timeBaixo, bolaBaixo.isSelected());
		MesaPanel mesaPanel = controleJogo.getMesaPanel();
		Goleiro goleiro1 = new Goleiro(100);
		goleiro1.setCentro(mesaPanel.golCima());
		timeCima.getBotoes().add(goleiro1);
		goleiro1.setTime(timeCima);
		Goleiro goleiro2 = new Goleiro(200);
		goleiro2.setCentro(mesaPanel.golBaixo());
		timeBaixo.getBotoes().add(goleiro2);
		goleiro2.setTime(timeBaixo);
		botoes.put(goleiro1.getId(), goleiro1);
		botoes.put(goleiro2.getId(), goleiro2);
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao b = (Botao) botoes.get(id);
			if (b instanceof Goleiro) {
				continue;
			}
			b.setImgBotao(CarregadorRecursos.carregaImg(b.getImagem()));
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

}
