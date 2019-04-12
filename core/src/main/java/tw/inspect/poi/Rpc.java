package tw.inspect.poi;
import com.fasterxml.jackson.annotation.*;
import tw.inspect.poi.RpcExec;

public class Rpc {

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "op",
            visible = true)
    @JsonSubTypes(value = {
            @JsonSubTypes.Type(value = AddSheet.class, name = "ADD_SHEET"),
            @JsonSubTypes.Type(value = DeleteSheet.class, name = "DELETE_SHEET"),
            @JsonSubTypes.Type(value = RenameSheet.class, name = "RENAME_SHEET"),
            @JsonSubTypes.Type(value = CopyRows.class, name = "COPY_ROWS"),
            @JsonSubTypes.Type(value = Fill.class, name = "FILL")
    })
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Section {
        @JsonProperty("op")
        final String op;

        public Section(final String op) {
            this.op = op;
        }

        public RpcExec exec(RpcExec rpcExec) {
            return rpcExec.exec(this);
        }
    }

    @JsonTypeName("ADD_SHEET")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddSheet extends Section {

        @JsonProperty
        final String name;
        @JsonProperty
        final String order;

        public AddSheet(@JsonProperty("name") final String name, @JsonProperty("order") final String order) {
            super("ADD_SHEET");
            this.name = name;
            this.order = order;
        }
    }

    @JsonTypeName("DELETE_SHEET")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeleteSheet extends Section {
        @JsonProperty
        final String name;

        public DeleteSheet(@JsonProperty("name") final String name) {
            super("DELETE_SHEET");
            this.name = name;
        }
    }

    @JsonTypeName("RENAME_SHEET")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RenameSheet extends Section {

        @JsonProperty
        final String oldName;
        @JsonProperty
        final String newName;

        public RenameSheet(@JsonProperty("oldName") final String oldName, @JsonProperty("newName") final String newName) {

            super("RENAME_SHEET");
            this.oldName = oldName;
            this.newName = newName;
        }
    }

    @JsonTypeName("COPY_ROWS")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CopyRows extends Section {
        @JsonProperty
        final String srcSheet;
        @JsonProperty
        final String srcRowRange;
        @JsonProperty
        final String dstSheet;
        @JsonProperty
        final String dstRow;
        @JsonProperty
        final String extra;

        public CopyRows(
                @JsonProperty("srcSheet") final String srcSheet,
                @JsonProperty("srcRowRange") final String srcRowRange,
                @JsonProperty("dstSheet") final String dstSheet,
                @JsonProperty("dstRow") final String dstRow,
                @JsonProperty("extra") final String extra
        ) {
            super("COPY_ROWS");
            this.srcSheet = srcSheet;
            this.srcRowRange = srcRowRange;
            this.dstSheet = dstSheet;
            this.dstRow = dstRow;
            this.extra = extra;
        }
    }

    @JsonTypeName("FILL")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fill extends Section {
        @JsonProperty
        final String sheet;
        @JsonProperty
        final String co;
        @JsonProperty
        final String title;
        @JsonProperty
        final String extra;
        @JsonProperty
        final String value;


        public Fill(
                @JsonProperty("sheet") final String sheet,
                @JsonProperty("co") final String co,
                @JsonProperty("title") final String title,
                @JsonProperty("extra") final String extra,
                @JsonProperty("value") final String value
        ) {
            super("FILL");
            this.sheet = sheet;
            this.co = co;
            this.title = title;
            this.extra = extra;
            this.value = value;
        }
    }

}