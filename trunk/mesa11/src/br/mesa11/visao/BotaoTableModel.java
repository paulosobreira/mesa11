package br.mesa11.visao;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import br.hibernate.Botao;
import br.recursos.Lang;

public class BotaoTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6491939723021276044L;
	protected List data;

	public BotaoTableModel(List data) {
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
							return 0;
						}
					});
					fireTableDataChanged();

					break;

				case 1:
					Collections.sort(data, new Comparator() {
						public int compare(Object arg0, Object arg1) {
							return 0;
						}
					});
					fireTableDataChanged();

					break;

				case 2:
					Collections.sort(data, new Comparator() {
						public int compare(Object arg0, Object arg1) {
							return 0;
						}
					});
					fireTableDataChanged();

					break;
				case 3:
					Collections.sort(data, new Comparator() {
						public int compare(Object arg0, Object arg1) {
							return 0;
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
		default:
			return "";
		}
	}

	public int getRowCount() {
		return data.size();
	}

	public int getColumnCount() {
		return 7;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Botao botao = (Botao) data.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return botao.getNome();

		case 1:
			return botao.getNumero();

		case 2:
			return botao.isTitular() ? "S" : "N";
		case 3:
			return botao.isGoleiro() ? "S" : "N";
		case 4:
			return botao.getPrecisao() + "";
		case 5:
			return botao.getForca() + "";
		case 6:
			return botao.getDefesa() + "";

		default:
			return null;
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public Class getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return String.class;
		case 2:
			return String.class;
		case 3:
			return String.class;
		case 4:
			return String.class;
		case 5:
			return String.class;
		case 6:
			return String.class;
		default:
			return null;
		}
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Botao botao = (Botao) data.get(rowIndex);

		switch (columnIndex) {
		case 0:

			// String data = (String) aValue;
			//
			// if (data.length() >= 16) {
			// taskBean.setHoraTarefa(TimeUtil.string2GregorianCalendar(data));
			// }

			break;

		case 1:
			// taskBean.setDescricao((String) aValue);

			break;

		case 2:
			// taskBean.setAtivada(((Boolean) aValue).booleanValue());

		case 3:
			// if (aValue instanceof String)
			// taskBean.setTipoAtividade((String) aValue);
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
