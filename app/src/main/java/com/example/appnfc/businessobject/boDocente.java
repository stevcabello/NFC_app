package com.example.appnfc.businessobject;

import android.util.Log;

import com.example.appnfc.Global;
import com.example.appnfc.info.Docente;
import com.example.appnfc.info.Materia;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;







public class boDocente {

	/**
	 * Teacher login
	 * @param usr
	 * @param pwd
	 * @return the Teacher
	 */
	public Docente LoginDocente(String usr, String pwd)
	{
		Docente vResult = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://" + Global.DireccionIPWebServices +"/Api/Docente/GetParametrosLogin");
		post.setHeader("content-type", "application/json");
		
		try{
			
			JSONObject dato = new JSONObject();
			if (pwd == null){
				pwd = "";
			}
			
			dato.put("Usuario", usr);
			dato.put("Clave", pwd);
			StringEntity entity = new StringEntity(dato.toString());
	        post.setEntity(entity);
	        
            HttpResponse resp = httpClient.execute(post);
	        String respStr = EntityUtils.toString(resp.getEntity());
	       
	        
	        if (!respStr.isEmpty()) { 
		        Log.i("REsultadoD", respStr);
		        JSONObject r = new JSONObject(respStr);
		        vResult = new Docente();
		        vResult.setId(r.getInt("Id"));
		        vResult.setNombre(r.getString("Nombre"));
		        vResult.setApellido(r.getString("Apellido"));
		        vResult.setEmail(r.getString("Email"));
		        return vResult;
	        } else return null;
	        
		}
		catch(ClientProtocolException e)
		{
			Log.i("Protocol", "Error Protocolo");
			e.printStackTrace();
			return vResult ;
		}
		catch(IOException e)
		{
			Log.i("IOExecption", "Error IOExeption");
			e.printStackTrace();
			return vResult ;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.i("JSONExecption", "Error JSON");
			e.printStackTrace();
			return vResult ;
		}
    }

	/**
	 * Save a Teacher
	 * @param item .. provide a Teacher
	 * @return 1 is Teacher data saved successfully, 0 otherwise
	 */
	public int Registrar(Docente item)
	{
		int vResult = 0;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://" + Global.DireccionIPWebServices +"/Api/Docente/Docente/0");
		post.setHeader("content-type", "application/json");
		
		try{
			
			JSONObject dato = new JSONObject();
			dato.put("Id",item.getId()); 			
			dato.put("Nombre", item.getNombre());
			dato.put("Apellido", item.getApellido());
			dato.put("Usuario", item.getUsuario());
			dato.put("Contrasenia", item.getContrasenia());
			dato.put("Email", item.getEmail());
			dato.put("Cedula", item.getCedula());

			Log.i("Salida", dato.toString());

		    StringEntity entity2 = new StringEntity(dato.toString());
	        

            post.setEntity(entity2);
            
	        HttpResponse resp = httpClient.execute(post);
	        String respStr = EntityUtils.toString(resp.getEntity());
	        Log.i("Respuesta", respStr);
	        
	        return Integer.parseInt(respStr);
		}
		catch(ClientProtocolException e)
		{
			Log.i("Protocol", "Error Protocolo");
			e.printStackTrace();
			return vResult ;
		}
		catch(IOException e)
		{
			Log.i("IOExecption", "Error IOExeption");
			e.printStackTrace();
			return vResult ;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.i("JSONExecption", "Error JSON");
			e.printStackTrace();
			return vResult ;
		}
		
		
	}


	/**
	 * Get a Teacher
	 * @param id ... teacher id
	 * @return the Teacher
	 */
	public Docente ObtenerDocente(int id)
	{
		Docente vResult = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://"+ Global.DireccionIPWebServices +"/Api/Docente/Docente/" + id);
		get.setHeader("content-type", "application/json");
		
		try{
			
			HttpResponse resp = httpClient.execute(get);
			Log.i("Ejecucion", "ok");
	        String respStr = EntityUtils.toString(resp.getEntity());
	        Log.i("Respuesta", respStr);
	        
	        	JSONObject respJSON = new JSONObject(respStr);
		        vResult = new Docente();
		        vResult.setId(respJSON.getInt("IdDocente"));
		        vResult.setNombre(respJSON.getString("Nombre"));
		        vResult.setApellido(respJSON.getString("Apellido"));
		        vResult.setEmail(respJSON.getString("Email"));
	
		       
		    
	    }
		catch(ClientProtocolException e)
		{
			Log.i("Protocol", "Error Protocolo");
			e.printStackTrace();
			return vResult ;
		}
		catch(IOException e)
		{
			Log.i("IOExecption", "Error IOExeption");
			e.printStackTrace();
			return vResult ;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.i("JSONExecption", "Error JSON");
			e.printStackTrace();
			return vResult ;
		}
		
		return vResult;
	}


	/**
	 * Get the subjects of a teacher
	 * @param idDocente ... teacher id
	 * @return the list of subjects
	 */
	public Materia[] GetMateriasxDocente(int idDocente)
	{
		Materia[] vResult= null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet del = new HttpGet("http://"+ Global.DireccionIPWebServices +"/Api/Docente/ObtenerMateriasXDocente/" + idDocente);
	
		del.setHeader("content-type", "application/json");
		
		try{
			
			HttpResponse resp = httpClient.execute(del);
	        String respStr = EntityUtils.toString(resp.getEntity());
	        JSONArray respJSON = new JSONArray(respStr);
	        vResult = new Materia[respJSON.length()];
	        
	        for(int i=0; i<respJSON.length(); i++)
	        {
	            JSONObject obj = respJSON.getJSONObject(i);
	 
	            Materia reg = new Materia();
				reg.setId(obj.getInt("Id"));
				reg.setNombre(obj.getString("Nombre"));
				vResult[i]=reg;
	        }
			return vResult;
		}
		catch(Exception ex)
		{
			return vResult;
		}
		
		
	}


	
	

	
}
	