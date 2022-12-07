using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class ColorSwatch {
	private string name;
	private Image imageSelf;
	private bool enabled;
	private GameObject GOSelf;

	public ColorSwatch(string _name, Image _imageSelf){
		name = _name;
		imageSelf = _imageSelf;
		enabled = false;
		GOSelf = GameObject.Find(name + "_Swatch");
	}

	public string getName() {return name;}
	public bool isEnabled() {return enabled;}

	public void enable(){
		imageSelf.color = new Color(imageSelf.color.r, imageSelf.color.g, imageSelf.color.b, 1.0f);
		GOSelf.GetComponent<Image>().color = imageSelf.color;
		enabled = true;
	}

	public void disable(){
		imageSelf.color = new Color(imageSelf.color.r, imageSelf.color.g, imageSelf.color.b, 0.4f);
		GOSelf.GetComponent<Image>().color = imageSelf.color;
		enabled = false;
	}
}

public class UserInterface : MonoBehaviour
{
	FlikittCore FlikittCore;
	MicrophoneManager MicrophoneManager;
	DrawingManager DrawingManager;
	SaveLoad SaveLoad;
	ShareManager ShareManager;

	public Image fadeIn;
	private float t = 1;

	public bool canDraw, canPlay, screenshot;
	private List<ColorSwatch> colorSwatches = new List<ColorSwatch>();

	//UI Vars
	public Text frameCounter, trackCounter, currentTrackCounter, drawingSlider;
	public Sprite cam, deletePic, notPlaying, playing, pencilOn, pencilOff, eraserOn, eraserOff, copy, paste, micOn, micOff, manOn, manOff;
	public Image pencil, eraser, selectCam, selectDraw, copyFrame, trash, insert, selectEdit, copyLength1, copyLength2, copyPaste, WAVEFORM, mic, manipulate;
	public Slider fpsSlider, thicknessSlider, frameScrubber;

	public string mode;

	public AudioClip silence;

	private string frameSelected1 = "?"; private string frameSelected2 = "?"; private string frameToCopy = "?";
	void Start(){
		FlikittCore = GameObject.Find("Flikitt Core").GetComponent<FlikittCore>();
		//CameraManager = GameObject.Find("Camera Manager").GetComponent<CameraManager>();
		MicrophoneManager = GameObject.Find("Microphone Manager").GetComponent<MicrophoneManager>();
		DrawingManager = GameObject.Find("Drawing Manager").GetComponent<DrawingManager>();
		SaveLoad = GameObject.Find("Easy Save 3 Manager").GetComponent<SaveLoad>();
		ShareManager = GameObject.Find("Share Manager").GetComponent<ShareManager>();


		fpsSlider.maxValue = 60.0f; fpsSlider.minValue = 0.5f;

		string[] colors = new string[9]{"Red", "Orange", "Yellow", "Green", "Blue", "Indigo", "Purple", "White", "Black"};
		for(int i = 0; i < colors.Length; i++){
			Image swatchImage = GameObject.Find(colors[i] + "_Swatch").GetComponent<Image>();
			ColorSwatch colorSwatch = new ColorSwatch(colors[i], swatchImage);
			colorSwatches.Add((new ColorSwatch(colors[i], swatchImage)));
		}
		DisableAllSwatches();
		EnableSwatch("White");

		SetMode("Capture");

		copyLength1.color = new Color(copyLength1.color.r, copyLength1.color.g, copyLength1.color.b, 1.0f);
		fadeIn.color = new Color(fadeIn.color.r, fadeIn.color.g, fadeIn.color.b, 1.0f);

		foreach(var obj in Resources.FindObjectsOfTypeAll<Transform>() as Transform[]){
			if(obj.gameObject.name == "Saving Dock" || obj.gameObject.name == "Save Text"){
				obj.gameObject.SetActive(false);
			}
		}
}

	void DisableAllSwatches(){
		for(int i = 0; i < colorSwatches.Count; i++){
			colorSwatches[i].disable();
		}
	}

	void EnableSwatch(string color){
		for(int i = 0; i < colorSwatches.Count; i++){
			if(colorSwatches[i].getName() == color){
				colorSwatches[i].enable();
			}
		}
	}

