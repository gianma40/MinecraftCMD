import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MinecraftCMDLauncherGUI extends JFrame {

    private JComboBox<String> versionBox;
    private JTextField javaPathField;
    private JCheckBox useJavawBox;
    private JTextArea logArea;

    private final String gameDir;

    public MinecraftCMDLauncherGUI() {

        gameDir = resolveGameDir();

        setTitle("Minecraft CMD Launcher");
        setSize(520, 360);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new GridLayout(4, 1));

        versionBox = new JComboBox<>(loadVersions());

        javaPathField = new JTextField(guessJavaPath());

        JButton browseJava = new JButton("Scegli Java");
        browseJava.addActionListener(e -> chooseJava());

        useJavawBox = new JCheckBox("Usa javaw (senza terminale)", false);

        JButton playButton = new JButton("Gioca");
        playButton.addActionListener(e -> launch());

        JPanel javaPanel = new JPanel(new BorderLayout());
        javaPanel.add(javaPathField, BorderLayout.CENTER);
        javaPanel.add(browseJava, BorderLayout.EAST);

        top.add(versionBox);
        top.add(javaPanel);
        top.add(useJavawBox);
        top.add(playButton);

        add(top, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        log("Cartella di gioco rilevata: " + gameDir);

        setVisible(true);
    }

    // ================= PORTABILITY: RILEVAMENTO CARTELLA =================
    // Invece di un percorso fisso ("C:\minecraftcmd2"), la cartella del
    // launcher viene calcolata dalla posizione reale del file/classe in
    // esecuzione. Cosi' il launcher funziona da qualunque cartella/drive
    // sia stato copiato o estratto, senza bisogno di modifiche manuali.
    private String resolveGameDir() {
        try {
            File source = new File(
                    MinecraftCMDLauncherGUI.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI()
            );

            // Se la classe viene eseguita da un .jar, la cartella di gioco
            // e' quella che contiene il jar. Se viene eseguita da un file
            // .class "sciolto" (come fa bat/Minecraft-Launcher.bat con
            // "cd .. && java MinecraftCMDLauncherGUI"), source e' gia' la
            // cartella radice del launcher.
            if (source.isFile()) {
                source = source.getParentFile();
            }

            return source.getAbsolutePath();

        } catch (URISyntaxException | NullPointerException e) {
            // fallback: cartella corrente da cui e' stato avviato java
            return System.getProperty("user.dir");
        }
    }

    // Prova a indovinare un java.exe utilizzabile senza dipendere da un
    // percorso fisso: prima cerca un runtime portabile dentro la cartella
    // ".java" accanto al launcher, poi si affida al "java" del PATH.
    private String guessJavaPath() {
        File bundled = new File(gameDir + File.separator + ".java"
                + File.separator + "bin" + File.separator + "java.exe");

        if (bundled.isFile()) {
            return bundled.getAbsolutePath();
        }

        // nessun runtime portabile trovato: usa il comando "java" del PATH
        // di sistema (l'utente puo' comunque cambiarlo col pulsante "Scegli Java")
        return "java";
    }

    // ================= LOAD VERSIONS =================
    private String[] loadVersions() {

        File dir = new File(gameDir + File.separator + "versions");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".jar"));

        if (files == null) {
            return new String[]{"NO_VERSIONS"};
        }

        List<String> list = new ArrayList<>();

        for (File f : files) {
            list.add(f.getName());
        }

        list.sort(String::compareToIgnoreCase);

        return list.toArray(new String[0]);
    }

    // ================= JAVA SELECT =================
    private void chooseJava() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleziona java.exe");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            javaPathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
    }

    // ================= LAUNCH =================
    private void launch() {

        String javaPath = javaPathField.getText();
        String jar = (String) versionBox.getSelectedItem();

        boolean useJavaw = useJavawBox.isSelected();

        List<String> cmd = new ArrayList<>();

        String exec = useJavaw
                ? javaPath.replace("java.exe", "javaw.exe")
                : javaPath;

        cmd.add(exec);

        // ================= JVM FLAGS =================
        cmd.add("-Djava.library.path=" + gameDir + File.separator + "bin");
        cmd.add("-Dorg.lwjgl.librarypath=" + gameDir + File.separator + "bin");
        cmd.add("-Dsun.arch.data.model=32");
        cmd.add("-Djava.awt.headless=false");

        // ================= INDEV FIX =================
        if ("Indev.jar".equals(jar)) {
            cmd.add("-Dorg.lwjgl.opengl.Display.allowSoftwareOpenGL=true");
            cmd.add("-Dorg.lwjgl.opengl.Display.useLegacyContext=true");
            cmd.add("-Dsun.java2d.opengl=false");
        }

        // ================= CLASSPATH =================
        cmd.add("-cp");

        String classpath =
                gameDir + File.separator + "versions" + File.separator + jar + File.pathSeparator +
                gameDir + File.separator + "libs" + File.separator + "*" + File.pathSeparator +
                gameDir + File.separator + "retrowrapper.jar" + File.pathSeparator +
                gameDir;

        cmd.add(classpath);

        // ================= ENTRY =================
        String entry = getEntry(jar);
        cmd.add(entry);

        log("=== LAUNCH ===");
        log("Jar: " + jar);
        log("Entry: " + entry);
        log(cmd.toString());

        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(new File(gameDir));
            pb.inheritIO();
            pb.start();
        } catch (Exception e) {
            log("ERROR: " + e.getMessage());
        }
    }

    // ================= ENTRY SELECT =================
    // Prima si guarda DENTRO al jar selezionato: se contiene una classe che
    // si chiama esattamente "RubyDung" (in qualsiasi package), il gioco
    // viene avviato direttamente come applicazione standalone, indipendente
    // dal nome del file .jar. Se il jar viene rinominato (es. non piu'
    // "rd-132211.jar") ma contiene ancora RubyDung, viene comunque
    // riconosciuto correttamente. Solo se la classe RubyDung non e' presente
    // si ricade sul wrapper per le versioni applet-based (Infdev, Indev, ecc).
    private String getEntry(String jar) {

        String rubyDungClass = findRubyDungClass(
                new File(gameDir + File.separator + "versions" + File.separator + jar)
        );

        if (rubyDungClass != null) {
            log("Rilevata classe RubyDung nel jar (" + rubyDungClass + "): avvio diretto senza applet.");
            return rubyDungClass;
        }

        // tutte le applet-based (Infdev / Indev / ecc)
        return "MinecraftAppletWrapper";
    }

    // Scansiona le entry dello zip/jar cercando un file il cui nome semplice
    // sia esattamente "RubyDung.class" (evita falsi positivi come
    // "RubyDung$1.class" o classi con nome simile ma diverso).
    private String findRubyDungClass(File jarFile) {

        if (!jarFile.isFile()) {
            return null;
        }

        try (ZipFile zip = new ZipFile(jarFile)) {

            Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (entry.isDirectory()) {
                    continue;
                }

                String name = entry.getName();
                String simpleName = name.substring(name.lastIndexOf('/') + 1);

                if (simpleName.equals("RubyDung.class")) {
                    return name.substring(0, name.length() - ".class".length())
                            .replace('/', '.');
                }
            }

        } catch (Exception e) {
            log("Impossibile leggere il jar \"" + jarFile.getName() + "\": " + e.getMessage());
        }

        return null;
    }

    public static void main(String[] args) {
        new MinecraftCMDLauncherGUI();
    }
}
