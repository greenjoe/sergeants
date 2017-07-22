package pl.joegreen.sergeants.simulator.viewer;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import pl.joegreen.sergeants.simulator.GameMap;

import java.io.File;

public class FileViewerWriter implements ViewerWriter {
    private JsonGenerator jsonGenerator;

    @SneakyThrows
    public FileViewerWriter(File file) {
        JsonFactory jsonFactory = new JsonFactory(new ObjectMapper());
        jsonGenerator = jsonFactory.createGenerator(file, JsonEncoding.UTF8);
        jsonGenerator.writeStartArray();
    }

    @Override
    @SneakyThrows
    public void write(GameMap gameMap){
        jsonGenerator.writeString(GameMap.toViewerMapState(gameMap).serializeToString());
    }

    @Override
    @SneakyThrows
    public void close(){
        jsonGenerator.writeEndArray();
        jsonGenerator.close();;
    }



}
