package bsu.rfact.java.laba7;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;





@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private static final String FRAME_TITLE = "Клиент мгновенных сообщений";

    private static final int FRAME_MINIMUM_WIDTH = 500;
    private static final int FRAME_MINIMUM_HEIGHT = 500;

    private static final int FROM_FIELD_DEFAULT_COLUMNS = 10;
    private static final int TO_FIELD_DEFAULT_COLUMNS = 20;
    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;

    private static final int SMALL_GAP = 5;
    private static final int MEDIUM_GAP = 10;
    private static final int LARGE_GAP = 15;
    private static final int SERVER_PORT = 4567;

    private final JTextField textFieldFrom;
    private final JTextField textFieldTo;
    private final JTextField login;

    private final JTextArea textAreaIncoming;
    private final JTextArea textAreaOutgoing;
    private String date;

    private boolean flagPrivate;

    private DialogFrame dialogFrame;

    private ChatDataBase listoOfUsers;


    public MainFrame() {

        super(FRAME_TITLE);

        setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));


        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2,
                (kit.getScreenSize().height - getHeight()) / 2);


        textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0);
        textAreaIncoming.setEditable(false);


        final JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);


        final JLabel labelFrom = new JLabel("Отправитель");
        final JLabel labelTo = new JLabel("Получатель");


        textFieldFrom = new JTextField(FROM_FIELD_DEFAULT_COLUMNS);
        textFieldTo = new JTextField(TO_FIELD_DEFAULT_COLUMNS);


        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);


        final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);


        final JPanel messagePanel = new JPanel();
        messagePanel.setBorder(BorderFactory.createTitledBorder("Сообщение"));

        listoOfUsers = new ChatDataBase();
        flagPrivate = false;

        login = new JTextField(20);
        while(true) {

            JOptionPane.showMessageDialog(MainFrame.this,
                    login, "" +
                            "Введите ваш логин", JOptionPane.PLAIN_MESSAGE);
            if (login.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите логин!", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                login.grabFocus();
            }
            else {
                textFieldFrom.setText(login.getText());
                break;
            }
        }


        final JButton sendButton = new JButton("Отправить");

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                date=getDateTime();
                sendMessage();}
        });


        final JButton listButton = new JButton("Список пользователей");
        listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Box listBox = Box.createVerticalBox();
                listBox.add(Box.createVerticalGlue());
                for(User user: listoOfUsers.getUsers())
                {
                    JLabel name = new JLabel(user.getName());
                    JLabel IP = new JLabel(user.getAddres());
                    Box oneUser=Box.createHorizontalBox();
                    oneUser.add(Box.createHorizontalGlue());
                    oneUser.add(name);
                    oneUser.add(Box.createHorizontalStrut(40));
                    oneUser.add(IP);
                    oneUser.add(Box.createHorizontalGlue());
                    listBox.add(oneUser);
                    listBox.add(Box.createVerticalStrut(20));
                }
                listBox.add(Box.createVerticalGlue());
                JOptionPane.showMessageDialog(MainFrame.this,
                        listBox, "" +
                                "Список пользователей", JOptionPane.INFORMATION_MESSAGE);
            }
        });



        JTextField name = new JTextField(10);
        JTextField adress = new JTextField(10);
        final JButton plusUser = new JButton("+");
        plusUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JLabel inputName = new JLabel("Логин: ");
                JLabel inputAdress = new JLabel("IP: ");

                Box registr = Box.createVerticalBox();
                registr.add(Box.createVerticalGlue());
                Box first = Box.createHorizontalBox();
                first.add(Box.createHorizontalGlue());
                first.add(inputName);
                first.add(Box.createHorizontalStrut(10));
                first.add(name);
                first.add(Box.createHorizontalGlue());
                Box second = Box.createHorizontalBox();
                second.add(Box.createHorizontalGlue());
                second.add(inputAdress);
                second.add(Box.createHorizontalStrut(10));
                second.add(adress);
                second.add(Box.createHorizontalGlue());
                registr.add(first);
                registr.add(Box.createVerticalStrut(10));
                registr.add(second);
                registr.add(Box.createVerticalGlue());
                JOptionPane.showMessageDialog(bsu.rfact.java.laba7.MainFrame.this,
                        registr, "" +
                                "Регистрация пользователя", JOptionPane.INFORMATION_MESSAGE);

            }
        });
        if(!name.getText().isEmpty() && !adress.getText().isEmpty())
            listoOfUsers = listoOfUsers.plusUser(name.getText(),adress.getText());


        final JButton searchButton = new JButton("Поиск");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JLabel who = new JLabel("Кого ищем?");
                JTextField searchName = new JTextField(10);
                Box search = Box.createHorizontalBox();
                search.add(Box.createHorizontalGlue());
                search.add(who);
                search.add(Box.createHorizontalStrut(10));
                search.add(searchName);
                search.add(Box.createHorizontalGlue());
                JOptionPane.showMessageDialog(MainFrame.this,
                        search, "" +
                                "Поиск", JOptionPane.QUESTION_MESSAGE);
                String nameToSearch = searchName.getText();
                boolean flag = false;
                for (User user : listoOfUsers.getUsers())
                {
                    if(user.getName().equals(nameToSearch)) {
                        JFrame resultFrame = new JFrame("Пользователь " + user.getName() + " найден");
                        resultFrame.setSize(400,200);
                        resultFrame.setLocation((kit.getScreenSize().width - resultFrame.getWidth()) / 2,
                                (kit.getScreenSize().height - resultFrame.getHeight()) / 2);
                        flag = true;
                        JLabel quation = new JLabel("Желаете начать с ним диалог?");
                        JButton yesButton = new JButton("да");

                        yesButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                dialogFrame = new DialogFrame(user, MainFrame.this);
                                flagPrivate=true;
                                resultFrame.setVisible(false);
                            }
                        });

                        JButton noButton = new JButton("нет");
                        noButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                resultFrame.setVisible(false);
                            }
                        });
                        Box result = Box.createVerticalBox();
                        result.add(Box.createVerticalGlue());
                        result.add(quation);
                        result.add(Box.createVerticalStrut(20));
                        Box answer = Box.createHorizontalBox();
                        answer.add(Box.createHorizontalGlue());
                        answer.add(yesButton);
                        answer.add(Box.createHorizontalStrut(10));
                        answer.add(noButton);
                        answer.add(Box.createHorizontalGlue());
                        result.add(answer);
                        result.add(Box.createVerticalGlue());
                        resultFrame.add(result);
                        resultFrame.setVisible(true);
                    }
                }
                if(!flag){
                    JLabel notFound= new JLabel("Такой пользователь не найден");
                    JOptionPane.showMessageDialog(MainFrame.this,
                            notFound, "" +
                                    "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });




        final GroupLayout layout2 = new GroupLayout(messagePanel);
        messagePanel.setLayout(layout2);
        layout2.setHorizontalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(Alignment.TRAILING)
                        .addGroup(layout2
                                .createSequentialGroup()
                                .addComponent(labelFrom)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldFrom)
                                .addGap(LARGE_GAP)
                                .addComponent(labelTo)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldTo))
                        .addComponent(scrollPaneOutgoing)
                        .addGroup(layout2
                                .createSequentialGroup()
                                .addComponent(plusUser)
                                .addGap(LARGE_GAP)
                                .addComponent(listButton)
                                .addGap(LARGE_GAP)
                                .addComponent(searchButton)
                                .addGap(LARGE_GAP)
                                .addComponent(sendButton)))
                .addContainerGap());

        layout2.setVerticalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2
                        .createParallelGroup(Alignment.BASELINE)
                        .addComponent(labelFrom)
                        .addComponent(textFieldFrom).addComponent(labelTo).addComponent(textFieldTo))
                .addGap(MEDIUM_GAP)
                .addComponent(scrollPaneOutgoing)
                .addGap(MEDIUM_GAP)
                .addGroup(layout2
                        .createParallelGroup(Alignment.BASELINE)
                        .addComponent(plusUser)
                        .addComponent(listButton)
                        .addComponent(searchButton)
                        .addComponent(sendButton))
                .addContainerGap());


        final GroupLayout layout1 = new GroupLayout(getContentPane());
        setLayout(layout1);
        layout1.setHorizontalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout1.createParallelGroup()
                        .addComponent(scrollPaneIncoming)
                        .addComponent(messagePanel))
                .addContainerGap());

        layout1.setVerticalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneIncoming)
                .addGap(MEDIUM_GAP)
                .addComponent(messagePanel)
                .addContainerGap());



        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                    while (!Thread.interrupted()) {
                        final Socket socket = serverSocket.accept();
                        final DataInputStream in = new DataInputStream(socket.getInputStream());

                        final String senderName = in.readUTF();

                        final String message = in.readUTF();
                        final String Dat = in.readUTF();

                        socket.close();


                        final String address = ((InetSocketAddress) socket
                                .getRemoteSocketAddress()).getAddress().getHostAddress();

                        if(flagPrivate && !dialogFrame.isVisible())
                            flagPrivate = false;
                        boolean flag = false;
                        for (User user : listoOfUsers.getUsers()) {
                            if (user.getAddres().equals(textFieldTo.getText()) && !flagPrivate ) {

                                textAreaIncoming.append(Dat + "  " + senderName + " -> " + user.getName() + " : " + message + "\n");
                                flag = true;
                            }
                        }
                        if (!flag && !flagPrivate) {
                            textAreaIncoming.append(Dat + "  " + senderName + " -> Неизвестный (" + textFieldTo.getText() + ") : " + message + "\n");
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "Ошибка в работе сервера",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();

    }

    public JTextField getLogin() { return login; }
    public static int getServerPort() { return SERVER_PORT; }
    public String getDateTime() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        Date date = new Date();

        return dateFormat.format(date);

    }

    private void sendMessage() {
        try {

            final String senderName = textFieldFrom.getText();
            final String destinationAddress = textFieldTo.getText();
            final String message = textAreaOutgoing.getText();
            final String Datt = date;



            if (senderName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите имя отправителя", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                textFieldFrom.grabFocus();
                return;
            }

            if (destinationAddress.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите адрес узла-получателя", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                textFieldTo.grabFocus();
                return;
            }
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите текст сообщения", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                textAreaOutgoing.grabFocus();
                return;
            }


            final Socket socket = new Socket(destinationAddress, SERVER_PORT);


            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());


            out.writeUTF(senderName);

            out.writeUTF(message);
            out.writeUTF(Datt);

            socket.close();

            textAreaOutgoing.setText("");

        }

        catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this,
                    "Не удалось отправить сообщение: узел-адресат не найден",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

        catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.this, "Не удалось отправить сообщение",
                    "Ошибка",JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final MainFrame frame = new MainFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }

}