	public void SetMode(string _mode){

		mode = _mode;
		foreach(var obj in Resources.FindObjectsOfTypeAll<Transform>() as Transform[]){
		 	if(obj.IsChildOf(GameObject.Find("User Interface").transform) && obj.name != "User Interface"){
		 		obj.gameObject.SetActive(false);
		 	}
		 }

		if(_mode != "Recording"){ 

			foreach(var obj in Resources.FindObjectsOfTypeAll<Transform>() as Transform[]){
				if(obj.gameObject.CompareTag("EssentialUI")){
					obj.gameObject.SetActive(true);
				}
			}
		}

		if(_mode == "Drawing"){
			foreach(var obj in Resources.FindObjectsOfTypeAll<Transform>() as Transform[]){
				if(obj.gameObject.CompareTag("DrawingTools")){
					obj.gameObject.SetActive(true);
				}

				if(obj.name == "Thickness Slider"){
					for(int i = 0; i < obj.childCount; i++){
						obj.GetChild(i).gameObject.SetActive(true);
						for(int p = 0; p < obj.GetChild(i).childCount; p++){
							obj.GetChild(i).GetChild(p).gameObject.SetActive(true);
						}
					}
				}
			}
		} else if (_mode == "Capture"){
			foreach(var obj in Resources.FindObjectsOfTypeAll<Transform>() as Transform[]){
				if(obj.gameObject.CompareTag("CaptureTools")){
					obj.gameObject.SetActive(true);
				}

				if(obj.name == "FPS Slider"){
					for(int i = 0; i < obj.childCount; i++){
						obj.GetChild(i).gameObject.SetActive(true);
						for(int p = 0; p < obj.GetChild(i).childCount; p++){
							obj.GetChild(i).GetChild(p).gameObject.SetActive(true);
						}
					}
				}
			}
		} else if (_mode == "Edit"){
			foreach(var obj in Resources.FindObjectsOfTypeAll<Transform>() as Transform[]){
				if(obj.gameObject.name == "Forward" || obj.gameObject.name == "Back"){
					obj.gameObject.SetActive(false);
				}
			}
			foreach(var obj in Resources.FindObjectsOfTypeAll<Transform>() as Transform[]){
				if(obj.gameObject.CompareTag("EditTools")){
					obj.gameObject.SetActive(true);
				}

				if(obj.name == "Frame Scrubber"){
					for(int i = 0; i < obj.childCount; i++){
						obj.GetChild(i).gameObject.SetActive(true);
						for(int p = 0; p < obj.GetChild(i).childCount; p++){
							obj.GetChild(i).GetChild(p).gameObject.SetActive(true);
						}
					}
				}

				if(obj.name == "Image Transparency"){
					for(int i = 0; i < obj.childCount; i++){
						obj.GetChild(i).gameObject.SetActive(true);
						for(int p = 0; p < obj.GetChild(i).childCount; p++){
							obj.GetChild(i).GetChild(p).gameObject.SetActive(true);
						}
					}

					ImageTransparency it = GameObject.Find("Image Transparency").GetComponent<ImageTransparency>();
					it.slider.value = 1.0f;
				}
			}
		} else if (_mode == "Recording"){
			foreach(var obj in Resources.FindObjectsOfTypeAll<Transform>() as Transform[]){
				if(obj.gameObject.CompareTag("Watermark")){
					obj.gameObject.SetActive(true);
				}
			}
		}
	}

