//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class RemoteControlClient extends JFrame {
    private int state = 1;
    private InetAddress HostIP = null;
    private int port = 0;
    private Socket socket = null;
    private PrintWriter wrt = null;
    private JButton btnNext;
    private JButton btnNext2;
    private JButton btnNext3;
    private JLabel lblIP;
    private JLabel lblPort;
    private JPanel pnlMain;
    private JScrollPane scrText;
    private JTextField txtHostIP;
    private JTextField txtHostPort;
    private JTextArea txtText;

    public RemoteControlClient() {
        this.initComponents();
    }

    private void initComponents() {
        this.pnlMain = new JPanel();
        this.txtHostIP = new JTextField();
        this.txtHostPort = new JTextField();
        this.lblIP = new JLabel();
        this.lblPort = new JLabel();
        this.btnNext = new JButton();
        this.btnNext2 = new JButton();
        this.btnNext3 = new JButton();
        this.scrText = new JScrollPane();
        this.txtText = new JTextArea();
        this.setDefaultCloseOperation(3);
        this.setTitle("RemoteControlTCP Client");
        this.pnlMain.setPreferredSize(new Dimension(400, 300));
        this.pnlMain.setLayout((LayoutManager)null);
        this.pnlMain.add(this.txtHostIP);
        this.txtHostIP.setBounds(90, 20, 150, 28);
        this.txtHostPort.setText("2033");
        this.txtHostPort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                RemoteControlClient.this.txtHostPortActionPerformed(evt);
            }
        });
        this.pnlMain.add(this.txtHostPort);
        this.txtHostPort.setBounds(330, 20, 60, 28);
        this.lblIP.setText("Host IP");
        this.pnlMain.add(this.lblIP);
        this.lblIP.setBounds(20, 30, 60, 16);
        this.lblPort.setText("Host Port");
        this.pnlMain.add(this.lblPort);
        this.lblPort.setBounds(250, 30, 70, 16);
        this.btnNext.setText("Click to connect to Host");
        this.btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                RemoteControlClient.this.btnNextActionPerformed(evt);
            }
        });
        this.pnlMain.add(this.btnNext);
        this.btnNext.setBounds(10, 60, 380, 29);
        this.btnNext2.setText("Shutdown");
        this.btnNext2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                RemoteControlClient.this.btnNext2ActionPerformed(evt);
            }
        });
        this.pnlMain.add(this.btnNext2);
        this.btnNext2.setBounds(10, 95, 380, 29);
        this.btnNext3.setText("Lock");
        this.btnNext3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                RemoteControlClient.this.btnNext3ActionPerformed(evt);
            }
        });
        this.pnlMain.add(this.btnNext3);
        this.btnNext3.setBounds(10, 125, 380, 29);
        this.btnNext2.setEnabled(false);
        this.btnNext3.setEnabled(false);
        this.txtText.setColumns(20);
        this.txtText.setEditable(false);
        this.txtText.setRows(5);
        this.scrText.setViewportView(this.txtText);
        this.pnlMain.add(this.scrText);
        this.scrText.setBounds(20, 180, 370, 60);
        this.getContentPane().add(this.pnlMain, "Center");
        this.txtHostIP.setText("");
        this.pack();
    }

    private void txtHostPortActionPerformed(ActionEvent evt) {
    }

    private void btnNextActionPerformed(ActionEvent evt) {
        if(this.txtHostIP.getText().equals("") || this.txtHostPort.getText().equals("")){
            return;
        }
        try {
            this.HostIP = InetAddress.getByName(this.txtHostIP.getText());
            this.port = Integer.parseInt(this.txtHostPort.getText());
            try {
                this.socket = new Socket(this.HostIP, this.port);
                try {
                    this.wrt = new PrintWriter(this.socket.getOutputStream());
                } catch (Exception var5) {
                    JOptionPane.showMessageDialog((Component)null, var5.getMessage(), "Unexpected Exception", 0);
                    return;
                }
            } catch (Exception var4) {
                JOptionPane.showMessageDialog((Component)null, var4.getMessage(), "Unexpected Exception", 64);
                return;
            }
        } catch (Exception var5) {
            JOptionPane.showMessageDialog((Component)null, var5.getMessage(), "Error in IP address or Port", 64);
            return;
        }
        this.txtText.append("Connected to host!\n");
        this.btnNext.setEnabled(false);
        this.btnNext2.setEnabled(true);
        this.btnNext3.setEnabled(true);
        return;
    
}

    private void btnNext2ActionPerformed(ActionEvent evt) {
        this.wrt.println("Shutdown");
        this.wrt.flush();
    }

    private void btnNext3ActionPerformed(ActionEvent evt) {
        this.wrt.println("Lock");
        this.wrt.flush();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                (new RemoteControlClient()).setVisible(true);
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

                    RemoteControlClient.this.txtText.append(line + "\n");
                }

                RemoteControlClient.this.txtText.append("DONE READING!\n");
            } catch (SocketException var2) {
                if (!var2.getMessage().equalsIgnoreCase("Socket Closed")) {
                    JOptionPane.showMessageDialog((Component)null, var2.getMessage(), "Unexpected Exception", 0);
                    RemoteControlClient.this.txtText.append("DONE READING!\n");
                }
            } catch (Exception var3) {
                JOptionPane.showMessageDialog((Component)null, var3.getMessage(), "Unexpected Exception", 0);
                RemoteControlClient.this.txtText.append("DONE READING!\n");
            }
        }
    }
}
