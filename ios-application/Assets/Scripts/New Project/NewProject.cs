using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Linq;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using System.Text.RegularExpressions;

public class NewProject : MonoBehaviour
{
	public Image fade;
	private float t = 1.0f, rot;
	public string name = "";
	public Font font;
	public Text error, numF, numFPS, totalTime;

	private string workBoard;
	private List<string> existingProjects;

	private bool loading = false;

	private int numFrames = 25;
	private int fps = 5;

	public Slider numFramesSlider;
	public Slider fpsSlider;

	public RawImage bg;

	void Start(){
		if(ES3.KeyExists("Existing Projects")){
			existingProjects = ES3.Load<List<string>>("Existing Projects");
		} else {
			existingProjects = new List<string>();
		}

		error.text = "";

		numFramesSlider.value = numFrames;
		fpsSlider.value = fps;

		t = 1.0f;
		rot = 0.0f;
	}

	void Update(){
		if(!loading){

			rot -= Time.deltaTime;
			bg.GetComponent<RectTransform>().localEulerAngles = new Vector3(0,0,rot);

			numFrames = (int)numFramesSlider.value; fps = (int)fpsSlider.value;
			numF.text = numFrames.ToString(); numFPS.text = fps.ToString();
			float totalT = numFramesSlider.value * (1.0f / fpsSlider.value);
			print(totalT);

			if((int)(totalT % 60) >= 10 && (int)(totalT % 60) > 0){
				totalTime.text = "Recording Length: " + ((int)(totalT / 60)).ToString() + ":" + ((int)(totalT % 60)).ToString();
			} else if ((int)(totalT % 60) < 10 && (int)(totalT % 60) > 0) {
				totalTime.text = "Recording Length: " + ((int)(totalT / 60)).ToString() + ":0" + ((int)(totalT % 60)).ToString();
			} else {
				totalTime.text = "Recording Length: < 0.01";
			}

			if(t > 0.0f){
				t -= 0.1f;
				fade.color = new Color(fade.color.r, fade.color.g, fade.color.b, t);
			}

			if(!checkLegality(name) && name != ""){
				error.text = "You cannot use this name!";
			} else if (checkLegality(name)  && name != "") {
				error.text = "";
			}

			if(Input.GetMouseButtonDown(0)){
				Vector3 fingerPosition = Input.mousePosition;
				fingerPosition.z = Mathf.Infinity;
				RaycastHit2D hit = Physics2D.Raycast(fingerPosition, fingerPosition - Camera.main.ScreenToWorldPoint(fingerPosition), Mathf.Infinity);
				if(hit.collider != null){
					switch(hit.collider.gameObject.name){
						case "Create":
							if(name != "" && checkLegality(name)){
								workBoard = name;
								ES3.Save<string>("Work Board", name.ToLower());
								ES3.Save<int>("Frame Amount", numFrames);
								ES3.Save<int>("FPS", fps);
								t= 0.0f;
								loading = true;
							} else if (name == "") { 
								error.text = "Please input a name for your project!";
							}
							break;

						case "Back":
							SceneManager.LoadScene("Splash Screen");
							break;
					}
				}
			}
		} else {
			if(t < 1.0f){
				t += 0.1f;
				fade.color = new Color(fade.color.r, fade.color.g, fade.color.b, t);
			} else {
				SceneManager.LoadScene("Capture");
			}
		}
	}

	bool checkLegality(string _name){
		_name = name.ToLower();
		//Check if the name already exists...
		if(existingProjects.Contains(_name)) return false;

		return true;
	}

    void OnGUI(){
    	name = GUI.TextField(new Rect((Screen.width / 4), 145, Screen.width / 2, 55), name, 40);
    	name = Regex.Replace(name, @"[^a-zA-Z0-9 ]", "");
    	GUI.skin.textField.fontSize = 30;
    	GUI.skin.textField.alignment = TextAnchor.MiddleCenter;
    	GUI.skin.textField.font = font;
    }
}
