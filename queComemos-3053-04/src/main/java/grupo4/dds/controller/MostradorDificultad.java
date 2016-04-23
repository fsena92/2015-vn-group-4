package grupo4.dds.controller;

import java.util.ArrayList;

import queComemos.entrega3.dominio.Dificultad;

public class MostradorDificultad {
	Dificultad dificultad;
	
	public MostradorDificultad(Dificultad d){
		dificultad=d;
	}
	
	public static ArrayList<MostradorDificultad> getMostradores(Dificultad[] dificultades){
		ArrayList<MostradorDificultad> mostradores=new ArrayList<MostradorDificultad>();
		
		for(int x=0;x<dificultades.length;x++) {
			mostradores.add(new MostradorDificultad(dificultades[x]));
		}
		
		return mostradores;
	}
	
	public String getName(){
		return dificultad.toString();
	}
}
