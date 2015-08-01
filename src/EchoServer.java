import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;


public class EchoServer extends Component {
    private ServerSocket server;
    private ArrayList<LeseThread> alleLeseThreads = new ArrayList<LeseThread>();

    public static final Semaphore sem = new Semaphore(0, true);         // (Freigaben = 0, FIFO Strategie)


    public EchoServer(int port) throws IOException {
        server = new ServerSocket(port);
    }

    public void run() throws InterruptedException {
        LeseThread lt = null;
        while(true){
            try {

                Socket s = server.accept();
                 lt = new LeseThread(s, this);
                alleLeseThreads.add(lt);
                lt.start();

                sem.acquire(); // wird in LeseThread freigegeben, sobald der Name gesetzt wurde (für get_Name() wichtig)
                s = lt.getClient();
                String st = "3:" + lt.get_Name() + ":SERVER:" + lt.get_Name() + " connected\n";
                s.getOutputStream().write(st.getBytes());

                liste_senden();
            }

            catch (IOException e) {
                e.printStackTrace();
                try{
                    lt.getClient().close();
                    Window.listenModel.removeElement(lt.get_Name());
                    System.out.println(lt.get_Name()+ " entfernt");
                    liste_senden();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public synchronized void liste_senden() {
        String t = "";
        for (LeseThread  l: alleLeseThreads)
        {
            if(t=="")   t = l.get_Name();
            else        t = t + "." +l.get_Name();

        }
        anAlle("1::SERVER:"+ t +"\n");
    }

    // Elle sind alle
    public synchronized void anAlle(String line){
        for (int i= alleLeseThreads.size()-1; i>=0; i--){
            Socket s = alleLeseThreads.get(i).getClient();
            if (s==null || s.isClosed()) alleLeseThreads.remove(i);
            else
            try {
                s.getOutputStream().write(line.getBytes());


            } catch (IOException e) {
            }
        }

    }




    public static void main(String[] args) throws IOException, InterruptedException {
        EchoServer es = new EchoServer(44444);
        es.run();

    }




    public synchronized boolean addUser(String user) {
        for (int i=0; i< alleLeseThreads.size(); i++)
            if (user.equalsIgnoreCase(alleLeseThreads.get(i).get_Name()))
                return false;

        return true;
    }

    public boolean sendAn(String to, String msg) {
        for (int i=0; i< alleLeseThreads.size(); i++)
            if (to.equalsIgnoreCase(alleLeseThreads.get(i).getName())){
                try {
                    Socket s = alleLeseThreads.get(i).getClient();
                    s.getOutputStream().write(msg.getBytes());
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        return false;
    }
}
