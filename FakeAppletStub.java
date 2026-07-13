import java.applet.AppletStub;
import java.applet.AppletContext;
import java.applet.Applet;
import java.net.URL;
import java.util.HashMap;

public class FakeAppletStub implements AppletStub {

    private final HashMap<String, String> params = new HashMap<>();

    public FakeAppletStub() {

        // PARAMETRI MINIMI CHE MINECRAFT INFDEV/INDEV SI ASPETTA
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
        return null;
    }

    @Override
    public URL getCodeBase() {
        return null;
    }

    @Override
    public AppletContext getAppletContext() {
        return null;
    }

    @Override
    public void appletResize(int width, int height) {}
}