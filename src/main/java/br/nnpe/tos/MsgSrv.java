package br.nnpe.tos;

import java.io.Serializable;

import br.recursos.Lang;

/**
 * @author Paulo Sobreira Criado em 12/08/2007 as 13:02:01
 */
public class MsgSrv implements Serializable {
	private String messageString;

	/**
	 * @param messageString
	 */
	public MsgSrv(String messageString) {
		super();
		this.messageString = messageString;
	}

	public String getMessageString() {
		return messageString;
	}

	public void setMessageString(String messageString) {
		this.messageString = messageString;
	}

}
