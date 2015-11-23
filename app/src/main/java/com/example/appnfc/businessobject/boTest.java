package com.example.appnfc.businessobject;

import android.util.Log;

import com.example.appnfc.Global;
import com.example.appnfc.info.OpcionesMultiples;
import com.example.appnfc.info.RegistroTest;
import com.example.appnfc.info.Test;
import com.example.appnfc.info.TestList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;



public class boTest {
	
	public String ErrorMsg = "";

	/**
	 * Get the test template
	 * @param idestudiante .. student id
	 * @param idmateria .. subject id
	 * @param idtest .. test id
	 * @return the list of test
	 */
	public Test[] GetPlantillaTest(int idestudiante, int idmateria, int idtest)
	{
		Test[] vResult= null;
		HttpClient httpClient = new DefaultHttpClient();
		//HttpGet del = new HttpGet("http://"+ Global.DireccionIPWebServices +"/Api/Test/Test/" + idmateria);
		
		String Uri = "http://"+ Global.DireccionIPWebServices +"/Api/Test/Test?";
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		
	    params.add(new BasicNameValuePair("idestudiante", String.valueOf(idestudiante)));
	    params.add(new BasicNameValuePair("idmateria", String.valueOf(idmateria)));
	    params.add(new BasicNameValuePair("idtest", String.valueOf(idtest)));

	    String paramString = URLEncodedUtils.format(params, "utf-8");

	    Uri += paramString;
		HttpGet del = new HttpGet(Uri);
		
		del.setHeader("content-type", "application/json");
		
		try{
			
			HttpResponse resp = httpClient.execute(del);
	        String respStr = EntityUtils.toString(resp.getEntity());
	        Log.i("Aja", respStr);
	        Global.TestJson = respStr;
	        
	        
	        JSONArray respJSON = new JSONArray(respStr);
	        Log.i("Elementos", String.valueOf(respJSON.length()));
	 
	        
	        
	        vResult = new Test[respJSON.length()];
	        
	        
	        for(int i=0; i<respJSON.length(); i++)
	        {
	            JSONObject obj = respJSON.getJSONObject(i);
	 
	            Log.i("IdPRegunta", String.valueOf(obj.getInt("IdPregunta")));
	            
	            Test reg = new Test();
	            reg.setIdMateria(obj.getInt("IdMateria"));
	            reg.setIdTest(obj.getInt("IdTest"));
				reg.setIdPregunta(obj.getInt("IdPregunta"));
				reg.setPregunta(obj.getString("Pregunta"));
				
				JSONArray objOpcionesMultiples = new JSONArray();
				objOpcionesMultiples = obj.getJSONArray("OpcionesMultiples");
				List<OpcionesMultiples> lstOpcMult = new ArrayList<OpcionesMultiples>();
				
				for(int a=0;a<objOpcionesMultiples.length();a++)
				{
					JSONObject cItem= objOpcionesMultiples.getJSONObject(a);
					
					OpcionesMultiples objOpcMult = new OpcionesMultiples();
					objOpcMult.setRespuesta(cItem.getString("Respuesta"));
					objOpcMult.setIdRespuesta(cItem.getInt("IdRespuesta"));
					lstOpcMult.add(objOpcMult);
					
				}
				
				reg.setOpcMultiples(lstOpcMult);
				
				vResult[i]=reg;
	            
	        }
			
			return vResult;
		}
		catch(Exception ex)
		{
			ErrorMsg = ex.getMessage();
			return vResult;
		}
		
		
	}

	
	public Test[] GetPlantillaTest2(String test)
	{
		Test[] vResult= null;
		
		
		try{
			
			
	        String respStr = test;
	        Log.i("Aja", respStr);
	       // Global.TestJson = respStr;
	        
	        
	        JSONArray respJSON = new JSONArray(respStr);
	        Log.i("Elementos", String.valueOf(respJSON.length()));
	 
	        
	        
	        vResult = new Test[respJSON.length()];
	        
	        
	        for(int i=0; i<respJSON.length(); i++)
	        {
	            JSONObject obj = respJSON.getJSONObject(i);
	 
	            Log.i("IdPRegunta", String.valueOf(obj.getInt("IdPregunta")));
	            
	            Test reg = new Test();
	            reg.setIdMateria(obj.getInt("IdMateria"));
	            reg.setIdTest(obj.getInt("IdTest"));
				reg.setIdPregunta(obj.getInt("IdPregunta"));
				reg.setPregunta(obj.getString("Pregunta"));
				
				JSONArray objOpcionesMultiples = new JSONArray();
				objOpcionesMultiples = obj.getJSONArray("OpcionesMultiples");
				List<OpcionesMultiples> lstOpcMult = new ArrayList<OpcionesMultiples>();
				
				for(int a=0;a<objOpcionesMultiples.length();a++)
				{
					JSONObject cItem= objOpcionesMultiples.getJSONObject(a);
					
					OpcionesMultiples objOpcMult = new OpcionesMultiples();
					objOpcMult.setRespuesta(cItem.getString("Respuesta"));
					objOpcMult.setIdRespuesta(cItem.getInt("IdRespuesta"));
					lstOpcMult.add(objOpcMult);
					
				}
				
				reg.setOpcMultiples(lstOpcMult);
				
				vResult[i]=reg;
	            
	        }
			
			return vResult;
		}
		catch(Exception ex)
		{
			ErrorMsg = ex.getMessage();
			return vResult;
		}
		
		
	}

