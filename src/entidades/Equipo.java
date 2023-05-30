package entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="equiposLiga")
public class Equipo implements Serializable {
	@Id
	@GeneratedValue
	private int id;
	private String nombre;
	private String division;
	
	@OneToMany
	private List<Jugador> jugadores;
	
	
	public List<Jugador> getJugadores() {
		return jugadores;
	}

	public void setJugadores(List<Jugador> jugadores) {
		this.jugadores = jugadores;
	}

	public Equipo() {
	}
	
	public Equipo(int id, String nombre, String division) {
		this.id = id;
		this.nombre = nombre;
		this.division = division;
		this.jugadores=new ArrayList<Jugador>();
	}
	
	
	public Equipo(int id, String nombre, String division,List<Jugador> jugadores) {
		this.id = id;
		this.nombre = nombre;
		this.division = division;
		this.jugadores=jugadores;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
}
