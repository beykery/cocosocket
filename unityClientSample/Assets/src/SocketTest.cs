using UnityEngine;
using System.Collections;
using Game;
using UnityEngine.UI;
using cocosocket4unity;
using protocol;
using System.IO;
using System.Collections.Generic;

public class SocketTest : MonoBehaviour 
{

	private Button	open;
	private Button send;
	private Button close;
    private static USocket socket;
    private static string ip = "localhost";
    private static int port = 4887;
    List<Animator> birds;
    void Awake() 
    {
        DontDestroyOnLoad(gameObject);
    }
	void Start () 
	{
        open = GameObject.Find("open").GetComponent<Button>();
        send = GameObject.Find("send").GetComponent<Button>();
        close = GameObject.Find("close").GetComponent<Button>();
		EventTriggerListener.Get(open.gameObject).onClick =OnButtonClick;
		EventTriggerListener.Get(send.gameObject).onClick =OnButtonClick;
		EventTriggerListener.Get(close.gameObject).onClick =OnButtonClick;
        Messenger.AddListener<AuthResponse>(typeof(AuthResponse).FullName, ar);
        birds = new List<Animator>();
        Animator bird;
        GameObject go = Resources.Load<GameObject>("birds/bird_fly_0");
        for (int i = 0; i < 2; i++) {
            bird = Instantiate<GameObject>(go).GetComponent<Animator>();
            bird.name = "bird" + i;
            bird.transform.position = new Vector3(i*10, 0, 0);
            bird.GetComponent<SpriteRenderer>().sortingOrder = i;
            birds.Add(bird);
        }

        Invoke("delay", 3);
	}

    void delay() {
        foreach (Animator bird in birds) {
            bird.Play("fly");
        }
    }

    /**
     * 处理登陆请求
     */ 
    public void ar(AuthResponse response)
    {
        Debug.LogWarning("response:"+response.success);
    }

	private void OnButtonClick(GameObject go)
    {
		//在这里监听按钮的点击事件
		Debug.LogWarning(go.name);
		switch(go.name)
		{
		case "open":
                if (socket != null)
                {
                    if (socket.getStatus() != USocket.STATUS_CLOSED)
                    {
                        socket.Close();
                        socket = null;
                    }
                }
                SocketListner listner = new MyListner();
                Protocal p = new Varint32HeaderProtocol();
                socket = new USocket(listner,p);
                socket.Connect(ip, port);
			break;
		case "send":
            if (socket != null && socket.getStatus() == USocket.STATUS_CONNECTED)
            {
                AuthRequest auth = new AuthRequest();
                auth.loginid = "vH0cVEb2R2nRZOa4nxQz0ZsnTGC5pvf4Fn_sOxhLCQYsjGFRIEA5Pe1eTOwtPjrS";
                auth.serverid = 1;
                this.Send(auth);
            }
			break;
		case "close":
            if (socket != null)
            {
                if (socket.getStatus() != USocket.STATUS_CLOSED)
                {
                    socket.Close();
                    socket = null;
                }
            }
			break;
		}
	}

    private void Send(object param)
    {
        MemoryStream stream = new MemoryStream();
        ProtoBuf.Serializer.NonGeneric.Serialize(stream, param);
        byte[] bs = stream.ToArray();
        Frame f = null;
        if (socket.getProtocal().GetType() == typeof(Varint32HeaderProtocol))
        {
            f = new Varint32Frame(512);
        }
        else
        {
            f = new Frame(512);
        }
        f.PutShort(MessageQueueHandler.GetProtocolCMD(param.GetType()));
        Debug.LogWarning("上行 cmd=" + MessageQueueHandler.GetProtocolCMD(param.GetType()) + ", type=" + param.GetType().ToString() + ", " + Time.fixedTime);
        Statics.SetXor(bs);
        f.PutBytes(bs);
        f.End();
        socket.Send(f);
    }
}
