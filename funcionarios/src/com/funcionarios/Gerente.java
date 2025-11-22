package com.funcionarios;

public class Gerente extends Funcionario {
	
	private double bonus;
	private double salarioTotal = salario + bonus;
	
	
	public Gerente(String name, String cpf, double salario, double bonus) {
		super(name, cpf, salario);
		this.bonus = bonus;
	}


	public double getBonus() {
		return bonus;
	}


	public void setBonus(double bonus) {
		this.bonus = bonus;
	}


	public double getSalarioTotal() {
		return salarioTotal;
	}


	public void setSalarioTotal(double salarioTotal) {
		this.salarioTotal = salarioTotal;
	}
	
}
