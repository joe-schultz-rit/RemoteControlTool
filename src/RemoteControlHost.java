//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.*;

public class RemoteControlHost extends JFrame {
    private ServerSocket ssocket = null;
    private Socket socket = null;
    private RemoteControlHost.DataReader dataReader = null;
    private Thread readThread = null;
    private JButton btnOpen;
    private JButton btnAccept;
    private JButton btnClose;
    private JLabel lblPort;
    private JPanel pnlMain;
    private JScrollPane scrText;
    private JTextField txtPort;
    private JTextArea txtText;

    public RemoteControlHost() {
        this.initComponents();
    }

    private void initComponents() {
        this.pnlMain = new JPanel();
        this.txtPort = new JTextField();
        this.lblPort = new JLabel();
        this.btnOpen = new JButton();
        this.btnAccept = new JButton();
        this.btnClose = new JButton();
        this.scrText = new JScrollPane();
        this.txtText = new JTextArea();
        this.setDefaultCloseOperation(3);
        this.setTitle("RemoteControlHostTCP");
        this.pnlMain.setPreferredSize(new Dimension(400, 300));
        this.pnlMain.setLayout((LayoutManager)null);
        this.txtPort.setText("2033");
        this.txtPort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                RemoteControlHost.this.txtPortActionPerformed(evt);
            }
        });
        this.pnlMain.add(this.txtPort);
        this.txtPort.setBounds(95, 20, 60, 28);
        this.lblPort.setText("Port (0-65535)");
        this.pnlMain.add(this.lblPort);
        this.lblPort.setBounds(10, 30, 140, 10);
        this.btnOpen.setText("Click to open port");
        this.btnAccept.setText("Click to accept connections");
        this.btnClose.setText("Click to close port");
        this.btnOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                RemoteControlHost.this.btnOpenActionPerformed(evt);
            }
        });
        this.btnAccept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                RemoteControlHost.this.btnAccept2ActionPerformed(evt);
                RemoteControlHost.this.btnAcceptActionPerformed(evt);
            }
        });
        this.btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                RemoteControlHost.this.btnCloseActionPerformed(evt);
            }
        });
        this.pnlMain.add(this.btnOpen);
        this.btnOpen.setBounds(10, 60, 380, 29);
        this.pnlMain.add(this.btnAccept);
        this.btnAccept.setBounds(10, 91, 380, 29);
        this.pnlMain.add(this.btnClose);
        this.btnClose.setBounds(10, 123, 380, 29);
        this.txtText.setColumns(20);
        this.txtText.setEditable(false);
        this.txtText.setRows(5);
        this.scrText.setViewportView(this.txtText);
        this.pnlMain.add(this.scrText);
        this.scrText.setBounds(20, 180, 370, 60);
        this.btnClose.setEnabled(false);
        this.btnAccept.setEnabled(false);
        this.getContentPane().add(this.pnlMain, "Center");
        this.pack();
    }

    private void txtPortActionPerformed(ActionEvent evt) {
    }

    private void btnOpenActionPerformed(ActionEvent evt) {
        try {
            this.ssocket = new ServerSocket(Integer.parseInt(this.txtPort.getText()));
            this.txtText.append("Socket Created!\n");

        } catch (Exception var1) {
            JOptionPane.showMessageDialog((Component)null, var1.getMessage(), "Unexpected Exception", 0);
            try {
                this.txtText.append("Socket Closing...\n");
                this.ssocket.close();
                this.ssocket = null;
            } catch (Exception var6) {
                return;
            }
        }
        this.btnOpen.setEnabled(false);
        this.btnClose.setEnabled(true);
        this.btnAccept.setEnabled(true);
        this.txtText.append("Waiting for user to start accepting connections...\n");

    }

    private void btnAcceptActionPerformed(ActionEvent evt) {

        try {
            this.socket = this.ssocket.accept();
        } catch (Exception var5) {
            JOptionPane.showMessageDialog((Component)null, var5.getMessage(), "Unexpected Exception", 0);
            return;
        }
        this.txtText.append("Client Connected!\n");
        setState(Frame.ICONIFIED);
        this.dataReader = new RemoteControlHost.DataReader(this.socket);
        this.readThread = new Thread(this.dataReader);

        this.readThread.start();
        this.btnAccept.setEnabled(false);

    }

    private void btnCloseActionPerformed(ActionEvent evt) {
        try {
            this.txtText.append("Socket Closing...\n");
            this.ssocket.close();
            this.ssocket = null;
        } catch (Exception var6) {
            return;
        }
        this.btnOpen.setEnabled(true);
        this.btnClose.setEnabled(false);
    }

    private void btnAccept2ActionPerformed(ActionEvent evt) {
        this.txtText.append("Accepting Connections...\n");
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                (new RemoteControlHost()).setVisible(true);
            }
        });
    }

    private class DataReader implements Runnable {
        private Socket socket;
        private boolean running;
        private BufferedReader rdr = null;

        public DataReader(Socket _socket) {
            this.socket = _socket;
            this.running = true;
        }

        public void run() {
            try {
                String line = null;
                this.rdr = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

                while(this.running) {
                    line = this.rdr.readLine();
                    if (line == null) {
                        break;
                    }

                    switch (line){
                        case "Lock":
                            try {
                                final Process process = Runtime.getRuntime().exec("rundll32.exe user32.dll, LockWorkStation\n");
                                final InputStream in = process.getInputStream();
                                int ch;
                                while((ch = in.read()) != -1) {
                                    //  System.out.print((char)ch);
                                    RemoteControlHost.this.txtText.append(String.valueOf(ch));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;
                        case "Shutdown":
                            try {
                                final Process process = Runtime.getRuntime().exec("shutdown -s -t 3\n");
                                final InputStream in = process.getInputStream();
                                int ch;
                                while((ch = in.read()) != -1) {
                                    //  System.out.print((char)ch);
                                    RemoteControlHost.this.txtText.append(String.valueOf(ch));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;
                        case "Restart":
                            try {
                                final Process process = Runtime.getRuntime().exec("shutdown -r\n");
                                final InputStream in = process.getInputStream();
                                int ch;
                                while((ch = in.read()) != -1) {
                                    //  System.out.print((char)ch);
                                    RemoteControlHost.this.txtText.append(String.valueOf(ch));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;
                        case "Logoff":
                            try {
                                final Process process = Runtime.getRuntime().exec("shutdown /l\n");
                                final InputStream in = process.getInputStream();
                                int ch;
                                while((ch = in.read()) != -1) {
                                    //  System.out.print((char)ch);
                                    RemoteControlHost.this.txtText.append(String.valueOf(ch));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;
                        default:
                            try {
                                final Process process = Runtime.getRuntime().exec(line+"\n");
                                final InputStream in = process.getInputStream();
                                int ch;
                                while((ch = in.read()) != -1) {
                                  //  System.out.print((char)ch);
                                    RemoteControlHost.this.txtText.append(String.valueOf(ch));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;

                    }
                }

                RemoteControlHost.this.txtText.append("Client Disconnected!\n");
            } catch (SocketException var2) {
                if (!var2.getMessage().equalsIgnoreCase("Socket Closed")) {
                    JOptionPane.showMessageDialog((Component)null, var2.getMessage(), "Unexpected Exception", 0);
                    RemoteControlHost.this.txtText.append("DONE READING!\n");
                }
            } catch (Exception var3) {
                JOptionPane.showMessageDialog((Component)null, var3.getMessage(), "Unexpected Exception", 0);
                RemoteControlHost.this.txtText.append("DONE READING!\n");
            }
        }
    }
}


// setState(Frame.ICONIFIED);
