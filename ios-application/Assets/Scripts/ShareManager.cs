using System.Collections;
using System.Collections.Generic;
using UnityEngine;

#if PLATFORM_IOS || UNITY_EDITOR
using UnityEngine.iOS;
using UnityEngine.Apple.ReplayKit;

public class ShareManager : MonoBehaviour
{
	UserInterface ui;
	FlikittCore fc;
	public bool isRecording;

	void Start(){
		ui = GameObject.Find("User Interface").GetComponent<UserInterface>();
		fc = GameObject.Find("Flikitt Core").GetComponent<FlikittCore>();
	}

	public void StartRecording(){

		ui.SetMode("Recording");
		fc.StartRecPlay();
		isRecording = true;

		if(ReplayKit.APIAvailable){	
			ReplayKit.StartRecording(false, false);
		}
	}

	public void StopRecording(){
		if(ReplayKit.APIAvailable){
			if(ReplayKit.isRecording){
				isRecording = false;
				ReplayKit.StopRecording();
				ReplayKit.Preview();
			}
		}

		isRecording = false;
		ui.SetMode("Edit");
	}
}

#endif
