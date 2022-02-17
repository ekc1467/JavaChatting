package Network__;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;

import java.net.ConnectException;
import java.net.InetSocketAddress;

import java.net.Socket;



import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Client extends Application {
   Socket cs;
   TextArea textArea, textArea2;
   Scene scene1, scene2;
   Label loginInfo;
   Stage stage;

   @Override
   public void start(Stage stage) throws Exception {
      this.stage = stage;
      // 
      VBox root = new VBox();
      HBox root2 = new HBox();
      HBox root3 = new HBox();
      HBox root4 = new HBox();
      VBox win = new VBox();

      Button startButton = new Button("로그인");
      Button backButton = new Button("뒤로가기");
      Button stopButton = new Button("접속종료");
      Button sendButton = new Button("전송");
      Button errorButton = new Button("에러");
      
      loginInfo = new Label("");
      loginInfo.setTextFill(Color.RED);
      
      Alert a = new Alert(AlertType.ERROR);
      a.setContentText("이미 존재하는 아이디입니다.");

      win.setPrefSize(400, 300);
      root.setPrefSize(400, 300);
      root.setSpacing(5);

      textArea = new TextArea();
      textArea.setEditable(false);
      textArea.setPrefSize(300, 200);


      textArea2 = new TextArea();
      textArea2.setPrefSize(100, 200);

      TextField textInput = new TextField();
      TextField userName = new TextField();

      TextField socketIp = new TextField();
      socketIp.setPromptText("IP를 입력해주세요.");
      TextField socketPort = new TextField();
      socketPort.setPromptText("Port를 입력해주세요.");

      userName.setPrefWidth(100);
      userName.setPromptText("닉네임을 입력하세요");

      // 서버 접속
      startButton.setOnAction(new EventHandler<ActionEvent>() {
         Boolean loginCheck = false;
         @Override
         public void handle(ActionEvent arg0) {
            try {
               System.out.println("화면 띄움");
               // 시작하기 버튼을 누르면 채팅창으로 전환 됨.
//                  stage.setScene(scene2);
               //userName.setDisable(true);
               new Thread() {
                  public void run() {
                     try {
                        String ipNum = socketIp.getText();
                        String portNum = socketPort.getText();
                        System.out.println("연결 전");
                        cs = new Socket();
//                              cs.connect(new InetSocketAddress("192.168.55.248", 5002));
//                              System.out.println("연결 후");
                        cs.connect(new InetSocketAddress(ipNum, Integer.parseInt(portNum)));
                        DataOutputStream os  = new DataOutputStream( cs.getOutputStream());
                        String s = userName.getText();

                        if (s.startsWith("!#")) {
                           System.out.println("입력할수 없습니다.");
                        }
                        os.writeUTF(s);
                        receive();
                     } catch (ConnectException e) {
                        System.out.println("123");
                        Platform.runLater(() -> {
                           loginInfo.setText("존재하지 않는 소켓입니다.");
                        });
                        e.printStackTrace();
                     } catch (Exception e) {
                        e.printStackTrace();
                     }
                  };
               }.start();
               if (loginCheck) {
                  stage.setScene(scene2);
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
            backButton.setDisable(true);
            stopButton.setDisable(false);

         }
      });

      // 입력 텍스트 전송 기능
      textInput.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent arg0) {
            try {
               DataOutputStream os  = new DataOutputStream( cs.getOutputStream());
               String s = textInput.getText();

               // 서버에 텍스트 전송 
               os.writeUTF(s);
               // 전송 후 텍스트필드 비워 줌.
               textInput.setText("");
            } catch (Exception e) {e.printStackTrace();}
         }
      });


      // 접속 종료 기능
      stopButton.setOnAction(new EventHandler<ActionEvent>() {

         @Override
         public void handle(ActionEvent arg0) {
            try {
               // 소켓을 닫음.
               cs.close();
            } catch (Exception e) {}
            // 버튼들 활성화 변경
            backButton.setDisable(false);
            stopButton.setDisable(true);
         }
      });

      // 보내기 버튼
      // 엔터 뿐만아니라 마우스 클릭으로도 데이터를 보내기 위해서 추가 함.
      sendButton.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent arg0) {
            try {
               DataOutputStream os  = new DataOutputStream( cs.getOutputStream());
               String s = textInput.getText();

               os.writeUTF(s);
               textInput.setText("");
            } catch (Exception e) {   e.printStackTrace(); }
         }
      });

      // 뒤로가기 버튼
      // 다시 socket 접속하는 창으로 넘어 감. => win창으로 넘어감. 
      backButton.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent arg0) {
            // scene1은 socket 접속(로그인) 창 임.
            stage.setScene(scene1);
            userName.setText("");
            stopButton.setDisable(true);
         }
      });
      
      errorButton.setOnAction(new EventHandler<ActionEvent>() {
         
         @Override
         public void handle(ActionEvent arg0) {
            // TODO Auto-generated method stub
            a.show();
            
         }
      });

      backButton.setDisable(true);

      win.getChildren().addAll(socketIp, socketPort, userName, startButton, loginInfo);
      // 로그인 창에서 객체를 가운데로 정렬 시켜 줌.
      win.setPadding(new Insets(100, 100, 100, 100));


      root2.getChildren().addAll(stopButton, backButton, errorButton);
      root3.getChildren().addAll(textInput,sendButton);
      root4.getChildren().addAll(textArea, textArea2);
      root.getChildren().addAll(root2, root4, root3);

      // 로그인 창
      scene1 = new Scene(win);
      // 메인 창
      scene2 = new Scene(root);

      //Scene scene = new Scene(root);
      stage.setScene(scene1);
      stage.setTitle("");
      stage.show();

   }

   // 클라이언트 프로그램 종료
   public void stopClient() {
      try {
         if(cs != null && !cs.isClosed()) {
            cs.close();
         }
      }catch (Exception e) {
         e.printStackTrace();
      }

   }
   // 서버로부터 메시지 받음
   public void receive() {
      System.out.println("실행 됨?");
      
      Platform.runLater(()->{
         stage.setScene(scene2);
      });
      
//      Boolean nameCheck = true;
      while(true) {
         try {
            DataInputStream in=new DataInputStream(cs.getInputStream());
            String message = in.readUTF();

            Platform.runLater(()->{
               // 데이터 입력에서 !#이붙으면
               // textArea2(참가자 리스트)로 보냄.
               if ( message.startsWith("!#") == false) {   
                  textArea.appendText(message);
                  textArea.appendText("\n");
               }
               else {   
                  // 김민철
                  // ===
                 // 김민철
                  // 김철수
                  // 값이 업데이트 될때마다 리셋
                  textArea2.setText("");
                  // !#을 때고 나머지를 출력
                  textArea2.appendText(message.substring(2));
               }
            });
         } catch (EOFException e) {
            Platform.runLater(() -> {
               stage.setScene(scene1);
               Platform.runLater(() -> {
                  loginInfo.setText("이미 존재하는 아이디입니다.");
               });
//               stopButton.setDisable(true);
            });
            stopClient();
         } catch (Exception e) {
            e.printStackTrace();
            stopClient();
            break;
         }
      }
   }
   public static void main(String[] args) {
      launch();
   }

}