package it.polito.tdp.food.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.food.db.FoodDao;


public class Model {
	public FoodDao dao;
	private SimpleDirectedWeightedGraph<Food, DefaultWeightedEdge> grafo;
	private Map<Integer, Food> idMap;
	LinkedList<Arco> archi;
	public Model() {
		dao= new FoodDao();
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		idMap=new HashMap<Integer, Food>();
		archi=new LinkedList<>();
	}
	
	public String creaGrafo(int x) {
		String s="";
		dao.listVertices(x, idMap);
		Graphs.addAllVertices(grafo, idMap.values());
		s+="Vertici: "+grafo.vertexSet().size()+"\n";
		
		dao.listEdges(x, archi, idMap);
		for(Arco a:archi)
		{
			if(grafo.containsVertex(a.getF1()) && grafo.containsVertex(a.getF2())) {
				if(a.peso>0)
					Graphs.addEdge(grafo, a.getF1(), a.getF2(), a.getPeso());
				if(a.peso<0)
					Graphs.addEdge(grafo, a.getF2(), a.getF1(), -a.getPeso());
					
				
			}
		}
		s+="Archi: "+grafo.edgeSet().size()+"\n";

		return s;
	}
	public Set<Food> cibi(){
		return grafo.vertexSet();
	}
	
	public String minimi(Food f)
	{
		LinkedList<Food> vicini =new LinkedList<Food>(Graphs.neighborListOf(grafo, f));
		LinkedList<Arco> pesi= new LinkedList<Arco>();
		for(Food f1: vicini)
		{
			if(grafo.getEdge(f, f1)!=null)
				pesi.add(new Arco(f,f1,grafo.getEdgeWeight(grafo.getEdge(f, f1))));
			else
				pesi.add(new Arco(f,f1,grafo.getEdgeWeight(grafo.getEdge(f1, f))));
		}			
		Collections.sort(pesi, (p1,p2)->(int)((p1.peso-p2.peso)*100));
		String s="";
		if(pesi.size()<5)
		{
			
			for(int i=0;i<pesi.size();i++)
				s+=(pesi.get(i).f2)+"\n";
		}
		if(pesi.size()>=5)
		{
			
			for(int i=0;i<5;i++)
				s+=(pesi.get(i).f2)+"\n";
		}
		return s;
	}
	public LinkedList<Arco> prossimi(Food f)
	{
		LinkedList<Food> vicini =new LinkedList<Food>(Graphs.successorListOf(grafo, f));
		LinkedList<Arco> pesi= new LinkedList<Arco>();
		for(Food f1: vicini)
		{
			if(grafo.getEdge(f, f1)!=null)
				pesi.add(new Arco(f,f1,grafo.getEdgeWeight(grafo.getEdge(f, f1))));
			else
				pesi.add(new Arco(f,f1,grafo.getEdgeWeight(grafo.getEdge(f1, f))));
		}			
		Collections.sort(pesi, (p1,p2)->(int)((p1.peso-p2.peso)*100));
		
		return pesi;
	}
	
	//simulazione
	
	int kstazioni;
	Food iniziale;
	private PriorityQueue<Cucina> queue;
	public int init(int k, Food f) {
		System.out.println("Inizializzo");
		this.queue = new PriorityQueue<>();
		LinkedList<Arco> iniziali= new LinkedList<Arco>(prossimi(f));
		System.out.println(iniziali.size());
		for(int i=0; i<kstazioni||i<iniziali.size();i++)
		{
			queue.add(new Cucina(iniziali.get(i).f2,iniziali.get(i).peso));
			System.out.println("Creata cucina");
		}
	return run();
		
	}
	
	public int run() {
		int preparati= queue.size();
		double tempotot=0;
		while(!this.queue.isEmpty()) {
			Cucina c=this.queue.poll();
			System.out.println("Preparazione completata");
			preparati++;
			LinkedList<Arco> prossime= new LinkedList<Arco>(prossimi(c.f));
			if(prossime.size()!=0) {
				c.f=prossime.get(0).f2;
				c.tempo=prossime.get(0).peso+c.tempo;
				tempotot=prossime.get(0).peso+c.tempo;
				queue.add(c);
				
			}
		}
		return preparati;
	}
	
}
