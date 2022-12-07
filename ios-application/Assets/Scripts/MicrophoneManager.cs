using System.Collections;
using System.Collections.Generic;
using UnityEngine;

#if PLATFORM_ANDROID
using UnityEngine.Android;
#else
using UnityEngine.iOS;
#endif

public class MicrophoneManager : MonoBehaviour
{
	FlikittCore FlikittCore;
	private List<AudioSource> recordings;
	public int currentTrack;
	private string microphone;
	public bool currentlyRecording, hasRecording;

	void Start(){
		FlikittCore = GameObject.Find("Flikitt Core").GetComponent<FlikittCore>();

		//Android Permissions, and grabbing the phone microphone
		#if PLATFORM_ANDROID
		if(!Permission.HasUserAuthorizedPermission(Permission.Microphone)){
			Permission.RequestUserPermission(Permission.Microphone);
		}

		if(Microphone.devices != null){
			microphone = Microphone.devices[0];
		}
		currentTrack = 1;
		hasRecording = false;
		StartLoadMakeAudio();

		#else

		currentTrack = 1;
		hasRecording = false;
		StartLoadMakeAudio();

		#endif

	}

	private IEnumerator audioLoad;
	void StartLoadMakeAudio(){
		audioLoad = LoadMakeAudio();
		StartCoroutine(audioLoad);
	}

	private IEnumerator LoadMakeAudio(){
		yield return new WaitForSeconds(1);
		if(FlikittCore.project.getAllAudio() != null){
			if(FlikittCore.project.getAllAudio().Count == 0){ //No
				recordings = new List<AudioSource>();
			} else { 
				recordings = new List<AudioSource>();
				foreach(var clip in FlikittCore.project.getAllAudio()){
					AudioSource source = this.gameObject.AddComponent<AudioSource>();
					source.clip = clip;
					recordings.Add(source);
				}
			}
		}
	}

	void Update(){
		if (gameObject.GetComponent<AudioSource>() == null){
			hasRecording = false;
		} else {
			hasRecording = true;
		}
	}

	public List<AudioSource> getAllSources(){
		return recordings;
	}

	public void StartRecordingOverdub(){

		if(Microphone.devices != null){
			microphone = Microphone.devices[0];
		}
		
		currentTrack = recordings.Count + 1;
		AudioSource currentAudio = this.gameObject.AddComponent<AudioSource>();
		recordings.Add(currentAudio);
		currentAudio.mute = true;
		currentlyRecording = true;

		int time = (int)(Mathf.Round((1 / FlikittCore.project.getFps()) * FlikittCore.project.getAllFrames().Count)) + 1;
		recordings[currentTrack - 1].clip = null;
		recordings[currentTrack - 1].clip = Microphone.Start(microphone, false, time, 25000);

		if(Microphone.IsRecording(microphone)){
			while(!(Microphone.GetPosition(microphone) > 0)){}
		} else {
			Debug.Log("An error occurred trying to record!");
			FlikittCore.isPlaying = false;
			recordings[currentTrack - 1].clip = null;
			return;
		}
	}

	public void StopRecording(){
		Microphone.End(microphone);
		currentlyRecording = false;
		recordings[currentTrack - 1].mute = false;
		if(recordings[currentTrack - 1].clip != null){
			FlikittCore.project.addAudio(recordings[currentTrack - 1].clip);
		}
		FlikittCore.isPlaying = false;
		FlikittCore.LoadPage(1);
	}

	public void StartRecordingNoOverdub(){
		for(int i = 0; i < gameObject.GetComponents<AudioSource>().Length; i++){
			Destroy(gameObject.GetComponents<AudioSource>()[i]);
		}
		currentTrack = 1;
		recordings = new List<AudioSource>();
		FlikittCore.project.removeAllAudio();
		AudioSource currentAudio = this.gameObject.AddComponent<AudioSource>();
		recordings.Add(currentAudio);
		currentAudio.mute = true;
		currentlyRecording = true;

		if(Microphone.devices != null){
			microphone = Microphone.devices[0];
		}
		
		int time = (int)(Mathf.Round((1 / FlikittCore.project.getFps()) * FlikittCore.project.getAllFrames().Count)) + 1;
		recordings[currentTrack - 1].clip = null;
		recordings[currentTrack - 1].clip = Microphone.Start(microphone, false, time, 25000);

		if(Microphone.IsRecording(microphone)){
			while(!(Microphone.GetPosition(microphone) > 0)){}
		} else {
			Debug.Log("An error occurred trying to record!");
			FlikittCore.isPlaying = false;
			recordings[currentTrack - 1].clip = null;
			return;
		}
	}

	public void deleteAllAudio(){
		for(int i = 0; i < gameObject.GetComponents<AudioSource>().Length; i++){
			Destroy(gameObject.GetComponents<AudioSource>()[i]);
		}
		recordings = new List<AudioSource>();
	}

	public Texture2D PaintWaveformSpectrum(AudioClip audio, float saturation, int width, int height, Color col, Color bgColor) {
		Texture2D tex = new Texture2D(width, height, TextureFormat.RGBA32, false);
		float[] samples = new float[audio.samples];
		float[] waveform = new float[width];
		audio.GetData(samples, 0);
		int packSize = ( audio.samples / width ) + 1;
		int s = 0;
		for (int i = 0; i < audio.samples; i += packSize) {
			waveform[s] = Mathf.Abs(samples[i]);
			s++;
		}

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				tex.SetPixel(x, y, bgColor);
			}
		}

		for (int x = 0; x < waveform.Length; x++) {
			for (int y = 0; y <= waveform[x] * ((float)height * .75f); y++) {
				tex.SetPixel(x, ( height / 2 ) + y, col);
				tex.SetPixel(x, ( height / 2 ) - y, col);
			}
		}
		tex.Apply();

		return tex;
 	}
}
