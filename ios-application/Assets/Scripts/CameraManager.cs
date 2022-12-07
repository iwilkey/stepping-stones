using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

#if PLATFORM_ANDROID
using UnityEngine.Android;
#else
using UnityEngine.iOS;
#endif

public class CameraManager : MonoBehaviour
{
	public RawImage camRender;
	WebCamTexture webTex = null;
	FlikittCore FlikittCore;
	string camName = null;

	void Start(){
		FlikittCore = GameObject.Find("Flikitt Core").GetComponent<FlikittCore>();

		//Android Permissions
		#if PLATFORM_ANDROID
		if(!Permission.HasUserAuthorizedPermission(Permission.Camera)){
			do {
				Permission.RequestUserPermission(Permission.Camera);
			} while (!Permission.HasUserAuthorizedPermission(Permission.Camera));
		}

		if(WebCamTexture.devices != null){
			camName = WebCamTexture.devices[0].name;
			webTex = new WebCamTexture(camName, Screen.width, Screen.height);
	
			camRender.texture = webTex;
			camRender.material.mainTexture = webTex;
			camRender.rectTransform.SetTop((int)((Screen.height-Screen.width) / 2));
			camRender.rectTransform.SetBottom((int)((Screen.height-Screen.width) / 2));
			camRender.rectTransform.SetLeft((int)((Screen.width-Screen.height) / 2));
			camRender.rectTransform.SetRight((int)((Screen.width-Screen.height) / 2));


				//For back facing camera.
			camRender.rectTransform.localEulerAngles = new Vector3(0,0,-90);
				//For Front Facing Camera.
			//camRender.rectTransform.localEulerAngles = new Vector3(0,180,-270);
 	
			if(webTex != null) webTex.Play();
		}
		#else
		//ANDROID ENDS HERE

		if (!(Application.HasUserAuthorization(UserAuthorization.WebCam))){
			do {
				Application.RequestUserAuthorization(UserAuthorization.WebCam);
			} while (!(Application.HasUserAuthorization(UserAuthorization.WebCam)));
		}

		if(WebCamTexture.devices != null){
			camName = WebCamTexture.devices[0].name;
			webTex = new WebCamTexture(camName, Screen.width, Screen.height);
	
			camRender.texture = webTex;
			camRender.material.mainTexture = webTex;
			camRender.rectTransform.SetTop((int)((Screen.height-Screen.width) / 2));
			camRender.rectTransform.SetBottom((int)((Screen.height-Screen.width) / 2));
			camRender.rectTransform.SetLeft((int)((Screen.width-Screen.height) / 2));
			camRender.rectTransform.SetRight((int)((Screen.width-Screen.height) / 2));


				//For back facing camera.
			camRender.rectTransform.localEulerAngles = new Vector3(0,0,-90);
				//For Front Facing Camera.
			//camRender.rectTransform.localEulerAngles = new Vector3(0,180,-270);
 	
			if(webTex != null) webTex.Play();
		}

		#endif

	}

	void Update(){
		if(WebCamTexture.devices != null){
			if(!FlikittCore.getCurrentFrame().getHasPicture()){
				if(!webTex.isPlaying) webTex.Play();
			} else {
				if(webTex.isPlaying) webTex.Stop();
			}
		}
	}

	public void DeleteCapture(){
		FlikittCore.getCurrentFrame().setPicture(null);
		FlikittCore.getCurrentFrame().setHasPicture(false);
	}

	public void DeleteSpecificCapture(int index){
		FlikittCore.project.getFrame(index).setPicture(null);
		FlikittCore.project.getFrame(index).setHasPicture(false);
	}

	public WebCamTexture getWebTex(){
		return webTex;
	}

	public void SwitchCam(){

		for(int i = 0; i < WebCamTexture.devices.Length; i++){
			Debug.Log(WebCamTexture.devices[i].name);
		}

		if(WebCamTexture.devices != null){
			webTex.Stop();
			webTex.deviceName = (webTex.deviceName == WebCamTexture.devices[0].name) ? WebCamTexture.devices[1].name : WebCamTexture.devices[0].name;
			camRender.rectTransform.localEulerAngles = (webTex.deviceName == WebCamTexture.devices[0].name) ? new Vector3(0,0,-90) : new Vector3(0,180,-270);
			//FlikittCore.getCurrentFrame().setOrientation();
			webTex.Play();
		} else {
			Debug.Log("No cameras found!");
			return;
		}
	}
}
