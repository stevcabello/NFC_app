package com.example.appnfc.info;

import java.util.List;



public class Test {
	private int IdMateria;
	private int IdTest;
	private int IdPregunta;
	private String Pregunta;
	private List<OpcionesMultiples> OpcMultiples;
	
	public int getIdMateria()
	{ return IdMateria;}
	public void setIdMateria(int Value)
	{ this.IdMateria = Value;}
	
	public int getIdTest()
	{ return IdTest;}
	public void setIdTest(int Value)
	{ this.IdTest = Value;}
	
	public int getIdPregunta()
	{ return IdPregunta;}
	public void setIdPregunta(int Value)
	{ this.IdPregunta = Value;}
	
	
	public String getPregunta()
	{ return Pregunta;}
	public void setPregunta(String Value)
	{ this.Pregunta = Value;}
	
	
	
	public List<OpcionesMultiples> getOpcMultiples() 
	{	return OpcMultiples;	}
	public void setOpcMultiples(List<OpcionesMultiples> Value) 
	{	OpcMultiples = Value;	}
	
}
