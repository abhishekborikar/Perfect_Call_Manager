package abhishek.redvelvet.com.android.Internal.telephony;

/**
 * Created by abhishek on 18/7/18.
 */

public interface ITelephony
//        extends android.os.IInterface
{
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}