	/**
	 * Save a test
	 * @param item .. the test
	 * @param Entidad C for saving the header of the test, D for savind the Details of the test
	 * @return
	 */
	public String setRegistrarTest(RegistroTest item, char Entidad)
	{
		String vResult = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = null;
		if (Entidad == 'C')
		{
			post = new HttpPost("http://"+ Global.DireccionIPWebServices +"/Api/Test/TestCabecera/" + 0);
			
		}
		else 
		{
			if(Entidad == 'D')
			{
				post = new HttpPost("http://"+ Global.DireccionIPWebServices +"/Api/Test/TestDetalle/" + 0);
			}
		}
		post.setHeader("content-type", "application/json");
		
		try{
			
			JSONObject dato = new JSONObject();
			 
	        dato.put("IdMateria", item.getIdMateria());
	        dato.put("IdTest", item.getIdTest());
	        dato.put("IdEstudiante", item.getIdEstudiante());
	        dato.put("IdPregunta", item.getIdPregunta());
	        dato.put("Opcion", item.getOpcion());


	        StringEntity entity = new StringEntity(dato.toString());
	        post.setEntity(entity);
			
			HttpResponse resp = httpClient.execute(post);
	        String respStr = EntityUtils.toString(resp.getEntity());
	        String[] ArrayResp = respStr.split(",");
	        Log.i("Respuesta1", ArrayResp[0].substring(1));
	        Log.i("Respuesta2", ArrayResp[1].substring(0,1));
        
	        return ArrayResp[0].substring(1);
		}
		catch(Exception ex)
		{
			Log.i("HORROR", ex.getMessage());
			ErrorMsg = ex.getMessage();
			return vResult;
		}
	}

	/**
	 * Get test per subject
	 * @param idMateria .. subject id
	 * @param estado .. A for actives tests, C for completed tests
	 * @param idEstudiante .. student id
	 * @return the list of tests
	 */
	public TestList[] GetTestxMateria(int idMateria, String estado, int idEstudiante)
	{
		TestList[] vResult= null;
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpGet del;
		
		if (estado.equals("A")){
			//del = new HttpGet("http://"+ Global.DireccionIPWebServices +"/Api/Test/ObtenerTestActivosxMateria/" + idMateria);
			
			String Uri = "http://"+ Global.DireccionIPWebServices +"/Api/Test/ObtenerTestActivosxMateria?";
			List<NameValuePair> params = new LinkedList<NameValuePair>();
			
		    params.add(new BasicNameValuePair("idmateria", String.valueOf(idMateria)));
		    params.add(new BasicNameValuePair("idestudiante", String.valueOf(idEstudiante)));

		    String paramString = URLEncodedUtils.format(params, "utf-8");

		    Uri += paramString;
			del = new HttpGet(Uri);
			
		} else if (estado.equals("C")){
			del= new HttpGet("http://"+ Global.DireccionIPWebServices +"/Api/Test/ObtenerTestCompletosxMateria/" + idMateria);
		} else
			del= new HttpGet("http://"+ Global.DireccionIPWebServices +"/Api/Test/ObtenerTestxMateria/" + idMateria);
	
		del.setHeader("content-type", "application/json");
		
		try{
			
			HttpResponse resp = httpClient.execute(del);
	        String respStr = EntityUtils.toString(resp.getEntity());
	        JSONArray respJSON = new JSONArray(respStr);
	        vResult = new TestList[respJSON.length()];
	        
	        for(int i=0; i<respJSON.length(); i++)
	        {
	            JSONObject obj = respJSON.getJSONObject(i);
	 
	            TestList reg = new TestList();
	            reg.setIdMateria(obj.getInt("IdMateria"));
	            reg.setIdTest(obj.getInt("IdTest"));
	            reg.setTitulo(obj.getString("Titulo"));
	            reg.setDescripcion(obj.getString("Descripcion"));
	            reg.setPromedio(obj.getInt("Promedio"));
	            reg.setEstado(obj.getString("Estado"));
	            reg.setTiempo(obj.getInt("Tiempo"));
	            
				vResult[i]=reg;
	        }

			return vResult;
		}
		catch(Exception ex)
		{
			Log.i("errorlisttest",ex.toString());
			return vResult;
		}
		
		
	}

