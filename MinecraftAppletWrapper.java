import java.applet.Applet;
import java.applet.AppletStub;
import java.applet.AppletContext;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import javax.swing.JFrame;

public class MinecraftAppletWrapper {

    public static void main(String[] args) {

        // PORTABILITY FIX: il vecchio percorso fisso "L:\minecraftcmd2\bin"
        // funzionava solo su un PC con quella lettera di unita' specifica.
        // Il launcher (MinecraftCMDLauncherGUI) avvia questo processo con
        // pb.directory(gameDir), quindi la working directory corrente
        // corrisponde SEMPRE alla cartella del launcher: la usiamo per
        // ricostruire il percorso di "bin" in modo dinamico.
        String gameDir = System.getProperty("user.dir");
        String binDir = gameDir + java.io.File.separator + "bin";

        System.setProperty("java.library.path", binDir);
        System.setProperty("org.lwjgl.librarypath", binDir);
        System.setProperty("sun.arch.data.model", "32");

        // ================= DIMENSIONI / RESIZABLE =================
        // Lette dalle system properties che il launcher passa con -D.
        // Se il wrapper viene avviato senza launcher (es. per test manuali),
        // si usano i default storici 854x480 non ridimensionabile.
        int width = parseIntSafe(System.getProperty("mc.width"), 854);
        int height = parseIntSafe(System.getProperty("mc.height"), 480);
        boolean resizable = Boolean.parseBoolean(System.getProperty("mc.resizable", "false"));

        String[] candidates = {
                "net.minecraft.client.MinecraftApplet",
		"MinecraftApplet",
                "net.minecraft.client.Minecraft",
		"net.minecraft.client.main.Main",
                "com.mojang.minecraft.MinecraftApplet"
        };

        Class<?> clazz = null;

        for (String c : candidates) {
            try {
                clazz = Class.forName(c);
                System.out.println("Found entry: " + c);
                break;
            } catch (Exception ignored) {}
        }

        if (clazz == null) {
            System.out.println("No Minecraft class found");
            return;
        }

        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();

            if (instance instanceof Applet) {

                Applet applet = (Applet) instance;

                applet.setStub(new FakeAppletStub(width, height));

                JFrame frame = new JFrame("Minecraft");
                frame.setSize(width, height);
                frame.setResizable(resizable);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                frame.add(applet);

                // Se la finestra e' ridimensionabile, propaghiamo il nuovo
                // formato all'applet quando l'utente trascina i bordi.
                // NB: alcune versioni molto vecchie di Minecraft non
                // reagiscono al resize a runtime (limite del motore
                // grafico dell'epoca, non del wrapper).
                if (resizable) {
                    frame.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            int newWidth = frame.getContentPane().getWidth();
                            int newHeight = frame.getContentPane().getHeight();
                            applet.setSize(newWidth, newHeight);
                            applet.resize(newWidth, newHeight);
                        }
                    });
                }

                frame.setVisible(true);

                // FIX IMPORTANTE: stabilizza LWJGL init
                try {
                    Thread.sleep(200);
                } catch (Exception ignored) {}

                applet.init();
                applet.start();

                System.out.println("Applet started successfully");

            } else {

                Method main = clazz.getMethod("main", String[].class);
                main.invoke(null, (Object) new String[]{});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int parseIntSafe(String value, int fallback) {
        if (value == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    // ================= FAKE APPLET STUB =================
    // NOTA: questa e' l'UNICA copia di FakeAppletStub effettivamente usata
    // dal wrapper. Il file FakeAppletStub.java separato nel repo e' un
    // duplicato inutilizzato: puoi eliminarlo per evitare confusione
    // in futuro (vedi promemoria in fondo al file).
    static class FakeAppletStub implements AppletStub {

        private final HashMap<String, String> params = new HashMap<>();

        public FakeAppletStub(int width, int height) {

            params.put("username", "Player");
            params.put("sessionid", "0");
            params.put("server", "");
            params.put("port", "25565");

            params.put("stand-alone", "true");

            params.put("width", String.valueOf(width));
            params.put("height", String.valueOf(height));
        }

        @Override
        public String getParameter(String name) {
            return params.get(name);
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public URL getDocumentBase() {
            try {
                return new URL("http://localhost/");
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public URL getCodeBase() {
            try {
                return new URL("http://localhost/");
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public AppletContext getAppletContext() {
            return null;
        }

        @Override
        public void appletResize(int width, int height) {}
    }
}

// ================= PROMEMORIA =================
// Il file FakeAppletStub.java (classe pubblica separata) NON viene usato
// da questo wrapper: qui si istanzia sempre la classe interna
// MinecraftAppletWrapper.FakeAppletStub. Se vuoi eliminare la duplicazione
// di codice segnalata nella code review, cancella semplicemente
// FakeAppletStub.java dal repo: nessun altro file lo referenzia.
