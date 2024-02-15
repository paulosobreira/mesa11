/**
 * 
 */
package br.mesa11.visao;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * @author Sobreira 22/06/2010
 */
public class SpinnerEditor extends AbstractCellEditor implements
		TableCellEditor {
	JSpinner spinner;

	// Initializes the spinner.
	public SpinnerEditor(JSpinner spinner) {
		this.spinner = spinner;
	}

	// Prepares the spinner component and returns it.
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		spinner.setValue(value);
		return spinner;
	}

	// Enables the editor only for double-clicks.
	public boolean isCellEditable(EventObject evt) {
		return true;
	}

	// Returns the spinners current value.
	public Object getCellEditorValue() {
		return spinner.getValue();
	}
}
