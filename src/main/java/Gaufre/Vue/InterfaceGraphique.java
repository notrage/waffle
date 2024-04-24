package Gaufre.Vue;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.DataInputStream;
import java.io.IOException;

import javax.swing.*;

import Gaufre.Controleur.EcouteurMenu;
import Gaufre.Configuration.ResourceLoader;
import Gaufre.Configuration.Config;

public class InterfaceGraphique implements Runnable {
    public final int MENU = 0;
    public final int JEU = 1;
    public final int QUIT = -1;
    private BufferedImage gaufreNE, gaufreSE, gaufreNO, gaufreSO, gaufreN, gaufreS, gaufreE, gaufreO,
            gaufreMilieu, poison, miettes1, miettes2, miettes3, miettes4, iconeGaufre;
    private ModeGraphique modele;
    private EcouteurMenu ecouteurMenu = new EcouteurMenu(this);
    private int etat;
    private JFrame fenetre;
    private GraphicsEnvironment ge;
    private Container plateau; // CONTIENT LE PLATEAU OU SE TROUVE LA GAUFRE

    InterfaceGraphique(ModeGraphique mg) {
        etat = MENU;
        modele = mg;
        try {
            ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Font titleFont = Font.createFont(Font.TRUETYPE_FONT,
                    ResourceLoader.getResourceAsStream("fonts/DEADLY_POISON_II.ttf"));
            ge.registerFont(titleFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        gaufreNO = ResourceLoader.lireImage("gaufreNO");
        gaufreN = ResourceLoader.lireImage("gaufreN");
        gaufreNE = ResourceLoader.lireImage("gaufreNE");
        gaufreO = ResourceLoader.lireImage("gaufreO");
        gaufreMilieu = ResourceLoader.lireImage("gaufreMilieu");
        gaufreE = ResourceLoader.lireImage("gaufreE");
        gaufreSO = ResourceLoader.lireImage("gaufreSO");
        gaufreS = ResourceLoader.lireImage("gaufreS");
        gaufreSE = ResourceLoader.lireImage("gaufreSE");
        poison = ResourceLoader.lireImage("poison");
        miettes1 = ResourceLoader.lireImage("miettes1");
        miettes2 = ResourceLoader.lireImage("miettes2");
        miettes3 = ResourceLoader.lireImage("miettes3");
        miettes4 = ResourceLoader.lireImage("miettes4");
        iconeGaufre = ResourceLoader.lireImage("iconeGaufre");
    }

    public static InterfaceGraphique demarrer(ModeGraphique m) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            Config.debug("Set Look and Feel to system.");
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
                | IllegalAccessException e) {
            System.err.println("Can't set look and feel : " + e);
        }
        System.setProperty("sun.java2d.noddraw", Boolean.TRUE.toString());
        InterfaceGraphique vue = new InterfaceGraphique(m);
        // Get graphical environment
        // JFrame.setDefaultLookAndFeelDecorated(true); // Permet de mieux resize mais
        // déplacer la fenêtre devient buggé
        SwingUtilities.invokeLater(vue);
        return vue;
    }

    public void run() {
        fenetre = new JFrame("Gauffre");
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fenetre.setLocationRelativeTo(null);
        fenetre.setSize(new Dimension(800, 600));
        metAJourFenetre();
        fenetre.setVisible(true);
    }

    private void metAJourFenetre() {
        Container panel;
        switch (etat) {
            case MENU:
                fenetre.getContentPane().removeAll();
                panel = creerMenu();
                break;

            case JEU:
                fenetre.getContentPane().removeAll();
                panel = creerJeu();
                break;

            case QUIT:
                fenetre.dispatchEvent(new WindowEvent(fenetre, WindowEvent.WINDOW_CLOSING));

            default:
                throw new UnsupportedOperationException("Etat de jeu " + etat + " non supporté");
        }
        fenetre.setContentPane(panel);
        fenetre.revalidate();
        fenetre.repaint();
    }

