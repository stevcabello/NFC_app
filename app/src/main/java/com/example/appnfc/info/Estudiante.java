package com.example.appnfc.info;

public class Estudiante {
	
	private int Id;
	private String Nombre;
	private String Apellido;
	private String Usuario;
	private String Contrasenia;
	private String Email;
	private String Estado;
	private String NumeroMatricula;
	
	

	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getNombre() {
		return Nombre;
	}
	public void setNombre(String nombre) {
		Nombre = nombre;
	}
	
	public String getApellido() {
		return Apellido;
	}
	public void setApellido(String apellido) {
		Apellido = apellido;
	}
	public String getUsuario() {
		return Usuario;
	}
	public void setUsuario(String usuario) {
		Usuario = usuario;
	}
	public String getContrasenia() {
		return Contrasenia;
	}
	public void setContrasenia(String contrasenia) {
		Contrasenia = contrasenia;
	}
	
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getEstado() {
		return Estado;
	}
	public void setEstado(String estado) {
		Estado = estado;
	}
	
	public String getNumeroMatricula() {
		return NumeroMatricula;
	}
	public void setNumeroMatricula(String value) {
		NumeroMatricula = value;
	}
	

}
