using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.IO;
using B83.Image.BMP;

public class Project : MonoBehaviour {
	DrawingManager dm;
	FlikittCore fc;
	private string name, type;
	private List<Frame> frames;
	private List<AudioClip> allAudio;
	private float fps;
	private GameObject linePrefab;

	//Constructing a new project
	public Project(string _name, string _type, GameObject _linePrefab){
		dm = GameObject.Find("Drawing Manager").GetComponent<DrawingManager>();
		fc = GameObject.Find("Flikitt Core").GetComponent<FlikittCore>();
		name = _name; 
		type = _type;
		linePrefab = _linePrefab;
		frames = new List<Frame>();
		allAudio = new List<AudioClip>();
		fps = 7.5f;
	}

	//Loading an existing project
	public Project(string _name, string _type, List<Frame> _frames, List<AudioClip> _audio, float _fps){
		dm = GameObject.Find("Drawing Manager").GetComponent<DrawingManager>();
		fc = GameObject.Find("Flikitt Core").GetComponent<FlikittCore>();
		name = _name;

		type = _type;
		frames = _frames;
		allAudio = _audio;
		fps = _fps;
	}

	//Accessor
	public string getName() {return name;}
	public string getType() {return type;}
	public AudioClip getAudio(int index) {return allAudio[index];}
	public List<AudioClip> getAllAudio() {return allAudio;}
	public float getFps() {return fps;}
	public Frame getFrame(int number) {return frames[number];}
	public List<Frame> getAllFrames() {return frames;}

	//Mutator
	public void addFrame(Frame frame) {frames.Add(frame);}
	public void addAudio(AudioClip _audio) {allAudio.Add(_audio);}
	public void removeAllAudio() {allAudio = new List<AudioClip>();}
	public void removeAudio(int index) {
		allAudio.RemoveAt(index);
	}
	public void setFps(float _fps) {fps = _fps;}
	public bool checkCompletion(){
		for(int i = 0; i < frames.Count; i++){
			if(!frames[i].getHasPicture()) return false;
		}
		return true;
	}
	public bool checkAudioCompletion(){
		for(int i = 0; i < allAudio.Count; i++){
			if(allAudio[i] == null) return false;
		}
		return true;
	}

	public void deleteFrame(Frame frame){
		for (int i = 0; i < frames.Count; i++){
			if (frames[i].getName() == frame.getName()){
				Destroy(frames[i].getGOSelf());
				frames.RemoveAt(i);
			}
		}

		for (int i = 0; i < frames.Count; i++){
			if(frames[i].getName() != "Frame " + i + 1){
				frames[i].setName(i + 1);
			}
		}
	}

	public void InsertFrame(int index){
		for(int i = 0; i < frames.Count; i++){
			if(i >= index){
				frames[i].setName(i + 2);
			}
		}

		frames.Insert(index, new Frame(index + 1));
		fc.LoadPage(index + 1);
	}

	public void CopyLines(Frame frame){
		int lineAmount = frame.getGOSelf().transform.childCount;
		List<Color> lineColors = new List<Color>();
		List<float> lineThicknesses = new List<float>();
		List<List<Vector2>> linePoints = new List<List<Vector2>>();

		for(int i = 0; i < lineAmount; i++){
			lineColors.Add(frame.getGOSelf().transform.GetChild(i).GetComponent<LineRenderer>().startColor);
			lineThicknesses.Add(frame.getGOSelf().transform.GetChild(i).GetComponent<LineRenderer>().startWidth);

			List<Vector2> points = new List<Vector2>();
			for(int p = 0; p < frame.getGOSelf().transform.GetChild(i).GetComponent<LineRenderer>().positionCount; p++){
				Vector3 slight = new Vector3(0,0,0);
				if(p % 5 == 0){
					slight = new Vector3(Random.Range(-0.01f, 0.01f), Random.Range(-0.01f, 0.01f), 0);
				}
				points.Add(frame.getGOSelf().transform.GetChild(i).GetComponent<LineRenderer>().GetPosition(p) + slight);
			}
			linePoints.Add(points);
		}

		for(int i = 0; i < lineAmount; i++){
			dm.currentLine++;
			GameObject line = Instantiate(linePrefab);
			line.name = "Line " + dm.currentLine;
			line.GetComponent<Line>().color = lineColors[i];

			LineRenderer renderer = line.GetComponent<LineRenderer>();
			renderer.SetColors(lineColors[i], lineColors[i]);
			renderer.SetWidth(lineThicknesses[i], lineThicknesses[i]);
			renderer.positionCount = linePoints[i].Count;

			for(int p = 0; p < renderer.positionCount; p++){
				renderer.SetPosition(p, linePoints[i][p]);
			}

			line.transform.parent = fc.project.getFrame(fc.currentFrame - 1).getGOSelf().transform;
		}
	}

