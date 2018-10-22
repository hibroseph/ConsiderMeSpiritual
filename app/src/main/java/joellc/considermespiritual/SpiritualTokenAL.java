package joellc.considermespiritual;

import android.app.Application;;
import android.content.Context;

public class SpiritualTokenAL extends Application {

    private static SpiritualTokenAL al;

    private SpiritualTokenAL() {
        // private constructor
    }

    public static void addSpiritualToken(Context context , SpiritualToken sp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Database.getDatabase(context).spiritualTokenDao().addSpiritualToken(sp);
            }
        }).start();

    }

    /*
    public static SpiritualToken getRandomSpiritualToken(Context context, SpiritualToken sp) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Database.getDatabase(context).spiritualTokenDao().getSpiritualToken();
            }
        }).start();


    }
    */

    public static SpiritualTokenAL getInstance() {
        if (al == null) {
            al = new SpiritualTokenAL();
        }

        return al;
    }
}
