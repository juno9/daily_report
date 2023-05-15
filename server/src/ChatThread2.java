import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

class ChatThread2 extends Thread {

    Socket 소켓 = null;
    BufferedReader 버퍼리더 = null;

    PrintWriter 프린트라이터 = null;
    String 이메일;
    user 유저;
    String 상대방이메일;

    public ChatThread2(BufferedReader 버퍼리더, PrintWriter 프린트라이터) {//생성자
        this.버퍼리더 = 버퍼리더;
        this.프린트라이터 = 프린트라이터;
//이미 서버에 존재하는 유저인지 확인하는게 먼저이다.


        try {


            이메일 = 버퍼리더.readLine();
            System.out.println("[" + 이메일 + "의 스레드]" + "이메일 받아옴: " +"---"+ 이메일+"---");

if(이메일.equals("")){
    System.out.println("[" + 이메일 + "의 스레드]" + " 이메일 안받아옴");
}
            if (!(이메일.equals(""))) {//받아온 이메일 값이 null이 아니라면-작동하지 않음
                if (NewServer2.유저해시맵.get(이메일) != null) {//이메일을 사용하는 유저가 이미 있다면
                    System.out.println("[" + 이메일 + "의 스레드]" + 이메일 + "유저는 이미 서버의 유저목록에 존재함, 기존의 유저를 제거함");
                    this.유저 = NewServer2.유저해시맵.remove(이메일);
                    synchronized (NewServer2.유저해시맵) {
                        System.out.println("[" + 이메일 + "의 스레드]" + 이메일 + "유저를 새로 생성");
                        유저 = new user(이메일, 버퍼리더, 프린트라이터);
                        System.out.println("[" + 이메일 + "의 스레드]" + 이메일 + "유저를 생성");
                        NewServer2.유저해시맵.put(이메일, 유저);
                        System.out.println("[" + 이메일 + "의 스레드]" + 이메일 + "유저를 유저목록에 추가함");
                        System.out.println("[" + 이메일 + "의 스레드]" + 이메일 + "유저의 프린트라이터: " + NewServer2.유저해시맵.get(이메일).get프린트라이터());

                    }


                } else {
                    synchronized (NewServer2.유저해시맵) {
                        System.out.println("[" + 이메일 + "의 스레드]" + 이메일 + "유저는 존재하지 않음");
                        유저 = new user(이메일, 버퍼리더, 프린트라이터);
                        System.out.println("[" + 이메일 + "의 스레드]" + 이메일 + "유저를 생성");
                        NewServer2.유저해시맵.put(이메일, 유저);
                        System.out.println("[" + 이메일 + "의 스레드]" + 이메일 + "유저를 유저목록에 추가함");
                        System.out.println("[" + 이메일 + "의 스레드]" + 이메일 + "유저의 프린트라이터: " + NewServer2.유저해시맵.get(이메일).get프린트라이터());

                    }
                }
            }

//클라이언트 입장에서 로그인 한 이후에는 이 코드가 실행되지 않는다.
        } catch (Exception e) {
            System.out.println(e);

        }


    }//생성자