	public void allImageTransparency(float t){
		foreach(var frame in frames){
			frame.setTransparency(t);
		}
	}

	public void imageTransparency(Frame frame, float t){
		frame.setTransparency(t);
	}

	public void CopyLengthLines(int start, int end){
		for(int i2 = 1; i2 < (end - start) + 1; i2++){
			Frame frame = fc.project.getFrame(start + i2 - 2);

			int lineAmount = frame.getGOSelf().transform.childCount;
			List<Color> lineColors = new List<Color>();
			List<float> lineThicknesses = new List<float>();
			List<List<Vector2>> linePoints = new List<List<Vector2>>();

			for(int i = 0; i < lineAmount; i++){
				lineColors.Add(frame.getGOSelf().transform.GetChild(i).GetComponent<LineRenderer>().startColor);
				lineThicknesses.Add(frame.getGOSelf().transform.GetChild(i).GetComponent<LineRenderer>().startWidth);

				List<Vector2> points = new List<Vector2>();
				for(int p = 0; p < frame.getGOSelf().transform.GetChild(i).GetComponent<LineRenderer>().positionCount; p++){
					Vector3 slight = new Vector3(0,0,0);
					if(p % 5 == 0){
						slight = new Vector3(Random.Range(-0.01f, 0.01f), Random.Range(-0.01f, 0.01f), 0);
					}
					points.Add(frame.getGOSelf().transform.GetChild(i).GetComponent<LineRenderer>().GetPosition(p) + slight);
				}
				linePoints.Add(points);
			}

			for(int i = 0; i < lineAmount; i++){
				dm.currentLine++;
				GameObject line = Instantiate(linePrefab);
				line.name = "Line " + dm.currentLine;
				line.GetComponent<Line>().color = lineColors[i];

				LineRenderer renderer = line.GetComponent<LineRenderer>();
				renderer.SetColors(lineColors[i], lineColors[i]);
				renderer.SetWidth(lineThicknesses[i], lineThicknesses[i]);
				renderer.positionCount = linePoints[i].Count;

				for(int p = 0; p < renderer.positionCount; p++){
					renderer.SetPosition(p, linePoints[i][p]);
				}

				line.transform.parent = fc.project.getFrame(start + i2 - 1).getGOSelf().transform;
			}
		}
	}
}

public class Frame : MonoBehaviour {

	private string name;
	private string orientation;
	private float transparency;
	private int number;
	private bool enabled, hasPicture;
	private GameObject goSelf;
	private RawImage imageSelf;
	private RectTransform rT = null;
	//CameraManager cm;

	public Frame(int _number){
		//cm = GameObject.Find("Camera Manager").GetComponent<CameraManager>();

		name = "Frame " + _number;
		number = _number;

		goSelf = new GameObject(name, typeof(RectTransform));
		goSelf.gameObject.tag = "Frame";
		goSelf.transform.parent = GameObject.Find("Project").transform;
	
		rT = goSelf.GetComponent<RectTransform>();
		rT.SetAnchor(AnchorPresets.StretchAll);
		rT.localScale = new Vector3(1,1,1);

		goSelf.AddComponent<RawImage>();
		imageSelf = goSelf.GetComponent<RawImage>();
		imageSelf.GetComponent<RectTransform>().SetAnchor(AnchorPresets.StretchAll);
		transparency = 1.0f;

		hasPicture = false;
		enabled = true;
	}

	public Frame(int _number, RawImage _image){
		//cm = GameObject.Find("Camera Manager").GetComponent<CameraManager>();

		name = "Frame " + _number;
		number = _number;

		goSelf = new GameObject(name, typeof(RectTransform));
		goSelf.gameObject.tag = "Frame";
		goSelf.transform.parent = GameObject.Find("Project").transform;
	
		rT = goSelf.GetComponent<RectTransform>();
		rT.SetAnchor(AnchorPresets.StretchAll);
		rT.localScale = new Vector3(1,1,1);

		goSelf.AddComponent<RawImage>();
		imageSelf = goSelf.GetComponent<RawImage>();
		imageSelf.GetComponent<RectTransform>().SetAnchor(AnchorPresets.StretchAll);
		transparency = 1.0f;

		hasPicture = false;
		enabled = true;
	}

