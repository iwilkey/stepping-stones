using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.IO;
using UnityEngine.SceneManagement;

public class CameraCapture : MonoBehaviour
{
	//Camera
	public RawImage camRender;
	WebCamTexture webTex = null;
	string camName = null;

	float time;

	//Recording
	Recording Recording;
	private int currentFrame;
	public int numFrames;
	public int fps;

	//UI
	public Slider progress;
	public Slider frameProgress;
	public Text instruction;
	public RawImage fade;

	private bool processing;

	private float t;

	void Awake(){

		numFrames = ES3.Load<int>("Frame Amount");
		fps = ES3.Load<int>("FPS");

		Recording = GameObject.Find("Main Camera").GetComponent<Recording>();
		progress.value = 0;
		progress.minValue = 0; progress.maxValue = numFrames;
		frameProgress.value = 0;
		frameProgress.minValue = 0; frameProgress.maxValue = numFrames;
		Recording.enabled = false;

		instruction.text = "Tap anywhere to start recording";

		foreach(var obj in Resources.FindObjectsOfTypeAll<Transform>() as Transform[]){
				if(obj.gameObject.CompareTag("MoveOn")){
					obj.gameObject.SetActive(false);
				}

				if(obj.gameObject.CompareTag("Stop")){
					obj.gameObject.SetActive(false);
				}
			}
	}

	void Start(){
		if (!(Application.HasUserAuthorization(UserAuthorization.WebCam))){
			Application.RequestUserAuthorization(UserAuthorization.WebCam);
		}

		if(WebCamTexture.devices != null){
			camName = WebCamTexture.devices[0].name;
			webTex = new WebCamTexture(camName, Screen.width, Screen.height);
			webTex.requestedFPS = 60.0f;
	
			camRender.texture = webTex;
			camRender.material.mainTexture = webTex;
			camRender.rectTransform.SetTop((int)((Screen.height-Screen.width) / 2));
			camRender.rectTransform.SetBottom((int)((Screen.height-Screen.width) / 2));
			camRender.rectTransform.SetLeft((int)((Screen.width-Screen.height) / 2));
			camRender.rectTransform.SetRight((int)((Screen.width-Screen.height) / 2));


				//For back facing camera.
			camRender.rectTransform.localEulerAngles = new Vector3(0, -180,-90);
				//For Front Facing Camera.
			//camRender.rectTransform.localEulerAngles = new Vector3(0,180,-270);
 	
			if(webTex != null) webTex.Play();
		}

		time = 0;

		t = 1.0f;
		fade.color = new Color(fade.color.r, fade.color.g, fade.color.b, t);
	}

	void Update(){
		time += Time.deltaTime;

		if(t > 0){
			t -= Time.deltaTime;
			fade.color = new Color(fade.color.r, fade.color.g, fade.color.b, t);
		}

		progress.value = Recording.savingFrameNumber;
		frameProgress.value = Recording.frameNumber;

		if(Input.touchCount > 0 && (Screen.height - Input.GetTouch(0).position.y > 267) && time > 2.0f && !(frameProgress.value == Recording.maxFrames && (frameProgress.value == progress.value))){
			Recording.enabled = true;
			Recording.maxFrames = numFrames; Recording.frameRate = fps;
			foreach(var obj in Resources.FindObjectsOfTypeAll<Transform>() as Transform[]){
				if(obj.gameObject.CompareTag("MoveOn")){
					obj.gameObject.SetActive(false);
				}

				if(obj.gameObject.CompareTag("Stop")){
					obj.gameObject.SetActive(true);
				}
			}
		}

		if(!processing){
			if(frameProgress.value > 0 && frameProgress.value != Recording.maxFrames && progress.value < frameProgress.value){
				instruction.text = "Recording...";
			} else if (frameProgress.value == Recording.maxFrames && progress.value < Recording.maxFrames){
				instruction.text = "Processing frames, please wait...";
			} else if (frameProgress.value == Recording.maxFrames && (frameProgress.value == progress.value)){
				instruction.text = "Ready!";

				foreach(var obj in Resources.FindObjectsOfTypeAll<Transform>() as Transform[]){
					if(obj.gameObject.CompareTag("MoveOn")){
						obj.gameObject.SetActive(true);
					}

					if(obj.gameObject.CompareTag("Stop")){
						obj.gameObject.SetActive(false);
					}
				}
			}
		} else {
			instruction.text = "Processing recording... this may take a while";
		}

		if(Input.GetMouseButtonDown(0)){
			Vector3 fingerPosition = Input.mousePosition;
			fingerPosition.z = Mathf.Infinity;
			RaycastHit2D hit = Physics2D.Raycast(fingerPosition, fingerPosition - Camera.main.ScreenToWorldPoint(fingerPosition), Mathf.Infinity);
			if(hit.collider != null){
				switch(hit.collider.gameObject.name){
					case "Delete":
						DeleteDirectory(Application.persistentDataPath + "/UnprocessedFrames/");
						webTex.Stop();
						SceneManager.LoadScene("Capture");
						break;

					case "Stop":
						Recording.encoderThread.Abort();
						Recording.enabled = false;
						webTex.Stop();
						DeleteDirectory(Application.persistentDataPath + "/UnprocessedFrames");
						SceneManager.LoadScene("Capture");
						break;

					case "Switch":
						SwitchCam();
						break;

					case "Edit":
						processing = true;
						instruction.text = "Processing recording... this may take a while";
						SceneManager.LoadScene("Editor");
						break;
				}
			}
		}
	}

	void SwitchCam(){
		if(WebCamTexture.devices != null){
			webTex.Stop();
			webTex.deviceName = (webTex.deviceName == WebCamTexture.devices[0].name) ? WebCamTexture.devices[1].name : WebCamTexture.devices[0].name;
			camRender.rectTransform.localEulerAngles = (webTex.deviceName == WebCamTexture.devices[0].name) ? new Vector3(0,-180,-90) : new Vector3(0,180,-270 + 180);
			webTex.Play();
		}
	}

	void DeleteDirectory(string path){
		DirectoryInfo dataDir = new DirectoryInfo(path);
 		dataDir.Delete(true);
	}
}
