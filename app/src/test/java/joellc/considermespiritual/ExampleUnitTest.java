package joellc.considermespiritual;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    // Tests the function incrementPushId to make sure the last character increases by 1
    @Test
    public void incrementCharTest() throws Exception {
        MainActivity activity = new MainActivity();

        // Case 1 Simple id increment
        String string = "-LGOEw6muos-OonrZOnx";
        String incrementedString = activity.incrementPushId(string);
        assertEquals("-LGOEw6muos-OonrZOny", incrementedString);

        // Case 2 Simple name increment
        string = "Joseph";
        incrementedString = activity.incrementPushId(string);
        assertNotEquals("Joseph", incrementedString);
        assertEquals("Josepi", incrementedString);

        // Case 3 Simple number increment
        string = "1";
        incrementedString = activity.incrementPushId(string);
        assertEquals("2", incrementedString);

        // TODO: Add cases to handle weird incrementing like this
        // Case 4 Increment needs to change 2 chars
//        string = "LGOEw6muos-OonrZOnz";
//        incrementedString = activity.incrementPushId(string);
//        assertEquals("LGOEw6muos-OonrZOnz", incrementedString);

    }

}

