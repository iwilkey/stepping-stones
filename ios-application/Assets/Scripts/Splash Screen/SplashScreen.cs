using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using System;

public class SplashScreen : MonoBehaviour
{
    public Image next, prev;
   	public RawImage thumbnail;
    public Text projectName, instructions, dateTime;
    private Vector3 sp;
    private RectTransform pencilT;
    private float time, t = 0, z = 0, t2 = 0, t3 = 0, rot = 0;
   	public bool switchThumbnail = false, loading = false;

   	private int counter = 0;
   	private string currentProject;
   	private List<string> existingProjects;
   	private string workBoard;

   	public BoxCollider2D edit;

    void Start(){
    	if (!(Application.HasUserAuthorization(UserAuthorization.WebCam))){
			Application.RequestUserAuthorization(UserAuthorization.WebCam);
		}

    	time = 0;
    	t = 0;
    	z = 0;
    	t2 = 0;
    	t3 = 0;
    	rot = 0;
    	switchThumbnail = false;
    	counter = 0;

    	loading = false;

    	next.color = new Color(next.color.r, next.color.g, next.color.b, t3);
    	prev.color = new Color(prev.color.r, prev.color.g, prev.color.b, t3);
    	projectName.color = new Color(projectName.color.r, projectName.color.g, projectName.color.b, t3);
    	thumbnail.color = new Color(thumbnail.color.r, thumbnail.color.g, thumbnail.color.b, t3);

    	if(!ES3.KeyExists("Existing Projects")){
			existingProjects = new List<string>();
			ES3.Save<List<string>>("Existing Projects", existingProjects);
			existingProjects.Add("New Project");
		} else {
			existingProjects = ES3.Load<List<string>>("Existing Projects");
			existingProjects.Insert(0, "New Project");
			foreach(var proj in existingProjects){
				Debug.Log(proj);
			}
		}

		edit.size = new Vector2(Screen.width, Screen.height - 150);
		edit.offset = new Vector2(0, 150);

		Next();
		Previous();
    }

    void Update(){
    	if(!loading){

    		dateTime.text = System.DateTime.Now.ToString();

	    	counter = counter % existingProjects.Count;

	    	time += Time.deltaTime * 2.5f;
	    	rot += Time.deltaTime;
	    	thumbnail.GetComponent<RectTransform>().localEulerAngles = new Vector3(0,0,rot);

	    	if(currentProject == "New Project"){
	    		instructions.text = "Tap here to create a project!";
	    	} else {
	    		instructions.text = "Tap here to continue editing";
	    	}

	    	if(time >= 0.0f && time <= 1.0f){
	    		switchThumbnail = true;

	    		if(t3 < 1.0f){
	    			t3 += 0.1f;	
	    		}
	    		next.color = new Color(next.color.r, next.color.g, next.color.b, t3);
	    		prev.color = new Color(prev.color.r, prev.color.g, prev.color.b, t3);
	    		projectName.color = new Color(projectName.color.r, projectName.color.g, projectName.color.b, t3);
	    		thumbnail.color = new Color(thumbnail.color.r, thumbnail.color.g, thumbnail.color.b, t3);

	    	} else if(time > 1.0f) {
	    		if(counter - 1 < 0){
	    			prev.color = new Color(prev.color.r, prev.color.g, prev.color.b, 0.1f);
		    	} else {
		    		prev.color = new Color(prev.color.r, prev.color.g, prev.color.b, 1.0f);
		    	}

		    	if(existingProjects[(counter + 1) % existingProjects.Count] == "New Project"){
		    		next.color = new Color(next.color.r, next.color.g, next.color.b, 0.1f);
		    	} else {
		    		next.color = new Color(next.color.r, next.color.g, next.color.b, 1.0f);
		    	}
	    	}

	    	float tmS = 0.2f;
	    	if(switchThumbnail){
	    		t2 += Time.deltaTime / tmS;
	    		thumbnail.color = new Color(thumbnail.color.r, thumbnail.color.g, thumbnail.color.b, Mathf.SmoothStep(0.0f, 1.0f, t2));
	    	}

	    	currentProject = existingProjects[counter];
	    	projectName.text = currentProject;
	    }

    	if(loading){
    		t3 -= 0.1f;	
    		next.color = new Color(next.color.r, next.color.g, next.color.b, t3);
    		prev.color = new Color(prev.color.r, prev.color.g, prev.color.b, t3);
    		projectName.color = new Color(projectName.color.r, projectName.color.g, projectName.color.b, t3);
    		thumbnail.color = new Color(thumbnail.color.r, thumbnail.color.g, thumbnail.color.b, t3);

    		if(t3 <= 0.0f){
    			if(currentProject != "New Project"){
    				loading = false;
    				SceneManager.LoadScene("Editor");
    			} else {
    				loading = false;
    				SceneManager.LoadScene("New Project");
    			}
    		}
    	}

    	if(Input.GetMouseButtonDown(0)){
			Vector3 fingerPosition = Input.mousePosition;
			fingerPosition.z = Mathf.Infinity;
			RaycastHit2D hit = Physics2D.Raycast(fingerPosition, fingerPosition - Camera.main.ScreenToWorldPoint(fingerPosition), Mathf.Infinity);
			if(hit.collider != null){
				switch(hit.collider.gameObject.name){
					case "Next":

						if(!(counter + 1 > existingProjects.Count)){
							if(existingProjects[(counter + 1) % existingProjects.Count] != "New Project"){
								Next();
							}
						}
						break;

					case "Previous":
						if(!(counter - 1 < 0)){
							Previous();
						}

						break;

					case "Edit":

						if(time > 2.0f){
							workBoard = currentProject;
							ES3.Save<string>("Work Board", workBoard);
							loading = true;

						}

						break;
				}
			}
		}
    }

    public void Next(){
		switchThumbnail = false;
		thumbnail.color = new Color(thumbnail.color.r, thumbnail.color.g, thumbnail.color.b, Mathf.SmoothStep(0.0f, 1.0f, 0.0f));


		counter++;
		counter = counter % existingProjects.Count;
		currentProject = existingProjects[counter];
		projectName.color = new Color(projectName.color.r, projectName.color.g, projectName.color.b, 1.0f);
		projectName.text = existingProjects[counter];

		t2 = 0;
		switchThumbnail = true;
	}

	public void Previous(){
		switchThumbnail = false;
		thumbnail.color = new Color(thumbnail.color.r, thumbnail.color.g, thumbnail.color.b, Mathf.SmoothStep(0.0f, 1.0f, 0.0f));

		counter--;
		counter = counter % existingProjects.Count;
		currentProject = existingProjects[counter];
		projectName.color = new Color(projectName.color.r, projectName.color.g, projectName.color.b, 1.0f);
		projectName.text = existingProjects[counter];

		t2 = 0;
		switchThumbnail = true;
	}
}
