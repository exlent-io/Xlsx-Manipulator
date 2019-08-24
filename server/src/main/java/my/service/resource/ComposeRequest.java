package my.service.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import tw.inspect.poi.Rpc;


import java.util.ArrayList;

public class ComposeRequest {
    //@JsonProperty("template_url")
    //public final String templateUrl;

    @JsonProperty("template_base64")
    public final String templateBase64;

    @JsonProperty("sections")
    public final ArrayList<Rpc.Section> sections;


    public ComposeRequest(
            @JsonProperty("template_base64") final String templateBase64,
            @JsonProperty("sections") final ArrayList<Rpc.Section> sections,
            @JsonProperty("output_google_drive_path") final String outputGoogleDrivePath,
            @JsonProperty("output_filename") final String outputFilename
    ) {
        this.templateBase64 = templateBase64;
        this.sections = sections;
    }
}