    public void run() {
        super.run();
        try {


            String 상대방;
            while (true) {
                System.out.println("[" + 이메일 + "의 스레드]-------------------------------------클라이언트의 메시지 받아오는 반복문 시작-------------------------------------");
                상대방 = 버퍼리더.readLine();//a유저가 다른 누구와 메시지를 주고받을 때도 일단 상대유저의 메일을 보내기 때문에 상대유저의 메일을 일단 받아줘야 한다.
                //제일 처음 받는 메시지 내용이다.

                System.out.println("[" + 이메일 + "의 스레드] 받은 텍스트" + 상대방);
                if (상대방 != null) {
//                    if (상대방.equals("/quit")) {
//                        System.out.println("[" + 이메일 + "의 스레드] 유저 메일이 아니라 방을 나간다는 /quit 메시지를 클라이언트에게 받음");
//                        synchronized (NewServer2.유저해시맵) {
//                            NewServer2.유저해시맵.get(이메일).set위치("");
//                            System.out.println("[" + 이메일 + "의 스레드]" + 이메일 + "유저의 위치: " + NewServer2.유저해시맵.get(이메일).get위치());
//                        }
//                        continue;//while문 내의 이 줄 아래에 있는 코드들을 생략하고 다시 클라이언트 메시지받아오는 반복문 시작점으로 돌아간다.
//                    }
                    if (상대방.equals("/exit")) {
                        System.out.println("[" + 이메일 + "의 스레드] 유저 메일이 아니라 로그아웃한다는 /exit 메시지를 클라이언트에게 받음");
                        NewServer2.유저해시맵.remove(이메일);
                        System.out.println("[" + 이메일 + "의 스레드] 유저 삭제함");
                        System.out.println("[" + 이메일 + "의 스레드]" + "유저목록의 유저 수: " + NewServer2.유저해시맵.size());
                        버퍼리더.close();
                        프린트라이터.close();
                        System.out.println("[" + 이메일 + "의 스레드]" + "유저목록의 유저 수: " + NewServer2.유저해시맵.size());
                        break;

                    }
                    if (상대방.equals(" ")) {
                        System.out.println("[" + 이메일 + "의 스레드] 유저 메일이 아니라 빈칸을 받은 경우");
                        synchronized (NewServer2.유저해시맵) {
                            NewServer2.유저해시맵.get(이메일).set위치("");
                            System.out.println("[" + 이메일 + "의 스레드]" + 이메일 + "유저의 위치: " + NewServer2.유저해시맵.get(이메일).get위치());
                        }
                    }
//                    if (상대방.equals("/destroy")) {
//                        System.out.println("[" + 이메일 + "의 스레드] 클라이언트가 기존 스레드를 멈추기 위해 destroy값을 보냄");
//                        NewServer2.유저해시맵.get(이메일).get프린트라이터().println("/destroy");
//                        NewServer2.유저해시맵.get(이메일).get프린트라이터().flush();
//                        synchronized (NewServer2.유저해시맵) {
//                            NewServer2.유저해시맵.get(이메일).set위치("");
//                            System.out.println("[" + 이메일 + "의 스레드]" + 이메일 + "유저의 위치: " + NewServer2.유저해시맵.get(이메일).get위치());
//                        }
//                        interrupt();
//                    }
                }//클라이언트가 보내 온 메시지가 null이 아니라면
                else {
                    //클라이언트가 앱을 종료했다면 null값을 계속 보내온다
                    System.out.println("[" + 이메일 + "의 스레드] 유저가 앱을 종료함");
                    NewServer2.유저해시맵.get(이메일).get프린트라이터().println("/destroy");
                    NewServer2.유저해시맵.get(이메일).get프린트라이터().flush();
                    System.out.println("[" + 이메일 + "의 스레드]" + 이메일 + "에게 /destroy라고 보냄");
                    NewServer2.유저해시맵.remove(이메일);
                    System.out.println("[" + 이메일 + "의 스레드] 유저 삭제함");
                    버퍼리더.close();
                    프린트라이터.close();
                    System.out.println("[" + 이메일 + "의 스레드]" + "유저목록의 유저 수: " + NewServer2.유저해시맵.size());
                    break;

                }//클라이언트가 보내 온 메시지가 null이라면


                System.out.println("[" + 이메일 + "의 스레드]" + "상대 유저의 이메일을 받아옴: " + 상대방);
                유저.set위치(상대방);
                System.out.println("[" + 이메일 + "의 스레드]" + "유저의 위치를 설정함: " + 유저.get위치());
                while (true) {//클라이언트가 자신이 대화할 상대의 이메일 값을 먼저 보냄. 서버에서 이 값을 받았다면 이 반복문이 작동한다.

                    System.out.println("[" + 이메일 + "의 스레드]------------------------------------상대방 닉네임 받아온 후에 돌아가는 반복문 시작-------------------------------------");
                    String text = 버퍼리더.readLine();//클라이언트가 보내오는 메시지를 받음
                    System.out.println("[" + 이메일 + "의 스레드]" + "클라이언트가 보내온 메시지: " + text);
                    if (text != null) {//받은 텍스트가 null이 아니라면
                        if (text.equals("/quit")) {//받은 text가 null이 아니고 /quit이라면
                            synchronized (NewServer2.유저해시맵) {
                                NewServer2.유저해시맵.get(이메일).set위치("");
                                System.out.println("[" + 이메일 + "의 스레드]" + 이메일 + "유저의 위치: " + NewServer2.유저해시맵.get(이메일).get위치());
                            }
                            break;

//                            continue;
                        } else if(text.equals(상대방))
                        {
                            synchronized (NewServer2.유저해시맵) {
                                NewServer2.유저해시맵.get(이메일).set위치(상대방);
                                System.out.println("[" + 이메일 + "의 스레드]" + 이메일 + "유저의 위치: " + NewServer2.유저해시맵.get(이메일).get위치());
                            }

                        }else {//받은 텍스트가 null이 아니고 /quit도 아니라면=내용이 있다면 상대방이 있는지 먼저 확인


                            if (NewServer2.유저해시맵.get(상대방) != null) {//상대 유저가 유저 목록에 있다면
                                System.out.println("[" + 이메일 + "의 스레드]" + "상대 유저가 유저 목록에 있습니다");
                                System.out.println("[" + 이메일 + "의 스레드]" + "유저목록의 유저 수: " + NewServer2.유저해시맵.size());
                                if (NewServer2.유저해시맵.get(상대방).get위치().equals(유저.get이메일())) {//상대방의 위치가 유저의 이메일과 같다면
                                    System.out.println("[" + 이메일 + "의 스레드]" + "상대 유저의 위치가 유저의 이메일과 일치합니다");
                                    dbinsert(상대방, text);

                                    System.out.println("[" + 이메일 + "의 스레드]" + "유저목록의 유저 수: " + NewServer2.유저해시맵.size());
                                    user 상대유저객체 = NewServer2.유저해시맵.get(상대방);
                                    PrintWriter 상대방프린트라이터 = 상대유저객체.get프린트라이터();
                                    System.out.println("[" + 이메일 + "의 스레드]" + 상대방 + "유저의 프린트라이터: " + 상대방프린트라이터);
                                    상대방프린트라이터.println(이메일);//보낸 사람이 누구인지 메시지마다 알려주는거임
                                    상대방프린트라이터.flush();
                                    상대방프린트라이터.println(text);
//                                    상대방프린트라이터.println(text);
                                    상대방프린트라이터.flush();

                                } else {
                                    System.out.println("[" + 이메일 + "의 스레드]" + "유저목록의 유저 수: " + NewServer2.유저해시맵.size());
                                    System.out.println("[" + 이메일 + "의 스레드]" + "상대 유저의 위치가 유저의 이메일과 일치하지 않습니다 ");
                                    dbinsert(상대방, text);
                                    user 상대유저객체 = NewServer2.유저해시맵.get(상대방);
                                    PrintWriter 상대방프린트라이터 = 상대유저객체.get프린트라이터();
                                    상대방프린트라이터.println(이메일);//보낸 사람이 누구인지 메시지마다 알려주는거임
                                    상대방프린트라이터.flush();
                                    상대방프린트라이터.println(text);
                                    상대방프린트라이터.flush();
                                }

                            } else {//상대 유저가 유저 목록에 없다면
                                System.out.println("[" + 이메일 + "의 스레드]" + "유저목록의 유저 수: " + NewServer2.유저해시맵.size());
                                System.out.println("[" + 이메일 + "의 스레드]" + "상대 유저가 유저 목록에 존재하지 않습니다");
                                dbinsert(상대방, text);
//                                user 상대유저객체 = NewServer2.유저해시맵.get(상대방);
//                                PrintWriter 상대방프린트라이터 = 상대유저객체.get프린트라이터();
//                                상대방프린트라이터.println(이메일);//보낸 사람이 누구인지 메시지마다 알려주는거임
//                                상대방프린트라이터.flush();
//                                상대방프린트라이터.println(text);
//                                상대방프린트라이터.flush();
                            }


                        }
                    } else {
                        System.out.println("[" + 이메일 + "의 스레드] 멈춤");
                        break;
                    }
                    System.out.println("[" + 이메일 + "의 스레드]------------------------------------상대방 닉네임 받아온 후에 돌아가는 반복문 종료-------------------------------------");

                }

//
//
                System.out.println("[" + 이메일 + "의 스레드]-----------------------------------클라이언트의 메시지 받아오는 반복문 종료-------------------------------------");

            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void dbinsert(String 받는유저이메일, String 보내는메시지) {
        Connection 연결 = null;
        String 서버 = "localhost";
        String 데이터베이스 = "dailyreport";
        String 유저이름 = "root";
        String 비밀번호 = "Skdye1dye!@#";


        Date 오늘날짜 = new Date();
        // date 객체는 형변환 없이도 바로 출력이 가능하다.
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
        String 날짜변환 = df.format(오늘날짜);
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            System.out.println(" !! <JDBC 오류> Driver load 오류: " + e.getMessage());
//            e.printStackTrace();
//        }

        // 2.연결
        try {
            연결 = DriverManager.getConnection("jdbc:mysql://" + 서버 + "/" + 데이터베이스 + "?useSSL=false", 유저이름, 비밀번호);
            System.out.println(이메일 + " [dbinsert] 정상적으로 연결되었습니다.");
            Statement statement = 연결.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from user ");


            Statement statement2 = 연결.createStatement();
            boolean resultSet2 = statement2.execute("insert into dailyreport.message (sender_email, receiver_email, time,contents) values ('" + 이메일 + "','" + 받는유저이메일 + "','" + 날짜변환 + "','" + 보내는메시지 + "')");
            if (!resultSet2) {
                System.out.println(이메일 + " [dbinsert] 참임");
                System.out.println(이메일 + " [dbinsert] 인서트 됨!");
            } else {
                System.out.println(이메일 + " [dbinsert ]거짓임");
            }


        } catch (SQLException e) {
            System.out.println(이메일 + " [dbinsert] 연결오류:" + e.getMessage());
            e.printStackTrace();
        }

        // 3.해제
        try {
            if (연결 != null)
                연결.close();
        } catch (SQLException e) {
        }

    }


}