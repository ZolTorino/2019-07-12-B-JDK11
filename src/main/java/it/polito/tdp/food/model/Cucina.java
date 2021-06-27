package it.polito.tdp.food.model;

public class Cucina implements Comparable<Cucina> {
	Food f;
	double tempo;
	@Override
	public int compareTo(Cucina c) {
		
		return (int)((tempo*100)-(c.tempo*100));
	}
	public Cucina(Food f, double tempo) {
		super();
		this.f = f;
		this.tempo = tempo;
	}
	
}
