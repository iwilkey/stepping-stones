using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ImageTransparency : MonoBehaviour
{	
	public Slider slider;
	public Image toggle;
	public Sprite on, off;
	FlikittCore FlikittCore;
	void Start(){
		FlikittCore = GameObject.Find("Flikitt Core").GetComponent<FlikittCore>();
		toggle.sprite = off;
	}

	void Update(){
		slider.value = FlikittCore.getCurrentFrame().getTransparency();
	}

    public void changeTransparency(){
    	if(toggle.sprite == on){
    		FlikittCore.project.allImageTransparency(slider.value);
    	} else {
    		FlikittCore.project.imageTransparency(FlikittCore.getCurrentFrame(), slider.value);
    	}
    }

    public void toggleToggle(){
    	toggle.sprite = (toggle.sprite == off) ? on : off;
    }
}
