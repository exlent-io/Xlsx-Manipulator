package my.service.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import tw.inspect.poi.Rpc;


import java.util.ArrayList;

public class ComposeRequest {
    @JsonProperty("template_url")
    public final String templateUrl;

    @JsonProperty("template_base64")
    public final String templateBase64;

    @JsonProperty("sections")
    public final ArrayList<Rpc.Section> sections;

    @JsonProperty("gdrive_path")
    public final String gdrivePath;

    @JsonProperty("gdrive_filename")
    public final String gdriveFilename;


    public ComposeRequest(
            @JsonProperty("template_url") final String templateUrl,
            @JsonProperty("template_base64") final String templateBase64,
            @JsonProperty("sections") final ArrayList<Rpc.Section> sections,
            @JsonProperty("gdrive_path") final String gdrivePath,
            @JsonProperty("gdrive_filename") final String gdriveFilename
    ) {
        this.templateUrl = templateUrl;
        this.templateBase64 = templateBase64;
        this.sections = sections;
        this.gdrivePath = gdrivePath;
        this.gdriveFilename = gdriveFilename;
    }
}
