package grupo4.dds.controller;

public class Consulta {
	public String nombre;
	public String dificultad;
	public String temporada;
	public String desde;
	public String hasta;
	
	public Consulta(String n,String di,String t,String d,String h){
		nombre=n;
		dificultad=di;
		temporada=t;
		desde=d;
		hasta=h;
	}
	
	public String getNombre() {
		return nombre;
	}
	public String getDificultad() {
		return dificultad;
	}
	public String getTemporada() {
		return temporada;
	}
	public String getDesde() {
		return desde;
	}
	public String getHasta() {
		return hasta;
	}

}