	/***
	 * Update a test
	 * @param idmateria .. subject id
	 * @param idtest .. test id
	 * @param flag ..
	 * @param estado .. state
	 * @param tiempo .. time
	 * @return True if updated, false otherwise
	 */
	public Boolean ActualizarTest(int idmateria,int idtest, int flag ,String estado, int tiempo)
	{
		
		HttpClient httpClient = new DefaultHttpClient();
		String Uri = "http://"+ Global.DireccionIPWebServices +"/Api/Test/UpdateTest?";
		
		List<NameValuePair> params = new LinkedList<NameValuePair>();

	    params.add(new BasicNameValuePair("idmateria", String.valueOf(idmateria)));
	    params.add(new BasicNameValuePair("idtest", String.valueOf(idtest)));
	    params.add(new BasicNameValuePair("estado", estado));
	    params.add(new BasicNameValuePair("flag", String.valueOf(flag)));
	    params.add(new BasicNameValuePair("tiempo", String.valueOf(tiempo)));
	    
	    String paramString = URLEncodedUtils.format(params, "utf-8");

	    Uri += paramString;
		HttpGet del = new HttpGet(Uri);
		del.setHeader("content-type", "application/json");
		

		try{
			
			HttpResponse resp = httpClient.execute(del);
	        String respStr = EntityUtils.toString(resp.getEntity());
	        Log.i("Aja", respStr);
	        if (respStr.equals("1"))
	        {
	        	Log.i("ResultadoVeri", respStr);
	        	return true;
	        }
	        else
	        	return false;
		}
		catch(Exception ex)
		{
			
			Log.i("Error Para", "Ejemplo", ex);
			ErrorMsg = ex.getMessage();
			return false;
		}
		
		
		
	}

	/**
	 * Mark a test
	 * @param idmateria .. subject id
	 * @param idtest .. test id
	 * @param idestudiante .. student id
	 * @return true if ok, false otherwise
	 */
	public Boolean CalificarTest(int idmateria,int idtest, int idestudiante)
	{
		
		HttpClient httpClient = new DefaultHttpClient();
		String Uri = "http://"+ Global.DireccionIPWebServices +"/Api/Test/CalificarTest?";
		
		List<NameValuePair> params = new LinkedList<NameValuePair>();

	    params.add(new BasicNameValuePair("idmateria", String.valueOf(idmateria)));
	    params.add(new BasicNameValuePair("idtest", String.valueOf(idtest)));
	    params.add(new BasicNameValuePair("idestudiante", String.valueOf(idestudiante)));
	    
	    String paramString = URLEncodedUtils.format(params, "utf-8");

	    Uri += paramString;
		HttpGet del = new HttpGet(Uri);
		del.setHeader("content-type", "application/json");
		

		try{
			
			HttpResponse resp = httpClient.execute(del);
	        String respStr = EntityUtils.toString(resp.getEntity());
	        Log.i("Aja", respStr);
	        if (respStr.equals("1"))
	        {
	        	Log.i("ResultadoVeri", respStr);
	        	return true;
	        }
	        else
	        	return false;
		}
		catch(Exception ex)
		{
			
			Log.i("Error Para", "Ejemplo", ex);
			ErrorMsg = ex.getMessage();
			return false;
		}
		
		
		
	}


}
