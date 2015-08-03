import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.*;

/**
 * Created by Stefan_ on 26.07.2015.
 */
public class Window extends JFrame implements ActionListener, KeyListener {
    private String txt_ende = "\n";
    private String name = "";
    private Socket s;


    private JOptionPane jOptionPane;

    private String msg;

    public static JTextArea chat;
    public  JPanel rechts = new JPanel();
    public static DefaultListModel listenModel = new DefaultListModel();
    public static JList<String> jList_namen = new JList<String>(listenModel);

    private JScrollPane jschat;
    private JTextField eingabe;
    private JButton senden;

    private FlowLayout fl = new FlowLayout(2);
    private Container c = getContentPane();
    private JPanel panel_unten = new JPanel(fl);



    public Window() throws IOException {
        super("Chat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocation(400, 200);
        setSize(400, 400);
        setLayout(new BorderLayout(2, 4));

        Frame_content();                    // alle add's

        setMinimumSize(getSize());
        setVisible(true);

        ipDialog();

        // JDialog mit anschließender Anmeldung
        nameDialog();



        LeseThread_Client lt = new LeseThread_Client(s);
        lt.run();


    }




    private void Frame_content() {
        chat = new JTextArea();
        eingabe = new JTextField();
        jschat  = new JScrollPane(chat);
        JScrollPane scrollPane = new JScrollPane();
        senden = new JButton("senden");

        // Listener hinzufügen
        eingabe.addKeyListener(this);
        senden.addActionListener(this);

        // Anzeige_Teilnehmer
        jList_namen.setCellRenderer(new Chat_Members());
        scrollPane.getViewport().setView(jList_namen);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Größen Anpassung
        scrollPane.setPreferredSize(new Dimension(80, 400));
        jList_namen.setPreferredSize(new Dimension(80, 400));
        jschat.setPreferredSize(new Dimension(200,400));
        eingabe.setPreferredSize(new Dimension(240, 24));

        // Attribute setzen
        chat.setEditable(false);
        senden.setEnabled(true);
        DefaultCaret caret = (DefaultCaret)chat.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // Das ganze hinzufügen
        panel_unten.add(eingabe);
        panel_unten.add(senden);
        c.add(jschat, BorderLayout.CENTER);
        c.add(panel_unten, BorderLayout.SOUTH);
        c.add(scrollPane, BorderLayout.EAST);
    }

    private void nameDialog() throws IOException {

        name = JOptionPane.showInputDialog( "Name: ");

            if(name == null) nameDialog();
            else
            if (name.isEmpty()) {
                nameDialog();
            } else
            if (name.contains(".")) {
                nameDialog();
            } else {
                msg = "0:SERVER:" + name + ": Hallo" + txt_ende;
                s.getOutputStream().write(msg.getBytes());
            }
            setTitle("Chat [" + name + "]");

    }

    private void ipDialog() {
        String ip = JOptionPane.showInputDialog("IP des Hosts eingeben: ", get_ip());
        if(ip == null) System.exit(0);

        try {
            s = new Socket(ip, 44444);
        } catch (IOException e) {
            jOptionPane.showMessageDialog(this, "Server antwortet nicht.", "Verbindungsproblem", JOptionPane.WARNING_MESSAGE);
            ipDialog();
        }
    }

    private String get_ip()
    {
        String tmp = "";

        try {
            tmp = "" +InetAddress.getLocalHost();
          String ip[] = tmp.split("/");
            return ip[1];
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        return tmp;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {

        if(e.getSource() == senden)
        {
            sende_text();
        }

    }

    private void sende_text() {
        if(!eingabe.getText().equals(""))
        {
            msg = "2::" + name + ":" + eingabe.getText() + "" + txt_ende;
            System.out.print(msg);
            try {
                s.getOutputStream().write(msg.getBytes());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            eingabe.setText("");
        }
    }



    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            sende_text();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
