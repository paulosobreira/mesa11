package br.nnpe.tos;

import java.io.Serializable;

/**
 * @author paulo.sobreira
 * 
 */
public class SessaoCliente implements Serializable {

	private static final long serialVersionUID = -1814045404166555104L;

	private long ulimaAtividade;

	private String nomeJogador;

	public long getUlimaAtividade() {
		return ulimaAtividade;
	}

	public void setUlimaAtividade(long ulimaAtividade) {
		this.ulimaAtividade = ulimaAtividade;
	}

	public String getNomeJogador() {
		return nomeJogador;
	}

	public void setNomeJogador(String apelido) {
		this.nomeJogador = apelido;
	}

	public boolean equals(Object obj) {
		if (nomeJogador == null) {
			return super.equals(obj);
		}
		SessaoCliente sessaoCliente = (SessaoCliente) obj;
		return nomeJogador.equals(sessaoCliente.getNomeJogador());
	}

	@Override
	public int hashCode() {
		if (nomeJogador == null) {
			return super.hashCode();
		}
		return nomeJogador.hashCode();
	}

	public String toString() {
		return nomeJogador;
	}
}
