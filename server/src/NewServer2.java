import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class NewServer2 {
    public static HashMap<String, user> 유저해시맵;


    //유저목록으로 만들어 진 어레이리스트를 아이템으로 갖는 어레이리스트를 만들어서 방 개념을 만들어 줌.
    //a,b,c,d 유저가 있을 때  a,b가 있는 방은 1개 뿐 4명일때 만들어 질 수 있는 방의갯수는 6개


    public static void main(String[] args) {//메인 스레드
        try {
            ServerSocket server = new ServerSocket(10005 );//서버 소켓 생성-10000포트 바인딩 계속 돌면서 접속을 받음
            유저해시맵 = new HashMap<>();

            while (true) {
                System.out.println("[서버 메인 스레드]접속을 기다립니다.");
                Socket sock = server.accept();//클라이언트에서 보낸 연결 요청을 수락하면서 통신을 위한 소켓을 만들어 줌
                InputStream 인풋스트림 = sock.getInputStream();
                System.out.println("[서버 메인 스레드]인풋스트림 선언");
                OutputStream 아웃풋스트림 = sock.getOutputStream();
                System.out.println("[서버 메인 스레드]아웃풋스트림 선언");
                InputStreamReader 인풋스트림리더 = new InputStreamReader(인풋스트림);
                System.out.println("[서버 메인 스레드]인풋스트림리더 선언");
                OutputStreamWriter 아웃풋스트림라이터 = new OutputStreamWriter(아웃풋스트림);
                System.out.println("[서버 메인 스레드]아웃풋스트림 라이터 선언");
                BufferedReader 버퍼리더 = new BufferedReader(인풋스트림리더);

                System.out.println("[서버 메인 스레드]버퍼리더 선언");
                PrintWriter 프린트라이터 = new PrintWriter(아웃풋스트림라이터);
                System.out.println("[서버 메인 스레드]프린트라이터 선언");

                ChatThread2 chatThread = new ChatThread2(버퍼리더,프린트라이터);//채팅을 위한 스레드를 만들고 통신을 위해 만들어진 소켓과 유저정보를 인자로 넣어줌
                chatThread.start();//만든 스레드를 생성

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


