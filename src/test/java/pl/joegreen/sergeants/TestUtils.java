package pl.joegreen.sergeants;

import pl.joegreen.sergeants.api.test.FakeSocket;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TestUtils {

    /**
     * SocketIO java client is using json.org json objects
     */
    public static Object asJsonOrgObject(String eventJson)  {
        try {
            return new JSONTokener(eventJson).nextValue();
        } catch (JSONException e) {
            throw new RuntimeException("Cannot deserialize:" + eventJson, e);
        }
    }

    public static List<FakeSocket.Event> readEventsFromClassPathFile(String fileName) throws URISyntaxException, IOException {
        Path path = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
        return Files.lines(path).map(TestUtils::lineToEvent).collect(toList());
    }

    private static FakeSocket.Event lineToEvent(String line){
        String separator = "###";
        String[] eventDescriptionParts = line.split(separator);
        String eventName = eventDescriptionParts[0];
        List<Object> arguments = Arrays.stream(eventDescriptionParts).skip(1).map(TestUtils::asJsonOrgObject)
                .collect(toList());
        return new FakeSocket.Event(eventName, arguments.toArray());
    }
}
