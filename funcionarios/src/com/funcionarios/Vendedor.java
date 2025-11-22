package com.funcionarios;

public class Vendedor extends Funcionario{
	
	private double comissao;
	private double salarioTotal = salario + comissao;
	
	public Vendedor(String name, String cpf, double salario, double comissao) {
		super(name, cpf, salario);
		this.comissao = comissao;
	}

	public double getComissao() {
		return comissao;
	}

	public void setComissao(double comissao) {
		this.comissao = comissao;
	}

	public double getSalarioTotal() {
		return salarioTotal;
	}

	public void setSalarioTotal(double salarioTotal) {
		this.salarioTotal = salarioTotal;
	}
	
}
