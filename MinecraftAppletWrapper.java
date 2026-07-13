import java.applet.Applet;
import java.applet.AppletStub;
import java.applet.AppletContext;
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

        String[] candidates = {
                "net.minecraft.client.MinecraftApplet",
                "net.minecraft.client.Minecraft",
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

                applet.setStub(new FakeAppletStub());

                JFrame frame = new JFrame("Minecraft");
                frame.setSize(854, 480);
                frame.setResizable(false);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                frame.add(applet);

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

    // ================= FAKE APPLET STUB =================
    static class FakeAppletStub implements AppletStub {

        private final HashMap<String, String> params = new HashMap<>();

        public FakeAppletStub() {

            params.put("username", "Player");
            params.put("sessionid", "0");
            params.put("server", "");
            params.put("port", "25565");

            params.put("stand-alone", "true");

            params.put("width", "854");
            params.put("height", "480");
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