	public void Enable(){
		if(!enabled){
			foreach(var obj in Resources.FindObjectsOfTypeAll<Transform>() as Transform[]){
				if(obj.CompareTag("Frame") && obj.name == this.name){
					obj.gameObject.SetActive(true);
					enabled = true;
				}
			}
		} else {
			return;
		}
	}

	public void Disable(){
		if(enabled){
			foreach(var obj in Resources.FindObjectsOfTypeAll<Transform>() as Transform[]){
				if(obj.CompareTag("Frame") && obj.name == this.name){
					obj.gameObject.SetActive(false);
					enabled = false;
				}
			}
		} else {
			return;
		}
	}

	public GameObject getLine(int index){
		if(index <= goSelf.transform.childCount - 1){
			return goSelf.transform.GetChild(index).gameObject;
		}
		return null;
	}
	public int getNumber(){return number;}
	public void setName(int num) {
		name = "Frame " + num;
		goSelf.name = name;
	}
	public RawImage getImage() {return imageSelf;}
	public string getOrientation() {return orientation;}
	public float getTransparency() {return transparency;}
	public void setTransparency(float t) {
		transparency = t;
		imageSelf.color = new Color(imageSelf.color.r, imageSelf.color.g, imageSelf.color.b, t);
	}
	public string getName() {return name;}
	public bool getHasPicture(){return hasPicture;}
	public void setHasPicture(bool p) {hasPicture = p;}
	public void setPicture(Texture image) {imageSelf.texture = image;}
	public Texture getPicture() {return imageSelf.texture;}
	public bool isEnabled() {return enabled;}
	public void setStatus(string status){
		if(status == "On"){
			Enable();
		} else if (status == "Off"){
			Disable();
		} else {
			return;
		}
	}
	public GameObject getGOSelf() {return goSelf;}
}

public class FlikittCore : MonoBehaviour
{
	public Project project;
	public GameObject linePrefab;
	//CameraManager CameraManager;
	MicrophoneManager MicrophoneManager;
	SaveLoad SaveLoad;
	ShareManager ShareManager;

	public int currentFrame = 1;
	public bool isPlaying, continuousShot, isShooting, overdub;
	public float spf;
	public string drawMode;

	private string workBoard;
	private List<string> existingProjects = new List<string>();

	void Awake(){
		//CameraManager = GameObject.Find("Camera Manager").GetComponent<CameraManager>();
		MicrophoneManager = GameObject.Find("Microphone Manager").GetComponent<MicrophoneManager>();
		SaveLoad = GameObject.Find("Easy Save 3 Manager").GetComponent<SaveLoad>();
		ShareManager = GameObject.Find("Share Manager").GetComponent<ShareManager>();
		//Use easysave to find out if a project is being loaded or not...

		if(ES3.KeyExists("Work Board")){
			workBoard = ES3.Load<string>("Work Board");
		} else {
			workBoard = "New Project";
		}

		if(ES3.KeyExists("Existing Projects")){
			existingProjects = ES3.Load<List<string>>("Existing Projects");
		} else {
			existingProjects = new List<string>();
		}

		if(checkExistance(workBoard)){
			StartLoad(workBoard);
		} else {
			//If a new project
			string name = workBoard;
			string type = "Frame-by-Frame";
			project = new Project(name, type, linePrefab);

			int fps = ES3.Load<int>("FPS");
			project.setFps(fps);

			int numFrames = ES3.Load<int>("Frame Amount");
			for(int i = 0; i < numFrames; i++){
				NewPage();

			}
			foreach(Frame frame in project.getAllFrames()){
				Texture2D tex = LoadTexture(Application.persistentDataPath + "/UnprocessedFrames/frame" + frame.getNumber() + ".bmp");
				Texture tex2 = tex as Texture;
				frame.setPicture(tex2);
				frame.setHasPicture(true);
			}

			DirectoryInfo dataDir = new DirectoryInfo(Application.persistentDataPath + "/UnprocessedFrames");
 			dataDir.Delete(true);

			spf = 1 / project.getFps();

			LoadPage(1);

			drawMode = "Pencil";
		}
	}

