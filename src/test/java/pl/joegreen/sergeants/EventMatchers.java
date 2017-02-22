package pl.joegreen.sergeants;

import pl.joegreen.sergeants.api.test.FakeSocket;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;

public class EventMatchers {
    public static Matcher<FakeSocket.Event> withArguments(Object... arguments){
        return new TypeSafeMatcher<FakeSocket.Event>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Event arguments should be equal to ").appendValueList("(",", ", ")", arguments);
            }

            @Override
            protected boolean matchesSafely(FakeSocket.Event item) {
                return Arrays.equals(item.getArguments(), arguments);
            }

            @Override
            protected void describeMismatchSafely(FakeSocket.Event item, Description mismatchDescription) {
                mismatchDescription.appendText(" Event arguments were equal to" ).appendValueList("(", ", ", ")", item.getArguments());
            }
        };
    }
}
