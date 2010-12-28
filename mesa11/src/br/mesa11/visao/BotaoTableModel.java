package br.mesa11.visao;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import br.hibernate.Botao;
import br.mesa11.conceito.ControleJogo;
import br.nnpe.Logger;
import br.recursos.Lang;

public class BotaoTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6491939723021276044L;
	protected List data;
	private ControleJogo controleJogo;

	public BotaoTableModel(List data, ControleJogo controleJogo) {
		this.controleJogo = controleJogo;
		this.data = data;
	}

	public void addMouseListener(final JTable table) {
		table.getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				int tableColumn = table.columnAtPoint(event.getPoint());
				int modelColumn = table.convertColumnIndexToModel(tableColumn);

				switch (modelColumn) {
				case 0:
					Collections.sort(data, new Comparator() {
						public int compare(Object arg0, Object arg1) {
							Botao botao0 = (Botao) arg0;
							Botao botao1 = (Botao) arg1;
							if (botao0.getNome() == null
									|| botao1.getNome() == null) {
								return 0;
							}
							return botao0.getNome().compareTo(botao1.getNome());
						}
					});
					fireTableDataChanged();

					break;

				case 1:
					Collections.sort(data, new Comparator() {
						public int compare(Object arg0, Object arg1) {
							Botao botao0 = (Botao) arg0;
							Botao botao1 = (Botao) arg1;
							if (botao0.getNumero() == null
									|| botao1.getNumero() == null) {
								return 0;
							}
							return botao0.getNumero().compareTo(
									botao1.getNumero());
						}
					});
					fireTableDataChanged();

					break;

				case 2:
					Collections.sort(data, new Comparator() {
						public int compare(Object arg0, Object arg1) {
							Botao botao0 = (Botao) arg0;
							Botao botao1 = (Botao) arg1;
							return new Boolean(botao0.isTitular())
									.compareTo(new Boolean(botao1.isTitular()));
						}
					});
					fireTableDataChanged();

					break;
				case 3:
					Collections.sort(data, new Comparator() {
						public int compare(Object arg0, Object arg1) {
							Botao botao0 = (Botao) arg0;
							Botao botao1 = (Botao) arg1;
							return new Boolean(botao0.isGoleiro())
									.compareTo(new Boolean(botao1.isGoleiro()));
						}
					});
					fireTableDataChanged();

					break;
				case 4:
					Collections.sort(data, new Comparator() {
						public int compare(Object arg0, Object arg1) {
							Botao botao0 = (Botao) arg0;
							Botao botao1 = (Botao) arg1;
							if (botao0.getPrecisao() == null
									|| botao1.getPrecisao() == null) {
								return 0;
							}
							return botao1.getPrecisao().compareTo(
									botao0.getPrecisao());
						}
					});
					fireTableDataChanged();

					break;
				case 5:
					Collections.sort(data, new Comparator() {
						public int compare(Object arg0, Object arg1) {
							Botao botao0 = (Botao) arg0;
							Botao botao1 = (Botao) arg1;
							if (botao0.getForca() == null
									|| botao1.getForca() == null) {
								return 0;
							}
							return botao1.getForca().compareTo(
									botao0.getForca());
						}
					});
					fireTableDataChanged();

					break;
				case 6:
					Collections.sort(data, new Comparator() {
						public int compare(Object arg0, Object arg1) {
							Botao botao0 = (Botao) arg0;
							Botao botao1 = (Botao) arg1;
							if (botao0.getDefesa() == null
									|| botao1.getDefesa() == null) {
								return 0;
							}
							return botao1.getDefesa().compareTo(
									botao0.getDefesa());
						}
					});
					fireTableDataChanged();

					break;

				default:
					break;
				}
			}
		});
	}

	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return Lang.msg("nomeBotao");
		case 1:
			return Lang.msg("numeroBotao");
		case 2:
			return Lang.msg("titular");
		case 3:
			return Lang.msg("goleiro");
		case 4:
			return Lang.msg("precisao");
		case 5:
			return Lang.msg("forca");
		case 6:
			return Lang.msg("defesa");
		case 7:
			return Lang.msg("imgNome");
		case 8:
			return Lang.msg("imagem");
		default:
			return "";
		}
	}

	public int getRowCount() {
		return data.size();
	}

	public int getColumnCount() {
		return 9;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Botao botao = (Botao) data.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return botao.getNome();

		case 1:
			return botao.getNumero();

		case 2:
			return botao.isTitular() ? Lang.msg("sim") : Lang.msg("nao");
		case 3:
			return botao.isGoleiro() ? Lang.msg("sim") : Lang.msg("nao");
		case 4:
			return botao.getPrecisao() == null ? 0 : botao.getPrecisao();
		case 5:
			return botao.getForca() == null ? 0 : botao.getForca();
		case 6:
			return botao.getDefesa() == null ? 0 : botao.getDefesa();
		case 7:
			return botao.getImagem();
		case 8:
			String arquivo = botao.getImagem();
			if (arquivo == null || !arquivo.endsWith("jpg")
					|| controleJogo == null) {
				return "";
			}
			URL url = null;
			try {
				url = new URL(controleJogo.getMesa11Applet().getCodeBase()
						+ "midia/" + arquivo);
				Logger.logar(url);
			} catch (MalformedURLException e) {
				Logger.logarExept(e);
			}
			ImageIcon icon = new ImageIcon(url);
			return icon;
		default:
			return null;
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex < 8)
			return true;
		else
			return false;
	}

	public Class getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return Integer.class;
		case 2:
			return String.class;
		case 3:
			return String.class;
		case 4:
			return Integer.class;
		case 5:
			return Integer.class;
		case 6:
			return Integer.class;
		case 7:
			return String.class;
		case 8:
			return ImageIcon.class;
		default:
			return super.getColumnClass(columnIndex);
		}
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Botao botao = (Botao) data.get(rowIndex);

		switch (columnIndex) {
		case 0:
			botao.setNome((String) aValue);
			break;
		case 1:
			botao.setNumero((Integer) aValue);
			break;
		case 2:
			botao.setTitular(Lang.key((String) aValue).equals("sim") ? true
					: false);
			break;
		case 3:
			botao.setGoleiro(Lang.key((String) aValue).equals("sim") ? true
					: false);
			break;
		case 4:
			botao.setPrecisao((Integer) aValue);
			break;
		case 5:
			botao.setForca((Integer) aValue);
			break;
		case 6:
			botao.setDefesa((Integer) aValue);
			break;
		case 7:
			botao.setImagem((String) aValue);
			break;
		default:
			break;
		}
	}

	public void inserirLinha(Object bean) {
		int row = data.size();
		data.add(bean);
		fireTableRowsInserted(row, row);
	}

	public void removerLinha(int row) {
		data.remove(row);
		fireTableRowsDeleted(row, row);
	}
}