    private Container creerMenu() {
        int menuWidth = fenetre.getWidth();
        int menuHeight = fenetre.getHeight();
        Config.debug("Height :", menuHeight, "Width :", menuWidth);
        // Create the background panel
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(menuWidth, menuHeight));
        layeredPane.setOpaque(false);
        FondMenu fond = new FondMenu();
        fond.setBounds(0, 0, menuWidth, menuHeight);
        fond.setOpaque(true);
        layeredPane.add(fond, JLayeredPane.DEFAULT_LAYER);

        // Main container using BorderLayout
        JPanel pane = new JPanel(new BorderLayout());
        pane.setOpaque(false);
        pane.setBounds(0, 0, menuWidth, menuHeight);

        // Title section
        JLabel title = new JLabel("GAUFRE", SwingConstants.CENTER);
        title.setOpaque(false);
        title.setForeground(new Color(5, 199, 79));
        int titleFontSize = (int) (menuHeight / 4);
        title.setFont(new Font("DEADLY POISON II", Font.BOLD, titleFontSize));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add title to the top of the BorderLayout
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(Box.createVerticalGlue());
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalGlue());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add titlePanel to the top of the BorderLayout
        pane.add(titlePanel, BorderLayout.PAGE_START);

        // Middle section with vertically stacked buttons using GridBagLayout
        JPanel middlePanel = new JPanel(new GridBagLayout());
        middlePanel.setOpaque(false);
        int insetSize = menuWidth / 3;
        Insets insets = new Insets(20, insetSize, 20, insetSize);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = insets;

        JButton button1J = new JButton("1 joueur");
        button1J.setMinimumSize(new Dimension(40, 20));
        button1J.addActionListener(ecouteurMenu);
        button1J.setActionCommand("Jeu1J");
        button1J.setAlignmentX(Component.CENTER_ALIGNMENT);
        middlePanel.add(button1J, gbc);

        JButton button2J = new JButton("2 joueurs");
        button2J.setMinimumSize(new Dimension(40, 20));
        button2J.addActionListener(ecouteurMenu);
        button2J.setActionCommand("Jeu2J");
        button2J.setAlignmentX(Component.CENTER_ALIGNMENT);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        middlePanel.add(button2J, gbc);

        pane.add(middlePanel, BorderLayout.CENTER);

        // Bottom section with horizontally stacked buttons and text
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(menuWidth, (int) (menuHeight * 0.2)));
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));

        JPanel bottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomLeft.setOpaque(false);
        BufferedImage volImg;
        if (Config.estMuet()) {
            volImg = ResourceLoader.lireImage("muet");
        } else {
            volImg = ResourceLoader.lireImage("volume");
        }
        JButton volumeButton = new JButton();
        Image scaledImg = volImg.getScaledInstance(
                (int) (0.15 * menuHeight),
                (int) (0.15 * menuHeight),
                Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImg);
        volumeButton.setIcon(icon);
        volumeButton.setPreferredSize(new Dimension((int) (0.15 * menuHeight), (int) (0.15 * menuHeight)));
        volumeButton.addActionListener(ecouteurMenu);
        volumeButton.setActionCommand("volume");
        volumeButton.setBorderPainted(false);
        volumeButton.setFocusPainted(false);
        volumeButton.setContentAreaFilled(false);
        bottomLeft.add(volumeButton);
        bottomPanel.add(bottomLeft);

        JPanel bottomRight = new JPanel();
        bottomRight.setLayout(new BoxLayout(bottomRight, BoxLayout.Y_AXIS));
        bottomRight.setOpaque(false);
        JLabel versionLabel = new JLabel(getClass().getPackage().getImplementationVersion());
        versionLabel.setFont(new Font("Arial", Font.PLAIN, (int) (titleFontSize / 6)));
        versionLabel.setForeground(new Color(34, 84, 124));
        bottomRight.add(Box.createVerticalGlue());
        bottomRight.add(versionLabel);
        bottomPanel.add(bottomRight);

        pane.add(bottomPanel, BorderLayout.PAGE_END);

        layeredPane.add(pane, JLayeredPane.PALETTE_LAYER);

        fenetre.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Calculate font size based on window width
                int newTitleFontSize = Math.max(50, fenetre.getHeight() / 4);
                title.setFont(new Font("DEADLY POISON II", Font.BOLD, newTitleFontSize));
                versionLabel.setFont(new Font("Arial", Font.PLAIN, (int) (newTitleFontSize / 6)));

                // Calculate new background bounds
                layeredPane.setBounds(0, 0, fenetre.getWidth(), fenetre.getHeight());
                fond.setBounds(0, 0, fenetre.getWidth(), fenetre.getHeight());
                pane.setBounds(0, 0, fenetre.getWidth(), fenetre.getHeight());

                // Change buttons sizes and margins
                int insetSize = fenetre.getWidth() / 3;
                Insets insets = new Insets(20, insetSize, 20, insetSize);
                gbc.insets = insets;
                middlePanel.removeAll();
                middlePanel.add(button1J, gbc);
                middlePanel.add(button2J, gbc);
                volumeButton.setPreferredSize(
                        new Dimension((int) (0.15 * fenetre.getHeight()), (int) (0.15 * fenetre.getHeight())));
                Image scaledImg = volImg.getScaledInstance(
                        (int) (0.15 * fenetre.getHeight()),
                        (int) (0.15 * fenetre.getHeight()),
                        Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(scaledImg);
                volumeButton.setIcon(icon);
            }
        });
        return layeredPane;
    }

    public void toggleSon() {
        metAJourFenetre();
    }

    private Container creerJeu() {
        Container pane = new Container();
        JLabel texte;
        GridBagConstraints c = new GridBagConstraints();

        pane.setLayout(new GridBagLayout());

        // Titre
        texte = new JLabel("GAUFRE");
        c.ipady = 40;
        c.gridwidth = 4;
        c.weightx = 0.5;
        c.gridy = 0;
        c.gridx = 0;
        pane.add(texte, c);
        this.plateau = new Container();
        this.plateau.setLayout(new GridLayout(modele.getGaufre().getNbLignes(), modele.getGaufre().getNbColonnes()));
        pane.add(plateau);
        modele.reset();
        afficherGaufre();

        return pane;
    }

    public void afficherGaufre() {

        // TODO
    }

    public void setEtat(int newEtat) {
        etat = newEtat;
        metAJourFenetre();
    }

    public int getEtat() {
        return etat;
    }

    public ModeGraphique getMG() {
        return modele;
    }

    public int getTaillePlateauX() {
        return plateau.getWidth();
    }

    public int getTaillePlateauY() {
        return plateau.getHeight();
    }

    public int getTailleCelluleX() {
        return plateau.getWidth() / modele.getGaufre().getNbColonnes();
    }

    public int getTailleCelluleY() {
        return plateau.getHeight() / modele.getGaufre().getNbLignes();
    }

    public Graphics2D getGraphics() {
        return (Graphics2D) plateau.getGraphics();
    }
}

class FondMenu extends JPanel {
    private BufferedImage textureImage;
    private int BIHeight;
    private int BIWidth;
    private TexturePaint texturePaint;
    private int xOffset = 0;
    private int yOffset = 0;

    public FondMenu() {
        textureImage = ResourceLoader.lireImage("gaufreMilieu");
        BIHeight = textureImage.getHeight();
        BIWidth = textureImage.getWidth();
        texturePaint = new TexturePaint(textureImage,
                new Rectangle(0, 0, textureImage.getWidth(), textureImage.getHeight()));

        // Start the animation
        Timer timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update the offsets to create the panning effect
                xOffset = (xOffset + 1) % BIWidth;
                yOffset = (yOffset + 1) % BIHeight;

                // Trigger a repaint
                repaint();
                getToolkit().sync();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Adjust the texture paint's position based on the offsets
        g2d.translate(-xOffset, -yOffset);
        g2d.setPaint(texturePaint);
        g2d.fillRect(0, 0, getWidth() + BIWidth, getHeight() + BIHeight);
    }
}