	public static Texture2D LoadTexture(string filePath)
	{
    	Texture2D tex = null;

   		if (File.Exists(filePath))
    {
	        BMPLoader bmpLoader = new BMPLoader();
	        bmpLoader.ForceAlphaReadWhenPossible = true; //Uncomment to read alpha too

	        //Load the BMP data
	        BMPImage bmpImg = bmpLoader.LoadBMP(filePath);

	        //Convert the Color32 array into a Texture2D
	        tex = bmpImg.ToTexture2D();
	    }
	    return tex;
	}

	public bool checkExistance(string name){
		for(int i = 0; i < existingProjects.Count; i++){
			if(existingProjects[i] == name) return true;
		}

		return false;
	}

	private IEnumerator load;
	void StartLoad(string name){
		load = LoadTime(name);
		StartCoroutine(load);
	}

	private IEnumerator LoadTime(string name){
		yield return new WaitForSeconds(0.008f);
		project = SaveLoad.Load(name);
		LoadPage(1);
	}

	public void setProject(Project proj){
		project = proj;
	}

	void Update(){
		spf = 1 / project.getFps();
	}

	public void NewPage(){
		DisableAll();
		currentFrame++;
		Frame frame = new Frame(currentFrame);
		project.addFrame(frame);
		EnableActive(currentFrame);
	}

	public void LoadPage(int frame){
		DisableAll();
		currentFrame = frame;
		EnableActive(currentFrame);
	}

	void DisableAll(){
		for(int i = 0; i < project.getAllFrames().Count; i++){
			project.getFrame(i).setStatus("Off");
		}
	}

	void EnableActive(int currentFrame){
		for(int i = 0; i < project.getAllFrames().Count; i++){
			if(project.getFrame(i).getName() == "Frame " + currentFrame){
				project.getFrame(i).setStatus("On");
			}
		}
	}

	public Frame getCurrentFrame(){
		for(int i = 0; i <= project.getAllFrames().Count; i++){
			if(project.getFrame(i).getName() == (string)("Frame " + currentFrame.ToString())){
				return project.getFrame(i);
			}
		}
		return null;
	}

	private IEnumerator coroutine;
	public void StartPlay(){
		if(project.getAllFrames().Count > 1){
			LoadPage(1);
			coroutine = Play();
			StartCoroutine(coroutine);
		} else {
			return;
		}
	}

	private IEnumerator Play(){

		if(project.getAllAudio() != null){
			if(project.getAllAudio().Count >= 1 && project.checkAudioCompletion()){
				for(int a = 0; a < MicrophoneManager.getAllSources().Count; a++){
					MicrophoneManager.getAllSources()[a].Stop();
					MicrophoneManager.getAllSources()[a].Play();
				}
			}
		}

		for (int i = 1; i <= project.getAllFrames().Count; i++){
			DisableAll();
			EnableActive(i);
			currentFrame = i;

			if(isPlaying){
				yield return new WaitForSeconds(spf);
			} else {
				break;
			}
		}
		if(isPlaying){
			if(MicrophoneManager.currentlyRecording){
				isPlaying = false;
				LoadPage(1);
				yield break;
			} else {
				StartPlay();
			}
		} else {
			if(project.getAllAudio() != null){
				for(int a = 0; a < MicrophoneManager.getAllSources().Count; a++){
					MicrophoneManager.getAllSources()[a].Stop();
				}
			}
			LoadPage(1);
			yield break;
		}
	}

	private IEnumerator rec;
	public void StartRecPlay(){
		if(project.getAllFrames().Count > 1){
			LoadPage(1);
			rec = RecPlay();
			StartCoroutine(rec);
		} else {
			return;
		}
	}

	private IEnumerator RecPlay(){

		if(project.getAllAudio() != null){
			if(project.getAllAudio().Count >= 1 && project.checkAudioCompletion()){
				for(int a = 0; a < MicrophoneManager.getAllSources().Count; a++){
					MicrophoneManager.getAllSources()[a].Stop();
					MicrophoneManager.getAllSources()[a].Play();
				}
			}
		}

		for (int i = 1; i <= project.getAllFrames().Count; i++){
			DisableAll();
			EnableActive(i);
			currentFrame = i;

			yield return new WaitForSeconds(spf);
		}
		

		ShareManager.StopRecording();
		LoadPage(1);
		yield break;
	}
}