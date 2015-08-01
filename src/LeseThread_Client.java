import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;

public class LeseThread_Client extends Thread {
    private Socket client;
    private Scanner scanner;


    public LeseThread_Client(Socket client) throws  IOException
    {
        this.client = client;
        scanner = new Scanner(client.getInputStream());
    }




    @Override
    public void run() {
        try{
            while (true){
                Message m;

                String tmp;

                try {
                    m = new Message(scanner.nextLine());
                    switch (m.getTyp()) {
                        case Message.TYP_TO_ALL:
                            tmp = Window.chat.getText();
                            Window.chat.setText(tmp + "\n" + m.getFrom() + ": " + m.getContent());
                            break;
                        case Message.TYP_TO_ONE:
                            tmp = Window.chat.getText();
                            Window.chat.setText(tmp + "\n" + m.getFrom() + ": " + m.getContent());
                            break;
                        case Message.TYP_LIST:
                            System.out.println(m.getFrom());
                            tmp = m.getContent();
                            Window.listenModel.clear();
                            String[] test = tmp.split(Pattern.quote("."));

                            for(int i=0; i < test.length; i++)
                            {
                                Window.listenModel.addElement(test[i]);
                            }
                            break;

                        default:
                            throw new MessageException("kein Bekannter Nachrichtentyp");
                    }
                } catch(Exception e)
                {

                }

            }


        }catch (Exception e){
            e.printStackTrace();
            try {
                client.close();
            } catch (IOException e1) {
            }
        }
    }
}
