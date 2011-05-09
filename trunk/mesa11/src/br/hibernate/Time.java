package br.hibernate;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import br.nnpe.Logger;
import br.nnpe.Util;

@Entity
public class Time extends Mesa11Dados {

	private String campo;
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "time", targetEntity = Botao.class)
	private List<Botao> botoes = new ArrayList<Botao>();
	@Column(nullable = false, unique = true)
	private String nomeAbrev;
	@Column(nullable = false, unique = true)
	private String nome;
	@Column(nullable = false)
	private String nomeJogador;
	private Integer qtdePontos;
	private boolean corMeiaNumero1;
	private boolean corMeiaNumero2;
	private boolean agol;
	private boolean controladoCPU;
	private boolean segundoUniforme;
	private String imagem;
	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	private int cor1RGB;
	private int cor2RGB;
	private int cor3RGB;
	private int cor4RGB;
	private int cor5RGB;
	private int cor6RGB;
	private int tipoUniforme1 = 0;
	private int tipoUniforme2 = 0;

	public boolean isControladoCPU() {
		return controladoCPU;
	}

	public void setControladoCPU(boolean controladoCPU) {
		this.controladoCPU = controladoCPU;
	}

	public int getTipoUniforme1() {
		return tipoUniforme1;
	}

	public void setTipoUniforme1(int tipoUniforme1) {
		this.tipoUniforme1 = tipoUniforme1;
	}

	public int getTipoUniforme2() {
		return tipoUniforme2;
	}

	public void setTipoUniforme2(int tipoUniforme2) {
		this.tipoUniforme2 = tipoUniforme2;
	}

	public int getCor1() {
		if (segundoUniforme) {
			return cor4RGB;
		} else {
			return cor1RGB;
		}
	}

	public int getCor2() {
		if (segundoUniforme) {
			return cor5RGB;
		} else {
			return cor2RGB;
		}
	}

	public int getCor3() {
		if (segundoUniforme) {
			return cor6RGB;
		} else {
			return cor3RGB;
		}
	}

	public int getCor1RGB() {
		return cor1RGB;
	}

	public boolean isCorMeiaNumero1() {
		return corMeiaNumero1;
	}

	public void setCorMeiaNumero1(boolean corMeiaNumero1) {
		this.corMeiaNumero1 = corMeiaNumero1;
	}

	public boolean isCorMeiaNumero2() {
		return corMeiaNumero2;
	}

	public void setCorMeiaNumero2(boolean corMeiaNumero2) {
		this.corMeiaNumero2 = corMeiaNumero2;
	}

	public void setCor1RGB(int cor1rgb) {
		cor1RGB = cor1rgb;
	}

	public int getCor2RGB() {
		return cor2RGB;
	}

	public void setCor2RGB(int cor2rgb) {
		cor2RGB = cor2rgb;
	}

	public int getCor3RGB() {
		return cor3RGB;
	}

	public void setCor3RGB(int cor3rgb) {
		cor3RGB = cor3rgb;
	}

	public int getCor4RGB() {
		return cor4RGB;
	}

	public void setCor4RGB(int cor4rgb) {
		cor4RGB = cor4rgb;
	}

	public int getCor5RGB() {
		return cor5RGB;
	}

	public void setCor5RGB(int cor5rgb) {
		cor5RGB = cor5rgb;
	}

	public int getCor6RGB() {
		return cor6RGB;
	}

	public void setCor6RGB(int cor6rgb) {
		cor6RGB = cor6rgb;
	}

	public Integer getQtdePontos() {
		return qtdePontos;
	}

	public void setQtdePontos(Integer qtdePontos) {
		this.qtdePontos = qtdePontos;
	}

	public String getCampo() {
		return campo;
	}

	public void setCampo(String campo) {
		this.campo = campo;
	}

	public List<Botao> getBotoes() {
		return botoes;
	}

	public void setBotoes(List<Botao> botoes) {
		this.botoes = botoes;
	}

	public String getNomeAbrev() {
		return nomeAbrev;
	}

	public void setNomeAbrev(String nome) {
		this.nomeAbrev = nome;
	}

	public boolean isAgol() {
		return agol;
	}

	public void setAgol(boolean agol) {
		this.agol = agol;
	}

	@Override
	public String toString() {
		return " Nome: " + nomeAbrev + " Campo: " + campo;
	}

	public static void main(String[] args) {
		Logger.logar(Color.BLUE.toString());
		Color color = new Color(100);
		color.getRGB();

	}

	public boolean isCorMeiaNumero() {
		if (segundoUniforme) {
			return isCorMeiaNumero2();
		} else {
			return isCorMeiaNumero1();
		}
	}

	public boolean isSegundoUniforme() {
		return segundoUniforme;
	}

	public void setSegundoUniforme(boolean segundoUniforme) {
		this.segundoUniforme = segundoUniforme;
	}

	public Goleiro obterGoleiro() {
		List botoes = getBotoes();
		for (Iterator iterator = botoes.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			if (object instanceof Goleiro) {
				return (Goleiro) object;
			}
		}
		return null;
	}

	public String getNomeJogador() {
		return nomeJogador;
	}

	public void setNomeJogador(String nomeJogador) {
		this.nomeJogador = nomeJogador;
	}

	@Override
	public boolean equals(Object obj) {
		if (getId() == null && !Util.isNullOrEmpty(nomeAbrev)) {
			Time time = (Time) obj;
			return nome.equals(time.getNome());
		}
		return super.equals(obj);
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public int hashCode() {
		if (getId() == null && !Util.isNullOrEmpty(nomeAbrev)) {
			return nomeAbrev.hashCode();
		}
		return super.hashCode();
	}
}
