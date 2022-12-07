using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class DrawWaveform : MonoBehaviour
{
	FlikittCore fc;
	MicrophoneManager mm;

    private int width = 381;
    private int height = 55;
    private Color waveformColor = Color.white;
    private Color bgColor = Color.clear;
    private float sat = .5f;
    public int currentTrack = 1;

    [SerializeField] Image img;

    void Start(){
    	fc = GameObject.Find("Flikitt Core").GetComponent<FlikittCore>();
    	mm = GameObject.Find("Microphone Manager").GetComponent<MicrophoneManager>();
    }

    public void GenerateWaveformImage(AudioClip clip){
    	Texture2D texture = mm.PaintWaveformSpectrum(clip, sat, width, height, waveformColor, bgColor);
    	img = gameObject.GetComponent<Image>();
    	img.overrideSprite = Sprite.Create(texture, new Rect(0f, 0f, texture.width, texture.height), new Vector2(0.5f, 0.5f));
    }
}
