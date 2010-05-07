package br.mesa11.conceito;

import java.awt.GridLayout;
import java.io.IOException;
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
import br.recursos.CarregadorRecursos;
import br.recursos.Lang;

public class GenrenciadorBotoes {

	private ControleJogo controleJogo;
	private Hashtable times;

	public GenrenciadorBotoes(ControleJogo controleJogo) {
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
		escolhaTimesPanel.setBorder(new TitledBorder("baterCentro"));
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
		JFrame frame = controleJogo.getFrame();
		Map botoes = controleJogo.getBotoes();
		int val = JOptionPane.showConfirmDialog(frame, escolhaTimesPanel, Lang
				.msg("escolhaTimes"), JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		if (val != JOptionPane.YES_OPTION) {
			return;
		}
		Time timeCima = new Time();
		timeCima.setCampo(ConstantesMesa11.CAMPO_CIMA);
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

		Time timeBaixo = new Time();
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
		Goleiro goleiro2 = new Goleiro(200);
		goleiro2.setCentro(mesaPanel.golBaixo());
		timeBaixo.getBotoes().add(goleiro2);
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

}
