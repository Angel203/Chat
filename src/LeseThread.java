import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LeseThread extends Thread {
    private Socket client;
    private Scanner scanner;
    private EchoServer server;
    private String name;

    public LeseThread(Socket client, EchoServer server) throws IOException {
        this.client = client;
        this.server = server;
        scanner = new Scanner(client.getInputStream());
    }


    public Socket getClient() {
        return client;
    }

    private void login(String line0){
        Message m = new Message(line0);

        if (m.getTyp()!=Message.TYP_LOGIN)
            throw new MessageException("Blödarsch!");


        if (server.addUser(m.getFrom()))
        {
            name = m.getFrom();
            EchoServer.sem.release();
        }
        else
        {
            EchoServer.sem.release();
            throw new MessageException("Name schon da!");
        }


    }




    public String get_Name() {
        return name;
    }

    public void set_Name(String name)
    {
        this.name= name;
    }

    @Override
    public void run() {
        try{
            login(scanner.nextLine());


            while (true){
                Message m = new Message(scanner.nextLine());

                switch (m.getTyp()){
                    case Message.TYP_TO_ALL:
                         server.anAlle("2::" + m.getFrom() + ":" + m.getContent() + "\n");
                        server.liste_senden();
                         break;
                    case Message.TYP_TO_ONE:
                         if (!server.sendAn(
                                 m.getTo(),
                                 "3:"+m.getTo()+":"+m.getFrom()+
                                         ":"+m.getContent()))
                             // todo fehlermeldung an sender;
                             ;
                         break;

                    default: throw new MessageException("alle blöd");
                }

            }


        }
        catch(NoSuchElementException e)
        {

           server.anAlle("2::SERVER:" + name + " has disconnected.\n");
           Window.listenModel.removeElement(name);
            System.out.println(name+ " entfernt");
          server.liste_senden();

            try {
                client.close();

            } catch (IOException e1) {
                e1.printStackTrace();
            }


        }
        catch (Exception e){
            name = null;
            e.printStackTrace();
            try {
                client.close();
            } catch (IOException e1) {
            }
        }
    }
}
