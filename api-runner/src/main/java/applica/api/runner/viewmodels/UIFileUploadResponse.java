package applica.api.runner.viewmodels;

public class UIFileUploadResponse {
    private final String name;
    private final String path;

    public UIFileUploadResponse(String originalFilename, String path) {
        this.name = originalFilename;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
