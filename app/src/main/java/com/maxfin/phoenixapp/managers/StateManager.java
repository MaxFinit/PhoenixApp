package com.maxfin.phoenixapp.managers;

import com.maxfin.phoenixapp.XMPPServerConnection.*;

import static com.maxfin.phoenixapp.managers.SipServerManager.*;

public class StateManager {

    private static StateManager sStateManager;


    private CallSIPState mCallSIPState;
    private ConnectionSIPState mConnectionSIPState;
    private ConnectionXMPPState mConnectionXMPPState;
    private LoggedInXMPPState mLoggedInXMPPState;

    public CallSIPState getCallSIPState() {
        return mCallSIPState;
    }

    public void setCallSIPState(CallSIPState callSIPState) {
        mCallSIPState = callSIPState;
    }

    public ConnectionSIPState getConnectionSIPState() {
        return mConnectionSIPState;
    }

    public void setConnectionSIPState(ConnectionSIPState connectionSIPState) {
        mConnectionSIPState = connectionSIPState;
    }

    public ConnectionXMPPState getConnectionXMPPState() {
        return mConnectionXMPPState;
    }

    public void setConnectionXMPPState(ConnectionXMPPState connectionXMPPState) {
        mConnectionXMPPState = connectionXMPPState;
    }

    public LoggedInXMPPState getLoggedInXMPPState() {
        return mLoggedInXMPPState;
    }

    public void setLoggedInXMPPState(LoggedInXMPPState loggedInXMPPState) {
        mLoggedInXMPPState = loggedInXMPPState;
    }

    public static StateManager getStateManager() {
        if (sStateManager == null) {
            sStateManager = new StateManager();
        }
        return sStateManager;
    }


}
