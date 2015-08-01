/**
 * Created by Stefan_ on 27.07.2015.
 */
import java.awt.Component;
import java.awt.GridLayout;
import java.net.Socket;

import javax.swing.*;


public class Chat_Mates extends JPanel implements ListCellRenderer<String> {


    private JLabel name;


    public Chat_Mates() {
        name = new JLabel();
        add(name);



    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends String> list, String s, int index,
            boolean isSelected, boolean cellHasFocus) {

        name.setText(s);


        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }



}


