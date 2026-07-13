import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MinecraftCMDLauncherGUI extends JFrame {

    // ================= DEFAULT JAVA PATH =================
    // Percorso di fallback su Windows quando non viene trovato un JDK
    // portatile dentro la cartella ".java" accanto al launcher.
    private static final String DEFAULT_WINDOWS_JAVA_PATH =
            "C:\\Program Files (x86)\\Java\\jdk1.8.0_202\\bin\\java.exe";

    private JComboBox<String> versionBox;
    private JComboBox<String> languageBox;
    private JTextField javaPathField;
    private JTextField widthField;
    private JTextField heightField;
    private JCheckBox useJavawBox;
    private JCheckBox resizableBox;
    private JLabel widthLabel;
    private JLabel heightLabel;
    private JButton browseJavaButton;
    private JButton playButton;
    private JTextArea logArea;

    private final String gameDir;
    private String lang = "it"; // lingua corrente: "it" oppure "en"

    // ================= DIZIONARIO LINGUE =================
    private final Map<String, String> it = new HashMap<>();
    private final Map<String, String> en = new HashMap<>();

    public MinecraftCMDLauncherGUI() {

        buildDictionaries();

        gameDir = resolveGameDir();

        setTitle("Minecraft CMD Launcher");
        setSize(520, 440);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new GridLayout(7, 1));

        languageBox = new JComboBox<>(new String[]{"Italiano", "English"});
        languageBox.addActionListener(e -> {
            lang = languageBox.getSelectedIndex() == 0 ? "it" : "en";
            applyLanguage();
        });

        versionBox = new JComboBox<>(loadVersions());

        javaPathField = new JTextField(guessJavaPath());

        browseJavaButton = new JButton();
        browseJavaButton.addActionListener(e -> chooseJava());

        useJavawBox = new JCheckBox();
        if (!isWindows()) {
            useJavawBox.setToolTipText(t("javawTooltipNotAvailable"));
        }

        resizableBox = new JCheckBox();

        widthField = new JTextField("854");
        heightField = new JTextField("480");
        widthLabel = new JLabel();
        heightLabel = new JLabel();

        JPanel sizePanel = new JPanel(new GridLayout(1, 4));
        sizePanel.add(widthLabel);
        sizePanel.add(widthField);
        sizePanel.add(heightLabel);
        sizePanel.add(heightField);

        playButton = new JButton();
        playButton.addActionListener(e -> launch());

        JPanel javaPanel = new JPanel(new BorderLayout());
        javaPanel.add(javaPathField, BorderLayout.CENTER);
        javaPanel.add(browseJavaButton, BorderLayout.EAST);

        top.add(languageBox);
        top.add(versionBox);
        top.add(javaPanel);
        top.add(sizePanel);
        top.add(useJavawBox);
        top.add(resizableBox);
        top.add(playButton);

        add(top, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        applyLanguage();

        log(t("logGameDirDetected") + gameDir);
        log(t("logOsDetected") + System.getProperty("os.name"));

        setVisible(true);
    }

    // ================= DIZIONARIO: COSTRUZIONE =================
    private void buildDictionaries() {

        it.put("browseJava", "Scegli Java");
        it.put("useJavaw", "Usa javaw (senza terminale)");
        it.put("javawTooltipNotAvailable", "Non disponibile su questo sistema operativo: verra' usato \"java\" normale.");
        it.put("resizable", "Finestra ridimensionabile");
        it.put("widthLabel", "Larghezza:");
        it.put("heightLabel", "Altezza:");
        it.put("play", "Gioca");
        it.put("chooseJavaDialogTitle", "Seleziona eseguibile Java");
        it.put("logGameDirDetected", "Cartella di gioco rilevata: ");
        it.put("logOsDetected", "Sistema operativo rilevato: ");
        it.put("logLaunchHeader", "=== AVVIO ===");
        it.put("logJar", "Jar: ");
        it.put("logEntry", "Entry: ");
        it.put("logDimensions", "Dimensioni: ");
        it.put("logResizableSuffix", " (ridimensionabile: ");
        it.put("logInvalidDimension", "Valore dimensione non valido (\"%s\"), uso default %d");
        it.put("logJavawNote", "Nota: \"javaw\" non esiste su questo sistema operativo, uso \"java\" normale.");
        it.put("logRubyDungDetected", "Rilevata classe RubyDung nel jar (%s): avvio diretto senza applet.");
        it.put("logJarReadError", "Impossibile leggere il jar \"%s\": %s");
        it.put("logError", "ERRORE: ");

        en.put("browseJava", "Choose Java");
        en.put("useJavaw", "Use javaw (no console)");
        en.put("javawTooltipNotAvailable", "Not available on this operating system: regular \"java\" will be used instead.");
        en.put("resizable", "Resizable window");
        en.put("widthLabel", "Width:");
        en.put("heightLabel", "Height:");
        en.put("play", "Play");
        en.put("chooseJavaDialogTitle", "Select Java executable");
        en.put("logGameDirDetected", "Game folder detected: ");
        en.put("logOsDetected", "Operating system detected: ");
        en.put("logLaunchHeader", "=== LAUNCH ===");
        en.put("logJar", "Jar: ");
        en.put("logEntry", "Entry: ");
        en.put("logDimensions", "Dimensions: ");
        en.put("logResizableSuffix", " (resizable: ");
        en.put("logInvalidDimension", "Invalid dimension value (\"%s\"), using default %d");
        en.put("logJavawNote", "Note: \"javaw\" does not exist on this operating system, using regular \"java\" instead.");
        en.put("logRubyDungDetected", "RubyDung class detected in jar (%s): launching directly without applet.");
        en.put("logJarReadError", "Could not read jar \"%s\": %s");
        en.put("logError", "ERROR: ");
    }

    private String t(String key) {
        Map<String, String> dict = "en".equals(lang) ? en : it;
        return dict.getOrDefault(key, key);
    }

    // Applica la lingua corrente a tutti i componenti visibili.
    // I messaggi gia' scritti nel log restano nella lingua in cui sono
    // stati scritti; solo i nuovi log e le etichette si aggiornano.
    private void applyLanguage() {
        browseJavaButton.setText(t("browseJava"));
        useJavawBox.setText(t("useJavaw"));
        resizableBox.setText(t("resizable"));
        widthLabel.setText(t("widthLabel"));
        heightLabel.setText(t("heightLabel"));
        playButton.setText(t("play"));

        if (!isWindows()) {
            useJavawBox.setToolTipText(t("javawTooltipNotAvailable"));
        }
    }

    // ================= CROSS-PLATFORM: RILEVAMENTO OS =================
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    // ================= PORTABILITY: RILEVAMENTO CARTELLA =================
    private String resolveGameDir() {
        try {
            File source = new File(
                    MinecraftCMDLauncherGUI.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI()
            );

            if (source.isFile()) {
                source = source.getParentFile();
            }

            return source.getAbsolutePath();

        } catch (URISyntaxException | NullPointerException e) {
            return System.getProperty("user.dir");
        }
    }

    // Ordine di ricerca del Java da usare:
    // 1) JDK portatile dentro la cartella ".java" accanto al launcher
    //    (mantiene il launcher completamente autosufficiente/portabile)
    // 2) Il JDK 8 di default indicato in DEFAULT_WINDOWS_JAVA_PATH
    //    (solo su Windows, solo se effettivamente presente sul disco)
    // 3) Il comando generico "java" preso dal PATH di sistema
    private String guessJavaPath() {

        String execName = isWindows() ? "java.exe" : "java";

        File bundled = new File(gameDir + File.separator + ".java"
                + File.separator + "bin" + File.separator + execName);

        if (bundled.isFile()) {
            return bundled.getAbsolutePath();
        }

        if (isWindows()) {
            File defaultJdk = new File(DEFAULT_WINDOWS_JAVA_PATH);
            if (defaultJdk.isFile()) {
                return defaultJdk.getAbsolutePath();
            }
        }

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
        chooser.setDialogTitle(t("chooseJavaDialogTitle"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            javaPathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
    }

    private int parseDimension(JTextField field, int fallback) {
        try {
            int value = Integer.parseInt(field.getText().trim());
            return value > 0 ? value : fallback;
        } catch (NumberFormatException e) {
            log(String.format(t("logInvalidDimension"), field.getText(), fallback));
            return fallback;
        }
    }

    // ================= LAUNCH =================
    private void launch() {

        String javaPath = javaPathField.getText();
        String jar = (String) versionBox.getSelectedItem();

        boolean useJavaw = useJavawBox.isSelected();
        boolean resizable = resizableBox.isSelected();

        int width = parseDimension(widthField, 854);
        int height = parseDimension(heightField, 480);

        List<String> cmd = new ArrayList<>();

        String exec = (useJavaw && isWindows())
                ? javaPath.replace("java.exe", "javaw.exe")
                : javaPath;

        if (useJavaw && !isWindows()) {
            log(t("logJavawNote"));
        }

        cmd.add(exec);

        cmd.add("-Djava.library.path=" + gameDir + File.separator + "bin");
        cmd.add("-Dorg.lwjgl.librarypath=" + gameDir + File.separator + "bin");
        cmd.add("-Dsun.arch.data.model=32");
        cmd.add("-Djava.awt.headless=false");

        cmd.add("-Dmc.width=" + width);
        cmd.add("-Dmc.height=" + height);
        cmd.add("-Dmc.resizable=" + resizable);

        if ("Indev.jar".equals(jar)) {
            cmd.add("-Dorg.lwjgl.opengl.Display.allowSoftwareOpenGL=true");
            cmd.add("-Dorg.lwjgl.opengl.Display.useLegacyContext=true");
            cmd.add("-Dsun.java2d.opengl=false");
        }

        cmd.add("-cp");

        String classpath =
                gameDir + File.separator + "versions" + File.separator + jar + File.pathSeparator +
                gameDir + File.separator + "libs" + File.separator + "*" + File.pathSeparator +
                gameDir + File.separator + "retrowrapper.jar" + File.pathSeparator +
                gameDir;

        cmd.add(classpath);

        String entry = getEntry(jar);
        cmd.add(entry);

        log(t("logLaunchHeader"));
        log(t("logJar") + jar);
        log(t("logEntry") + entry);
        log(t("logDimensions") + width + "x" + height + t("logResizableSuffix") + resizable + ")");
        log(cmd.toString());

        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(new File(gameDir));
            pb.inheritIO();
            pb.start();
        } catch (Exception e) {
            log(t("logError") + e.getMessage());
        }
    }

    // ================= ENTRY SELECT =================
    private String getEntry(String jar) {

        String rubyDungClass = findRubyDungClass(
                new File(gameDir + File.separator + "versions" + File.separator + jar)
        );

        if (rubyDungClass != null) {
            log(String.format(t("logRubyDungDetected"), rubyDungClass));
            return rubyDungClass;
        }

        return "MinecraftAppletWrapper";
    }

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
            log(String.format(t("logJarReadError"), jarFile.getName(), e.getMessage()));
        }

        return null;
    }

    public static void main(String[] args) {
        new MinecraftCMDLauncherGUI();
    }
}
