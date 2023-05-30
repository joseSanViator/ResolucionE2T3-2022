package entidades;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Jugador implements Serializable{
	@Id
	private String dni;
	private String nombre;
	private int edad;
	private String posicion;
	
	
	public Jugador() {

	}
	
	public Jugador(String dni, String nombre, int edad, String posicion) {
		this.dni = dni;
		this.nombre = nombre;
		this.edad = edad;
		this.posicion = posicion;
	}
	
	public boolean equals(Jugador otro) {
		return this.dni.equalsIgnoreCase(otro.getDni());
	}
	
	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public int getEdad() {
		return edad;
	}


	public void setEdad(int edad) {
		this.edad = edad;
	}


	public String getPosicion() {
		return posicion;
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}
	
}

