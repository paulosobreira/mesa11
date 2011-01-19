package br.nnpe;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import br.recursos.Lang;

/**
 * @author Paulo Sobreira Criado em 25/10/2009 as 17:27:25
 */
public class Logger {

	public static Map topExceptions = new HashMap();

	public static boolean debug = false;

	public static boolean novaSession = false;

	public static void topExecpts(Exception e) {
		if (debug) {
			logarExept(e);
		}
		if (topExceptions == null) {
			topExceptions = new HashMap();
		}
		if (topExceptions.size() < 100) {
			StackTraceElement[] trace = e.getStackTrace();
			StringBuffer retorno = new StringBuffer();
			int size = ((trace.length > 5) ? 5 : trace.length);
			retorno.append(e.getClass() + " - " + e.getLocalizedMessage()
					+ "<br>");
			for (int i = 0; i < size; i++)
				retorno.append(trace[i] + "<br>");
			String val = retorno.toString();
			Integer numExceps = (Integer) topExceptions.get(val);
			if (numExceps == null) {
				topExceptions.put(val, new Integer(1));
			} else {
				topExceptions.put(val, new Integer(numExceps.intValue() + 1));
			}
		}

	}

	public static void logar(String val) {
		if (debug) {
			System.out.println(val);
		}

	}

	public static void logar(int val) {
		if (debug) {
			System.out.println(val);
		}

	}

	public static void logar(double val) {
		if (debug) {
			System.out.println(val);
		}

	}

	public static void logar(Object val) {
		if (debug) {
			System.out.println(val);
		}
	}

	public static void logarExept(Throwable e) {
		if (debug) {
			e.printStackTrace();
		} else if (e instanceof Exception) {
			topExecpts((Exception) e);
		}
	}

	public static void logarExeptVisual(Throwable e, Component component) {
		if (debug) {
			StackTraceElement[] trace = e.getStackTrace();
			StringBuffer retorno = new StringBuffer();
			int size = ((trace.length > 10) ? 10 : trace.length);

			for (int i = 0; i < size; i++)
				retorno.append(trace[i] + "\n");
			JOptionPane.showMessageDialog(component, retorno.toString(), Lang
					.msg("erroEnviando"), JOptionPane.ERROR_MESSAGE);
			Logger.logarExept(e);
		} else if (e instanceof Exception) {
			topExecpts((Exception) e);
		}
	}
}