	void Update(){

		if(t > 0.0f){
			t -= 0.01f;
			fadeIn.color = new Color(fadeIn.color.r, fadeIn.color.g, fadeIn.color.b, t);	
		} else {
			if(GameObject.Find("FadeIn") != null){
				GameObject.Find("FadeIn").SetActive(false);
			}
		}


		int cframe = FlikittCore.currentFrame;
		int projlength = FlikittCore.project.getAllFrames().Count;

		FlikittCore.project.setFps(fpsSlider.value);
		DrawingManager.width = thicknessSlider.value;

		frameCounter.text = cframe + " / " + projlength;
		//trackCounter.text = MicrophoneManager.currentTrack.ToString();

		if(MicrophoneManager.hasRecording){
			fpsSlider.interactable = false;
		} else {
			fpsSlider.interactable = true;
		}

		if(mode == "Edit"){
			if(!MicrophoneManager.hasRecording){
				WAVEFORM.color = new Color(WAVEFORM.color.r, WAVEFORM.color.g, WAVEFORM.color.b, 0.0f);
			} else {
				WAVEFORM.color = new Color(WAVEFORM.color.r, WAVEFORM.color.g, WAVEFORM.color.b, 1.0f);
			}
		}

		if(!FlikittCore.isPlaying){
			if(frameSelected1 == "?"){
				copyLength1.color = new Color(copyLength1.color.r, copyLength1.color.g, copyLength1.color.b, 1.0f);
				copyLength2.color = new Color(copyLength2.color.r, copyLength2.color.g, copyLength2.color.b, 0.1f);
			} else if (frameSelected2 == "?") {
				if(FlikittCore.currentFrame > int.Parse(frameSelected1)){
					copyLength1.color = new Color(copyLength1.color.r, copyLength1.color.g, copyLength1.color.b, 0.1f);
					copyLength2.color = new Color(copyLength2.color.r, copyLength2.color.g, copyLength2.color.b, 1.0f);
				} else if (FlikittCore.currentFrame < int.Parse(frameSelected1)){
					frameSelected2 = "?";
					frameSelected1 = "?";
				}
			}
		} else {
			copyLength1.color = new Color(copyLength1.color.r, copyLength1.color.g, copyLength1.color.b, 0.1f);
			copyLength2.color = new Color(copyLength2.color.r, copyLength2.color.g, copyLength2.color.b, 0.1f);
		}

		if(!FlikittCore.isPlaying){
			copyPaste.color = new Color(copyPaste.color.r, copyPaste.color.g, copyPaste.color.b, 1.0f);
			if(frameToCopy == "?"){
				copyPaste.sprite = copy;
			} else {
				copyPaste.sprite = paste;
			}
		} else {
			copyPaste.color = new Color(copyPaste.color.r, copyPaste.color.g, copyPaste.color.b, 0.1f);
		}

		if(mode == "Edit"){
			Text frameCounter2 = GameObject.Find("Frame Counter_Edit").GetComponent<Text>();
			frameCounter2.text = cframe + " / " + projlength;

			Text start = GameObject.Find("CopyLengthStart").GetComponent<Text>();
			start.text = frameSelected1.ToString();

			Text end = GameObject.Find("CopyLengthEnd").GetComponent<Text>();
			end.text = frameSelected2.ToString();

			Text frameToCopi = GameObject.Find("FrameToCopy").GetComponent<Text>();
			frameToCopi.text = frameToCopy;
		}

		if(mode == "Edit"){
			frameScrubber.maxValue = FlikittCore.project.getAllFrames().Count; frameScrubber.minValue = 1;

			if(FlikittCore.isPlaying){
				frameScrubber.value = FlikittCore.currentFrame;
			} else {
				FlikittCore.LoadPage((int)frameScrubber.value);
			}
		}

		if(FlikittCore.drawMode == "Pencil"){
			pencil.sprite = pencilOn;
			eraser.sprite = eraserOff;
			manipulate.sprite = manOff;
		} else if (FlikittCore.drawMode == "Eraser") {
			pencil.sprite = pencilOff;
			eraser.sprite = eraserOn;
			manipulate.sprite = manOff;
		} else {
			pencil.sprite = pencilOff;
			eraser.sprite = eraserOff;
			manipulate.sprite = manOn;
		}

		//If frame one, or some frame doesn't have a picture on it
		if(FlikittCore.project.getAllFrames().Count > 1){
			bool allFine = true;
			for(int i = 0; i < FlikittCore.project.getAllFrames().Count; i++){
				if(!FlikittCore.project.getFrame(i).getHasPicture()) allFine = false;
			}

			if (allFine){
				canPlay = true;
			} else {
				canPlay = false;
			}
		} else {
			canPlay = false;
		}
		if(mode == "Capture"){
			if(!canPlay){
				Image playButton = GameObject.Find("Play / Pause").GetComponent<Image>();
				playButton.color = new Color(playButton.color.r, playButton.color.g, playButton.color.b, 0.1f);
			} else {
				Image playButton = GameObject.Find("Play / Pause").GetComponent<Image>();
				playButton.color = new Color(playButton.color.r, playButton.color.g, playButton.color.b, 1.0f);
			}
		}

		if(mode == "Edit"){
			selectEdit.color = new Color(selectEdit.color.r, selectEdit.color.g, selectEdit.color.b, 1.0f);
		} else {
			selectEdit.color = new Color(selectEdit.color.r, selectEdit.color.g, selectEdit.color.b, 0.1f);
		}

		if(mode == "Capture"){
			selectCam.color = new Color(selectCam.color.r, selectCam.color.g, selectCam.color.b, 1.0f);
		} else {
			selectCam.color = new Color(selectCam.color.r, selectCam.color.g, selectCam.color.b, 0.1f);
		}

		if(mode == "Drawing"){
			selectDraw.color = new Color(selectDraw.color.r, selectDraw.color.g, selectDraw.color.b, 1.0f);
		} else {
			selectDraw.color = new Color(selectDraw.color.r, selectDraw.color.g, selectDraw.color.b, 0.1f);
		}


		//Muting frame selectors
		if(!FlikittCore.isPlaying && mode != "Edit" && mode != "Recording"){
			//Muting forward button event
			if(!FlikittCore.getCurrentFrame().getHasPicture() || FlikittCore.project.getAllFrames().Count == FlikittCore.getCurrentFrame().getNumber()){
				Image forward = GameObject.Find("Forward").GetComponent<Image>();
				forward.color = new Color(forward.color.r, forward.color.g, forward.color.b, 0.1f);
			} else {
				Image forward = GameObject.Find("Forward").GetComponent<Image>();
				forward.color = new Color(forward.color.r, forward.color.g, forward.color.b, 1.0f);
			}

			//Muting back button event
			if(FlikittCore.currentFrame != 1){
				if(!FlikittCore.getCurrentFrame().getHasPicture()){
					Image back = GameObject.Find("Back").GetComponent<Image>();
					back.color = new Color(back.color.r, back.color.g, back.color.b, 0.1f);
				} else {
					Image back = GameObject.Find("Back").GetComponent<Image>();
					back.color = new Color(back.color.r, back.color.g, back.color.b, 1.0f);
				}
			} else{
				Image back = GameObject.Find("Back").GetComponent<Image>();
				back.color = new Color(back.color.r, back.color.g, back.color.b, 0.1f);
			}
		} else if (FlikittCore.isPlaying && mode != "Edit") {
			Image forward = GameObject.Find("Forward").GetComponent<Image>();
			forward.color = new Color(forward.color.r, forward.color.g, forward.color.b, 0.1f);
			Image back = GameObject.Find("Back").GetComponent<Image>();
			back.color = new Color(back.color.r, back.color.g, back.color.b, 0.1f);
		}

		if(mode == "Capture"){
			//Playing events
			if(FlikittCore.isPlaying){
				Image playButton = GameObject.Find("Play / Pause").GetComponent<Image>();
				playButton.sprite = playing;

				//Image camButton = GameObject.Find("Capture Button").GetComponent<Image>();
				//camButton.color = new Color(camButton.color.r, camButton.color.g, camButton.color.b, 0.1f);

				//Image micButton = GameObject.Find("Microphone").GetComponent<Image>();
				//micButton.color = new Color(micButton.color.r, micButton.color.g, micButton.color.b, 0.1f);

			} else {
				Image playButton = GameObject.Find("Play / Pause").GetComponent<Image>();
				playButton.sprite = notPlaying;

				//Image camButton = GameObject.Find("Capture Button").GetComponent<Image>();
				//camButton.color = new Color(camButton.color.r, camButton.color.g, camButton.color.b, 1.0f);

				//Image micButton = GameObject.Find("Microphone").GetComponent<Image>();
				//micButton.color = new Color(micButton.color.r, micButton.color.g, micButton.color.b, 1.0f);
			}
		} else if (mode != "Edit" && mode != "Recording") {
			if (FlikittCore.project.getAllFrames().Count < FlikittCore.currentFrame + 1){
				Image forward = GameObject.Find("Forward").GetComponent<Image>();
				forward.color = new Color(forward.color.r, forward.color.g, forward.color.b, 0.1f);
			}
		}

		if(mode == "Edit"){
			if(FlikittCore.isPlaying){
				Image playButton = GameObject.Find("Play / Pause_Edit").GetComponent<Image>();
				playButton.sprite = playing;
			} else {
				Image playButton = GameObject.Find("Play / Pause_Edit").GetComponent<Image>();
				playButton.sprite = notPlaying;
			}
		}

		if(FlikittCore.drawMode == "Pencil"){
			drawingSlider.text = "line thickness";
		}

		if(FlikittCore.drawMode == "Manipulate"){
			drawingSlider.text = "catch radius";
			if(Input.touchCount >= 1){
				Touch touch = Input.GetTouch(0);
				Vector3 touchPos = touch.position;
				touchPos.z = 0;
				touchPos = Camera.main.ScreenToWorldPoint(touchPos);
				Vector2 twoDTouchPos = new Vector2(touchPos.x, touchPos.y);
				
				for(int i = 0; i < FlikittCore.getCurrentFrame().getGOSelf().transform.childCount; i++){
					for(int p = 0; p < FlikittCore.getCurrentFrame().getGOSelf().transform.GetChild(i).GetComponent<LineRenderer>().positionCount; p++){
						if(Vector2.Distance(twoDTouchPos, FlikittCore.getCurrentFrame().getGOSelf().transform.GetChild(i).GetComponent<LineRenderer>().GetPosition(p)) <= thicknessSlider.value){
							LineRenderer line = FlikittCore.getCurrentFrame().getGOSelf().transform.GetChild(i).GetComponent<LineRenderer>();
							Vector3 change = touch.deltaPosition;
							Vector3 dragDistance = new Vector3((change.x / Screen.width) * 2,
                               (change.y / Screen.height) * 2, 0);

							if(Input.touchCount >= 2){
								Touch zoom = Input.GetTouch(1);

								Vector3 zo = zoom.deltaPosition;
								Vector3 drawZo = new Vector3((zo.x / Screen.width),
                                (zo.y / Screen.height), 0);

								for(int i3 = 0; i3< line.positionCount && Input.touchCount >= 2; i3++){
									if(Input.touchCount != 2){
										break;
									}
									line.SetPosition(i3, Vector3.Scale(line.GetPosition(i3), new Vector3(1, 1,1) + drawZo));
								}
							}

							for(int i2 = 0; i2 < line.positionCount; i2++){
								line.SetPosition(i2, line.GetPosition(i2) + dragDistance);
							}
						}
					}
				}
			}
		}

		if(FlikittCore.drawMode == "Eraser"){
			thicknessSlider.interactable = false;
			drawingSlider.text = "";
			if(Input.touchCount == 1){
				Touch touch = Input.GetTouch(0);
				Vector3 touchPos = touch.position;
				touchPos.z = 0;
				touchPos = Camera.main.ScreenToWorldPoint(touchPos);
				Vector2 twoDTouchPos = new Vector2(touchPos.x, touchPos.y);
				
				for(int i = 0; i < FlikittCore.getCurrentFrame().getGOSelf().transform.childCount; i++){
					for(int p = 0; p < FlikittCore.getCurrentFrame().getGOSelf().transform.GetChild(i).GetComponent<LineRenderer>().positionCount; p++){
						if(Vector2.Distance(twoDTouchPos, FlikittCore.getCurrentFrame().getGOSelf().transform.GetChild(i).GetComponent<LineRenderer>().GetPosition(p)) < 0.2f){
							Destroy(FlikittCore.getCurrentFrame().getGOSelf().transform.GetChild(i).gameObject);
						}
					}
				}
			}
		} else {
			thicknessSlider.interactable = true;
		}

		//This is bad way to do this, find a way to meld them together...
		if(Input.touchCount > 0){
			Vector3 fingerPosition1 = Input.mousePosition;
			fingerPosition1.z = Mathf.Infinity;
			RaycastHit2D hit1 = Physics2D.Raycast(fingerPosition1, fingerPosition1 - Camera.main.ScreenToWorldPoint(fingerPosition1), Mathf.Infinity);
			if(hit1.collider != null){
				canDraw = false;
			} else {
				canDraw = true;
			}
		}

		if(Input.touchCount == 0 && FlikittCore.isShooting){
			FlikittCore.isShooting = false;
			print("DID!");
			if(FlikittCore.project.getAllFrames().Count - 1 > 1){ //Check if project is bigger than one frame...
				if(FlikittCore.project.getAllFrames().Count >= FlikittCore.currentFrame + 1){ //Check if we aren't on the last frame
					FlikittCore.project.deleteFrame(FlikittCore.project.getFrame(FlikittCore.currentFrame - 1));
					FlikittCore.LoadPage(FlikittCore.currentFrame - 1);
				} else {
					FlikittCore.project.deleteFrame(FlikittCore.project.getFrame(FlikittCore.project.getAllFrames().Count - 1));
					FlikittCore.LoadPage(FlikittCore.currentFrame - 1);
				}
			}
		}

		if(FlikittCore.currentFrame == 1 && !FlikittCore.isPlaying){
			copyFrame.color = new Color(copyFrame.color.r, copyFrame.color.g, copyFrame.color.b, 0.1f);
		} else if (FlikittCore.currentFrame != 1 && !FlikittCore.isPlaying) {
			if(FlikittCore.getCurrentFrame().getHasPicture()){
				if(FlikittCore.project.getFrame(FlikittCore.currentFrame - 2).getGOSelf().transform.childCount != 0){
					copyFrame.color = new Color(copyFrame.color.r, copyFrame.color.g, copyFrame.color.b, 1.0f);
				} else {
					copyFrame.color = new Color(copyFrame.color.r, copyFrame.color.g, copyFrame.color.b, 0.1f);
				}
			} else {
				copyFrame.color = new Color(copyFrame.color.r, copyFrame.color.g, copyFrame.color.b, 0.1f);
			}
		} else if (FlikittCore.isPlaying){
			copyFrame.color = new Color(copyFrame.color.r, copyFrame.color.g, copyFrame.color.b, 0.1f);
		}

		if(FlikittCore.project.getAllFrames().Count == 1 && !FlikittCore.isPlaying){
			trash.color = new Color(trash.color.r, trash.color.g, trash.color.b, 0.1f);
		} else if (FlikittCore.isPlaying){
			trash.color = new Color(trash.color.r, trash.color.g, trash.color.b, 0.1f);
		} else {
			trash.color = new Color(trash.color.r, trash.color.g, trash.color.b, 1.0f);
		}

		if(MicrophoneManager.currentlyRecording){
			mic.sprite = micOn;
			if(Input.touchCount == 0){
				MicrophoneManager.StopRecording();
			}
		} else {
			mic.sprite = micOff;
		}

		if(FlikittCore.isPlaying && !MicrophoneManager.currentlyRecording){
			mic.sprite = micOff;
			mic.color = new Color(mic.color.r, mic.color.g, mic.color.b, 0.1f);
		} else if (FlikittCore.isPlaying && MicrophoneManager.currentlyRecording){
			mic.sprite = micOn;
			mic.color = new Color(mic.color.r, mic.color.g, mic.color.b, 1.0f);
		} else {
			if(FlikittCore.project.checkCompletion()){
				mic.sprite = micOff;
				mic.color = new Color(mic.color.r, mic.color.g, mic.color.b, 1.0f);
			} else {
				mic.color = new Color(mic.color.r, mic.color.g, mic.color.b, 0.1f);
			}
		}

		/*
		MODE MANAGEMENT!
		*/

		//For buttons (happens once)
		if(Input.GetMouseButtonDown(0)){
			Vector3 fingerPosition = Input.mousePosition;
			fingerPosition.z = Mathf.Infinity;
			RaycastHit2D hit = Physics2D.Raycast(fingerPosition, fingerPosition - Camera.main.ScreenToWorldPoint(fingerPosition), Mathf.Infinity);
			if(hit.collider != null){
				switch(hit.collider.gameObject.name){
					case "Forward":

						if(!FlikittCore.isPlaying){
							if(FlikittCore.getCurrentFrame().getHasPicture()){
								if(cframe + 1 > projlength){
								} else {
									FlikittCore.LoadPage(cframe + 1);
								}
							}
						}

						break;

					case "Back":

						if(!FlikittCore.isPlaying){
							if(cframe - 1 <= 0){
								FlikittCore.LoadPage(1);
							} else {
								if(FlikittCore.getCurrentFrame().getHasPicture()){
									FlikittCore.LoadPage(cframe - 1);
								}
							}
						}

						break;

					case "Play / Pause":

						if(!FlikittCore.isPlaying){
							if(canPlay){
								FlikittCore.isPlaying = true;
								FlikittCore.StartPlay();
							}
						} else {
							FlikittCore.LoadPage(1);
							FlikittCore.isPlaying = false;
						}

						break;

					case "Record":

						if(!FlikittCore.isPlaying){
							if(!MicrophoneManager.currentlyRecording){
								if(FlikittCore.project.checkCompletion()){
									FlikittCore.isPlaying = true;
									FlikittCore.StartPlay();
									if(FlikittCore.overdub){
										MicrophoneManager.StartRecordingOverdub();
									} else {
										MicrophoneManager.StartRecordingNoOverdub();
									}
								}
							}
						}
						break;

					case "Play / Pause_Edit":

						if(!FlikittCore.isPlaying){
							if(canPlay){
								FlikittCore.isPlaying = true;
								FlikittCore.StartPlay();
							}
						} else {
							FlikittCore.LoadPage(1);
							FlikittCore.isPlaying = false;
						}

						break;

					case "Red_Swatch":

						DrawingManager.colorName = "Red";
						DisableAllSwatches();
						EnableSwatch("Red");
						break;

					case "Orange_Swatch":

						DrawingManager.colorName = "Orange";
						DisableAllSwatches();
						EnableSwatch("Orange");
						break;

					case "Yellow_Swatch":

						DrawingManager.colorName = "Yellow";
						DisableAllSwatches();
						EnableSwatch("Yellow");
						break;

					case "Green_Swatch":

						DrawingManager.colorName = "Green";
						DisableAllSwatches();
						EnableSwatch("Green");
						break;

					case "Blue_Swatch":

						DrawingManager.colorName = "Blue";
						DisableAllSwatches();
						EnableSwatch("Blue");
						break;

					case "Indigo_Swatch":

						DrawingManager.colorName = "Indigo";
						DisableAllSwatches();
						EnableSwatch("Indigo");
						break;

					case "Purple_Swatch":

						DrawingManager.colorName = "Purple";
						DisableAllSwatches();
						EnableSwatch("Purple");
						break;

					case "White_Swatch":

						DrawingManager.colorName = "White";
						DisableAllSwatches();
						EnableSwatch("White");
						break;

					case "Black_Swatch":

						DrawingManager.colorName = "Black";
						DisableAllSwatches();
						EnableSwatch("Black");
						break;

					case "Pencil":

						if(FlikittCore.drawMode != "Pencil") {FlikittCore.drawMode = "Pencil";}
						break;

					case "Manipulate":

						if(FlikittCore.drawMode != "Manipulate") {FlikittCore.drawMode = "Manipulate";}
						break;

					case "Eraser":

						if(FlikittCore.drawMode != "Eraser") {FlikittCore.drawMode = "Eraser";}
						break;

					case "SelectCapture":

						if(mode != "Capture") { SetMode("Capture"); }
						break;

					case "SelectDrawing":

						bool canProceed = true;
						for(int i = 0; i < FlikittCore.project.getAllFrames().Count; i++){
							if(!FlikittCore.project.getFrame(i).getHasPicture()) canProceed = false;
						}

						if(canProceed) { if(mode != "Drawing") { SetMode("Drawing"); } }
						break;

					case "SelectEdit":

						bool canProceed2 = true;
						for(int i = 0; i < FlikittCore.project.getAllFrames().Count; i++){
							if(!FlikittCore.project.getFrame(i).getHasPicture()) canProceed2 = false;
						}

						if(canProceed2) { if(mode != "Edit") { SetMode("Edit"); } }

						break;


					case "Trash":

					//...You can still delete frame one?! Resolved?
						if(FlikittCore.project.getAllFrames().Count != 1){
							if(!FlikittCore.isPlaying){
								if(FlikittCore.project.getAllFrames().Count >= FlikittCore.currentFrame + 1){
									FlikittCore.project.deleteFrame(FlikittCore.project.getFrame(FlikittCore.currentFrame - 1));
									FlikittCore.LoadPage(FlikittCore.currentFrame);
								} else if (FlikittCore.currentFrame == FlikittCore.project.getAllFrames().Count){
									FlikittCore.project.deleteFrame(FlikittCore.project.getFrame(FlikittCore.currentFrame - 1));
									FlikittCore.LoadPage(FlikittCore.currentFrame - 1);
								}
							}
						}
						break;

					case "Copy":

						if(!FlikittCore.isPlaying){
							if(FlikittCore.getCurrentFrame().getHasPicture()){
								if(FlikittCore.currentFrame != 1)
									FlikittCore.project.CopyLines(FlikittCore.project.getFrame(FlikittCore.currentFrame - 2));
							}
						}
						break;

					case "CopyLength_Phase1":


						if(!FlikittCore.isPlaying){
							if(copyLength1.color == new Color(copyLength1.color.r, copyLength1.color.g, copyLength1.color.b, 1.0f)){
								frameSelected1 = FlikittCore.currentFrame.ToString();
								copyLength1.color = new Color(copyLength1.color.r, copyLength1.color.g, copyLength1.color.b, 0.1f);
							}
						}

						break;

					case "CopyLength_Phase2":

						if(!FlikittCore.isPlaying){
							if(copyLength2.color == new Color(copyLength2.color.r, copyLength2.color.g, copyLength2.color.b, 1.0f)){
								frameSelected2 = FlikittCore.currentFrame.ToString();

								if(frameSelected2 != "?"){
									if(int.Parse(frameSelected2) > int.Parse(frameSelected1)){
										FlikittCore.project.CopyLengthLines(int.Parse(frameSelected1), int.Parse(frameSelected2));
										frameSelected1 = "?";
										frameSelected2 = "?";
									} else if (int.Parse(frameSelected2) == int.Parse(frameSelected1)){
										frameSelected1 = "?";
										frameSelected2 = "?";
									}
								}

								copyLength2.color = new Color(copyLength2.color.r, copyLength2.color.g, copyLength2.color.b, 0.1f);
								copyLength1.color = new Color(copyLength1.color.r, copyLength1.color.g, copyLength1.color.b, 1.0f);
							}
						}
						break;

					case "CopyPaste":

						if(!FlikittCore.isPlaying){
							if(copyPaste.sprite == copy){
								frameToCopy = FlikittCore.currentFrame.ToString();
								copyPaste.sprite = paste;
							} else {
								FlikittCore.project.CopyLines(FlikittCore.project.getFrame(int.Parse(frameToCopy) - 1));
								frameToCopy = "?";
							}
						}

						break;

					case "Select Track":

						if(FlikittCore.project.getAllAudio() != null){

							DrawWaveform dw = WAVEFORM.GetComponent<DrawWaveform>();
							int maxSize = FlikittCore.project.getAllAudio().Count;

							if(dw.currentTrack + 1 > maxSize){
								dw.currentTrack = 1;
							} else {
								dw.currentTrack++;
							}

							AudioClip toBeDrawn = FlikittCore.project.getAllAudio()[dw.currentTrack - 1];
							dw.GenerateWaveformImage(toBeDrawn);

							currentTrackCounter.text = dw.currentTrack.ToString();

						}

						break;

					case "Delete Track":
						if(FlikittCore.project.getAllAudio() != null){
							DrawWaveform dw = WAVEFORM.GetComponent<DrawWaveform>();
							MicrophoneManager.deleteAllAudio();
							FlikittCore.project.removeAllAudio();
							dw.GenerateWaveformImage(silence);
							currentTrackCounter.text = 1.ToString();
							MicrophoneManager.currentTrack = 1;
						}
						break;

					case "Save and Quit":
						if(!FlikittCore.isPlaying){
							if(FlikittCore.project.checkCompletion()){

								foreach(var obj in Resources.FindObjectsOfTypeAll<Transform>() as Transform[]){
									if(obj.gameObject.name == "Saving Dock" || obj.gameObject.name == "Save Text"){
										obj.gameObject.SetActive(true);
									}
								}

								//Take a screenshot for thumbnail
								SetMode("Recording");
								ScreenCapture.CaptureScreenshot(Application.persistentDataPath + "/Project " + FlikittCore.project.getName() + " thumbnail.jpg", 1);

								SaveLoad.Save(FlikittCore.project.getName());
								//CameraManager.getWebTex().Stop();
								SceneManager.LoadScene("Splash Screen");
							}
						}
						break;

					case "Share":

						if(!FlikittCore.isPlaying){
							ShareManager.StartRecording();
						}
						break;

					case "Image Transparency Toggle":


						if(mode == "Edit"){
							ImageTransparency it = GameObject.Find("Image Transparency").GetComponent<ImageTransparency>();
							it.toggleToggle();
						}
						break;


					default:
						break;
				}
			}
		}
	}
}
